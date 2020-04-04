package ru.bio4j.spring.commons.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;
import ru.bio4j.spring.commons.converter.DateParseException;
import ru.bio4j.spring.commons.converter.DateTimeParser;

public class DateTimeParserTest {

	@Test
	public void detectFormat() {
		Assert.assertEquals("yyyyMMddHHmmss", DateTimeParser.getInstance().detectFormat("20000101222222"));
		Assert.assertEquals("dd.MM.yyyy HH:mm:ss", DateTimeParser.getInstance().detectFormat("01.01.2000 22:22:22"));
		Assert.assertEquals("yyyy.MM.dd HH:mm:ss", DateTimeParser.getInstance().detectFormat("2000.01.01 22:22:22"));
		Assert.assertEquals("yyyy.MM.dd", DateTimeParser.getInstance().detectFormat("2000.01.01"));
		Assert.assertEquals("dd.MM.yyyy", DateTimeParser.getInstance().detectFormat("01.01.2000"));
		Assert.assertEquals("yyyyMMdd", DateTimeParser.getInstance().detectFormat("20000101"));
		Assert.assertEquals("yyyyMM", DateTimeParser.getInstance().detectFormat("200001"));
		Assert.assertEquals("ddMMyyyy", DateTimeParser.getInstance().detectFormat("01012000"));
		Assert.assertEquals("yyyy-MM-dd'T'HH:mm:ss", DateTimeParser.getInstance().detectFormat("2000-01-01T22:22:22"));
		Assert.assertEquals("yyyy-MM-dd HH:mm:ss", DateTimeParser.getInstance().detectFormat("2000-01-01 22:22:22"));
		Assert.assertEquals("dd.MM.yyyy H:mm:ss", DateTimeParser.getInstance().detectFormat("01.01.2000 2:22:22"));
		Assert.assertEquals("yyyy.MM.dd HH:mm", DateTimeParser.getInstance().detectFormat("2000.01.01 22:22"));
		Assert.assertEquals("yyyyMMdd HH:mm:ss", DateTimeParser.getInstance().detectFormat("20000101 22:22:22"));
		Assert.assertEquals("yyyyMMdd HH:mm", DateTimeParser.getInstance().detectFormat("20000101 22:22"));
		Assert.assertEquals("dd.MM.yyyy H:mm", DateTimeParser.getInstance().detectFormat("01.01.2000 2:22"));
	}

	@Test
	public void parsStringString() {
		try {
			java.util.Date date = null;
			try {
				String dateStr = "2000-01-01T22:22:22";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				date = sdf.parse(dateStr);
			} catch (ParseException ex) {
				Assert.fail(ex.getMessage());
			}
			Assert.assertEquals(date, DateTimeParser.getInstance().pars("2000-01-01T22:22:22", "yyyy-MM-dd'T'HH:mm:ss"));
		} catch (DateParseException ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void parsString() {
		try {
			java.util.Date date = null;
			try {
				String dateStr = "2000-01-01T22:22:22";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				date = sdf.parse(dateStr);
			} catch (ParseException ex) {
				Assert.fail(ex.getMessage());
			}
			Assert.assertEquals(date, DateTimeParser.getInstance().pars("2000-01-01T22:22:22"));
		} catch (DateParseException ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void parsString1() {
		Matcher m = Regexs.match("2019-06-19T08:00:00.000", "[012]\\d{3}-[01]\\d{1}-[0123]\\d{1}T[012]\\d{1}:[012345]\\d{1}:[012345]\\d{1}\\.\\d{3}", Pattern.CASE_INSENSITIVE);
		boolean found = m.find();
		boolean matches = m.matches();
		try {
			java.util.Date date = null;
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
				date = sdf.parse("2019-06-19T08:00:00.000");
			} catch (ParseException ex) {
				Assert.fail(ex.getMessage());
			}
			Assert.assertEquals(date, DateTimeParser.getInstance().pars("2019-06-19T08:00:00.000Z"));
		} catch (DateParseException ex) {
			Assert.fail(ex.getMessage());
		}
	}
}
