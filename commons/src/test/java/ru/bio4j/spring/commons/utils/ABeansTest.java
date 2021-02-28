package ru.bio4j.spring.commons.utils;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.spring.commons.converter.DateTimeParser;
import ru.bio4j.spring.model.transport.ABean;
import ru.bio4j.spring.model.transport.Param;
import ru.bio4j.spring.model.transport.Prop;
import ru.bio4j.spring.model.transport.jstore.Sort;
import ru.bio4j.spring.model.transport.jstore.filter.Filter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.bio4j.spring.commons.utils.ABeans.*;
import static ru.bio4j.spring.commons.utils.Lists.arrayCopyOf;
import static ru.bio4j.spring.commons.utils.Reflex.findAnnotation;
import static ru.bio4j.spring.commons.utils.Reflex.typesIsSame;

public class ABeansTest {
    private final static Logger LOG = LoggerFactory.getLogger(ABeansTest.class);


    @Test
    public void testApplyBeanProps2BeanProps() {
        TTimer timer = new TTimer();
        timer.setDatetime("2021-03-02T18:45:00");
        TTimer2 timer2 = new TTimer2();
        ABeans.applyBeanProps2BeanProps(timer, timer2, true);
        Assert.assertEquals(LocalDateTime.of(2021, 03, 02, 18, 45), timer2.getDatetime());
    }

}

