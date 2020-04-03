package ru.bio4j.ng.commons.converter;

import org.junit.Assert;
import org.junit.Test;
import ru.bio4j.ng.model.transport.Param;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;

public class ConverterTest {

    @Test
    public void typeIsNumberTest() {
        Assert.assertTrue(Types.typeIsNumber(int.class));
    }

    @Test
    public void ConvertBigDecimal2Double() {
        try {
            BigDecimal inValue = new BigDecimal(123.654);
            Double actual = Converter.toType(inValue, Double.class);
            Double expected = 123.654;
            Assert.assertEquals(actual, expected);
        } catch (ConvertValueException ex) {
            Assert.fail(ex.getMessage());
        }
    }

	@Test
	public void ConvertString2Double1() {
		try {
			Double actual = Converter.toType("123.654", Double.class);
			Double expected = 123.654;
			Assert.assertEquals(actual, expected);
		} catch (ConvertValueException ex) {
			Assert.fail(ex.getMessage());
		}
	}
	@Test
	public void ConvertString2Double2() {
		try {
			Double actual = Converter.toType(" 123 123,654", Double.class);
			Double expected = 123123.654;
			Assert.assertEquals(actual, expected);
		} catch (ConvertValueException ex) {
			Assert.fail(ex.getMessage());
		}
	}

    @Test
    public void ConvertString2BigDecimal() {
        try {
            BigDecimal actual = Converter.toType(" 123 123,654", BigDecimal.class);
            BigDecimal expected = new BigDecimal(123123.654);
            Assert.assertEquals(actual, expected);
        } catch (ConvertValueException ex) {
            Assert.fail(ex.getMessage());
        }
    }

	@Test
	public void ConvertString2Float() {
		try {
			Object actual = Converter.toType(" 123 123,654", Float.class);
			Float expected = new Float(123123.654);
			Assert.assertEquals(actual, expected);
		} catch (ConvertValueException ex) {
			Assert.fail(ex.getMessage());
		}
	}

    @Test
    public void ConvertNull2Long() {
        try {
            Object actual = Converter.toType(null, Long.class);
            Long expected = null;
            Assert.assertEquals(actual, expected);
        } catch (ConvertValueException ex) {
            Assert.fail(ex.getMessage());
        }
    }

//    public static <T> T number2Number(Number inValue, Class<T> targetType) throws ParseException {
//    	try {
//    		T rslt = (T)inValue; // Как мне сделать, что бы тут возбуждалось исключение?
//    		return rslt; 
//    	} catch (Exception ex) {
//    		new ParseException(String.format("", inValue, targetType));
//    	}
//    	return null;
//    }
//	@Test
//	public void ConvertDouble2Integer() {
//		try {
//			Integer rslt = number2Number(new Double(123.2), Integer.class);
//		} catch (ParseException ex) {
//			Assert.assertTrue(true);
//		} catch (Exception ex) {
//			Assert.fail(ex.getMessage());
//		}
//	}
	
	@Test
	public void ConvertString2Integer() {
		try {
			Integer actual = Converter.toType("123,2", int.class);
			Integer expected = 123;
			Assert.assertEquals(actual, expected);
		} catch (ConvertValueException ex) {
			Assert.fail(ex.getMessage());
		}
	}

    @Test
    public void ConvertString2Integer2() {
        try {
            Integer actual = Converter.toType("", Integer.class);
            Integer expected = null;
            Assert.assertEquals(actual, expected);
        } catch (ConvertValueException ex) {
            Assert.fail(ex.getMessage());
        }
    }

	private void checkAType(Number value){
		try {
			Assert.assertEquals(Converter.toType(value, Byte.class), new Byte((byte)12));
			Assert.assertEquals(Converter.toType(value, Short.class), new Short((short)12));
			Assert.assertEquals(Converter.toType(value, Integer.class), new Integer((int)12));
			Assert.assertEquals(Converter.toType(value, Long.class), new Long((long)12));
			Assert.assertEquals(Converter.toType(value, Float.class), new Float((float)12));
			Assert.assertEquals(Converter.toType(value, Double.class), new Double((double)12));
		} catch (ConvertValueException ex) {
			Assert.fail(ex.getMessage());
		}
	}
	
	@Test
	public void ConvertNumber2Number() {
		checkAType((byte)12);
		checkAType((short)12);
		checkAType((int)12);
		checkAType((long)12);
		checkAType((float)12);
		checkAType((double)12);
	}
	
