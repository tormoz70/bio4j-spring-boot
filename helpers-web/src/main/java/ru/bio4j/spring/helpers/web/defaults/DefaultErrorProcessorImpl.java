package ru.bio4j.spring.helpers.web.defaults;

import ru.bio4j.spring.commons.types.ErrorProcessor;
import ru.bio4j.spring.commons.utils.Jecksons;
import ru.bio4j.spring.commons.utils.Utl;
import ru.bio4j.spring.database.commons.DbUtils;
import ru.bio4j.spring.model.transport.errors.BioError;
import ru.bio4j.spring.model.transport.errors.BioSQLApplicationError;
import ru.bio4j.spring.model.transport.LoginResult;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.function.Function;

public class DefaultErrorProcessorImpl implements ErrorProcessor {

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

    public static ErrorResponseEntry process(Exception exception, Function<Exception, ErrorResponseEntry> builder) {
        try {
            if (builder != null)
                return builder.apply(exception);
            boolean isLoginError = exception instanceof BioError.Login;
            BioError.Login loginError = isLoginError ? (BioError.Login) exception : null;
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
                LoginResult result = LoginResult.Builder.error(errorBean);
                return createEntry(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), result, MediaType.APPLICATION_JSON);
            } else if (exception instanceof javax.ws.rs.NotAllowedException) {
                LoginResult result = LoginResult.Builder.error(new BioError.Login.MethodNotAllowed());
                return createEntry(Response.Status.METHOD_NOT_ALLOWED.getStatusCode(), result, MediaType.APPLICATION_JSON);
            } else if (exception instanceof javax.ws.rs.NotFoundException) {
                LoginResult result = LoginResult.Builder.error(exception);
                return createEntry(Response.Status.NOT_FOUND.getStatusCode(), result, MediaType.APPLICATION_JSON);
            }
            LoginResult result = LoginResult.Builder.error(new BioError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Неизвестная ошибка на сервере"));
            return createEntry(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), result, MediaType.APPLICATION_JSON);
        } catch (Exception e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static Response process(Exception exception) {
        ErrorResponseEntry entry = process(exception, null);
        return Response.status(entry.code).entity(entry.entry).type(entry.type).build();
    }

    public void doResponse(Exception exception, ServletResponse response) {
        try {
            ErrorResponseEntry rspEntry = process(exception, null);
            ((HttpServletResponse) response).setStatus(rspEntry.code);
            response.setContentType(rspEntry.type);
            response.getWriter().print(rspEntry.entry);
            response.flushBuffer();
        } catch (Exception e) {
            throw BioError.wrap(e);
        }
    }

}
