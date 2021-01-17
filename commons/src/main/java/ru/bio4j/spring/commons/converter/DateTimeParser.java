package ru.bio4j.spring.commons.converter;

import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.model.transport.errors.DateParseException;

import java.util.Date;

import static ru.bio4j.spring.commons.converter.hanlers.DateTimePatterns.detectFormat;

/**
 * @author ayrat
 * 
 * Класс для преобразований из строки в дату.
 * 
 */
public class DateTimeParser {

	/**
	 * Экземпляр класса.
	 */
	private static DateTimeParser instance;

	public static DateTimeParser getInstance() {
		if (instance == null)
			synchronized (DateTimeParser.class) {
				if (instance == null)
					createDateTimeParser();
			}
		return instance;
	}

	private static void createDateTimeParser() {
		instance = new DateTimeParser();
	}

	private DateTimeParser() {
	}

	public Date parse(String value, String format) {
		if (!Strings.isNullOrEmpty(value)) {
			if (value.toUpperCase().equals("NOW"))
				return new Date();
			if (value.toUpperCase().equals("MAX"))
				return Types.maxDate();
			if (value.toUpperCase().equals("MIN"))
				return Types.minDate();
			try {
				return Types.parseDate(value, format);
			} catch (Exception ex) {
				throw new DateParseException("Ошибка разбора даты. Параметры: (" + value + ", " + format + "). Сообщение: " + ex.toString());
			}
		}
		return null;
	}

	public Date parse(String value) {
		String datetimeFormat = detectFormat(value);
		if (Strings.isNullOrEmpty(datetimeFormat))
			throw new DateParseException("Не верная дата: [" + value + "]. Невозможно определить формат даты.");
		return parse(value, datetimeFormat);
	}

}
