package ru.bio4j.spring.database.pgsql;

import org.junit.*;
import ru.bio4j.spring.commons.types.LogWrapper;
import ru.bio4j.spring.commons.types.Paramus;
import ru.bio4j.spring.commons.utils.Utl;
import ru.bio4j.spring.database.api.*;
import ru.bio4j.spring.database.commons.DbContextFactory;
import ru.bio4j.spring.database.commons.DbUtils;
import ru.bio4j.spring.model.BaseDataSourceProperties;
import ru.bio4j.spring.model.transport.*;
import ru.bio4j.spring.model.transport.errors.BioSQLException;
import ru.bio4j.spring.model.transport.errors.ConvertValueException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@Ignore
public class SQLFactoryTest {
    private static final LogWrapper LOG = LogWrapper.getLogger(SQLFactoryTest.class);
    //    private static final String testDBDriverName = "oracle.jdbc.driver.OracleDriver";
//    private static final String testDBUrl = "jdbc:oracle:thin:@stat4-ora-dev:1521:MICEXDB";
    private static final String testDBDriverName = "org.postgresql.Driver";
    //    private static final String testDBUrl = "jdbc:postgresql://192.168.50.47:5432/postgres";
    private static final String testDBUrl = "jdbc:postgresql://localhost:5432/postgres";
//    private static final String testDBUrl = "jdbc:postgresql://10.10.0.221:5432/postgres";

