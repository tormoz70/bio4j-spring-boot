package ru.bio4j.spring.database.oracle;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestApiAdapterTest {
    private static final Logger LOG = LoggerFactory.getLogger(RestApiAdapterTest.class);
    private static final String testDBDriverName = "oracle.jdbc.driver.OracleDriver";
    private static final String testDBUrl = "jdbc:oracle:thin:@192.168.70.30:1521:EKBS02";
//    private static final String testDBUrl = "jdbc:oracle:thin:@stat4-ora-dev:1521:MICEXDB";
//    private static final String testDBUrl = "jdbc:oracle:thin:@cmon-ora-dev:1521:MICEXDB";
    //private static final String testDBUrl = "jdbc:oracle:oci:@GIVCDB_EKBS03";
    //private static final String testDBUrl = "jdbc:oracle:thin:@https://databasetrial0901-rugivcmkrftrial07058.db.em1.oraclecloudapps.com/apex:1521:databasetrial0901";
    private static final String testDBUsr = "SCOTT";
    private static final String testDBPwd = "tiger";


    @Test
    public void testExport() throws Exception {

//        SQLContext ctx = TestContextFactory.createHikariCP(
//                SQLConnectionPoolConfig.builder()
//                        .poolName("TEST-CONN-POOL-123")
//                        .dbDriverName(testDBDriverName)
//                        .dbConnectionUrl(testDBUrl)
//                        .dbConnectionUsr("GIVCADMIN")
//                        .dbConnectionPwd("j12")
//                        .build(),
//                OraContext.class);
//
//
//        ctx.execBatch((context) -> {
//
//            SQLDefinition cursor = CursorParser.pars(Thread.currentThread().getContextClassLoader().getResourceAsStream("films.xml"), "films");
//            HSSFWorkbook wb = CrudReaderApi.toExcel(null, null, null, context, cursor);
//            try {
//                FileOutputStream out = new FileOutputStream("d:\\test.xls");
//                wb.write(out);
//            } catch(IOException e) {
//                throw Utl.wrapErrorAsRuntimeException(e);
//            }
//        }, null);
//        Assert.assertEquals("qwe", "qwe");
    }


}
