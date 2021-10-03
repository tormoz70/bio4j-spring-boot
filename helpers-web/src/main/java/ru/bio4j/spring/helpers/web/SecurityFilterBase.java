package ru.bio4j.spring.helpers.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.WebApplicationContextUtils;
import ru.bio4j.spring.commons.types.LoginProcessor;
import ru.bio4j.spring.commons.types.WrappedRequest;
import ru.bio4j.spring.commons.utils.Strings;
import ru.bio4j.spring.model.transport.errors.BioError;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Configuration
public class SecurityFilterBase {
    private final Logger LOG = LoggerFactory.getLogger(SecurityFilterBase.class);

    private LoginProcessor loginProcessor;
    private boolean disableAnonymouse;

    public void init(FilterConfig filterConfig, boolean disableAnonymouse) throws ServletException {
        this.disableAnonymouse = disableAnonymouse;
        if (filterConfig != null) {
            //
        }

        ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());
        LoginProcessor defaultLoginProcessor = (LoginProcessor) ctx.getBean("defaultLoginProcessor");
        LoginProcessor overrideLoginProcessor = null;
        try {
            overrideLoginProcessor = (LoginProcessor) ctx.getBean("loginProcessor");
        } catch (NoSuchBeanDefinitionException ex) {
            LOG.debug("No override for login processor found. Using default login processor.");
        }
        this.loginProcessor = overrideLoginProcessor != null ? overrideLoginProcessor : defaultLoginProcessor;
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
        if(disableAnonymouse && rereq.getBioQueryParams() != null && Strings.compare(rereq.getBioQueryParams().stoken, "anonymouse", true)) {
            rereq.getBioQueryParams().stoken = null;
        }
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
        } catch (IOException | ServletException ex) {
            LOG.error(null, ex);
            throw ex;
        } catch (Exception ex) {
            LOG.error(null, ex);
            throw BioError.wrap(ex);
        }
    }

    public void destroy() {
        LOG.debug("Trying destroy");
    }
}
