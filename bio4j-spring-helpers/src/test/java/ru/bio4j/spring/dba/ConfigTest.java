package ru.bio4j.spring.dba;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

@TestConfiguration
@Import(DbaAutoConfiguration.class)
public class ConfigTest {
}
