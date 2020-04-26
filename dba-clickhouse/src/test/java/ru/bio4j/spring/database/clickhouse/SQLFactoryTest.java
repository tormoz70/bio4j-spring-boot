package ru.bio4j.spring.database.clickhouse;

import org.junit.*;
import ru.bio4j.spring.commons.utils.ABeans;
import ru.bio4j.spring.database.commons.CrudReaderApi;
import ru.bio4j.spring.database.commons.CursorParser;
import ru.bio4j.spring.commons.types.Paramus;
import ru.bio4j.spring.commons.utils.Utl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.spring.database.api.*;
import ru.bio4j.spring.database.commons.DbContextFactory;
import ru.bio4j.spring.model.transport.*;
import ru.bio4j.spring.model.transport.jstore.Sort;
import ru.bio4j.spring.model.transport.jstore.filter.Filter;

import java.util.*;

public class SQLFactoryTest {

//    адрес 192.168.70.101. порт tcp - 9000, http 8123
//    логин: default/j12
//    таблица jdbc_test.data0

    private static final Logger LOG = LoggerFactory.getLogger(SQLFactoryTest.class);
    private static final String testDBDriverName = "com.github.housepower.jdbc.ClickHouseDriver";
//    private static final String testDBDriverName = "ru.yandex.clickhouse.ClickHouseDriver";
//    private static final String testDBUrl = "jdbc:clickhouse://192.168.70.101:8123/default";
    private static final String testDBUrl = "jdbc:clickhouse://192.168.70.101:9000";
//    private static final String testDBUrl = "jdbc:oracle:thin:@stat4-ora-dev:1521:MICEXDB";
//    private static final String testDBUrl = "jdbc:oracle:thin:@cmon-ora-dev:1521:MICEXDB";
    //private static final String testDBUrl = "jdbc:oracle:oci:@GIVCDB_EKBS03";
    //private static final String testDBUrl = "jdbc:oracle:thin:@https://databasetrial0901-rugivcmkrftrial07058.db.em1.oraclecloudapps.com/apex:1521:databasetrial0901";
    private static final String testDBUsr = "default";
    private static final String testDBPwd = "j12";

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
                DataSourceProperties.builder()
                        .driverClassName(testDBDriverName)
                        .url(testDBUrl)
                        .username(testDBUsr)
                        .password(testDBPwd)
                        .build(),
                ChContext.class);
    }

    @AfterClass
    public static void finClass() throws Exception {
    }

    @Test
    public void testCreateSQLConnectionPool() throws Exception {
//        LOG.debug(Utl.buildBeanStateInfo(context.getStat(), null, null));
        context.execBatch(new SQLActionScalar0<Object>() {
            @Override
            public Object exec(SQLContext context) {
                Assert.assertNotNull(context.getCurrentConnection());
                return null;
            }
        }, null);

    }

    @Test
    public void testSQLCommandOpenCursor() {
        try {
            Double dummysum = context.execBatch(new SQLActionScalar0<Double>() {
                @Override
                public Double exec(SQLContext context) {
                    Var var = new Var();
                    String sql = "select a.*, :dummy as dm from jdbc_test.data0 as a";
                    List<Param> prms = Paramus.set(new ArrayList<Param>()).add("dummy", 1).pop();
                    context.createCursor()
                            .init(context.getCurrentConnection(), sql, null)
                            .fetch(prms, context.getCurrentUser(), rs->{
                                var.dummy += rs.getValue("DM", Double.class);
                                return true;
                            });
                    return var.dummy;
                }
            }, null);
            LOG.debug("dummysum: " + dummysum);
            Assert.assertEquals(dummysum, 100.0, 0);
        } catch (Exception ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }

    }

    @Test
    public void testSQLCommandOpenCursor1() {
        String bioPath = Utl.extractBioParentPath("data0");
        Assert.assertEquals("", bioPath);
    }

    @Test
    public void testSQLCommandOpenCursor2() {
        try {
            SQLDefinition sqlDefinition = CursorParser.pars("data0");
            BeansPage<ABean> rst = CrudReaderApi.loadPage(null, null, null, context, sqlDefinition, null,
                    CrudOptions.builder()
                        .forceCalcCount(true)
                        .recordsLimit(10000)
                        .appendMetadata(true).build(),
            ABean.class);
            Assert.assertEquals(100, rst.getTotalCount());
            Assert.assertEquals(50, rst.getPaginationCount());
            Assert.assertEquals(1, rst.getPaginationPage());
            Assert.assertEquals(0, rst.getPaginationOffset());
            Assert.assertEquals(50, rst.getPaginationPageSize());
        } catch (Exception ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }

    }

    @Test
    public void testSQLCommandOpenCursor3() {
        try {
            SQLDefinition sqlDefinition = CursorParser.pars("data0minmax");
            ABean rstMinMax = CrudReaderApi.loadFirstRecordExt(context, sqlDefinition, null, ABean.class);
            sqlDefinition = CursorParser.pars("data0");
            List<Sort> sorts = new ArrayList<>();
            sorts.add(Sort.builder()
                    .fieldName("summ")
                    .direction(Sort.Direction.DESC)
                    .build());
            Filter filter = Utl.restoreSimpleFilter("{region_id:\"0100000000000 \"}");
            BeansPage<ABean> rst = CrudReaderApi.loadPage(null, null, sorts, context, sqlDefinition, null,
                    CrudOptions.builder()
                            .forceCalcCount(true)
                            .recordsLimit(10000)
                            .appendMetadata(true).build(),
                    ABean.class);
            Assert.assertEquals(100, rst.getTotalCount());
            Assert.assertEquals(50, rst.getPaginationCount());
            Assert.assertEquals(1, rst.getPaginationPage());
            Assert.assertEquals(0, rst.getPaginationOffset());
            Assert.assertEquals(50, rst.getPaginationPageSize());
            Assert.assertEquals(ABeans.extractAttrFromBean(rst.getRows().get(0), "summ", Double.class, 0.0), ABeans.extractAttrFromBean(rstMinMax, "summ_max", Double.class, 0.0));
        } catch (Exception ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }

    }

}
