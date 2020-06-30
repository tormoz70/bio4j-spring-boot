package ru.bio4j.spring.dba;

//import ru.bio4j.ng.commons.utils.Jsons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.bio4j.spring.commons.types.*;
import ru.bio4j.spring.model.transport.BioError;
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
    @Qualifier("default")
    private SecurityService defaultSecurityService;

    @Autowired
    @Qualifier("default")
    private ErrorProcessor defaultErrorProcessor;

    private SecurityService getSecurityService() {
        return defaultSecurityService.getOverride() != null ? defaultSecurityService.getOverride() : defaultSecurityService;
    }

    private ErrorProcessor getErrorProcessor() {
        return defaultErrorProcessor.getOverride() != null ? defaultErrorProcessor.getOverride() : defaultErrorProcessor;
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
