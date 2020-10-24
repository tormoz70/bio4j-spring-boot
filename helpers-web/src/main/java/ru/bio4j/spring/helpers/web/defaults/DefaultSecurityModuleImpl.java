package ru.bio4j.spring.helpers.web.defaults;

import ru.bio4j.spring.commons.types.LogWrapper;
import ru.bio4j.spring.commons.types.SecurityService;
import ru.bio4j.spring.model.transport.*;

import javax.servlet.http.HttpServletRequest;


public class DefaultSecurityModuleImpl implements SecurityService {
    private static final LogWrapper LOG = LogWrapper.getLogger(DefaultSecurityModuleImpl.class);

    @Override
    public boolean checkPathIsOpened(HttpServletRequest request) { return true; }

    @Override
    public User login(final BioQueryParams qprms) {
        return null;
    }

    @Override
    public User refresh(BioQueryParams qprms) {
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
    public void storeParam(BioQueryParams qprms, String paramCode, String paramName, Object paramValue) {

    }

    @Override
    public <T> T restoreParam(BioQueryParams qprms, String paramCode, Class<T> paramType, T defaultValue) {
        return null;
    }

    @Override
    public void storePushtoken(BioQueryParams qprms, String pushToken) {

    }

    @Override
    public void pushEnable(BioQueryParams qprms, boolean pushEnabled) {

    }

    @Override
    public boolean status(final BioQueryParams qprms) {
        return false;
    }

}
