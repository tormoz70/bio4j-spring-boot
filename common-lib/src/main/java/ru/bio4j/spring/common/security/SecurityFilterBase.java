package ru.bio4j.spring.common.security;

import ru.bio4j.spring.common.LogWrapper;
import ru.bio4j.spring.common.stringHelper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class SecurityFilterBase {
    LogWrapper LOG = LogWrapper.getLogger(SecurityFilterBase.class);

    private boolean bioDebug = false;
    private String errorPage;

    public void init(FilterConfig filterConfig) throws ServletException {
        if (filterConfig != null) {
            //
        }
    }

    private static String[] AVAMETHODS = {"GET", "POST", "PUT", "DELETE", "PATCH", "HEAD"};

    public void doSequrityFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        //GET,POST,PUT,DELETE,PATCH,HEAD
        if(Arrays.asList(AVAMETHODS).contains(((HttpServletRequest)request).getMethod())) {
//            if(RestHelper.loginProcessor() != null)
//                RestHelper.loginProcessor().process(request, response, chain);
//            else
            chain.doFilter(request, response);
        }
    }

//    public WrappedRequest prepareRequest(final ServletRequest request) throws Exception {
//        WrappedRequest rereq = null;
//        if (request instanceof WrappedRequest)
//            rereq = (WrappedRequest)request;
//        else
//            rereq = new WrappedRequest((HttpServletRequest)request);
//        rereq.putHeader("Access-Control-Allow-Origin", "*");
//        rereq.putHeader("Access-Control-Allow-Methods", Strings.combineArray(AVAMETHODS, ","));
//        return rereq;
//    }

    public void prepareResponse(final ServletResponse response) {
        ((HttpServletResponse) response).setHeader("Access-Control-Allow-Origin", "*");
        ((HttpServletResponse) response).setHeader("Access-Control-Allow-Credentials", "true");
        ((HttpServletResponse) response).setHeader("Access-Control-Allow-Methods", stringHelper.combineArray(AVAMETHODS, ","));
        ((HttpServletResponse) response).setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Credentials, Origin, X-Requested-With, Content-Type, Accept, X-SToken, X-Pagination-Current-Page, X-Pagination-Per-Page, Authorization");
        ((HttpServletResponse) response).setHeader("Access-Control-Expose-Headers", "Content-Disposition, X-Suggested-Filename");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
//            WrappedRequest rereq = prepareRequest(request);
            ServletRequest rereq = request;
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
//            prepareResponse(response);
//            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public void destroy() {
        LOG.debug("Trying destroy");
    }
}
