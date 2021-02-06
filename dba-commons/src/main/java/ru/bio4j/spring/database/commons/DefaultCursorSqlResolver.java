package ru.bio4j.spring.database.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.spring.commons.utils.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultCursorSqlResolver implements CursorSqlResolver {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultCursorSqlResolver.class);


    private static String PATTERN_EXTRACT_FILE_NAME = "(?<=\\{text-file:)(\\w|-)+\\.sql(?=\\})";

    @Override
    public String tryLoadSQL(final String bioCode, String sqlText) {
        String separator = File.separator;
        String bioParentPath = Utl.extractBioParentPath(bioCode, separator);
        Matcher m = Regexs.match(sqlText, PATTERN_EXTRACT_FILE_NAME, Pattern.CASE_INSENSITIVE);
        if (m.find()) {
            String sqlFileName = bioParentPath + (Strings.isNullOrEmpty(bioParentPath) ? "" : separator) + m.group();
            try {
                sqlText = Strings.loadResourceAsString(sqlFileName);
            } catch (IOException e) {
                throw Utl.wrapErrorAsRuntimeException(String.format("The %s file referenced by %s is not found in resources!", sqlFileName, bioCode));
            }
        }
        return sqlText;
    }

}
