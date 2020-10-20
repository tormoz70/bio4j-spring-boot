package ru.bio4j.spring.helpers.web.defaults;

import org.springframework.beans.factory.annotation.Autowired;
import ru.bio4j.spring.commons.types.*;
import ru.bio4j.spring.model.transport.errors.BioError;
import ru.bio4j.spring.model.transport.BioQueryParams;
import ru.bio4j.spring.model.transport.User;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DefaultLoginProcessorImpl implements LoginProcessor {
    private static final LogWrapper LOG = LogWrapper.getLogger(DefaultLoginProcessorImpl.class);

    @Autowired
    private SecurityService defaultSecurityService;
    @Autowired(required = false)
    private SecurityService securityService;

    @Autowired
    private ErrorProcessor defaultErrorProcessor;
    @Autowired(required = false)
    private ErrorProcessor errorProcessor;

    private SecurityService getSecurityService() {
        return securityService != null ? securityService : defaultSecurityService;
    }

    private ErrorProcessor getErrorProcessor() {
        return errorProcessor != null ? errorProcessor : defaultErrorProcessor;
    }

    private BioQueryParams decodeQParams(final HttpServletRequest request) {
        return request instanceof WrappedRequest ? ((WrappedRequest)request).getBioQueryParams() : null;
    }

    private boolean doCheckPathIsOpened(final HttpServletRequest request) {
        return getSecurityService().checkPathIsOpened(request);
    }

    private void doOthers(final HttpServletRequest request, final HttpServletResponse response) {
        final WrappedRequest req = (WrappedRequest)request;
        final BioQueryParams qprms = req.getBioQueryParams();
        User user = getSecurityService().getUser(decodeQParams(request));
        req.setUser(user);
        ServletContextHolder.setCurrentUser(user);
    }

    public void process(final ServletRequest request, final ServletResponse response, final FilterChain chain) {
        final HttpServletRequest reqs = (HttpServletRequest) request;
        final HttpServletResponse resp = (HttpServletResponse) response;
        if(getSecurityService() != null) {
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json");
            try {
                if(!doCheckPathIsOpened(reqs))
                    doOthers(reqs, resp);
                chain.doFilter(reqs, resp);
            } catch (Exception e) {
                LOG.error(null, e);
                getErrorProcessor().doResponse(e, resp);
            }
        } else {
            try {
                chain.doFilter(reqs, resp);
            } catch (ServletException | IOException e) {
                throw BioError.wrap(e);
            }
        }
    }

}
