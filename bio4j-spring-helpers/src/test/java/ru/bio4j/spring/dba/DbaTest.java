package ru.bio4j.spring.dba;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import ru.bio4j.spring.database.api.SQLContext;
import ru.bio4j.spring.model.transport.ABean;
import ru.bio4j.spring.model.transport.Param;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DbaAutoConfiguration.class)
@TestPropertySource(locations="classpath:test.properties")
public class DbaTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DbaAdapter dbaAdapter;

    @Test
    public void doTest() {
        //DbaAdapter dbaAdapter = (DbaAdapter)applicationContext.getBean("dbaAdapter");
        ABean d = dbaAdapter.loadFirstBean("rcard", (List<Param>) null, null, ABean.class);
        Assert.assertTrue(d!= null);
    }
}
