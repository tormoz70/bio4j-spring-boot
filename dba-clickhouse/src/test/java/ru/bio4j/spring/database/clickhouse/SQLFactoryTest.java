package ru.bio4j.spring.database.clickhouse;

import org.junit.*;
import ru.bio4j.spring.commons.converter.DateTimeParser;
import ru.bio4j.spring.commons.types.LogWrapper;
import ru.bio4j.spring.commons.types.Paramus;
import ru.bio4j.spring.commons.utils.ABeans;
import ru.bio4j.spring.commons.utils.Utl;
import ru.bio4j.spring.database.api.SQLContext;
import ru.bio4j.spring.database.api.SQLDefinition;
import ru.bio4j.spring.database.commons.CrudReaderApi;
import ru.bio4j.spring.database.commons.CursorParser;
import ru.bio4j.spring.database.commons.DbContextFactory;
import ru.bio4j.spring.model.config.props.DataSourceProperties;
import ru.bio4j.spring.model.transport.*;
import ru.bio4j.spring.model.transport.jstore.Sort;
import ru.bio4j.spring.model.transport.jstore.Total;
import ru.bio4j.spring.model.transport.jstore.filter.Filter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Ignore
public class SQLFactoryTest {

//    адрес 192.168.70.101. порт tcp - 9000, http 8123
//    логин: default/j12
//    таблица jdbc_test.data0

