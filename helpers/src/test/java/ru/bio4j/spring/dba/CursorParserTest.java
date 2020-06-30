package ru.bio4j.spring.dba;

import org.junit.Test;
import org.w3c.dom.Document;
import ru.bio4j.spring.commons.types.LogWrapper;
import ru.bio4j.spring.commons.utils.Utl;
import ru.bio4j.spring.database.api.SQLDefinition;
import ru.bio4j.spring.database.commons.CursorParser;

import java.io.InputStream;

public class CursorParserTest {
    private static final LogWrapper LOG = LogWrapper.getLogger(CursorParser.class);

    @Test
    public void toStringTest() throws Exception {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("rcard.xml");
        Document document = Utl.loadXmlDocument(inputStream);
        SQLDefinition cursor = CursorParser.pars(document, "eve.rcard");
        String out = cursor.toString();
        System.out.println(out);
    }

}
