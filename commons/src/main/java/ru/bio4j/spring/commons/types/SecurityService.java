package ru.bio4j.spring.commons.types;

import ru.bio4j.spring.model.transport.BioQueryParams;
import ru.bio4j.spring.model.transport.User;
import ru.bio4j.spring.model.transport.errors.BioError;

import javax.servlet.http.HttpServletRequest;

public interface SecurityService {
    boolean checkPathIsOpened(HttpServletRequest request);
    boolean status(final BioQueryParams qprms);
    User restoreUser(final String stokenOrUsrUid);
    User getUser(final BioQueryParams qprms);
    User login(final BioQueryParams qprms);
    User refresh(final BioQueryParams qprms);
    void logout(final BioQueryParams qprms);
    boolean loggedin(final BioQueryParams qprms);
    void storeParam(final BioQueryParams qprms, final String paramCode, final String paramName, final Object paramValue);
    <T> T restoreParam(final BioQueryParams qprms, final String paramCode, Class<T> paramType, T defaultValue);
    void storePushtoken(final BioQueryParams qprms, final String pushToken);
    void pushEnable(final BioQueryParams qprms, final boolean pushEnabled);
}
