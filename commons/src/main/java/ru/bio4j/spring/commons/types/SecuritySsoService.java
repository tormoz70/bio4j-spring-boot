package ru.bio4j.spring.commons.types;

import ru.bio4j.spring.model.transport.BioQueryParams;
import ru.bio4j.spring.model.transport.SsoUser;
import ru.bio4j.spring.model.transport.User;

import javax.servlet.http.HttpServletRequest;

public interface SecuritySsoService {
    boolean checkPathIsOpened(HttpServletRequest request);
    boolean status(final BioQueryParams qprms);
    SsoUser restoreUser(final String stokenOrUsrUid);
    SsoUser getUser(final BioQueryParams qprms);
    SsoUser login(final BioQueryParams qprms);
    void logout(final BioQueryParams qprms);
    boolean loggedin(final BioQueryParams qprms);
}
