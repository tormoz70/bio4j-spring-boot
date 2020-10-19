package ru.bio4j.spring.commons.types;

import ru.bio4j.spring.model.transport.BioQueryParams;
import ru.bio4j.spring.model.transport.User;

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
}
