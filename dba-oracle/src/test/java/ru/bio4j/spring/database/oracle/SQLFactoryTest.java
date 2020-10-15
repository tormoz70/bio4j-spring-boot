package ru.bio4j.spring.database.oracle;

import org.junit.*;
import ru.bio4j.spring.commons.types.LogWrapper;
import ru.bio4j.spring.commons.types.Paramus;
import ru.bio4j.spring.commons.utils.Utl;
import ru.bio4j.spring.database.api.SQLContext;
import ru.bio4j.spring.database.api.SQLDefinition;
import ru.bio4j.spring.database.api.SQLStoredProc;
import ru.bio4j.spring.database.commons.CrudReaderApi;
import ru.bio4j.spring.database.commons.CursorParser;
import ru.bio4j.spring.database.commons.DbContextFactory;
import ru.bio4j.spring.database.commons.DbUtils;
import ru.bio4j.spring.model.BaseDataSourceProperties;
import ru.bio4j.spring.model.transport.*;
import ru.bio4j.spring.model.transport.errors.BioSQLApplicationError;
import ru.bio4j.spring.model.transport.errors.BioSQLException;
import ru.bio4j.spring.model.transport.errors.ConvertValueException;
import ru.bio4j.spring.model.transport.jstore.Sort;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SQLFactoryTest {
    private static final LogWrapper LOG = LogWrapper.getLogger(SQLFactoryTest.class);
    private static final String testDBDriverName = "oracle.jdbc.driver.OracleDriver";
    private static final String testDBUrl = "jdbc:oracle:thin:@192.168.70.30:1521:EKBS02";
//    private static final String testDBUrl = "jdbc:oracle:thin:@stat4-ora-dev:1521:MICEXDB";
//    private static final String testDBUrl = "jdbc:oracle:thin:@cmon-ora-dev:1521:MICEXDB";
    //private static final String testDBUrl = "jdbc:oracle:oci:@GIVCDB_EKBS03";
    //private static final String testDBUrl = "jdbc:oracle:thin:@https://databasetrial0901-rugivcmkrftrial07058.db.em1.oraclecloudapps.com/apex:1521:databasetrial0901";
    private static final String testDBUsr = "SCOTT";
    private static final String testDBPwd = "tiger";

    private static SQLContext context;

    private static class Var {
        int iummy;
        double dummy;
        String summy;
        byte[] bummy;
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        context = DbContextFactory.createHikariCP(
                BaseDataSourceProperties.builder()
                        .driverClassName(testDBDriverName)
                        .url(testDBUrl)
                        .username(testDBUsr)
                        .password(testDBPwd)
                        .build(),
                OracleContext.class);
        //if(true) return;

        try {
            context.execBatch((conn) -> {
                String sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_test_table.sql"));
                DbUtils.execSQL(conn, sql);
                sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_simple.sql"));
                DbUtils.execSQL(conn, sql);
                sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_error.sql"));
                DbUtils.execSQL(conn, sql);
                sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_ret_cursor.sql"));
                DbUtils.execSQL(conn, sql);
                sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_with_inout.sql"));
                DbUtils.execSQL(conn, sql);
                sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_long.sql"));
                DbUtils.execSQL(conn, sql);

                sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_storeclob.sql"));
                DbUtils.execSQL(conn, sql);
                return null;
            }, null);
        } catch (BioSQLException ex) {
            LOG.error("Error!", ex);
        }
    }

    @AfterClass
    public static void finClass() throws Exception {
        //if(true) return;
        try {
            context.execBatch(conn -> {
                DbUtils.execSQL(conn, "drop procedure test_stored_prop");
                DbUtils.execSQL(conn, "drop procedure test_stored_error");
                DbUtils.execSQL(conn, "drop procedure test_stored_cursor");
                DbUtils.execSQL(conn, "drop procedure test_stored_inout");
                DbUtils.execSQL(conn, "drop table test_tbl");
                return null;
            }, null);
        } catch (BioSQLException ex) {
            LOG.error("Error!", ex);
        }
    }

    @Test
    public void testCreateSQLConnectionPool() throws Exception {
//        LOG.debug(Utl.buildBeanStateInfo(context.getStat(), null, null));
        context.execBatch(conn -> {
            Assert.assertNotNull(conn);
        }, null);

    }

    @Test
    public void testSQLCommandOpenCursor() {
        try {
            Double dummysum = context.execBatch(conn -> {
                Var var = new Var();
                String sql = "select user as curuser, :dummy as dm, :dummy1 as dm1 from dual";
                List<Param> prms = Paramus.set(new ArrayList<Param>()).add("dummy", 101).pop();
                context.createCursor()
                        .init(conn, sql, null)
                        .fetch(prms, context.getCurrentUser(), rs->{
                            var.dummy += rs.getValue("DM", Double.class);
                            return true;
                        });
                return var.dummy;
            }, null);
            LOG.debug("dummysum: " + dummysum);
            Assert.assertEquals(dummysum, 101.0, 0);
        } catch (Exception ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }

    }

    @Ignore
    @Test
    public void testSQLCommandOpenCursor1() {
        try {
            byte[] schema = context.execBatch(conn -> {
                Var var = new Var();
                String sql = "select * from table(givcapi.upld.get_schemas)";
                context.createCursor()
                        .init(conn, sql, null)
                        .fetch(context.getCurrentUser(), rs->{
                            if(var.bummy == null)
                                var.bummy = rs.getValue("XSD_BODY", byte[].class);
                            return true;
                        });
                return var.bummy;
            }, null);
            Assert.assertTrue(schema.length > 0);
        } catch (Exception ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }

    }

    @Test
    public void testSQLCommandExecSQL() throws Exception {
        try {
            int leng = context.execBatch(conn -> {
                int leng1 = 0;
                LOG.debug("conn: " + conn);

                SQLStoredProc cmd = context.createStoredProc();
                String storedProgName = "test_stored_prop";
                try(Paramus paramus = Paramus.set(new ArrayList<Param>())) {
                    paramus.add("p_param1", "FTW")
                          .add(Param.builder()
                                  .name("p_param2")
                                  .type(MetaType.INTEGER)
                                  .direction(Param.Direction.OUT)
                                  .build());
                    cmd.init(conn, storedProgName);
                    cmd.execSQL(paramus.get(), context.getCurrentUser());
                }
                try(Paramus paramus = Paramus.set(cmd.getParams())) {
                    leng1 = Utl.nvl(paramus.getParamValue("p_param2", Integer.class), 0);
                }
                conn.rollback();
                return leng1;
            }, null);
            LOG.debug("leng: " + leng);
            Assert.assertEquals(leng, 3);
        } catch (BioSQLException ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }
    }

    public void execSQLCommandExecSQLLong() throws Exception {
        context.execBatch(conn -> {

            SQLStoredProc cmd = context.createStoredProc();
            String storedProgName = "test_stored_long";
            try(Paramus paramus = Paramus.set(new ArrayList<Param>())) {
                paramus.add("p_param1", "FTW")
                        .add(Param.builder()
                                .name("p_param2")
                                .type(MetaType.INTEGER)
                                .direction(Param.Direction.OUT)
                                .build());
                cmd.init(conn, storedProgName);
                cmd.execSQL(paramus.get(), context.getCurrentUser());
            }
        }, null);
    }

    @Ignore
    @Test
    public void testSQLCommandExecSQLLong() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(3);;
        List<Future> futures = new ArrayList<>();
        for(int i=0; i<4; i++) {
            Future future = executor.submit(() -> {
                while (true)
                    execSQLCommandExecSQLLong();
            });
            futures.add(future);
        }
        for(Future<?> future : futures)
            future.get();

    }

    @Test
    public void testSQLCommandExecSQL1() throws Exception {
        try {
            int leng = context.execBatch(conn -> {
                int leng1 = 0;
                LOG.debug("conn: " + conn);

                SQLStoredProc cmd = context.createStoredProc();
                String storedProgName = "test_stored_prop";
                List<Param> prms = new ArrayList<Param>();
                prms.add(Param.builder().name("p_param1").value("FTW").build());
                prms.add(Param.builder().name("p_param2").type(MetaType.INTEGER).direction(Param.Direction.OUT).build());
                cmd.init(conn, storedProgName);
                Paramus.setParamValue(prms, "p_param1", "QWE");
                Paramus.setParamValue(prms, "p_param3", "ASD");
                cmd.execSQL(prms, context.getCurrentUser());
                try(Paramus paramus = Paramus.set(cmd.getParams())) {
                    leng1 = Utl.nvl(paramus.getParamValue("p_param2", Integer.class), 0);
                }
                conn.rollback();
                return leng1;
            }, null);
            LOG.debug("leng: " + leng);
            Assert.assertEquals(leng, 3);
        } catch (BioSQLException ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }
    }

    @Test
    public void testSQLCommandExecINOUTSQL() throws Exception {
        try {
            int leng = context.execBatch(conn -> {
                int leng1 = 0;
                LOG.debug("conn: " + conn);

                SQLStoredProc cmd = context.createStoredProc();
                try(Paramus paramus = Paramus.set(new ArrayList<Param>())) {
                    paramus.add(Param.builder().name("p_param1").type(MetaType.INTEGER).value(null).build())
                            .add("p_param2", "QWE")
                            .add(Param.builder().name("p_param3").type(MetaType.BOOLEAN).value(true).build())
                            .add(Param.builder().name("p_param4").type(MetaType.DECIMAL).value("").build());
                    cmd.init(conn, "test_stored_inout");
                    cmd.execSQL(paramus.get(), context.getCurrentUser());
                }
                try(Paramus paramus = Paramus.set(cmd.getParams())) {
                    leng1 = Utl.nvl(paramus.getParamValue("p_param1", Integer.class), 0);
                }
                conn.rollback();
                return leng1;
            }, null);
            LOG.debug("leng: " + leng);
            Assert.assertEquals(leng, 3);
        } catch (BioSQLException ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }
    }

    private String creXML() {
        XLRCfg xlrCfg = new XLRCfg();

        XLRCfg.DataSource ds = new XLRCfg.DataSource();
        ds.setSql("select 1 from dual");

        ds.setSorts(new ArrayList<>());
        Sort s = new Sort();
        s.setFieldName("sortField");
        s.setDirection(Sort.Direction.DESC);
        ds.getSorts().add(s);

        XLRCfg.ColumnDefinition cd = new XLRCfg.ColumnDefinition();
        cd.setFieldName("field1");
        cd.setTitle("Колонка 1");
        cd.setFormat("##0.00");
        ds.getColumnDefinitions().add(cd);

        xlrCfg.setDss(new ArrayList<>());
        xlrCfg.getDss().add(ds);

        xlrCfg.setAppend(new XLRCfg.Append());
        xlrCfg.getAppend().setInParams(new ArrayList<>());
        xlrCfg.getAppend().getInParams().add(Param.builder().name("inparam1").type(MetaType.STRING).direction(Param.Direction.IN).value("inparam1-value").build());
        xlrCfg.getAppend().setSessionID("sess-id");
        xlrCfg.getAppend().setUserUID("user-uid");
        xlrCfg.getAppend().setUserName("user-name");
        xlrCfg.getAppend().setUserOrgId("user-org-id");
        xlrCfg.getAppend().setUserRoles("user-roles");
        xlrCfg.getAppend().setRemoteIP("remote-ip");


        String encoding = "UTF-8";
        return null; ///XStreamUtility.getInstance().toXml(xlrCfg, encoding);

    }

    @Test
    public void testSQLCommandExecStoreCLOB() throws Exception {
        try {
            context.execBatch((conn) -> {
                LOG.debug("conn: " + conn);

                String xml = creXML();

                SQLStoredProc cmd = context.createStoredProc();
                List<Param> prms = new ArrayList<>();
                        Paramus.setParam(prms, Param.builder().name("p_param1").type(MetaType.STRING).value("ASD").build(), true);
                        Paramus.setParam(prms, Param.builder().name("p_param2").type(MetaType.INTEGER).value(1).build(), true);
                        Paramus.setParam(prms, Param.builder().name("p_param3").type(MetaType.CLOB).value(xml).build(), true);
                cmd.init(conn, "test_store_clob");
                cmd.execSQL(prms, context.getCurrentUser());

            }, null);
        } catch (Exception ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }
    }

    @Test
    public void testSQLCommandExecStoreCLOB1() throws Exception {
        try {
            context.execBatch((conn) -> {
                LOG.debug("conn: " + conn);

                String xml = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("qqq.xml"));

                SQLStoredProc cmd = context.createStoredProc();
                List<Param> prms = new ArrayList<>();
                Paramus.setParam(prms, Param.builder().name("p_param1").type(MetaType.STRING).value("ASD").build(), true);
                Paramus.setParam(prms, Param.builder().name("p_param2").type(MetaType.INTEGER).value(1).build(), true);
                Paramus.setParam(prms, Param.builder().name("p_param3").type(MetaType.CLOB).value(xml).build(), true);
                cmd.init(conn, "test_store_clob");
                cmd.execSQL(prms, context.getCurrentUser());

            }, null);
        } catch (BioSQLException ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }
    }

    @Test
    public void testSQLCommandExecExtParam() throws Exception {
        try {
            int leng = context.execBatch(conn -> {
                int leng1 = 0;
                LOG.debug("conn: " + conn);

                SQLStoredProc cmd = context.createStoredProc();
                String storedProgName = "test_stored_prop";
                List<Param> prms = new ArrayList<>();
                try(Paramus paramus = Paramus.set(prms)) {
                    paramus.add("param1", "FTW")
                            .add("param3", "ext");
                }
                cmd.init(conn, storedProgName);
                cmd.execSQL(prms, context.getCurrentUser());
                try(Paramus paramus = Paramus.set(cmd.getParams())) {
                    leng1 = Utl.nvl(paramus.getParamValue("p_param2", Integer.class), 0);
                }
                conn.rollback();
                return leng1;
            }, null);
            LOG.debug("leng: " + leng);
            Assert.assertEquals(leng, 3);
        } catch (BioSQLException ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }
    }

    @Test
    public void testSQLCommandExecError() throws Exception {
        try {
            context.execBatch((conn, param) -> {
                LOG.debug("conn: " + conn + "; param: " + param);

                SQLStoredProc cmd = context.createStoredProc();
                String storedProgName = "test_stored_error";
                List<Param> prms = new ArrayList<Param>();
                prms.add(Param.builder().name("p_param1").value("FTW").build());
                prms.add(Param.builder()
                    .name("p_param2")
                    .type(MetaType.INTEGER)
                    .direction(Param.Direction.OUT)
                    .build());
                cmd.init(conn, storedProgName);

                cmd.execSQL(prms, context.getCurrentUser());
            }, "AnContext", null);
        } catch (BioSQLException ex) {
            LOG.error("Error!", ex);
            Assert.assertEquals(ex.getErrorCode(), 20000);
        }
    }
    
    @Test
    public void testSQLCommandExecSQLAutoCommit() throws Exception {
        try {
            int leng = context.execBatch(conn -> {
                int leng1 = 0;
                LOG.debug("conn: " + conn);

                SQLStoredProc cmd = context.createStoredProc();
                String storedProgName = "test_stored_prop";
                try(Paramus paramus = Paramus.set(new ArrayList<Param>())) {
                    paramus.add("p_param1", "FTW")
                        .add(Param.builder()
                                .name("p_param2")
                                .type(MetaType.INTEGER)
                                .direction(Param.Direction.OUT)
                                .build());
                    cmd.init(conn, storedProgName).execSQL(paramus.get(), context.getCurrentUser());
                }
                try(Paramus paramus = Paramus.set(cmd.getParams())) {
                    leng1 = Utl.nvl(paramus.getParamValue("p_param2", Integer.class), 0);
                }
                return leng1;
            }, null);
            LOG.debug("leng: " + leng);
            Assert.assertEquals(leng, 3);
        } catch (Exception ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }
    }

    private static <T> T getParamValue(List<Param> params, Class<T> type, String paramName) throws SQLException {
        try {
            return Paramus.set(params).getValueByName(type, paramName, true);
        } catch (ConvertValueException ex) {
            throw new SQLException(ex);
        } finally {
            Paramus.instance().pop();
        }
    }

    @Ignore
    @Test
    public void testSQLCommandStoredProc() throws Exception {
        try {

            Paramus paramus = Paramus.set(new ArrayList<Param>());
            paramus.add("p_login", "qweqwe@asd.com/qegedipe")
                   .add(Param.builder().name("v_uid").type(MetaType.STRING).direction(Param.Direction.OUT).build());
            List<Param> params = paramus.pop();
            String uid = context.execBatch((conn, p) -> {
                SQLStoredProc prc = context.createStoredProc();
                prc.init(conn, "bio_login2.check_login").execSQL(p, context.getCurrentUser());
                try(Paramus paramus1 = Paramus.set(prc.getParams())) {
                    return paramus1.getValueAsStringByName("v_uid", true);
                }

            }, params, null);

            LOG.debug(String.format("Login: OK; uid: %s", uid));
            Assert.assertEquals(uid, "FTW");
        } catch (BioSQLException ex) {
            LOG.error("Error!!!", ex);
        }
    }

    @Ignore
    @Test
    public void testSQLCommandStoredProcRetCursor() throws Exception {
        try {
            int c = context.execBatch(conn -> {
                ResultSet resultSet = null;
                LOG.debug("conn: " + conn);

                SQLStoredProc cmd = context.createStoredProc();
                String storedProgName = "test_stored_cursor";
                try(Paramus paramus = Paramus.set(new ArrayList<Param>())) {
                    paramus.add("p_param1", "FTW")
                            .add(Param.builder()
                                    .name("p_param2")
                                    .type(MetaType.CURSOR)
                                    .direction(Param.Direction.OUT)
                                    .build());
                    cmd.init(conn, storedProgName).execSQL(paramus.get(), context.getCurrentUser());
                }
                try(Paramus paramus = Paramus.set(cmd.getParams())) {
                    resultSet = paramus.getParamValue("p_param2", ResultSet.class);
                    if(resultSet.next()) {
                        String userName = resultSet.getString("USERNAME");
                        LOG.debug("userName: " + userName);
                        Assert.assertEquals(userName, "SCOTT");
                    }
                }
                return 0;
            }, null);
        } catch (Exception ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }
    }

