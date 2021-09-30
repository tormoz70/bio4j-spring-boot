package ru.bio4j.spring.helpers.dba;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.bio4j.spring.commons.types.Paramus;
import ru.bio4j.spring.commons.utils.Utl;
import ru.bio4j.spring.database.api.SQLStoredProc;
import ru.bio4j.spring.model.transport.ABean;
import ru.bio4j.spring.model.transport.MetaType;
import ru.bio4j.spring.model.transport.Param;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DbaTestAutoConfiguration.class)
@TestPropertySource(locations="classpath:test.properties")
public class DbaTest {

    @Autowired
    @Qualifier("clickhouseDbaHelper")
    private DbaHelper chHelper;

    @Autowired
    @Qualifier("pgsqlDbaHelper")
    private DbaHelper pgsqlHelper;

    @Autowired
    @Qualifier("oracleDbaHelper")
    private DbaHelper oracleHelper;

    @Test
    public void doTest1() {
        //DbaAdapter dbaAdapter = (DbaAdapter)applicationContext.getBean("dbaAdapter");
        ABean d1 = chHelper.loadFirstBean("rcard", (List<Param>) null, null, ABean.class);
        Assert.assertTrue(d1 != null);
        ABean d2 = pgsqlHelper.loadFirstBean("pgtest", (List<Param>) null, null, ABean.class);
        Assert.assertTrue(d2 != null);
    }

    @Test
    @Ignore
    public void doTest2() {
        //DbaAdapter dbaAdapter = (DbaAdapter)applicationContext.getBean("dbaAdapter");
        List<Param> params = Paramus.createParams(
            "sortord", "sum",
            "rlocale", "ru",
            "currId", 0,
            "userOrgId", "244",
            "userRole", "6",
            "nationId", 0
        );
        ABean d = chHelper.loadFirstBean("bios.curTopAll", params, null, ABean.class);
        Assert.assertTrue(d != null);
    }

    @Test
    public void doTestStoreClob() {
        String xml = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("qqq.xml"));
        List<Param> prms = new ArrayList<>();
        Paramus.setParam(prms, Param.builder().name("p_param1").type(MetaType.STRING).value("ASD").build(), true);
        Paramus.setParam(prms, Param.builder().name("p_param2").type(MetaType.INTEGER).value(0).build(), true);
        Paramus.setParam(prms, Param.builder().name("p_param3").type(MetaType.CLOB).value(xml).build(), true);

        oracleHelper.exec("bios.store_clob", prms, null);
        prms.clear();
        Paramus.setParam(prms, Param.builder().name("p1").type(MetaType.INTEGER).value(0).build(), true);

        ABean d = oracleHelper.loadFirstBean("bios.read_clob", prms, null, ABean.class);
        Assert.assertTrue(d != null);
        Assert.assertEquals(xml, d.get("fld3"));
    }
}
