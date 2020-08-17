package ru.bio4j.spring.commons.types;

import ru.bio4j.spring.model.transport.BioQueryParams;
import ru.bio4j.spring.model.transport.SsoUser;

public interface Sso2Client {
    Boolean status(final BioQueryParams qprms);
    SsoUser login(final BioQueryParams qprms);
    SsoUser refresh(final BioQueryParams qprms);
    SsoUser restoreUser(final String stokenOrUsrUid, final String remoteIP, final String remoteClient);
    SsoUser curUser(final BioQueryParams qprms);
    void logout(final BioQueryParams qprms);
    Boolean loggedin(final BioQueryParams qprms);
}
