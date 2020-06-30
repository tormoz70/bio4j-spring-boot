package ru.bio4j.spring.dba;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.bio4j.spring.commons.types.LogWrapper;
import ru.bio4j.spring.commons.types.SecurityService;
import ru.bio4j.spring.model.transport.*;

import javax.servlet.http.HttpServletRequest;


public class DefaultSecurityModuleImpl implements SecurityService {
    private static final LogWrapper LOG = LogWrapper.getLogger(DefaultSecurityModuleImpl.class);

    @Autowired(required = false)
    @Qualifier("override")
    private SecurityService overrideSecurityService;

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
    public boolean loggedin(final BioQueryParams qprms) {
        return false;
    }

    @Override
    public SecurityService getOverride() {
        return overrideSecurityService;
    }

    @Override
    public boolean status(final BioQueryParams qprms) {
        return false;
    }


}
