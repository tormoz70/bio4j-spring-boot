package ru.bio4j.spring.database.pgsql;

import org.junit.*;
import ru.bio4j.spring.database.api.SQLContext;
import ru.bio4j.spring.database.commons.DbContextFactory;
import ru.bio4j.spring.model.config.props.DataSourceProperties;
import ru.bio4j.spring.model.transport.MetaType;
import ru.bio4j.spring.model.transport.Param;

import java.util.ArrayList;
import java.util.List;

//@Ignore
public class SQLFactoryTest1 {
    private static final String testDBDriverName = "org.postgresql.Driver";
//    private static final String testDBUrl = "jdbc:postgresql://192.168.50.47:5432/postgres";
    private static final String testDBUrl = "jdbc:postgresql://localhost:5432/postgres";
//    private static final String testDBUrl = "jdbc:oracle:thin:@cmon-ora-dev:1521:MICEXDB";
//    private static final String testDBUrl = "jdbc:postgresql://10.10.0.221:5432/postgres";
    //private static final String testDBUrl = "jdbc:oracle:oci:@GIVCDB_EKBS03";
    //private static final String testDBUrl = "jdbc:oracle:thin:@https://databasetrial0901-rugivcmkrftrial07058.db.em1.oraclecloudapps.com/apex:1521:databasetrial0901";
//    private static final String testDBUsr = "SCOTT";
//    private static final String testDBPwd = "tiger";
    private static final String testDBUsr = "postgres";
    private static final String testDBPwd = "root";

    private static SQLContext context;

    @BeforeClass
    public static void setUpClass() throws Exception {
        context = DbContextFactory.createHikariCP(
                DataSourceProperties.builder()
                        .driverClassName(testDBDriverName)
                        .url(testDBUrl)
                        .username(testDBUsr)
                        .password(testDBPwd)
                        .build(),
                PgSQLContext.class);
    }

    @AfterClass
    public static void finClass() throws Exception {
    }

    @Test
    public void testCreateSQLConnectionPool() throws Exception {
        context.execBatch((conn) -> {
            Assert.assertNotNull(conn);
            return null;
        }, null);

    }

    @Test
    public void testCutDirName() throws Exception {
        String s = PgSQLUtilsImpl.cutDirNames("OUT p_param2 integer");
        Assert.assertEquals(s, "p_param2 integer");
    }

    @Test
    public void testCutDirName1() throws Exception {
        String s = PgSQLUtilsImpl.cutDirNames("p_param2 integer");
        Assert.assertEquals(s, "p_param2 integer");
    }

    @Test
    public void extractDirName() throws Exception {
        String s = PgSQLUtilsImpl.extractDirName("OUT p_param2 integer");
        Assert.assertEquals(s, "OUT");
    }

    @Test
    public void extractDirName1() throws Exception {
        String s = PgSQLUtilsImpl.extractDirName("p_param2 integer");
        Assert.assertEquals(s, "IN");
    }

    @Test
    public void testParsParams() throws Exception {
        StringBuilder args = new StringBuilder();
        String paramsList = "p_param1 character varying, OUT p_param2 integer";
        List<Param> params = new ArrayList<>();
        PgSQLUtilsImpl.parsParams(paramsList, params, null);
        Assert.assertEquals(params.get(0).getDirection(), Param.Direction.IN);
        Assert.assertEquals(params.get(0).getType(), MetaType.STRING);
        Assert.assertEquals(params.get(1).getDirection(), Param.Direction.OUT);
        Assert.assertEquals(params.get(1).getType(), MetaType.INTEGER);
    }

    @Test
    public void testParsParams1() throws Exception {
        StringBuilder args = new StringBuilder();
        String paramsList = "p_param1 character varying, OUT p_param2 integer";
        List<Param> pps = new ArrayList<>();
        pps.add(Param.builder().name("param1").value("qwe").type(MetaType.STRING).direction(Param.Direction.INOUT).override(true).build());
        pps.add(Param.builder().name("param2").value(0).type(MetaType.INTEGER).override(true).build());
        List<Param> params = new ArrayList<>();
        PgSQLUtilsImpl.parsParams(paramsList, params, pps);
        Assert.assertEquals(params.get(0).getDirection(), Param.Direction.IN);
        Assert.assertEquals(params.get(0).getType(), MetaType.STRING);
        Assert.assertEquals(params.get(1).getDirection(), Param.Direction.OUT);
        Assert.assertEquals(params.get(1).getType(), MetaType.INTEGER);
    }
}
