package ru.bio4j.spring.commons.utils;

//import org.testng.Assert;
//import org.testng.annotations.Test;

import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.*;

public class StringUtlTest {

	@Test
	public void appendStr() {
		String line = null;
		line = Strings.append(line, "qwe1", "|");
		Assert.assertEquals(line, "qwe1");
		line = Strings.append(line, "qwe2", "|");
		Assert.assertEquals(line, "qwe1|qwe2");
		line = Strings.append(line, "", "|");
		Assert.assertEquals(line, "qwe1|qwe2|");
		line = Strings.append(line, null, "|");
		Assert.assertEquals(line, "qwe1|qwe2||");
		line = Strings.append(line, "asd", "|");
		Assert.assertEquals(line, "qwe1|qwe2|||asd");
	}

	@Test
	public void compareStrings() {
		Assert.assertEquals(Strings.compare(null, null, false), true);
		Assert.assertEquals(Strings.compare("", null, false), false);
		Assert.assertEquals(Strings.compare(null, "", false), false);
		Assert.assertEquals(Strings.compare("asd", "asd", false), true);
		Assert.assertEquals(Strings.compare("asd", "ASD", false), false);
		Assert.assertEquals(Strings.compare("asd", "ASD", true), true);
	}

	@Test
	public void split() {
		//System.out.println(String.format("UPPER(%%%s%%)", "FIELD1"));
		//System.out.println(String.format("UPPER(%s) LIKE UPPER('\u0025'||:%s||'\u0025')", "FIELD1", "test"));
		//System.out.println(String.format("%s IS NULL", "fff1", "fff2"));
		String[] strs = Strings.split("qwe,asd,zxc", ",");
		Assert.assertEquals(strs[0], "qwe");
		Assert.assertEquals(strs[1], "asd");
		Assert.assertEquals(strs[2], "zxc");
	}

    @Test
    public void split1() {
        String[] strs = Strings.split("qwe asd xcv.tyu asd@fgh", ' ', ',', ';');
        Assert.assertEquals(strs[0], "qwe");
        Assert.assertEquals(strs[3], "asd@fgh");
    }

	@Test
	public void isNullOrEmpty() {
		Assert.assertEquals(Strings.isNullOrEmpty(null), true);
		Assert.assertEquals(Strings.isNullOrEmpty(""), true);
		Assert.assertEquals(Strings.isNullOrEmpty("qwe"), false);
	}

    @Test
    public void combineArrayTest() {
        int[] a = {1,2,3,4,5};
        String lst = Strings.combineArray(a, ";");
        Assert.assertEquals(lst, "1;2;3;4;5");
    }

    @Test
    public void trimTest() {
        String s = Strings.trim("     [\"'asd fgh'\"] ", " []\"'");
        Assert.assertEquals(s, "asd fgh");
    }

    @Test
    public void getFirstItemTest() throws Exception {
        String list1 = "qwe1/qwe2";
        Assert.assertEquals(Strings.getFirstItem(list1, "/"), "qwe1");
        String list2 = "qwe1";
        Assert.assertEquals(Strings.getFirstItem(list2, "/"), "qwe1");
        String list3 = "";
        Assert.assertEquals(Strings.getFirstItem(list3, "/"), "");
    }
    @Test
    public void cutFirstItemTest() throws Exception {
        String list1 = "qwe1/qwe2/qwe3";
        Assert.assertEquals(Strings.cutFirstItem(list1, "/"), "qwe2/qwe3");
        String list2 = "qwe1";
        Assert.assertEquals(Strings.cutFirstItem(list2, "/"), null);
        String list3 = "";
        Assert.assertEquals(Strings.cutFirstItem(list3, "/"), null);
    }

    @Test
    public void replace0() throws Exception {
        final String src = "Something in the air!!!";
        Assert.assertEquals(Strings.replace(src, 10, 12, ""), "Something the air!!!");
    }
    @Test
    public void replace1() throws Exception {
        final String src = "Something in the air!!!";
        Assert.assertEquals(Strings.replace(src, 10, 11, "on"), "Something on the air!!!");
    }
    @Test
    public void replace2() throws Exception {
        final String src = "Something in the air!!!";
        Assert.assertEquals(Strings.replace(src, 10, 100, ""), "Something ");
    }
    @Test
    public void replace3() throws Exception {
        final String src = "Something in the air!!!";
        Assert.assertEquals(Strings.replace(src, 10, 100, "qwe"), "Something qwe");
    }
    @Test
    public void replace4() throws Exception {
        final String src = "Something in the air!!!";
        Assert.assertEquals(Strings.replace(src, "in ", "qwe"), "Something qwethe air!!!");
        Assert.assertEquals(Strings.replace(src, "air!!!", "qwe"), "Something in the qwe");
    }

    @Test
    public void replace5() throws Exception {
        Set<String> src = new HashSet<>();
        src.add("qwe");
        src.add("FTW");
        src.add("asd");
        Assert.assertTrue(Strings.containsIgnoreCase(src, "QWE"));
        Assert.assertTrue(Strings.containsIgnoreCase(src, "ftW"));
    }

    @Test
    public void replace6() throws Exception {
        Map<String, String> src = new HashMap<>();
        src.put("qwe", "1");
        src.put("FTW", "2");
        src.put("asd", "3");
        Assert.assertEquals(Strings.findIgnoreCase(src, "QWE"), "1");
        Assert.assertEquals(Strings.findIgnoreCase(src, "ftW"), "2");
    }


    @Test
    public void replace7() throws Exception {
        String src = "asd ''::text, 0::numeric";
        String pstr = Strings.replace(src, "::", "ddd");
        Assert.assertEquals(pstr, "asd ''dddtext, 0dddnumeric");
    }

    @Test
    public void formatIntervalTest() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        Date firstDate = sdf.parse("01.01.2019 12:00:00.000");
        Date secondDate = sdf.parse("02.01.2019 13:03:51.020");
        long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
        String r = Strings.formatInterval(diffInMillies);
        Assert.assertEquals(r, "1 01:03:51.020");
    }
}
