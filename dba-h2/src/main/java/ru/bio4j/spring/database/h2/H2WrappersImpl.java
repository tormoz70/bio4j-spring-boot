package ru.bio4j.spring.database.h2;

import ru.bio4j.spring.commons.types.LogWrapper;
import ru.bio4j.spring.database.commons.WrappersBaseImpl;
public class H2WrappersImpl extends WrappersBaseImpl {
    private static final LogWrapper LOG = LogWrapper.getLogger(H2WrappersImpl.class);
    public H2WrappersImpl(String dbmsName) {
        super(dbmsName, new H2WrapperInterpreter());
        LOG.debug("Wrappers for \"{}\" database initialized.", dbmsName);
    }
}