//    @Test
//    public void testCallStoredProc() {
//        try {
//            try (Connection conn = param.getConnection()) {
//                CallableStatement cs = conn.prepareCall( "{call test_stored_prop(:p_param1, :p_param2)}");
//                cs.setString("p_param1", "FTW");
//                cs.registerOutParameter("p_param2", Types.INTEGER);
//                cs.execute();
//                int leng = cs.getInt("p_param2");
//                Assert.assertEquals(leng, 3);
//            }
//        } catch (SQLException ex) {
//            LOG.error("Error!", ex);
//        }
//    }

    @Test
    public void sqlExceptionExtTest() {
        List<Param> params = new ArrayList<>();
        params.add(Param.builder()
                .name("qwe")
                .value(123)
                .build()
        );
        StringBuilder sb = new StringBuilder();
        sb.append("{OraCommand.Params(before exec): {\n");
        for (Param p : params)
            sb.append("\t"+p.toString()+",\n");
        sb.append("}}");

        SQLException e = new SQLException("QWE-TEST");
        BioSQLException r = BioSQLException.create(String.format("%s:\n - sql: %s;\n - %s", "Error on execute command.", "select * from dual", sb.toString()), e);
        String msg = r.getMessage();
        LOG.debug(msg);
    }

    @Ignore
    @Test
    public void testSQLCommandOpenCursor2() {
        try {

            SQLContext contextLocal = DbContextFactory.createHikariCP(
                    BaseDataSourceProperties.builder()
                            .driverClassName(testDBDriverName)
                            .url(testDBUrl)
                            .username("GIVCADMIN")
                            .password("j12")
                            .build(),
                    OracleContext.class);

            String rslt = contextLocal.execBatch(conn -> {
                String sql = "SELECT * FROM (\n" +
                        "        SELECT pgng_wrpr0.*, ROWNUM rnum_pgng\n" +
                        "          FROM ( with \n" +
                        "orgs as (\n" +
                        "  select /*+ MATERIALIZE */ o.id_org, o.holding_id, o.time_zone, o.id_vnd, DECODE (o.test, 1, 'тестовый', 'реальный') AS test,\n" +
                        "        v.vndname, v.contacts AS vndcontacts\n" +
                        "    from org$r o \n" +
                        "      left join givc_org.softvendor v ON v.id_vnd = o.id_vnd\n" +
                        "),\n" +
                        "ekbps as (\n" +
                        "SELECT /*+ MATERIALIZE */\n" +
                        "   a.packet_id as id,\n" +
                        "  a.org_id,\n" +
                        "  a.sess_prnt_org_id,\n" +
                        "  a.sess_org_id,\n" +
                        "  a.ip_addr as ip,\n" +
                        "  a.registred as date_incoming,\n" +
                        "  decode(a.packet_name, '...', a.zip_name, a.packet_name) as packet_name,\n" +
                        "  a.zip_name,\n" +
                        "  a.processed as date_processing,\n" +
                        "  a.is_loaded,\n" +
                        "  a.cur_pstate as cur_pstate0,\n" +
                        "  decode(a.is_loaded, '1', 'загружен', s.description) as cur_pstate,\n" +
                        "  a.cur_pstate_msg as last_pstate_msg,\n" +
                        "  a.is_log_downloaded,\n" +
                        "  a.log_downloaded,\n" +
                        "  nvl(o1.time_zone, o2.time_zone) as time_zone,\n" +
                        "  nvl(o1.id_vnd, o2.id_vnd) as id_vnd, \n" +
                        "  nvl(o1.vndname, o2.vndname) as vndname, \n" +
                        "  nvl(o1.vndcontacts, o2.vndcontacts) as vndcontacts,\n" +
                        "  nvl(o1.test, o2.test) as test,\n" +
                        "  decode(a.load_method, \n" +
                        "          0, 'Авт. система', \n" +
                        "          1, 'Из кабинета', \n" +
                        "          2, 'CreateXMLStatic', \n" +
                        "          3, 'CreateXMLMobile', \n" +
                        "          4, 'EkbUploadRobot', \n" +
                        "          5, 'Grader', 'не определен') as load_method,\n" +
                        "  decode(a.show_date, null, '00000000', to_char(a.show_date, 'YYYYMMDD')) part_key\n" +
                        "  FROM FPACKET a\n" +
                        "        left join orgs o1 on o1.id_org = a.sess_org_id\n" +
                        "        left join orgs o2 on o2.id_org = a.org_id\n" +
                        "        left join nsi$ekbpstate s on s.id = a.cur_pstate\n" +
                        "  WHERE a.packet_name = nvl(:packet_name_full, a.packet_name) and\n" +
                        "        ((a.registred >= biosys.ai_utl.db_datetime(decode(:reg_from, null, trunc(sysdate), :reg_from), :time_zone)) AND\n" +
                        "         (a.registred < biosys.ai_utl.db_datetime(decode(:reg_to, null, trunc(sysdate), :reg_to), :time_zone)+1))\n" +
                        "    AND ((:force_org_id IS NULL) OR \n" +
                        "          ((a.org_id = :force_org_id) OR ((a.sess_org_id = :force_org_id) and (a.org_id = o1.holding_id))\n" +
                        "          or (a.sess_prnt_org_id =:force_org_id))\n" +
                        "         )\n" +
                        "    \n" +
                        ")\n" +
                        "SELECT \n" +
                        "  a.id, \n" +
                        "  a.org_id,\n" +
                        "  a.sess_prnt_org_id,\n" +
                        "  a.sess_org_id,\n" +
                        "  a.ip, \n" +
                        "  a.date_incoming,\n" +
                        "  a.packet_name, \n" +
                        "  a.zip_name,\n" +
                        "  a.date_processing,\n" +
                        "  a.time_zone,\n" +
                        "  a.is_loaded,\n" +
                        "  a.cur_pstate0,\n" +
                        "  a.cur_pstate,\n" +
                        "  a.last_pstate_msg,\n" +
                        "  a.is_log_downloaded,\n" +
                        "  a.log_downloaded, \n" +
                        "  a.id_vnd, a.vndname, a.vndcontacts,\n" +
                        "  a.load_method,\n" +
                        "  a.test,\n" +
                        "  a.part_key\n" +
                        "  FROM ekbps a\n" +
                        "  WHERE ((:p_sys_curusr_roles in ('6')) or\n" +
                        "         ((:p_sys_curusr_roles in ('4')) and (\n" +
                        "           (a.org_id = to_number(:p_sys_curodepuid)) or\n" +
                        "           (a.org_id in (select o.id_org from givc_org.org o where o.holding_id = to_number(:p_sys_curodepuid)))\n" +
                        "          )\n" +
                        "         ) or\n" +
                        "         ((:p_sys_curusr_roles in ('3')) and (\n" +
                        "            /*(a.sess_org_id = to_number(:SYS_CURODEPUID)) and*/\n" +
                        "            ((a.org_id = to_number(:p_sys_curodepuid)) or\n" +
                        "             (a.org_id in (select nvl(o.holding_id, o.id_org) from givc_org.org o where o.id_org = to_number(:p_sys_curodepuid)))\n" +
                        "            )\n" +
                        "          )\n" +
                        "         )\n" +
                        "    ) AND \n" +
                        "    (\n" +
                        "      (a.org_id = decode(:org_id, null, a.org_id, nvl(to_number(regexp_substr(:org_id, '^\\d+$')), 0))) and\n" +
                        "      (nvl(a.sess_prnt_org_id, 0) = decode(:sess_prnt_org_id, null, nvl(a.sess_prnt_org_id, 0), nvl(to_number(regexp_substr(:sess_prnt_org_id, '^\\d+$')), 0))) and\n" +
                        "      (a.sess_org_id = decode(:sess_org_id, null, a.sess_org_id, nvl(to_number(regexp_substr(:sess_org_id, '^\\d+$')), 0))) and\n" +
                        "      (a.packet_name like decode(:packet_name, null, '%', '%'||lower(:packet_name)||'%')) and\n" +
                        "      (a.ip like decode(:ip, null, '%', '%'||lower(:ip)||'%')) and\n" +
                        "      (a.cur_pstate like decode(:cur_pstate, null, '%', '%'||lower(:cur_pstate)||'%')) and\n" +
                        "      (a.last_pstate_msg like decode(:message, null, '%', '%'||lower(:message)||'%')) and\n" +
                        "      (lower(a.load_method) like decode(:load_method, null, '%', '%'||lower(:load_method)||'%')) and\n" +
                        "      (a.test like decode(:test, null, '%', '%'||lower(:test)||'%')) \n" +
                        "    )\n" +
                        "  ORDER BY date_incoming desc\n" +
                        " ) pgng_wrpr0\n" +
                        "    ) pgng_wrpr WHERE (pgng_wrpr.rnum_pgng > :paging$offset) AND (pgng_wrpr.rnum_pgng <= :paging$last)";
                int cnt = 0;
                List<Param> params = new ArrayList<>();


                //params.add(Param.builder().name("packet_name_full").type(MetaType.STRING).build());
                //params.add(Param.builder().name("reg_from").type(MetaType.DATE).build());
                //params.add(Param.builder().name("reg_to").type(MetaType.DATE).build());
                //params.add(Param.builder().name("time_zone").type(MetaType.INTEGER).build());
                params.add(Param.builder().name("p_sys_curusr_roles").value("6").type(MetaType.STRING).build());
                //params.add(Param.builder().name("p_sys_curodepuid").type(MetaType.STRING).build());

                params.add(Param.builder().name("force_org_id").type(MetaType.INTEGER).build());
                //params.add(Param.builder().name("org_id").type(MetaType.INTEGER).build());
                //params.add(Param.builder().name("sess_org_id").type(MetaType.INTEGER).build());

                //params.add(Param.builder().name("sess_prnt_org_id").type(MetaType.INTEGER).build());
                //params.add(Param.builder().name("packet_name").type(MetaType.STRING).build());
                //params.add(Param.builder().name("ip").type(MetaType.STRING).build());
                //params.add(Param.builder().name("cur_pstate").type(MetaType.STRING).build());
                //params.add(Param.builder().name("message").type(MetaType.STRING).build());
                //params.add(Param.builder().name("load_method").type(MetaType.STRING).build());
                //params.add(Param.builder().name("test").type(MetaType.STRING).build());
                //params.add(Param.builder().name("p_sys_curusr_uid").type(MetaType.STRING).build());
                //params.add(Param.builder().name("p_sys_curusr_grants").type(MetaType.STRING).build());
                //params.add(Param.builder().name("prm1").value("qwe").build());
                //params.add(Param.builder().name("prm2").value("qwe").build());
                params.add(Param.builder().name("paging$offset").value(0).type(MetaType.INTEGER).build());
                params.add(Param.builder().name("paging$last").value(25).type(MetaType.INTEGER).build());
                Var var = new Var();
                context.createCursor()
                        .init(conn, sql, params)
                        .fetch(context.getCurrentUser(), rs->{
                            var.iummy++;
                            return true;
                        });
                return ""+cnt;
            }, null);
            Assert.assertEquals("25", rslt);
        } catch (Exception ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }

    }

    @Ignore
    @Test
    public void testSQLCommandOpenCursor3() {
        try {

            SQLContext contextLocal = DbContextFactory.createHikariCP(
                BaseDataSourceProperties.builder()
                    .driverClassName(testDBDriverName)
                    .url(testDBUrl)
                    .username("GIVCADMIN")
                    .password("j12")
                    .build(),
                OracleContext.class);

            String rslt = contextLocal.execBatch(conn -> {
                String sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("111.sql"));

                int cnt = 0;
                List<Param> params = new ArrayList<>();

/*
                1-p_sys_curusr_roles(in)(VARCHAR)..................."6";
                2-p_sys_curusr_org_uid(in)(VARCHAR)................."5567";
                3-org_id(in)(INTEGER)...............................[244];
                4-reg_from(in)(DATE)................................[Thu Aug 04 00:00:00 GMT+03:00 2016];
                5-reg_to(in)(DATE)..................................[Thu Aug 04 00:00:00 GMT+03:00 2016];
                6-film(in)(VARCHAR)................................."";
                7-sroom_id(in)(INTEGER).............................[null];
                8-force_org_id(in)(VARCHAR).........................[null];
                9-paging$offset(in)(INTEGER)........................[0];
                10-paging$last(in)(INTEGER).........................[25];
*/

                params.add(Param.builder().name("p_sys_curusr_roles").value("6").type(MetaType.STRING).build());
                params.add(Param.builder().name("p_sys_curusr_org_uid").value("5567").type(MetaType.STRING).build());
                params.add(Param.builder().name("org_id").value(244).type(MetaType.INTEGER).build());

                Date testDateValue = new Date();
                params.add(Param.builder().name("reg_from").value(testDateValue).type(MetaType.DATE).build());
                params.add(Param.builder().name("reg_to").value(testDateValue).type(MetaType.DATE).build());

                params.add(Param.builder().name("film").value("").type(MetaType.STRING).build());

                        params.add(Param.builder().name("force_org_id").type(MetaType.INTEGER).build());
                params.add(Param.builder().name("paging$offset").value(0).type(MetaType.INTEGER).build());
                params.add(Param.builder().name("paging$last").value(25).type(MetaType.INTEGER).build());
                Var var = new Var();
                context.createCursor()
                        .init(conn, sql, params)
                        .fetch(context.getCurrentUser(), rs->{
                            var.iummy++;
                            return true;
                        });
                return ""+cnt;
            }, null);
            Assert.assertEquals(rslt, "17");
        } catch (Exception ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }

    }

    @Test
    public void testSQLCommandOpenCursor4() throws Exception {

        SQLContext context = DbContextFactory.createHikariCP(
                BaseDataSourceProperties.builder()
                        .driverClassName(testDBDriverName)
                        .url(testDBUrl)
                        .username("GIVCADMIN")
                        .password("j12")
                        .build(),
                OracleContext.class);


        String rslt = context.execBatch((conn) -> {

            String sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("111.sql"));

            int cnt = 0;
            List<Param> params = new ArrayList<>();

/*
                1-p_sys_curusr_roles(in)(VARCHAR)..................."6";
                2-p_sys_curusr_org_uid(in)(VARCHAR)................."5567";
                3-org_id(in)(INTEGER)...............................[244];
                4-reg_from(in)(DATE)................................[Thu Aug 04 00:00:00 GMT+03:00 2016];
                5-reg_to(in)(DATE)..................................[Thu Aug 04 00:00:00 GMT+03:00 2016];
                6-film(in)(VARCHAR)................................."";
                7-sroom_id(in)(INTEGER).............................[null];
                8-force_org_id(in)(VARCHAR).........................[null];
                9-paging$offset(in)(INTEGER)........................[0];
                10-paging$last(in)(INTEGER)..........................[25];
*/

            params.add(Param.builder().name("p_sys_curusr_roles").value("6").type(MetaType.STRING).build());
            params.add(Param.builder().name("p_sys_curusr_org_uid").value("5567").type(MetaType.STRING).build());
            params.add(Param.builder().name("org_id").value(244).type(MetaType.INTEGER).build());

            Date testDateValue = new Date();
            params.add(Param.builder().name("reg_from").value(testDateValue).type(MetaType.DATE).build());
            params.add(Param.builder().name("reg_to").value(testDateValue).type(MetaType.DATE).build());

            params.add(Param.builder().name("film").value("").type(MetaType.STRING).build());

            params.add(Param.builder().name("force_org_id").type(MetaType.INTEGER).build());
            params.add(Param.builder().name("paging$offset").value(0).type(MetaType.INTEGER).build());
            params.add(Param.builder().name("paging$last").value(25).type(MetaType.INTEGER).build());

            Var var = new Var();
            context.createCursor()
                    .init(conn, sql, params)
                    .fetch(context.getCurrentUser(), rs->{
                        var.iummy++;
                        return true;
                    });
            return ""+cnt;

        }, null);
        //response;
    }

    @Ignore
    @Test
    public void testSQLCommandOpenCursor5() throws Exception {

        SQLContext context = DbContextFactory.createHikariCP(
                BaseDataSourceProperties.builder()
                        .driverClassName(testDBDriverName)
                        .url(testDBUrl)
                        .username("GIVCADMIN")
                        .password("j12")
                        .build(),
                OracleContext.class);

        String rslt = context.execBatch((conn) -> {

            String sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("333.sql"));

            List<Param> params = new ArrayList<>();
            params.add(Param.builder().name("p_rpt_uid").value("4F923F3C7A395D05E0531E32A8C06A1D").type(MetaType.STRING).build());

            return context.createCursor()
                    .init(conn, sql, params)
                    .scalar(context.getCurrentUser(), "state_desc", String.class, null);

        }, null);

        Assert.assertTrue(rslt.length() > 0);
    }

    @Test
    public void testCursorParser() {
        try {
            CursorParser.pars("bios.movies1");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("is not found"));
        }
    }

    @Test
    public void testCrudReaderApi_loadPageExt0() {

        SQLContext context = DbContextFactory.createHikariCP(
                BaseDataSourceProperties.builder()
                        .driverClassName(testDBDriverName)
                        .url(testDBUrl)
                        .username("GIVCADMIN")
                        .password("j12")
                        .build(),
                OracleContext.class);

        SQLDefinition sqlDefinition = CursorParser.pars("bios.movies");
        List<Param> prms = Paramus.createParams(
                "searchString", "икс",
                Rest2sqlParamNames.PAGINATION_PARAM_PAGE, "last",
                Rest2sqlParamNames.PAGINATION_PARAM_LIMIT, 5
        );

        List<ABean> rslt = CrudReaderApi.loadPageExt(prms, null, null, context, sqlDefinition, null, ABean.class);

        Assert.assertTrue(rslt.size() > 0);
    }

    public static class TestROject {
        @Prop(name = "id")
        public Integer fid;
        @Prop(name = "name")
        public String fname;
    }

    @Test
    public void test67() throws Exception {

        SQLContext ctx = DbContextFactory.createHikariCP(
                BaseDataSourceProperties.builder()
                        .driverClassName(testDBDriverName)
                        .url(testDBUrl)
                        .username("GIVCADMIN")
                        .password("j12")
                        .build(),
                OracleContext.class);


        String rslt = ctx.execBatch((conn) -> {

            String sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("222.sql"));

            List<Param> params = new ArrayList<>();
            TestROject r =  ctx.createCursor()
                    .init(conn, sql, params).firstBean(ctx.getCurrentUser(), TestROject.class);
            return r.fname;
        }, null);
        Assert.assertEquals(rslt, "qwe");
    }


    @Ignore
    @Test
    public void testSQLCommandOpenCursor888() throws Exception {
        SQLContext context = DbContextFactory.createHikariCP(
                BaseDataSourceProperties.builder()
                        .driverClassName(testDBDriverName)
                        .url(testDBUrl)
                        .username("GIVCADMIN")
                        .password("j12")
                        .build(),
                OracleContext.class);

        final User usr = new User();
        usr.setOrgId("1009686");
        usr.setRoles("21");
        usr.setGrants("151,152");

        int rrr = context.execBatch((conn) -> {

            String sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("888.sql"));

            List<Param> params = new ArrayList<>();

/*
	   1-rlocale(in)(VARCHAR)..............................[null];
	   2-currid(in)(DECIMAL)...............................[null];
	   3-id(in)(VARCHAR)...................................[null];
	   4-datestart(in)(VARCHAR)............................"2019.07.07";
	   5-dateend(in)(VARCHAR).............................."2019.08.02";
	   6-p_sys_curusr_org_uid(in)(VARCHAR)................."1009686";
	   7-p_sys_curusr_roles(in)(VARCHAR)..................."21";
	   8-p_sys_curusr_grants(in)(VARCHAR).................."151,152";
	   9-query_value(in)(VARCHAR)..........................[null];
	  10-pagination_offset(in)(DECIMAL)....................[0];
	  11-pagination_limit(in)(DECIMAL).....................[50];
	 */

//            Paramus.setParamValue(params, "rlocale", null);
//            Paramus.setParamValue(params, "currid", null);
//            Paramus.setParamValue(params, "id", null);
            Paramus.setParamValue(params, "datestart", "2019.07.07");
            Paramus.setParamValue(params, "dateend", "2019.08.02");
            Paramus.setParamValue(params, "p_sys_curusr_org_uid", "1009686");
            Paramus.setParamValue(params, "p_sys_curusr_roles", "21");
            Paramus.setParamValue(params, "p_sys_curusr_grants", "151,152");
//            Paramus.setParamValue(params, "query_value", null);
            Paramus.setParamValue(params, "pagination_offset", 0);
            Paramus.setParamValue(params, "pagination_limit", 50);


            Var var = new Var();
            context.createCursor()
                    .init(conn, sql, params)
                    .fetch(context.getCurrentUser(), rs->{
                        var.iummy++;
                        return true;
                    });
            return var.iummy;

        }, null);
        Assert.assertNotEquals(rrr, 0);
    }

    @Test
    public void testExtractAppError() throws Exception {
        SQLException e = new SQLException("ORA-20001: Не верное имя или пароль пользователя!\n" +
                "ORA-06512: на  \"GIVCAPI.GACC\", line 316\n" +
                "ORA-06512: на  \"GIVCAPI.GACC\", line 331\n" +
                "ORA-06512: на  line 1");
        BioSQLApplicationError er = DbUtils.getInstance().extractStoredProcAppErrorMessage(e);
        Assert.assertTrue(er.getErrorCode() == 20001);

    }

    @Test
    public void testExtractAppError1() throws Exception {
        SQLException e = new SQLException("ORA-20401: \n" +
                "ORA-06512: на  \"GIVCADMIN.BIO_LOGIN3\", line 408\n" +
                "ORA-20001: Не верное имя или пароль пользователя!\n" +
                "ORA-06512: на  line 1");
        BioSQLApplicationError er = DbUtils.getInstance().extractStoredProcAppErrorMessage(e);
        Assert.assertTrue(er.getErrorCode() == 20401);
        Assert.assertTrue(er.getMessage().equals("Не верное имя или пароль пользователя!"));

    }

}
