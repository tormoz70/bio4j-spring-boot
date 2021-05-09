package ru.bio4j.spring.database.commons;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import ru.bio4j.spring.commons.types.Paramus;
import ru.bio4j.spring.commons.utils.Lists;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.commons.utils.Utl;
import ru.bio4j.spring.model.transport.ABean;
import ru.bio4j.spring.model.transport.BeansPage;
import ru.bio4j.spring.model.transport.Param;
import ru.bio4j.spring.model.transport.jstore.Total;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ayrat
 * Date: 02.12.13
 * Time: 21:09
 * To change this template use File | Settings | File Templates.
 */
public class someTests {
    @Test
    public void namedStatementTest() throws Exception {
        Map<String, int[]> indexMap = new HashMap();
        String parsedQuery = DbNamedParametersStatement.parse("asdasd :ert SELECT :method_name \"sdf :sdf\" pg_get_function_identity_" +
                " asdasdasd /* :dfgdfgdg*/ fghfghfh\n" +
                " asdasdasd -- :dfgdfgdg\n" +
                "arguments(:method_name::regproc) :ert as rslt :method_name1", null, indexMap);
//        Assert.assertEquals(parsedQuery, "asdasd ? SELECT ? \"sdf :sdf\" pg_get_function_identity_ asdasdasd /* :dfgdfgdg*/ fghfghfh\n" +
//                " asdasdasd -- :dfgdfgdg\n" +
//                "arguments(?::regproc) ? as rslt ?");

        Assert.assertEquals(parsedQuery, "asdasd ? SELECT ? \"sdf ?\" pg_get_function_identity_ asdasdasd /* :dfgdfgdg*/ fghfghfh\n" +
                " asdasdasd -- :dfgdfgdg\n" +
                "arguments(?::regproc) ? as rslt ?");

        Assert.assertEquals(indexMap.get("ert")[0], 1);
        Assert.assertEquals(indexMap.get("ert")[1], 5);
        Assert.assertEquals(indexMap.get("method_name")[0], 2);
        Assert.assertEquals(indexMap.get("method_name")[1], 4);
    }

    @Test
    public void nameOfByteArray() throws Exception {
        Class<?> t = Byte[].class;
        String className = t.getName();
        Class<?> type = getClass().getClassLoader().loadClass("java.lang.String");
        System.out.println(type.getCanonicalName());
    }

    @Test
    public void cutParamPrefix() throws Exception {
        String r = DbUtils.cutParamPrefix("okeeper_id");
        Assert.assertEquals(r, "okeeper_id");
        r = DbUtils.cutParamPrefix("p_okeeper_id");
        Assert.assertEquals(r, "okeeper_id");
        r = DbUtils.cutParamPrefix("v_okeeper_id");
        Assert.assertEquals(r, "okeeper_id");
        r = DbUtils.cutParamPrefix("P_OKEEPER_ID");
        Assert.assertEquals(r, "OKEEPER_ID");
        r = DbUtils.cutParamPrefix("V_OKEEPER_ID");
        Assert.assertEquals(r, "OKEEPER_ID");
    }

    @Test
    public void cutEmptyFilterConditions0Test() throws Exception {
        String sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("load_log1.sql"));
        List<Param> prms = new ArrayList<>();
        Paramus.setParamValue(prms, "org_id", null);
        Paramus.setParamValue(prms, "SYS_CURUSERROLES", null);
        sql = DbUtils.cutFilterConditions(sql, prms);
        System.out.println(sql);
    }

    @Test
    public void cutEmptyFilterConditions1Test() throws Exception {
        String sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("films.sql"));
        List<Param> prms = new ArrayList<>();
        Paramus.setParamValue(prms, "p_calcFrom", null);
        Paramus.setParamValue(prms, "p_genre", "123");
        sql = DbUtils.cutFilterConditions(sql, prms);
        System.out.println(sql);
    }

    @Test
    public void findRoundedStrTest() throws Exception {
        String sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("films.sql"));
        Strings.findRoundedStr(sql, "/*${cutiif}*/", "/*{cutiif}$*/", new Strings.IRoundedStrProcessor() {
            @Override
            public String process(String found) {
                return null;
            }
        });
    }


    @Test
    public void findRoundedStrTest1() throws Exception {
        System.out.println(DbUtils.calcfactOffset(5144, 1));
        System.out.println(DbUtils.calcfactOffset(5144, 2));
        System.out.println(DbUtils.calcfactOffset(5144, 3));
        System.out.println(DbUtils.calcfactOffset(5144, 5));
        System.out.println(DbUtils.calcfactOffset(5144, 8));
    }


//    @Test
//    public void parsClockhouse4jJsonTest() {
//        String json = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("1.json"));
//        BeansPage<ABean> rrr = parsClockhouse4jJson(json, ABean.class);
//        Assert.assertTrue(rrr != null);
//    }

    @Test
    public void decodeParamsTest1() throws Exception {
        List<Param> params = DbUtils.decodeParams(Arrays.asList(12, "dfg", 123L));
        Assert.assertTrue(params.size() == 1);
    }

    @Test
    public void applyParamsToParamsTest1() {
        List<Object> inPrms = Arrays.asList(12, "dfg", 123L);
        List<Param> params = DbUtils.decodeParams(inPrms);
        params.get(1).setValue("asd");
        DbUtils.applyParamsToParams(params, inPrms, true, true, true);
        Assert.assertTrue(inPrms.get(1).equals("asd"));
    }

    @Test
    public void applyParamsToParamsTest2() {
        Object[] inPrms = new Object[] {12, "dfg", 123L};
        List<Param> params = DbUtils.decodeParams(inPrms);
        params.get(1).setValue("asd");
        DbUtils.applyParamsToParams(params, inPrms, true, true, true);
        Assert.assertTrue(inPrms[1].equals("asd"));
    }

    @Test
    public void applyParamsToParamsTest3() {
        List<Param> params = Paramus.createParams("p1", 1, "p2", 2, "p3", "3");
        Object[] inPrms = new Object[] {12, "dfg", 123L};
        List<Param> prms = DbUtils.decodeParams(inPrms);
        DbUtils.applyParamsToParams(prms, params, true, true, true);
        params.get(1).setValue("asd");
        DbUtils.applyParamsToParams(params, inPrms, true, true, true);
        Assert.assertTrue(inPrms[1].equals("asd"));
    }

    @Test
    public void applyParamsToParamsTest4() {
        List<Param> params = new ArrayList<>();
        List<Param> inparams = Paramus.createParams("p1", 1, "p2", 2, "p3", "3");
        DbUtils.applyParamsToParams(inparams, params, true, true, true);
        Assert.assertTrue(params.size() == 3);
    }

}
