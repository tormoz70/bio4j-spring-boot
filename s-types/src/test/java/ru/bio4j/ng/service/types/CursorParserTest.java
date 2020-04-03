package ru.bio4j.ng.service.types;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.SQLDefinition;
import ru.bio4j.ng.model.transport.BioConfig;


import java.io.InputStream;

public class CursorParserTest {
    private static final Logger LOG = LoggerFactory.getLogger(CursorParser.class);

    @Test
    public void toStringTest() throws Exception {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("rcard.xml");
        Document document = Utl.loadXmlDocument(inputStream);
        SQLDefinition cursor = CursorParser.pars(document, "eve.rcard");
        String out = cursor.toString();
        System.out.println(out);
    }

}