    //    private static final String testDBUrl = "jdbc:oracle:thin:@cmon-ora-dev:1521:MICEXDB";
    //private static final String testDBUrl = "jdbc:oracle:oci:@GIVCDB_EKBS03";
    //private static final String testDBUrl = "jdbc:oracle:thin:@https://databasetrial0901-rugivcmkrftrial07058.db.em1.oraclecloudapps.com/apex:1521:databasetrial0901";
//    private static final String testDBUsr = "master";
//    private static final String testDBPwd = "sysdba";
    private static final String testDBUsr = "postgres";
    private static final String testDBPwd = "root";

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
                PgSQLContext.class);


        //if(true) return;
        try {
            context.execBatch((conn) -> {
                try (Statement cs = conn.createStatement()) {
                    String sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_test_table.sql"));
                    cs.execute(sql);
                    sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_simple.sql"));
                    cs.execute(sql);
                    sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_error.sql"));
                    cs.execute(sql);
                    sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_ret_cursor.sql"));
                    cs.execute(sql);
                    sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_with_inout.sql"));
                    cs.execute(sql);
                }
            }, null);
        } catch (BioSQLException ex) {
            LOG.error("Error!", ex);
        }
    }

    @AfterClass
    public static void finClass() throws Exception {
        //if(true) return;
        try {
            context.execBatch((conn) -> {
                Statement cs = conn.createStatement();
                cs.execute("drop function test_stored_prop(varchar, out integer)");
                cs.execute("drop function test_stored_error(varchar, out integer)");
                cs.execute("drop function test_stored_cursor(varchar, out refcursor)");
                cs.execute("drop function test_stored_inout(inout integer, varchar, integer, numeric)");
                cs.execute("drop table test_tbl");
                return null;
            }, null);
        } catch (BioSQLException ex) {
            LOG.error("Error!", ex);
        }
    }

    @Test
    public void testCreateSQLConnectionPool() throws Exception {
//        LOG.debug(Utl.buildBeanStateInfo(context.getStat(), null, null));
        context.execBatch(new SQLActionScalar0<Object>() {
            @Override
            public Object exec(Connection conn) {
                Assert.assertNotNull(conn);
                return null;
            }
        }, null);

    }

    @Test
    public void testSQLCommandOpenCursor() {
        try {
            double dummysum = context.execBatch((conn) -> {
                Var var = new Var();
                String sql = "select user as curuser, :dummy as dm, :dummy1 as dm1";
                List<Param> prms = Paramus.set(new ArrayList<Param>()).add("dummy", 101).pop();
                context.createCursor()
                        .init(conn, sql, null)
                        .fetch(prms, context.getCurrentUser(), rs -> {
                            var.dummy += rs.getValue("DM", Double.class);
                            return true;
                        });
                return var.dummy;
            }, null);
            LOG.debug("dummysum: " + dummysum);
            Assert.assertEquals(dummysum, 101.0, 0.0);
        } catch (Exception ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }

    }

    @Test
    public void testSQLCommandOpenCursor111() {
        try {
            double dummysum = context.execBatch((conn) -> {
                Var var = new Var();
                String sql = "select * from test_tbl where fld2 = :fld2";
                List<Param> prms = Paramus.set(new ArrayList<Param>()).add(
                        Param.builder()
                                .name("fld2")
                                .type(MetaType.INTEGER)
                                .value(null)
                                .build()
                ).pop();
                context.createCursor()
                        .init(conn, sql, null)
                        .fetch(prms, context.getCurrentUser(), rs -> {
                            var.dummy += rs.getValue("fld1", Double.class);
                            return true;
                        });
                return var.dummy;
            }, null);
            LOG.debug("dummysum: " + dummysum);
            Assert.assertEquals(dummysum, 0.0, 0.0);
        } catch (Exception ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }

    }

    @Ignore
    @Test
    public void testSQLCommandOpenCursor1() {
        try {
            byte[] schema = context.execBatch((conn) -> {
                Var var = new Var();
                String sql = "select * from table(givcapi.upld.get_schemas)";
                context.createCursor()
                        .init(conn, sql, null)
                        .fetch(context.getCurrentUser(), rs -> {
                            if (var.bummy == null)
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
            int leng = context.execBatch((conn) -> {
                int leng1 = 0;
                LOG.debug("conn: " + conn);

                SQLStoredProc cmd = context.createStoredProc();
                String storedProgName = "test_stored_prop";
                try (Paramus paramus = Paramus.set(new ArrayList<Param>())) {
                    paramus.add("p_param1", "FTW")
                            .add(Param.builder()
                                    .name("p_param2")
                                    .type(MetaType.INTEGER)
                                    .direction(Param.Direction.OUT)
                                    .build());
                    cmd.init(conn, storedProgName);
                    cmd.execSQL(paramus.get(), context.getCurrentUser());
                }
                try (Paramus paramus = Paramus.set(cmd.getParams())) {
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
            int leng = context.execBatch((conn) -> {
                int leng1 = 0;
                LOG.debug("conn: " + conn);

                SQLStoredProc cmd = context.createStoredProc();
                List<Param> prms;
                try (Paramus paramus = Paramus.set(new ArrayList<Param>())) {
                    paramus.add(Param.builder().name("p_param1").type(MetaType.INTEGER).value(null).build())
                            .add("p_param2", "QWE")
                            .add(Param.builder().name("p_param3").type(MetaType.INTEGER).value(1).build())
                            .add(Param.builder().name("p_param4").type(MetaType.DECIMAL).value(0).build());
                    prms = paramus.get();
                }
                cmd.init(conn, "test_stored_inout");
                cmd.execSQL(prms, context.getCurrentUser());
                leng1 = cmd.getParamValue("p_param1", Integer.class, null);
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
    public void testSQLCommandExecINOUTSQL11() throws Exception {
        try {
            int leng = context.execBatch((conn) -> {
                int leng1 = 0;
                LOG.debug("conn: " + conn);

                SQLStoredProc cmd = context.createStoredProc();
                List<Param> prms;
                try (Paramus paramus = Paramus.set(new ArrayList<Param>())) {
                    paramus.add(Param.builder().name("p_param1").value(new BigDecimal(123)).build())
                            .add("p_param2", "QWE")
                            .add(Param.builder().name("p_param3").value(1D).build())
                            .add(Param.builder().name("p_param4").value(5L).build());
                    prms = paramus.get();
                }
                cmd.init(conn, "test_stored_inout");
                cmd.execSQL(prms, context.getCurrentUser());
                leng1 = cmd.getParamValue("p_param1", Integer.class, null);
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
    public void testDetectParamsOfSP() throws Exception {
        try {
            long leng = context.execBatch((conn) -> {
                long leng1 = 0;
                LOG.debug("conn: " + conn);

                PgSQLUtilsImpl utl = new PgSQLUtilsImpl();
                StoredProgMetadata md = utl.detectStoredProcParamsAuto("test_stored_inout", conn, null);
                LOG.debug("md: " + md);
                leng1 = md.getParamDeclaration().size();
                return leng1;
            }, null);
            LOG.debug("leng: " + leng);
            Assert.assertEquals(leng, 4);
        } catch (BioSQLException ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }
    }

    private static class TestParamObj {
        public Long param1;
        public String param2;
        public Integer param3;
        public Double param4;
    }

    @Test
    public void testSQLCommandExecINOUTSQL1() throws Exception {
        try {
            long leng = context.execBatch((conn) -> {
                long leng1 = 0;
                LOG.debug("conn: " + conn);

                SQLStoredProc cmd = context.createStoredProc();

                TestParamObj prms = new TestParamObj() {{
                    param1 = null;
                    param2 = "QWE";
                    param3 = 1;
                    param4 = null;
                }};

                cmd.init(conn, "test_stored_inout");
                cmd.execSQL(prms, context.getCurrentUser());
                leng1 = cmd.getParamValue("p_param1", Long.class, null);
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
    public void testSQLCommandExecExtParam() throws Exception {
        try {
            int leng = context.execBatch((conn) -> {
                int leng1 = 0;
                LOG.debug("conn: " + conn);

                SQLStoredProc cmd = context.createStoredProc();
                String storedProgName = "test_stored_prop";
                List<Param> prms = new ArrayList<>();
                try (Paramus paramus = Paramus.set(prms)) {
                    paramus.add("param1", "FTW")
                            .add(Param.builder()
                                    .name("param2")
                                    .type(MetaType.INTEGER)
                                    .direction(Param.Direction.OUT)
                                    .build())
                            .add("param3", "ext");
                }
                cmd.init(conn, storedProgName);
                cmd.execSQL(prms, context.getCurrentUser());
                leng1 = Paramus.paramValue(cmd.getParams(), "param2", Integer.class, 0);
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
    public void testStoredprocMetadata() throws Exception {

        try {
            context.execBatch((SQLActionVoid1<String>) (conn, param) -> {
                StoredProgMetadata sp = DbUtils.getInstance().detectStoredProcParamsAuto("test_stored_error", conn, null);
            }, "AnContext", null);
        } catch (BioSQLException ex) {
            LOG.error("Error!", ex);
            Assert.assertEquals(ex.getErrorCode(), 20000);
        }

    }

    @Test
    public void testSQLCommandExecError() throws Exception {
        try {
            context.execBatch((SQLActionVoid1<String>) (conn, param) -> {
                LOG.debug("conn: " + conn + "; param: " + param);

                SQLStoredProc cmd = context.createStoredProc();
                String storedProgName = "test_stored_error";
                try (Paramus paramus = Paramus.set(new ArrayList<Param>())) {
                    paramus.add("p_param1", "FTW")
                            .add(Param.builder()
                                    .name("p_param2")
                                    .type(MetaType.INTEGER)
                                    .direction(Param.Direction.OUT)
                                    .build());
                    cmd.init(conn, storedProgName);
                    cmd.execSQL(paramus.get(), context.getCurrentUser());
                }
            }, "AnContext", null);
        } catch (BioSQLException ex) {
            LOG.error("Error!", ex);
            Boolean errMsgOk = ex.getCause().getMessage().indexOf(": FTW") >= 0;
            Assert.assertTrue(errMsgOk);
        }
    }

    @Test
    public void testSQLCommandExecSQLAutoCommit() throws Exception {
        try {
            int leng = context.execBatch((conn) -> {
                int leng1 = 0;
                LOG.debug("conn: " + conn);

                SQLStoredProc cmd = context.createStoredProc();
                String storedProgName = "test_stored_prop";
                List<Param> prms = new ArrayList<>();
                try (Paramus paramus = Paramus.set(prms)) {
                    paramus.add("param1", "FTW")
                            .add(Param.builder()
                                    .name("param2")
                                    .type(MetaType.INTEGER)
                                    .direction(Param.Direction.OUT)
                                    .build());
                }
                cmd.init(conn, storedProgName).execSQL(prms, context.getCurrentUser());
                try (Paramus paramus = Paramus.set(cmd.getParams())) {
                    leng1 = Utl.nvl(paramus.getParamValue("param2", Integer.class), 0);
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
            int role = -1;
            int org_id = -1;
            Paramus paramus = Paramus.set(new ArrayList<Param>());
            paramus.add("p_user_name", "coordinator")
                    .add("p_password", "siolopon")
                    .add(Param.builder()./*owner(paramus.get()).*/name("v_role_id").type(MetaType.INTEGER).direction(Param.Direction.OUT).build())
                    .add(Param.builder()./*owner(paramus.get()).*/name("v_org_id").type(MetaType.INTEGER).direction(Param.Direction.OUT).build());
            List<Param> params = paramus.pop();
            context.execBatch((conn, param) -> {
                SQLStoredProc prc = context.createStoredProc();
                prc.init(conn, "gacc.check_login", param).execSQL(context.getCurrentUser());
                return null;
            }, params, null);
            role = getParamValue(params, int.class, "v_role_id");
            org_id = getParamValue(params, int.class, "v_org_id");
            LOG.debug(String.format("Login: OK; role: %d; org_id: %d", role, org_id));
            Assert.assertEquals(role, 6);
        } catch (SQLException ex) {
            LOG.error("Error!!!", ex);
        }
    }

    @Test
    public void testSQLCommandStoredProcRetCursor() throws Exception {
        try {
            int c = context.execBatch((conn) -> {
                ResultSet resultSet = null;
                LOG.debug("conn: " + conn);

                SQLStoredProc cmd = context.createStoredProc();
                String storedProgName = "test_stored_cursor";
                try (Paramus paramus = Paramus.set(new ArrayList<Param>())) {
                    paramus.add("p_param1", "FTW")
                            .add(Param.builder()
                                    .name("p_param2")
                                    .type(MetaType.CURSOR)
                                    .direction(Param.Direction.OUT)
                                    .build());
                    cmd.init(conn, storedProgName).execSQL(paramus.get(), context.getCurrentUser());
                }
                try (Paramus paramus = Paramus.set(cmd.getParams())) {
                    resultSet = paramus.getParamValue("p_param2", ResultSet.class);
                    if (resultSet.next()) {
                        String userName = resultSet.getString("ROLNAME");
                        LOG.debug("userName: " + userName);
                        Assert.assertTrue(Arrays.asList("PG_SIGNAL_BACKEND", "PG_MONITOR").contains(userName.toUpperCase()));
                    }
                }
                return 0;
            }, null);
        } catch (Exception ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }
    }

    @Test
    public void sqlExceptionExtTest() {
        List<Param> params = new ArrayList<>();
        params.add(Param.builder()
                .name("qwe")
                .value(123)
                .build()
        );
        StringBuilder sb = new StringBuilder();
        sb.append("{Command.Params(before exec): {\n");
        for (Param p : params)
            sb.append("\t" + p.toString() + ",\n");
        sb.append("}}");

        SQLException e = new SQLException("QWE-TEST");
        BioSQLException r = BioSQLException.create(String.format("%s:\n - sql: %s;\n - %s", "Error on execute command.", "select * from dual", sb.toString()), e);
        String msg = r.getMessage();
        LOG.debug(msg);
    }

    @Test
    public void testSQLCommandOpenCursorNewSbkItem() throws Exception {
        final String sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("new-sbkitem.sql"));
        try {
            Integer dummy = context.execBatch((conn) -> {
                context.createCursor()
                        .init(conn, sql, null)
                        .fetch(context.getCurrentUser(), rs -> {
                            return false;
                        });
                return 0;
            }, null);
            LOG.debug("dummy: " + dummy);
            Assert.assertEquals(dummy, new Integer(0));
        } catch (Exception ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }

    }

}
