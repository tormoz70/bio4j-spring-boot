package ru.bio4j.spring.dba;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.bio4j.spring.dba.configs.DefaultConfiguration;
import ru.bio4j.spring.dba.configs.CacheConfiguration;
import ru.bio4j.spring.dba.configs.ChHelperConfiguration;
import ru.bio4j.spring.dba.configs.DateSerializerConfig;

@Configuration
@Import({ DefaultConfiguration.class, DateSerializerConfig.class, ChHelperConfiguration.class, CacheConfiguration.class })
public class DbaTestAutoConfiguration {

}
