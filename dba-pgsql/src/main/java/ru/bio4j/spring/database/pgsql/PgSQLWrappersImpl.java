package ru.bio4j.spring.database.pgsql;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.spring.commons.types.LogWrapper;
import ru.bio4j.spring.database.commons.WrappersBaseImpl;

public class PgSQLWrappersImpl extends WrappersBaseImpl {
    private static final LogWrapper LOG = LogWrapper.getLogger(PgSQLWrappersImpl.class);
    public PgSQLWrappersImpl(String dbmsName) {
        super(dbmsName, new PgSQLWrapperInterpreter());
        LOG.debug("Wrappers for \"{}\" database initialized.", dbmsName);
    }
}