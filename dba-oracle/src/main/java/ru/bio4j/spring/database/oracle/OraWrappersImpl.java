package ru.bio4j.spring.database.oracle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.spring.commons.types.LogWrapper;
import ru.bio4j.spring.database.commons.WrappersBaseImpl;
public class OraWrappersImpl extends WrappersBaseImpl {
    private static final LogWrapper LOG = LogWrapper.getLogger(OraWrappersImpl.class);
    public OraWrappersImpl(String dbmsName) {
        super(dbmsName, new OraWrapperInterpreter());
        LOG.debug("Wrappers for \"{}\" database initialized.", dbmsName);
    }
}
