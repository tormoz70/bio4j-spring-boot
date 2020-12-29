package ru.bio4j.spring.helpers.dba;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.bio4j.spring.commons.types.Paramus;
import ru.bio4j.spring.model.transport.ABean;
import ru.bio4j.spring.model.transport.BeansPage;
import ru.bio4j.spring.model.transport.Param;
import ru.bio4j.spring.model.transport.Rest2sqlParamNames;
import ru.bio4j.spring.model.transport.jstore.Total;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=OracleTestAutoConfiguration.class)
@TestPropertySource(locations="classpath:test.properties")
public class CalcTotalsTest {

    @Autowired
    @Qualifier("oracleDbaHelper")
    private DbaHelper oraHelper;

    @BeforeClass
    public static void setUp() {

    }

    @Test
    public void testCalcTotals() {
        List<Param> params = Paramus.createParams(
                Rest2sqlParamNames.PAGINATION_PARAM_PAGE, 1,
                Rest2sqlParamNames.PAGINATION_PARAM_LIMIT, 3
        );
        List<Total> totalsDef = new ArrayList<>();
        totalsDef.add(Total.builder().fieldName("sumcol").aggrigate(Total.Aggregate.SUM).build());
        totalsDef.add(Total.builder().fieldName("sumcol").aggrigate(Total.Aggregate.MIN).build());
        totalsDef.add(Total.builder().fieldName("sumcol").aggrigate(Total.Aggregate.MAX).build());
        BeansPage<ABean> res = oraHelper.loadPage("bios.calcTotals", params, null, totalsDef, ABean.class);
        Assert.assertEquals(3, res.getRows().size());
        List<Total> totals = res.getTotals();
        Assert.assertEquals(5, totals.size());

        Total sumTtl = totals.stream().filter(t -> t.getAggregate().equals(Total.Aggregate.SUM)).findFirst().orElse(null);
        Assert.assertNotNull(sumTtl);
        Assert.assertEquals(14L, sumTtl.getFact());

        Total avgTtl = totals.stream().filter(t -> t.getAggregate().equals(Total.Aggregate.AVG)).findFirst().orElse(null);
        Assert.assertNotNull(avgTtl);
        Assert.assertEquals(avgTtl.getFieldType(), BigDecimal.class);
        BigDecimal avgVal = (BigDecimal) avgTtl.getFact();
        Assert.assertEquals(BigDecimal.valueOf(2.75), avgVal);

        Total minTtl = totals.stream().filter(t -> t.getAggregate().equals(Total.Aggregate.MIN)).findFirst().orElse(null);
        Assert.assertNotNull(minTtl);
        Assert.assertEquals(1L, minTtl.getFact());

        Total maxTtl = totals.stream().filter(t -> t.getAggregate().equals(Total.Aggregate.MAX)).findFirst().orElse(null);
        Assert.assertNotNull(maxTtl);
        Assert.assertEquals(8L, maxTtl.getFact());

        Total cntTtl = totals.stream().filter(t -> t.getAggregate().equals(Total.Aggregate.COUNT)).findFirst().orElse(null);
        Assert.assertNotNull(cntTtl);
        Assert.assertEquals(4L, cntTtl.getFact());
    }
}
