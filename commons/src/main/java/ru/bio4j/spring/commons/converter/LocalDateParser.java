package ru.bio4j.spring.commons.converter;

import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.model.transport.errors.DateParseException;

import java.time.LocalDate;

import static ru.bio4j.spring.commons.converter.hanlers.DateTimePatterns.detectFormat;

/**
 * @author ayrat
 * 
 * Класс для преобразований из строки в дату.
 * 
 */
public class LocalDateParser {

	/**
	 * Экземпляр класса.
	 */
	private static LocalDateParser instance;

	public static LocalDateParser getInstance() {
		if (instance == null)
			synchronized (LocalDateParser.class) {
				if (instance == null)
					createDateTimeParser();
			}
		return instance;
	}

	private static void createDateTimeParser() {
		instance = new LocalDateParser();
	}

	private LocalDateParser() {
	}

	public LocalDate parse(String value, String format) {
		if (!Strings.isNullOrEmpty(value)) {
			if (value.toUpperCase().equals("NOW"))
				return LocalDate.now();
			if (value.toUpperCase().equals("MAX"))
				return LocalDate.MAX;
			if (value.toUpperCase().equals("MIN"))
				return LocalDate.MIN;
			try {
				return Types.parseLocalDate(value, format);
			} catch (Exception ex) {
				throw new DateParseException("Ошибка разбора даты. Параметры: (" + value + ", " + format + "). Сообщение: " + ex.toString());
			}
		}
		return null;
	}

	public LocalDate parse(String value) {
		String datetimeFormat = detectFormat(value);
		if (Strings.isNullOrEmpty(datetimeFormat))
			throw new DateParseException("Не верная дата: [" + value + "]. Невозможно определить формат даты.");
		return parse(value, datetimeFormat);
	}

}
