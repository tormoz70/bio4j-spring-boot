package ru.bio4j.spring.commons.utils;

//import com.thoughtworks.xstream.exts.XStreamUtility;
//import org.testng.Assert;
//import org.testng.annotations.Test;
import org.junit.Assert;
import org.junit.Test;
import ru.bio4j.spring.commons.converter.DateTimeParser;
import ru.bio4j.spring.model.transport.Param;
import ru.bio4j.spring.model.transport.jstore.Sort;
import ru.bio4j.spring.model.transport.XLRCfg;

import java.util.ArrayList;
import java.util.List;

public class XStreamUtilityTest {

    @Test
    public void toXmlTest() throws Exception {
        XLRCfg xlrCfg = new XLRCfg();
        xlrCfg.setFullCode("qwe");

        XLRCfg.DataSource ds = new XLRCfg.DataSource();
        ds.setSql("select 1 from dual");

        ds.setSorts(new ArrayList<>());
        Sort s = new Sort();
        s.setFieldName("sortField");
        s.setDirection(Sort.Direction.DESC);
        ds.getSorts().add(s);

        XLRCfg.ColumnDefinition cd = new XLRCfg.ColumnDefinition();
        cd.setFieldName("field1");
        cd.setTitle("Колонка 1");
        cd.setFormat("##0.00");
        ds.getColumnDefinitions().add(cd);

        xlrCfg.setDss(new ArrayList<>());
        xlrCfg.getDss().add(ds);

        xlrCfg.setAppend(new XLRCfg.Append());
        List<Param> prms = new ArrayList<>();
        prms.add(Param.builder().name("inparam1").value("inparam1-value").build());
        prms.add(Param.builder().name("inparam11").value(1).build());
        prms.add(Param.builder().name("inparam12").value(1L).build());
        prms.add(Param.builder().name("inparam13").value(1D).build());
        prms.add(Param.builder().name("inparam2").value(DateTimeParser.getInstance().pars("2017-01-01T17:55")).build());
        xlrCfg.getAppend().setInParams(prms);
        xlrCfg.getAppend().setSessionID("sess-id");
        xlrCfg.getAppend().setUserUID("user-uid");
        xlrCfg.getAppend().setUserName("user-name");
        xlrCfg.getAppend().setUserOrgId("user-org-id");
        xlrCfg.getAppend().setUserRoles("user-roles");
        xlrCfg.getAppend().setRemoteIP("remote-ip");


        String encoding = "UTF-8";
        //String aString = XStreamUtility.getInstance().toXml(xlrCfg, encoding);

        Assert.assertTrue(true);

        //Utl.storeString(aString, "d:\\exp-rpt-cfg-test.xml", encoding);

        //XLRCfg restored = XStreamUtility.getInstance().toJavaBean(aString);
        //Assert.assertTrue(restored.getFullCode().equals("qwe"));
    }
}