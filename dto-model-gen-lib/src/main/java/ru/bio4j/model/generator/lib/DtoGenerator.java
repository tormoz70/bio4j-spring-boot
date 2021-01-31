package ru.bio4j.model.generator.lib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.commons.utils.Utl;

import java.io.IOException;
import java.io.InputStream;

public class DtoGenerator {
    private static Logger LOG = LoggerFactory.getLogger(DtoGenerator.class);
    public void generate(String path) {
        try (InputStream inputStream = Strings.openResourceAsStream(path)) {
            Document document = Utl.loadXmlDocument(inputStream);
            LOG.debug(document.getDocumentURI());
        } catch (IOException e) {
            LOG.error(e.toString());
        }

    }
}
