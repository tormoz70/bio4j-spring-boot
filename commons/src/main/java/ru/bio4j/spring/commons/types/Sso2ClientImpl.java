package ru.bio4j.spring.commons.types;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import ru.bio4j.spring.commons.utils.Jecksons;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.commons.utils.Utl;
import ru.bio4j.spring.model.Sso2ClientProperties;
import ru.bio4j.spring.model.transport.*;
import ru.bio4j.spring.model.transport.errors.BioError;

import java.io.IOException;

import static ru.bio4j.spring.commons.utils.Strings.isNullOrEmpty;

public class Sso2ClientImpl implements Sso2Client {
    private static final LogWrapper LOG = LogWrapper.getLogger(Sso2ClientImpl.class);

    private static final String SSO2SERVICE_URL_PARAM = "${ss2client.sso2.service.url}";

    private final String ssoServiceUrl;
    private final HttpSimpleClient httpSimpleClient;
    private final Sso2ClientProperties sso2ClientProperties;

    public Sso2ClientImpl(final Sso2ClientProperties sso2ClientProperties) {
        this.sso2ClientProperties = sso2ClientProperties;
        httpSimpleClient = new HttpSimpleClient();
        ssoServiceUrl = Strings.compare(sso2ClientProperties.getSso2ServiceUrl(), SSO2SERVICE_URL_PARAM, true) ? null : sso2ClientProperties.getSso2ServiceUrl();
    }

    private static SsoUser extractUserFromRsp(LoginResult lrsp) {
        SsoUser rslt = new SsoUser();
        Utl.applyValuesToBeanFromBean(lrsp.getUser(), rslt);
        return rslt;
    }

    private static SsoUser extractUserFromRsp(HttpResponse response) {
        LoginResult lrsp = restoreResponseObject(response);
        if(lrsp != null) {
            if (lrsp.isSuccess() && lrsp.getUser() != null)
                return extractUserFromRsp(lrsp);
            if (lrsp.isSuccess() && lrsp.getUser() == null)
                throw new BioError(6021, "Unexpected error on sso server!");
            if (!lrsp.isSuccess() && lrsp.getException() != null)
                throw BioError.wrap(lrsp.getException());
        }
        throw new BioError(6022, "Unexpected error on sso server!");
    }

    private static LoginResult restoreResponseObject(HttpResponse response) {
        HttpEntity entity = response.getEntity();
        String responseString = null;
        try {
            responseString = EntityUtils.toString(entity, "UTF-8");
        } catch(IOException e) {
            throw BioError.wrap(e);
        }
        return Jecksons.getInstance().decode(responseString, LoginResult.class);
    }

    @Override
    public SsoUser login(final BioQueryParams qprms) {
        final String apptoken = qprms.apptoken;
        final String login = qprms.login;
        final String remoteIP = qprms.remoteIP;
        final String remoteClient = qprms.remoteClient;
        if (isNullOrEmpty(login))
            throw new BioError.Login.Unauthorized();
        String reqstJson = String.format("{\"apptoken\":\"%s\", \"login\":\"%s\"}", apptoken, login);

        String requestUrl = String.format("%s/%s", ssoServiceUrl, SecurityRequestType.login.path());
        HttpResponse response = httpSimpleClient.requestPost(requestUrl, null, reqstJson, remoteIP, remoteClient);
        return extractUserFromRsp(response);
    }

    @Override
    public SsoUser refresh(BioQueryParams qprms) {
        final String remoteIP = qprms.remoteIP;
        final String remoteClient = qprms.remoteClient;
        final String jsonData = qprms.jsonData;

        String requestUrl = String.format("%s/%s", ssoServiceUrl, SecurityRequestType.refresh.path());
        HttpResponse response = httpSimpleClient.requestPost(requestUrl, qprms.stoken , jsonData, remoteIP, remoteClient);
        return extractUserFromRsp(response);
    }

    @Override
    public SsoUser restoreUser(final String stokenOrUsrUid, final String remoteIP, final String remoteClient) {
        String requestUrl = String.format("%s/%s/%s", ssoServiceUrl, SecurityRequestType.restoreUser.path(), stokenOrUsrUid);
        HttpResponse response = httpSimpleClient.requestGet(requestUrl, stokenOrUsrUid, remoteIP, remoteClient);
        return extractUserFromRsp(response);
    }

    @Override
    public SsoUser curUser(final BioQueryParams qprms) {
        final String stoken = qprms.stoken;
        final String remoteIP = qprms.remoteIP;
        final String remoteClient = qprms.remoteClient;
        String requestUrl = String.format("%s/%s", ssoServiceUrl, SecurityRequestType.curuser.path());
        HttpResponse response = httpSimpleClient.requestGet(requestUrl, stoken, remoteIP, remoteClient);
        return extractUserFromRsp(response);
    }

    @Override
    public void logout(final BioQueryParams qprms) {
        final String remoteIP = qprms.remoteIP;
        final String remoteClient = qprms.remoteClient;
        final String stoken = qprms.stoken;
        String requestUrl = String.format("%s/$s", ssoServiceUrl, SecurityRequestType.logout.path());
        HttpResponse response = httpSimpleClient.requestPost(requestUrl, stoken, null, remoteIP, remoteClient);
        LoginResult lrsp = restoreResponseObject(response);
        if(lrsp != null) {
            if (!lrsp.isSuccess() && lrsp.getException() != null)
                throw BioError.wrap(lrsp.getException());
        }
        throw new BioError("Unexpected error with code 22!");
    }

    public Boolean loggedin(final BioQueryParams qprms) {
        final String stoken = qprms.stoken;
        final String remoteIP = qprms.remoteIP;
        final String remoteClient = qprms.remoteClient;
        String requestUrl = String.format("%s/%s", ssoServiceUrl, SecurityRequestType.loggedin.path());
        HttpResponse response = httpSimpleClient.requestGet(requestUrl, stoken, remoteIP, remoteClient);
        LoginResult lrsp = restoreResponseObject(response);
        if(lrsp != null) {
            if (lrsp.isSuccess())
                return true;
            if (!lrsp.isSuccess())
                throw BioError.wrap(lrsp.getException());
        }
        throw new BioError("Unexpected error with code 22!");
    }

    public Boolean status(final BioQueryParams qprms) {
        return loggedin(qprms);
    }


}
