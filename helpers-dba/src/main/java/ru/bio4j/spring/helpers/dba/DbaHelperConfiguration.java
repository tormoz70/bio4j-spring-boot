package ru.bio4j.spring.helpers.dba;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.bio4j.spring.commons.types.ExcelBuilder;

@Configuration
public class DbaHelperConfiguration {

    @Bean
    public DbaHelperFactory dbaHelperFactory (@Autowired(required = false) ExcelBuilder excelBuilder) {
        return new DbaHelperFactory(excelBuilder);
    }

    @Bean
    public ExcelBuilder excelBuilder() {
        return new ExcelBuilderImpl();
    }

}
