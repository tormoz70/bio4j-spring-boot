package ru.bio4j.spring.commons.converter;

import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.model.transport.errors.DateParseException;

import java.time.LocalDateTime;

import static ru.bio4j.spring.commons.converter.hanlers.DateTimePatterns.detectFormat;

/**
 * @author ayrat
 * 
 * Класс для преобразований из строки в дату.
 * 
 */
public class LocalDateTimeParser {

	/**
	 * Экземпляр класса.
	 */
	private static LocalDateTimeParser instance;

	public static LocalDateTimeParser getInstance() {
		if (instance == null)
			synchronized (LocalDateTimeParser.class) {
				if (instance == null)
					createDateTimeParser();
			}
		return instance;
	}

	private static void createDateTimeParser() {
		instance = new LocalDateTimeParser();
	}

	private LocalDateTimeParser() {
	}


	public LocalDateTime parse(String value, String format) {
		if (!Strings.isNullOrEmpty(value)) {
			if (value.toUpperCase().equals("NOW"))
				return LocalDateTime.now();
			if (value.toUpperCase().equals("MAX"))
				return LocalDateTime.MAX;
			if (value.toUpperCase().equals("MIN"))
				return LocalDateTime.MIN;
			try {
				return Types.parseLocalDateTime(value, format);
			} catch (Exception ex) {
				throw new DateParseException("Ошибка разбора даты. Параметры: (" + value + ", " + format + "). Сообщение: " + ex.toString());
			}
		}
		return null;
	}

	public LocalDateTime parse(String value) {
		String datetimeFormat = detectFormat(value);
		if (Strings.isNullOrEmpty(datetimeFormat))
			throw new DateParseException("Не верная дата: [" + value + "]. Невозможно определить формат даты.");
		return parse(value, datetimeFormat);
	}

}
