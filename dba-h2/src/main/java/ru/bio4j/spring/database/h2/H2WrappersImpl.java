package ru.bio4j.spring.database.h2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.spring.database.commons.WrappersBaseImpl;
public class H2WrappersImpl extends WrappersBaseImpl {
    private static final Logger LOG = LoggerFactory.getLogger(H2WrappersImpl.class);
    public H2WrappersImpl(String dbmsName) {
        super(dbmsName, new H2WrapperInterpreter());
        LOG.debug("Wrappers for \"{}\" database initialized.", dbmsName);
    }
}
