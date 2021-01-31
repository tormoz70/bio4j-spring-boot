package ru.bio4j.spring.database.clickhouse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.spring.database.commons.WrappersBaseImpl;
public class ChWrappersImpl extends WrappersBaseImpl {
    private static final Logger LOG = LoggerFactory.getLogger(ChWrappersImpl.class);
    public ChWrappersImpl(String dbmsName) {
        super(dbmsName, new ChWrapperInterpreter());
        LOG.debug("Wrappers for \"{}\" database initialized.", dbmsName);
    }
}
