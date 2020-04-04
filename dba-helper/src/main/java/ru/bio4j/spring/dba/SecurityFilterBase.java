package ru.bio4j.spring.dba;

import ru.bio4j.spring.commons.types.ApplicationContextProvider;
import ru.bio4j.spring.commons.types.LogWrapper;
import ru.bio4j.spring.commons.types.LoginProcessor;
import ru.bio4j.spring.commons.types.WrappedRequest;
import ru.bio4j.spring.commons.utils.Strings;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class SecurityFilterBase {
    LogWrapper LOG = LogWrapper.getLogger(SecurityFilterBase.class);

    private boolean bioDebug = false;
    private String errorPage;
    private LoginProcessor loginProcessor;

    public void init(FilterConfig filterConfig) throws ServletException {
        if (filterConfig != null) {
            //
        }
        loginProcessor = (LoginProcessor) ApplicationContextProvider.getApplicationContext().getBean("loginProcessor");
    }

    private static String[] AVAMETHODS = {"GET", "POST", "PUT", "DELETE", "PATCH", "HEAD"};

    public void doSequrityFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        //GET,POST,PUT,DELETE,PATCH,HEAD
        if(Arrays.asList(AVAMETHODS).contains(((HttpServletRequest)request).getMethod())) {
            if(loginProcessor != null)
                loginProcessor.process(request, response, chain);
            else
                chain.doFilter(request, response);
        }
    }

    public WrappedRequest prepareRequest(final ServletRequest request) throws Exception {
        WrappedRequest rereq;
        if (request instanceof WrappedRequest)
            rereq = (WrappedRequest)request;
        else
            rereq = new WrappedRequest((HttpServletRequest)request);
        rereq.putHeader("Access-Control-Allow-Origin", "*");
        rereq.putHeader("Access-Control-Allow-Methods", Strings.combineArray(AVAMETHODS, ","));
        return rereq;
    }

    public void prepareResponse(final ServletResponse response) {
        ((HttpServletResponse) response).setHeader("Access-Control-Allow-Origin", "*");
        ((HttpServletResponse) response).setHeader("Access-Control-Allow-Credentials", "true");
        ((HttpServletResponse) response).setHeader("Access-Control-Allow-Methods", Strings.combineArray(AVAMETHODS, ","));
        ((HttpServletResponse) response).setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Credentials, Origin, X-Requested-With, Content-Type, Accept, X-SToken, X-Pagination-Current-Page, X-Pagination-Per-Page, Authorization");
        ((HttpServletResponse) response).setHeader("Access-Control-Expose-Headers", "Content-Disposition, X-Suggested-Filename");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            WrappedRequest rereq = prepareRequest(request);
            prepareResponse(response);
            doSequrityFilter(rereq, response, chain);
        } catch (IOException ex) {
            LOG.error(null, ex);
            throw ex;
        } catch (ServletException ex) {
            LOG.error(null, ex);
            throw ex;
        } catch (Exception ex) {
            LOG.error(null, ex);
            throw new ServletException(ex);
        }
    }

    public void destroy() {
        LOG.debug("Trying destroy");
    }
}
