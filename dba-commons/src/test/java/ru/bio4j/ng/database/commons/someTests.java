package ru.bio4j.ng.database.commons;

import org.junit.Assert;
import org.junit.Test;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

}