	@Test
	public void typeIsNumber() {
        Double doubleValue = 1.2;
        Assert.assertTrue(doubleValue instanceof Number);

        Class<?> type = Double.class;
        Assert.assertTrue(Number.class.isAssignableFrom(type));
	}

    @Test
    public void javaDate2sqlDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(2012, (12-1), 20, 15, 11, 50);
        java.util.Date javadate = calendar.getTime();
        try {
            java.sql.Date sqldate = Converter.toType(javadate, java.sql.Date.class);
            Assert.assertEquals(sqldate.getTime(), javadate.getTime());
        } catch (ConvertValueException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void sqlDate2javaDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(2012, (12-1), 20, 15, 11, 50);
        java.sql.Date sqldate = new java.sql.Date(calendar.getTime().getTime());
        try {
            java.util.Date javadate = Converter.toType(sqldate, java.util.Date.class);
            Assert.assertEquals(javadate.getTime(), sqldate.getTime());
        } catch (ConvertValueException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void sqlTimestamp2javaDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(2012, (12-1), 20, 15, 11, 50);
        Timestamp sqldate = new Timestamp(calendar.getTime().getTime());
        try {
            java.util.Date javadate = Converter.toType(sqldate, java.util.Date.class);
            Assert.assertEquals(javadate.getTime(), sqldate.getTime());
        } catch (ConvertValueException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void stringToBool() throws Exception {
        boolean r = Converter.toType("false", boolean.class);
        Assert.assertFalse(r);
        r = Converter.toType("t", boolean.class);
        Assert.assertTrue(r);
        r = Converter.toType("", boolean.class);
        Assert.assertFalse(r);
        r = Converter.toType(null, boolean.class);
        Assert.assertFalse(r);
    }

    @Test
    public void stringToEnum() throws Exception {
        String instr = "OUT";
        Param.Direction dir = Converter.toType(instr, Param.Direction.class);
        Assert.assertTrue(dir == Param.Direction.OUT);
    }

    @Test
    public void array1() throws Exception {
        String[] instr = {"OUT"};
        String[] dir = Converter.toType(instr, String[].class);
        Assert.assertTrue(Arrays.equals(instr, dir));
    }
    @Test
    public void array2() throws Exception {
        try {
            String[] instr = {"OUT"};
            int[] dir = Converter.toType(instr, int[].class);
            Assert.fail("This conversion must fail!");
        } catch (ConvertValueException e) {
            e.printStackTrace();
            Assert.assertTrue(true);
        }
    }

    @Test
    public void array3() throws Exception {
        Byte[] instr = {1,2,3};
        Byte[] dir = Converter.toType(instr, Byte[].class);
        Assert.assertEquals(dir[0], instr[0]);
    }

    @Test
    public void array31() throws Exception {
        Byte[] instr = {1,2,3};
        double[] dir = Converter.toType(instr, double[].class);
        Assert.assertEquals(dir[0], (double)instr[0], 0);
    }

    @Test
    public void array4() throws Exception {
        int[] indir = {1,3,6};
        String dir = Converter.toType(indir, String.class);
        Assert.assertEquals(dir, "1,3,6");
    }

    @Test
    public void array5() throws Exception {
        String instr = "1, 2, 3";
        int[] dir = Converter.toType(instr, int[].class);
        Assert.assertEquals(dir[0], 1);
    }

    @Test
    public void array6() throws Exception {
        String instr = "1, 2, 3";
        long[] dir = Converter.toType(instr, long[].class);
        Assert.assertEquals(dir[0], 1L);
    }

    @Test
    public void ConvertNumber2Boolean() {
        try {
            //BigDecimal ddd = new BigDecimal(new Long(0).toString());
            Boolean actual = Converter.toType(0, Boolean.class);
            Boolean expected = false;
            Assert.assertEquals(actual, expected);

            actual = Converter.toType(1, Boolean.class);
            expected = true;
            Assert.assertEquals(actual, expected);

            actual = Converter.toType(5L, Boolean.class);
            expected = true;
            Assert.assertEquals(actual, expected);

            actual = Converter.toType(-5D, Boolean.class);
            expected = false;
            Assert.assertEquals(actual, expected);

        } catch (ConvertValueException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void ConvertNumber2Boolean2() throws Exception {
        Boolean actual = Converter.toType(null, Boolean.class);
        Boolean expected = null;
        Assert.assertEquals(actual, expected);
    }
    @Test
    public void ConvertNumber2Boolean3() throws Exception {
        Boolean actual = Converter.toType(null, boolean.class);
        Boolean expected = false;
        Assert.assertEquals(actual, expected);
    }

}
