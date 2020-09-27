package ru.bio4j.spring.commons.types;

import org.junit.Assert;
import org.junit.Test;
import ru.bio4j.spring.model.transport.errors.BioError;

public class InhiritenceTest {


	@Test
	public void add1() {
        BioError.Login.Unauthorized unauthorized = new BioError.Login.Unauthorized();
        Exception exception = new Exception();

        Assert.assertTrue(unauthorized instanceof BioError.Login);
        Assert.assertFalse(exception instanceof BioError.Login);
    }

}
