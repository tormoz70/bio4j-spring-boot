package ru.bio4j.spring.model.transport.errors;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

public class BioError extends RuntimeException {

    protected int errorCode = 6000;

    public BioError() {
        super();
    }

    public BioError(int code) {
        super();
        errorCode = code;
    }

    public BioError(int code, String message) {
        super(message);
        errorCode = code;
    }
    public BioError(String message) {
        super(message);
    }
    public BioError(int code, String message, Exception e) {
        super(message, e);
        errorCode = code;
    }
    public BioError(String message, Exception e) {
        super(message, e);
    }

    public BioError(int code, Exception e) {
        super(e);
        errorCode = code;
    }
    public BioError(Exception e) {
        super(e);
    }

    public static BioError wrap(Exception e) {
        if(e != null) {
            if (e instanceof BioError)
                return (BioError) e;
            return new BioError(e);
        }
        return null;
    }

    public int getErrorCode() {
        return errorCode;
    }

    //********************************************************************************


    @JsonTypeInfo(use = Id.CLASS, property = "@class")
    public static class BadRequest extends BioError {

        public BadRequest() {
            super(6400, "Не правильный запрос!");
        }
        public BadRequest(String message) {
            super(6400, message);
        }
    }

    @JsonTypeInfo(use = Id.CLASS, property = "@class")
    public static class MethodNotImplemented extends BioError {
        public MethodNotImplemented() {
            super(6501, "Метод не реализован!");
        }
    }

    @JsonTypeInfo(use = Id.CLASS, property = "@class")
    public static abstract class Login extends BioError {
        public Login(int code) {
            super(code);
        }
        public Login(int code, String message) {
            super(code, message);
        }

        @JsonTypeInfo(use = Id.CLASS, property = "@class")
        public static class Unauthorized extends BioError.Login {
            public Unauthorized() {
                super(6401, "Неверное имя или пароль пользователя!");
            }
        }

        @JsonTypeInfo(use = Id.CLASS, property = "@class")
        public static class Forbidden extends BioError.Login {
            public Forbidden() {
                super(6403, "Доступ запрещен!");
            }
        }

        @JsonTypeInfo(use = Id.CLASS, property = "@class")
        public static class MethodNotAllowed extends BioError.Login {
            public MethodNotAllowed() {
                super(6405, "Метод недоступен!");
            }
        }

        @JsonTypeInfo(use = Id.CLASS, property = "@class")
        public static class BadSToken extends BioError.Login {
            public BadSToken() {
                super(6406, "Токен безопасности отсутствует!");
            }
        }

        @JsonTypeInfo(use = Id.CLASS, property = "@class")
        public static class BadAppToken extends BioError.Login {
            public BadAppToken() {
                super(6407, "Токен приложения отсутствует!");
            }
        }
    }

    @JsonTypeInfo(use = Id.CLASS, property = "@class")
    public static class BadIODescriptor extends BioError {
        public BadIODescriptor() {
            super();
        }
        public BadIODescriptor(String message) {
            super(message);
        }
    }

    public static BioError build(int code, String message) {
        switch (code) {
            case 6400: return new BadRequest();
            case 6401: return new Login.Unauthorized();
            case 6403: return new Login.Forbidden();
            case 6405: return new Login.MethodNotAllowed();
            case 6406: return new Login.BadSToken();
            case 6407: return new Login.BadAppToken();
            case 6501: return new MethodNotImplemented();
            default: return new BioError(code, message);
        }
    }

}
