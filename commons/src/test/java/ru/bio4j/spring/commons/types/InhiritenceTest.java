package ru.bio4j.spring.commons.types;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import ru.bio4j.spring.model.transport.BioError;
import ru.bio4j.spring.model.transport.MetaType;
import ru.bio4j.spring.model.transport.Param;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class InhiritenceTest {


	@Test
	public void add1() {
        BioError.Login.Unauthorized unauthorized = new BioError.Login.Unauthorized();
        Exception exception = new Exception();

        Assert.assertTrue(unauthorized instanceof BioError.Login);
        Assert.assertFalse(exception instanceof BioError.Login);
    }

}
