package ru.bio4j.ng.database.oracle.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.database.commons.WrappersBaseImpl;
public class OraWrappersImpl extends WrappersBaseImpl {
    private static final Logger LOG = LoggerFactory.getLogger(OraWrappersImpl.class);
    public OraWrappersImpl(String dbmsName) {
        super(dbmsName, new OraWrapperInterpreter());
        LOG.debug("Wrappers for \"{}\" database initialized.", dbmsName);
    }
}
