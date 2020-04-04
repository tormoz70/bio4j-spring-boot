package ru.bio4j.spring.dba;

//import ru.bio4j.ng.commons.utils.Jsons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.bio4j.spring.commons.types.*;
import ru.bio4j.spring.commons.utils.Jecksons;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.commons.utils.Utl;
import ru.bio4j.spring.model.transport.BioError;
import ru.bio4j.spring.model.transport.BioQueryParams;
import ru.bio4j.spring.model.transport.LoginResult;
import ru.bio4j.spring.model.transport.User;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DefaultLoginProcessorImpl implements LoginProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultLoginProcessorImpl.class);

    @Autowired
    private SecurityService securityService;
    @Autowired
    private ErrorProcessor errorProcessor;

    private BioQueryParams decodeQParams(final HttpServletRequest request) {
        return request instanceof WrappedRequest ? ((WrappedRequest)request).getBioQueryParams() : null;
    }

    private void _doGetUser(final HttpServletRequest request, final HttpServletResponse response) {
        User user = securityService.getUser(decodeQParams(request));
        LoginResult result = LoginResult.Builder.success(user);
        try {
            response.getWriter().append(Jecksons.getInstance().encode(result));
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    private void _doLogin(final HttpServletRequest request, final HttpServletResponse response) {
        User user = securityService.login(decodeQParams(request));
        LoginResult result = LoginResult.Builder.success(user);
        try {
            response.getWriter().append(Jecksons.getInstance().encode(result));
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    private void _doLogoff(final HttpServletRequest request, final HttpServletResponse response) {
        securityService.logout(decodeQParams(request));
        LoginResult result = LoginResult.Builder.success();
        try {
            response.getWriter().append(Jecksons.getInstance().encode(result));
        } catch (IOException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    private void doOthers(final HttpServletRequest request, final HttpServletResponse response) {
        final WrappedRequest req = (WrappedRequest)request;
        final BioQueryParams qprms = req.getBioQueryParams();
        User user;
        if (!Strings.isNullOrEmpty(qprms.login))
            user = securityService.login(decodeQParams(request));
        else
            user = securityService.getUser(decodeQParams(request));
        req.setUser(user);
        ServletContextHolder.setCurrentUser(user);
    }

    public void process(final ServletRequest request, final ServletResponse response, final FilterChain chain) {
        if(securityService != null) {
            final HttpServletRequest reqs = (HttpServletRequest) request;
            final HttpServletResponse resp = (HttpServletResponse) response;
            resp.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            try {
                String pathInfo = reqs.getPathInfo();
                if (!Strings.isNullOrEmpty(pathInfo) && Strings.compare(pathInfo, "/login", false)) {
                    _doLogin(reqs, resp);
                } else if (!Strings.isNullOrEmpty(pathInfo) && Strings.compare(pathInfo, "/curusr", false)) {
                    _doGetUser(reqs, resp);
                } else if (!Strings.isNullOrEmpty(pathInfo) && Strings.compare(pathInfo, "/logoff", false)) {
                    _doLogoff(reqs, resp);
                } else {
                    doOthers(reqs, resp);
                    chain.doFilter(request, resp);
                }
            } catch (Exception e) {
                LOG.error(null, e);
                errorProcessor.doResponse(e, resp);
            }
        } else {
            try {
                chain.doFilter(request, response);
            } catch (ServletException | IOException e) {
                throw BioError.wrap(e);
            }
        }
    }

}
