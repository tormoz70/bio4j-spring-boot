package ru.bio4j.spring.database.oracle;

import junit.framework.TestCase;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.spring.database.api.SQLContext;
import ru.bio4j.spring.database.commons.DbContextFactory;
import ru.bio4j.spring.model.config.props.DataSourceProperties;

public class SQLFactoryTest1 extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(SQLFactoryTest1.class);
    private static final String testDBDriverName = "oracle.jdbc.driver.OracleDriver";
    private static final String testDBUrl = "jdbc:oracle:thin:@192.168.70.30:1521:EKBS02";
//    private static final String testDBUrl = "jdbc:oracle:thin:@stat4-ora-dev:1521:MICEXDB";
//    private static final String testDBUrl = "jdbc:oracle:thin:@cmon-ora-dev:1521:MICEXDB";
//    private static final String testDBUrl = "jdbc:oracle:oci:@GIVCDB_EKBS03";
//    private static final String testDBUrl = "jdbc:oracle:thin:@https://databasetrial0901-rugivcmkrftrial07058.db.em1.oraclecloudapps.com/apex:1521:databasetrial0901";
    private static final String testDBUsr = "SCOTT";
    private static final String testDBPwd = "tiger";

    private static SQLContext context;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        context = DbContextFactory.createHikariCP(
            DataSourceProperties.builder()
                .driverClassName(testDBDriverName)
                .url(testDBUrl)
                .username(testDBUsr)
                .password(testDBPwd)
                .build(),
            OracleContext.class);
    }

    @Ignore
    @AfterClass
    public static void finClass() throws Exception {
    }

    @Test
    public void testCreateSQLConnectionPool() throws Exception {
        context.execBatch(conn -> {
            Assert.assertNotNull(context.currentConnection());
            return null;
        }, null);

    }

}