    private static final LogWrapper LOG = LogWrapper.getLogger(SQLFactoryTest.class);
//    private static final String testDBDriverName = "cc.blynk.clickhouse.ClickHouseDriver";
    private static final String testDBDriverName = "com.github.housepower.jdbc.ClickHouseDriver";
//    private static final String testDBDriverName = "ru.yandex.clickhouse.ClickHouseDriver";
//    private static final String testDBUrl = "jdbc:clickhouse://192.168.70.101:8123/default";
    private static final String testDBUrl = "jdbc:clickhouse://192.168.70.101:9000";
//    private static final String testDBUrl = "jdbc:clickhouse://192.168.70.101:8123";
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
                ClickhouseContext.class);
    }

    @AfterClass
    public static void finClass() throws Exception {
    }

    @Test
    public void testCreateSQLConnectionPool() throws Exception {
//        LOG.debug(Utl.buildBeanStateInfo(context.getStat(), null, null));
        context.execBatch(conn -> {
            Assert.assertNotNull(conn);
            return null;
        }, null);

    }

    @Test
    public void testSQLCommandOpenCursor0() {
        try {
//            Connection conn = DriverManager.getConnection("jdbc:clickhouse://192.168.70.101:8123/default", "default", "j12");
            Connection conn = DriverManager.getConnection("jdbc:clickhouse://192.168.70.101:9000", "default", "j12");
            PreparedStatement stmt = conn.prepareStatement("select ? as periodStart\n" +
                    "      ,toDate(?) as periodStart1\n" +
                    "      ,? as periodEnd\n" +
                    "      ,toDateTime(?) as periodEnd1\n" +
                    "      ,now() as emptyDate");
            java.sql.Date startDate = new java.sql.Date(DateTimeParser.getInstance().pars("2009-01-01").getTime());
            java.sql.Timestamp endDate = new java.sql.Timestamp(DateTimeParser.getInstance().pars("2009-01-02T12:00:00").getTime());
            stmt.setObject(1, startDate);
            stmt.setObject(2, startDate);
            stmt.setObject(3, endDate);
            stmt.setObject(4, endDate);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Object periodStart = rs.getObject("periodStart");
                Object periodEnd = rs.getObject("periodEnd");
                Object periodStart1 = rs.getObject("periodStart1");
                Object periodEnd1 = rs.getObject("periodEnd1");
                Object emptyDate = rs.getObject("emptyDate");
                System.out.println(
                        periodStart + "(" + periodStart.getClass() + ")\t" +
                                periodStart1 + "(" + periodStart1.getClass() + ")\t" +
                                periodEnd + "(" + periodEnd.getClass() + ")\t" +
                                periodEnd1 + "(" + periodEnd1.getClass() + ")\t" +
                                emptyDate + "(" + emptyDate.getClass() + ")\t");
            }

        } catch (Exception ex) {
            System.out.println(ex);
            Assert.fail();
        }

    }

    @Test
    public void testSQLCommandOpenCursor() {
        try {
            Double dummysum = context.execBatch(conn -> {
                Var var = new Var();
                String sql = "select a.*, :dummy as dm from jdbc_test.data0 as a";
                List<Param> prms = Paramus.set(new ArrayList<Param>()).add("dummy", 1).pop();
                context.createCursor()
                        .init(conn, sql, null)
                        .fetch(prms, context.getCurrentUser(), rs->{
                            var.dummy += rs.getValue("DM", Double.class);
                            return true;
                        });
                return var.dummy;
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
        String bioPath = Utl.extractBioParentPath("bios.data0");
        Assert.assertEquals("bios", bioPath);
    }

    @Test
    public void testSQLCommandOpenCursor2() {
        try {
            SQLDefinition sqlDefinition = CursorParser.pars("bios.data0");
            BeansPage<ABean> rst = CrudReaderApi.loadPage(null, null, null, null, context, sqlDefinition, null,
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
    public void testSQLCommandOpenCursor22() {
        try {
            SQLDefinition sqlDefinition = CursorParser.pars("bios.data0");
            List<Total> totals = new ArrayList<>();
            totals.add(Total.builder().fieldName("*").aggrigate(Total.Aggregate.COUNT).fieldType(long.class).build());
            totals.add(Total.builder().fieldName("1").aggrigate(Total.Aggregate.COUNT).fieldType(long.class).build());
            totals.add(Total.builder().fieldName("summ").aggrigate(Total.Aggregate.SUM).fieldType(double.class).build());
            totals.add(Total.builder().fieldName("summ").aggrigate(Total.Aggregate.MIN).fieldType(double.class).build());
            totals.add(Total.builder().fieldName("summ").aggrigate(Total.Aggregate.MAX).fieldType(double.class).build());
            BeansPage<ABean> rst = CrudReaderApi.loadPage(null, null, null, totals, context, sqlDefinition, null,
                    CrudOptions.builder()
                            .forceCalcCount(false)
                            .recordsLimit(10000)
                            .appendMetadata(true).build(),
                    ABean.class);
            Assert.assertEquals(100, rst.getTotalCount());
            Assert.assertEquals(50, rst.getPaginationCount());
            Assert.assertEquals(1, rst.getPaginationPage());
            Assert.assertEquals(0, rst.getPaginationOffset());
            Assert.assertEquals(50, rst.getPaginationPageSize());
            Assert.assertEquals(100L, rst.getTotals().stream().filter(f -> f.getAggregate() == Total.Aggregate.COUNT).findFirst().get().getFact());
            Assert.assertEquals(774097D, rst.getTotals().stream().filter(f -> f.getFieldName().equals("summ")).findFirst().get().getFact());
        } catch (Exception ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }

    }

    @Test
    public void testSQLCommandOpenCursor23() {
        try {
            SQLDefinition sqlDefinition = CursorParser.pars("bios.data0");
            List<Total> totals = new ArrayList<>();
            totals.add(Total.builder().fieldName("*").aggrigate(Total.Aggregate.COUNT).fieldType(long.class).build());
            totals.add(Total.builder().fieldName("1").aggrigate(Total.Aggregate.COUNT).fieldType(long.class).build());
            totals.add(Total.builder().fieldName("summ").aggrigate(Total.Aggregate.SUM).fieldType(double.class).build());
            totals.add(Total.builder().fieldName("summ").aggrigate(Total.Aggregate.MIN).fieldType(double.class).build());
            totals.add(Total.builder().fieldName("summ").aggrigate(Total.Aggregate.MAX).fieldType(double.class).build());
            BeansPage<ABean> rst = CrudReaderApi.loadAll(null, null, null, totals, context, sqlDefinition, null, ABean.class);
            Assert.assertEquals(100L, rst.getTotalCount());
            Assert.assertEquals(100L, rst.getPaginationCount());
            Assert.assertEquals(0, rst.getPaginationPage());
            Assert.assertEquals(0, rst.getPaginationOffset());
            Assert.assertEquals(0, rst.getPaginationPageSize());
            Assert.assertEquals(100L, rst.getTotals().stream().filter(f -> f.getAggregate() == Total.Aggregate.COUNT).findFirst().get().getFact());
            Assert.assertEquals(774097D, rst.getTotals().stream().filter(f -> f.getFieldName().equals("summ")).findFirst().get().getFact());
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
            sqlDefinition = CursorParser.pars("bios.data0");
            List<Sort> sorts = new ArrayList<>();
            sorts.add(Sort.builder()
                    .fieldName("summ")
                    .direction(Sort.Direction.DESC)
                    .build());
            Filter filter = Utl.restoreSimpleFilter("{region_id:\"0100000000000 \"}");
            BeansPage<ABean> rst = CrudReaderApi.loadPage(null, null, sorts, null, context, sqlDefinition, null,
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

    @Ignore
    @Test
    public void testSQLCommandOpenCursor4() {
        try {
            SQLDefinition sqlDefinition = CursorParser.pars("bios.testDT");
            List<Param> params = Paramus.createParams();
            Paramus.setParamValue(params, "periodStart", DateTimeParser.getInstance().pars("2009-01-01"));
            Paramus.setParamValue(params, "periodEnd", DateTimeParser.getInstance().pars("2009-01-02"));
            BeansPage<ABean> rst = CrudReaderApi.loadPage(params, null, null, null, context, sqlDefinition, null,
                    CrudOptions.builder()
                            .forceCalcCount(true)
                            .recordsLimit(10000)
                            .appendMetadata(true).build(),
                    ABean.class);
            Assert.assertEquals(Paramus.paramValue(params, "periodStart"), ABeans.extractAttrFromBean(rst.getRows().get(0), "periodStart", Object.class, null));
            Assert.assertEquals(Paramus.paramValue(params, "periodEnd"), ABeans.extractAttrFromBean(rst.getRows().get(0), "periodEnd", Object.class, null));
        } catch (Exception ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }

    }

}
