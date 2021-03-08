package ru.bio4j.spring.commons.converter.hanlers;

import ru.bio4j.spring.commons.converter.DateTimeParserTemplate;
import ru.bio4j.spring.commons.utils.Regexs;
import ru.bio4j.spring.commons.utils.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DateTimePatterns {

    private static final List<DateTimeParserTemplate> templates;
    static {
        templates = new ArrayList<>();
        templates.add(new DateTimeParserTemplate("yyyyMMddHHmmss", "^[012]\\d{3}[01]\\d{1}[0123]\\d{1}[012]\\d{1}[012345]\\d{1}[012345]\\d{1}$"));
        templates.add(new DateTimeParserTemplate("dd.MM.yyyy HH:mm:ss", "^[0123]\\d{1}\\.[01]\\d{1}\\.[012]\\d{3}\\s[012]\\d{1}[:][012345]\\d{1}[:][012345]\\d{1}$"));
        templates.add(new DateTimeParserTemplate("yyyy.MM.dd HH:mm:ss", "^[012]\\d{3}\\.[01]\\d{1}\\.[0123]\\d{1}\\s[012]\\d{1}[:][012345]\\d{1}[:][012345]\\d{1}$"));
        templates.add(new DateTimeParserTemplate("yyyy.MM.dd", "^[012]\\d{3}\\.[01]\\d{1}\\.[0123]\\d{1}$"));
        templates.add(new DateTimeParserTemplate("dd.MM.yyyy", "^[0123]\\d{1}\\.[01]\\d{1}\\.[012]\\d{3}$"));
        templates.add(new DateTimeParserTemplate("yyyyMMdd", "^[012]\\d{3}[01]\\d{1}[0123]\\d{1}$"));
        templates.add(new DateTimeParserTemplate("yyyyMM", "^[012]\\d{3}[01]\\d{1}$"));
        templates.add(new DateTimeParserTemplate("ddMMyyyy", "^[0123]\\d{1}[01]\\d{1}[012]\\d{3}$"));
        templates.add(new DateTimeParserTemplate("yyyy-MM-dd'T'HH:mm:ss.SSS", "^[012]\\d{3}-[01]\\d{1}-[0123]\\d{1}T[012]\\d{1}:[012345]\\d{1}:[012345]\\d{1}\\.\\d{3}"));
        templates.add(new DateTimeParserTemplate("yyyy-MM-dd'T'HH:mm:ss", "^[012]\\d{3}[-][01]\\d{1}[-][0123]\\d{1}[T][012]\\d{1}[:][012345]\\d{1}[:][012345]\\d{1}$"));
        templates.add(new DateTimeParserTemplate("yyyy.MM.dd'T'HH:mm:ss", "^[012]\\d{3}\\.[01]\\d{1}\\.[0123]\\d{1}[T][012]\\d{1}[:][012345]\\d{1}[:][012345]\\d{1}$"));
        templates.add(new DateTimeParserTemplate("yyyy-MM-dd'T'HH:mm", "^[012]\\d{3}[-][01]\\d{1}[-][0123]\\d{1}[T][012]\\d{1}[:][012345]\\d{1}$"));
        templates.add(new DateTimeParserTemplate("yyyy-MM-dd", "^[012]\\d{3}[-][01]\\d{1}[-][0123]\\d{1}$"));
        templates.add(new DateTimeParserTemplate("yyyy-MM-dd HH:mm:ss", "^[012]\\d{3}[-][01]\\d{1}[-][0123]\\d{1}\\s[012]\\d{1}[:][012345]\\d{1}[:][012345]\\d{1}$"));
        templates.add(new DateTimeParserTemplate("dd.MM.yyyy H:mm:ss", "^[0123]\\d{1}\\.[01]\\d{1}\\.[012]\\d{3}\\s[012]?\\d{1}[:][012345]\\d{1}[:][012345]\\d{1}$"));
        templates.add(new DateTimeParserTemplate("yyyy.MM.dd HH:mm", "^[012]\\d{3}\\.[01]\\d{1}\\.[0123]\\d{1}\\s[012]\\d{1}[:][012345]\\d{1}$"));
        templates.add(new DateTimeParserTemplate("yyyyMMdd HH:mm:ss", "^[012]\\d{3}[01]\\d{1}[0123]\\d{1}\\s[012]\\d{1}[:][012345]\\d{1}[:][012345]\\d{1}$"));
        templates.add(new DateTimeParserTemplate("yyyyMMdd HH:mm", "^[012]\\d{3}[01]\\d{1}[0123]\\d{1}\\s[012]\\d{1}[:][012345]\\d{1}$"));
        templates.add(new DateTimeParserTemplate("dd.MM.yyyy H:mm", "^[0123]\\d{1}\\.[01]\\d{1}\\.[012]\\d{3}\\s[012]?\\d{1}[:][012345]\\d{1}$"));
    }

    public static String detectFormat(String datetimeValue) {
        if(!Strings.isNullOrEmpty(datetimeValue)) {
            for (DateTimeParserTemplate f : templates) {
                if (Regexs.match(datetimeValue, f.getRegex(), Pattern.CASE_INSENSITIVE).find())
                    return f.getFormat();
            }
        }
        return null;
    }

}
