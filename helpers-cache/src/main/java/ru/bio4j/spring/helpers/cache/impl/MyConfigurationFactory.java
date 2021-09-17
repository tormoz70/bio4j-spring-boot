package ru.bio4j.spring.helpers.cache.impl;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.generator.ConfigurationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyConfigurationFactory {
    private static final Logger LOG = LoggerFactory.getLogger(MyConfigurationFactory.class.getName());

    /**
     * Configures a bean from an XML input stream.
     */
    public static Configuration parseConfiguration(final InputStream inputStream) throws CacheException {

        LOG.debug("Configuring ehcache from InputStream");

        Configuration configuration = new Configuration();
        try {
            InputStream translatedInputStream = translateSystemProperties(inputStream);
            final SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            final MyBeanHandler handler = new MyBeanHandler(configuration);
            parser.parse(translatedInputStream, handler);
        } catch (Exception e) {
            throw new CacheException("Error configuring from input stream. Initial cause was " + e.getMessage(), e);
        }
        configuration.setSource(ConfigurationSource.getConfigurationSource(inputStream));
        return configuration;
    }

    /**
     * Translates system properties which can be added as tokens to the config file using ${token} syntax.
     * <p/>
     * So, if the config file contains a character sequence "multicastGroupAddress=${multicastAddress}", and there is a system property
     * multicastAddress=230.0.0.12 then the translated sequence becomes "multicastGroupAddress=230.0.0.12"
     *
     * @param inputStream
     * @return a translated stream
     */
    private static InputStream translateSystemProperties(InputStream inputStream) throws IOException {

        StringBuilder sb = new StringBuilder();
        int c;
        Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        while ((c = reader.read()) != -1) {
            sb.append((char) c);
        }
        String configuration = sb.toString();

        Set<String> tokens = extractPropertyTokens(configuration);
        for (String token : tokens) {
            String trimmedToken = token.replaceAll("\\$\\{", "").replaceAll("\\}", "");
            String defaultValue = null;
            String propertyName = trimmedToken;
            int defDelimiter = propertyName.indexOf(":");
            if (defDelimiter >= 0) {
                defaultValue = propertyName.substring(defDelimiter + 1);
                propertyName = propertyName.substring(0, defDelimiter);
            }

            String property = System.getProperty(propertyName);
            if (property == null) {
                LOG.debug("Did not find a system property for the {} token specified in the configuration. Replacing with \"{}\"", token, defaultValue);
                property = defaultValue;
            }
            if (property != null) {
                //replaceAll by default clobbers \ and $
                String propertyWithQuotesProtected = Matcher.quoteReplacement(property);
                configuration = configuration.replaceAll("\\$\\{" + trimmedToken + "\\}", propertyWithQuotesProtected);

                LOG.debug("Used system property value of {} for the {} token specified in the configuration.", property, token);
            }
        }
        return new ByteArrayInputStream(configuration.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Extracts properties of the form ${...}
     *
     * @param sourceDocument the source document
     * @return a Set of properties. So, duplicates are only counted once.
     */
    static Set<String> extractPropertyTokens(String sourceDocument) {
        Set<String> propertyTokens = new HashSet<>();
        Pattern pattern = Pattern.compile("\\$\\{.+?\\}");
        Matcher matcher = pattern.matcher(sourceDocument);
        while (matcher.find()) {
            String token = matcher.group();
            propertyTokens.add(token);
        }
        return propertyTokens;
    }
}
