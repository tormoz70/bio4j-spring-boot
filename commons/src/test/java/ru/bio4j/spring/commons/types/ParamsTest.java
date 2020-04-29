package ru.bio4j.spring.commons.types;

import org.junit.Ignore;
import org.junit.Assert;
import org.junit.Test;
import ru.bio4j.spring.model.transport.MetaType;
import ru.bio4j.spring.model.transport.Param;

import java.text.SimpleDateFormat;
import java.util.*;

public class ParamsTest {

    @Test
    public void create1() {
        List<Param> params1 = Paramus.createParams("param1", 123, "param2", 124, "param3", 125);
        Assert.assertTrue(params1.size() == 3 && (int)params1.get(1).getValue() == 124);
        List<Param> params2 = Paramus.createParams(params1);
        Assert.assertTrue(params2.size() == 3 && (int)params2.get(1).getValue() == 124);
    }

	@Test
	public void add1() {
		List<Param> testParams = new ArrayList<>();
        try(Paramus paramus  = Paramus.set(testParams);){
            paramus.add("param1", 111).add("param1", 111, true);
            Assert.assertEquals(paramus.getValueByName("param1", false), 111);
            paramus.add("param3", 33);
            Assert.assertNotNull(paramus.getParam("param3"));
            Assert.assertTrue(paramus.paramExists("param3"));
            Assert.assertFalse(paramus.paramExists("Param3", false));
            Assert.assertTrue(paramus.paramExists("Param3"));
        }
	}
    @Test
    public void add2() throws Exception {
        List<Param> testParams = new ArrayList<>();
        testParams.add(Param.builder().name("p1").value("v1").build());
        testParams.add(Param.builder().name("p2").value("v2").build());
        testParams.add(Param.builder().name("p3").value("v3").build());

        Param p22 = Param.builder().name("p2").value("v22").build();

        Paramus.setParam(testParams, p22, true);
        Assert.assertTrue(Paramus.indexOf(testParams, "p2") == 1);
        Assert.assertTrue(Paramus.getParam(testParams, "p2") == p22);
    }


	@Test
	public void setValue() throws Exception {
		try(Paramus paramus = Paramus.set(new ArrayList<Param>());){
			Param.Builder pb = Param.builder()/*.owner(paramus.get())*/;
			paramus.add(pb
					.name("testDate")
					.type(MetaType.STRING)
					.direction(Param.Direction.IN)
					.format("to_date('yyyy.MM.dd HH:mm:ss');to_number('##0.###')")
					.build());
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			Date date = fmt.parse("2013-05-06");
			paramus.setValue("testDate", date);
			String val = paramus.getValueAsStringByName("testDate", true);
			Assert.assertEquals("2013.05.06 00:00:00", val);

			paramus.add(pb
					.name("testDouble")
					.type(MetaType.STRING)
					.direction(Param.Direction.IN)
					.format("to_date('yyyy.MM.dd HH:mm:ss');to_number('##0.#')")
					.build());
			Double dbl = 123.567;
			paramus.setValue("testDouble", dbl);
			val = paramus.getValueAsStringByName("testDouble", true);
			Assert.assertEquals("123.6", val);

		}

	}

    @Test
    public void getParamValueTest() throws Exception {
        try(Paramus paramus = Paramus.set(new ArrayList<Param>());){
            Param.Builder pb = Param.builder()/*.owner(paramus.get())*/;
            paramus.add(pb.name("v_packetzip_id").type(MetaType.INTEGER).direction(Param.Direction.OUT).build());
            long org = paramus.getParamValue("v_packetzip_id", long.class);
            Assert.assertEquals(0L, org);
        }
    }

    @Test
    public void cloneTest() throws Exception {
        List<Param> p = new ArrayList<Param>();
        try(Paramus pms = Paramus.set(p);){
            pms.add(Param.builder()
                            .name("p1")
                            .value(10)
                            .type(MetaType.CLOB)
                            .direction(Param.Direction.INOUT)
                            .build()
            );
            pms.setValue("p2", "76");
        }
        List<Param> pp = Paramus.clone(p);
        Assert.assertNotNull(pp);
        Assert.assertEquals(pp.size(), p.size());
        Assert.assertEquals("p1", pp.get(0).getName());
        Assert.assertEquals(10, pp.get(0).getValue());
        Assert.assertEquals(Param.Direction.INOUT, pp.get(0).getDirection());
        Assert.assertEquals(MetaType.CLOB, pp.get(0).getType());
    }

    @Test
    public void setTest() throws Exception {
        List<Param> prms = Paramus.set(new ArrayList<Param>()).add("dummy", 101).pop();
        Assert.assertEquals(prms.get(0).getValue(), 101);
    }

	@Ignore
	@Test
    public void toStringTest() throws Exception {
        List<Param> prms = Paramus.set(new ArrayList<>())
                .add("dummy1", 101)
                .add("dummy2", "101")
                .add("dummy3", null)
                .pop();
        StringBuilder sb = new StringBuilder();
        sb.append("{Params: {\n");
        for (Param p : prms)
            sb.append("\t"+Paramus.paramToString(p) + ",\n");
        sb.append("}}");
        String prmsStr = sb.toString();

        Assert.assertEquals(prmsStr, "");
    }

	@Ignore
	@Test
    public void toStringTest1() throws Exception {
        List<Param> prms = Paramus.set(new ArrayList<>())
                .add("dummy1", 101)
                .add("dummy2", "101")
                .add("dummy3", null)
                .pop();
        String prmsStr = Paramus.paramsAsString(prms);

        Assert.assertEquals(prmsStr, "");
    }

    @Test
    public void setParamsTest() throws Exception {
        List<Param> testParams = new ArrayList<>();
        testParams.add(Param.builder().name("p1").value("v1").build());
        testParams.add(Param.builder().name("p2").value("v2").build());
        testParams.add(Param.builder().name("p3").value("v3").build());

        List<Param> testParams2 = new ArrayList<>();

        Paramus.setParams(testParams2, testParams);
        Assert.assertTrue(Paramus.indexOf(testParams2, "p2") == 1);
        Assert.assertTrue(Paramus.paramValueAsString(testParams2, "p2").equals("v2"));
    }

	@Test
	public void creParamsTest() throws Exception {
		List<Param> testParams = Paramus.createParams(new HashMap<String, Object>(){{ put("1", 1); }});
		Assert.assertEquals(testParams.get(0).getValue(), 1);

		testParams = Paramus.createParams("1", 1, "2", 123.5, "3", "qwe");
		Assert.assertEquals(Paramus.paramValue(testParams, "1"), 1);
		Assert.assertEquals(Paramus.paramValue(testParams, "2"), 123.5);
		Assert.assertEquals(Paramus.paramValue(testParams, "3"), "qwe");
	}

}
