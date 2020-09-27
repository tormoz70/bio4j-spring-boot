package ru.bio4j.spring.database.h2;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.bio4j.spring.commons.types.LogWrapper;
import ru.bio4j.spring.commons.types.Paramus;
import ru.bio4j.spring.commons.utils.Utl;
import ru.bio4j.spring.database.api.DbServer;
import ru.bio4j.spring.database.api.SQLContext;
import ru.bio4j.spring.database.commons.DbContextFactory;
import ru.bio4j.spring.database.commons.DbUtils;
import ru.bio4j.spring.model.DataSourceProperties;
import ru.bio4j.spring.model.transport.Param;

import java.util.ArrayList;
import java.util.List;

public class H2ApiTest {
    private static final LogWrapper LOG = LogWrapper.getLogger(H2ApiTest.class);

    private static final String testDBDriverName = "org.h2.Driver";

    private static DbServer dbServer = new H2ServerImpl("9990");
    private static final String testDBUrl = "jdbc:h2:d:/tmp/h2test/test";
    private static final String testDBUsr = "sa";
    private static final String testDBPwd = "";

    private static SQLContext context;

    @BeforeClass
    public static void initTests() {
        dbServer.startServer();
        context = DbContextFactory.createHikariCP(
                DataSourceProperties.builder()
                        .driverClassName(testDBDriverName)
                        .url(testDBUrl)
                        .username(testDBUsr)
                        .password(testDBPwd)
                        .build(),
                H2Context.class);

        context.execBatch((conn) -> {
            String sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_test_table.sql"));
            DbUtils.execSQL(conn, sql);
        }, null);

    }

    @AfterClass
    public static void deinitTests() {
        dbServer.shutdownServer();
    }

    @Test
    public void conn5() {
        context.execBatch((conn) -> {
            Assert.assertNotNull(conn);
        }, null);
    }

    @Test
    public void selectTest() {
        long dummysum = context.execBatch(conn -> {
            String sql = "select sum(id) from test";
            List<Param> prms = Paramus.set(new ArrayList<Param>()).add("dummy", 101).pop();
            return context.createCursor()
                    .init(conn, sql, null)
                    .scalar(prms, context.getCurrentUser(), long.class, -1L);
        }, null);
        LOG.debug("dummysum: " + dummysum);
        Assert.assertEquals(dummysum, 3, 0);
    }
}
