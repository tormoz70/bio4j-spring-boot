package ru.bio4j.spring.dba;

import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.bio4j.spring.model.Sso2ClientProperties;

@ConfigurationProperties(prefix= "ss2client")
public class TestSso2ClientProperties extends Sso2ClientProperties {
}
