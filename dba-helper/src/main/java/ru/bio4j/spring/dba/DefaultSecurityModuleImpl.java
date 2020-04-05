package ru.bio4j.spring.dba;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.spring.commons.types.SecurityService;
import ru.bio4j.spring.model.transport.*;

import javax.servlet.http.HttpServletRequest;


public class DefaultSecurityModuleImpl implements SecurityService {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultSecurityModuleImpl.class);

    @Override
    public boolean checkPathIsOpened(HttpServletRequest request) { return true; }

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
