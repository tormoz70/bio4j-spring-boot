package ru.bio4j.spring.dba;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.bio4j.spring.commons.types.Paramus;
import ru.bio4j.spring.commons.types.SecurityService;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.database.api.BioSQLException;
import ru.bio4j.spring.model.transport.*;

import java.util.ArrayList;
import java.util.List;

import static ru.bio4j.spring.commons.utils.Strings.isNullOrEmpty;


@Component
public class DefaultSecurityModuleImpl implements SecurityService {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultSecurityModuleImpl.class);

    @Override
    public User login(final BioQueryParams qprms) {
        return null;
    }

    @Override
    public User restoreUser(String stokenOrUsrUid) {
        return null;
    }

    @Override
    public User getUser(final BioQueryParams qprms) {
        return null;
    }

    @Override
    public void logout(final BioQueryParams qprms) {
    }

    @Override
    public Boolean loggedin(final BioQueryParams qprms) {
        return false;
    }



}
