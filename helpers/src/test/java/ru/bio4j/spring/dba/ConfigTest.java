package ru.bio4j.spring.dba;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import ru.bio4j.spring.dba.configs.DbaConfiguration;

@TestConfiguration
@Import(DbaTestAutoConfiguration.class)
public class ConfigTest {
}
