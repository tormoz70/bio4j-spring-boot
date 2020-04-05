package ru.bio4j.spring.dba;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.spring.commons.types.ErrorProcessor;
import ru.bio4j.spring.model.transport.BioSQLApplicationError;
import ru.bio4j.spring.database.commons.DbUtils;
import ru.bio4j.spring.model.transport.BioError;
import ru.bio4j.spring.model.transport.LoginResult;
import ru.bio4j.spring.commons.utils.Jecksons;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.function.Function;

public class DefaultErrorProcessorImpl implements ErrorProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultErrorProcessorImpl.class);

    public static class ErrorResponseEntry {
        public int code;
        public String entry;
        public String type;
    }

    public static ErrorResponseEntry createEntry(int code, LoginResult entry, String type) {
        ErrorResponseEntry rslt = new ErrorResponseEntry();
        rslt.code = code;
        rslt.entry = Jecksons.getInstance().encode(entry);
        rslt.type = type;
        return rslt;
    }

    public static ErrorResponseEntry process(Throwable exception, Function<Throwable, ErrorResponseEntry> builder) {
        try {
            if(builder != null)
                return builder.apply(exception);

            BioError.Login loginError = exception instanceof BioError.Login ? (BioError.Login) exception : null;
            if (loginError != null) {
                LoginResult result = LoginResult.Builder.error(loginError);
                return createEntry(Response.Status.UNAUTHORIZED.getStatusCode(), result, MediaType.APPLICATION_JSON);
            }
            BioError errorBean = exception instanceof BioError ? (BioError) exception : null;
            if (errorBean == null) {
                BioSQLApplicationError storedProcAppError = DbUtils.getInstance().extractStoredProcAppErrorMessage(errorBean);
                if (storedProcAppError != null)
                    errorBean = new BioError(Response.Status.BAD_REQUEST.getStatusCode(), storedProcAppError.getMessage());
            }
            if (errorBean != null) {
                LOG.error(null, exception);
                LoginResult result = LoginResult.Builder.error(errorBean);
                return createEntry(errorBean.getErrorCode(), result, MediaType.APPLICATION_JSON);
            } else if (exception instanceof javax.ws.rs.NotAllowedException) {
                LOG.error(null, exception);
                LoginResult result = LoginResult.Builder.error(new BioError.MethodNotAllowed());
                return createEntry(Response.Status.METHOD_NOT_ALLOWED.getStatusCode(), result, MediaType.APPLICATION_JSON);
            } else if (exception instanceof javax.ws.rs.NotFoundException) {
                LOG.error(null, exception);
                LoginResult result = LoginResult.Builder.error(new BioError.MethodNotImplemented());
                return createEntry(Response.Status.NOT_IMPLEMENTED.getStatusCode(), result, MediaType.APPLICATION_JSON);
            }
            LOG.error(null, exception);
            LoginResult result = LoginResult.Builder.error(new BioError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Неизвестная ошибка на сервере"));
            return createEntry(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), result, MediaType.APPLICATION_JSON);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Response process(Throwable exception) {
        ErrorResponseEntry entry = process(exception, null);
        return Response.status(entry.code).entity(entry.entry).type(entry.type).build();
    }

    public void doResponse(Throwable exception, ServletResponse response) {
        try {
            ErrorResponseEntry rspEntry = process(exception, null);
            ((HttpServletResponse)response).setStatus(rspEntry.code);
            response.setContentType(rspEntry.type);
            response.setCharacterEncoding("UTF-8");
//            response.getOutputStream().write(rspEntry.entry.getBytes(Charset.forName("UTF-8")));;
            response.getWriter().write(rspEntry.entry);
            response.flushBuffer();
            //TODO Ошибки, которые возникают при выполнении SQL(BioSQLException) не доходят до клиента.... Надо с этим разобраться
        } catch(Exception e) {
            throw BioError.wrap(e);
        }
    }

}
