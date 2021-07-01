package ru.bio4j.spring.model.transport;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

public class LoginResult {
    private boolean success;
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
    private Exception exception;
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
    private SsoUser user;

    public boolean isSuccess() {
        return success;
    }

    public SsoUser getUser() {
        return user;
    }

    public Exception getException() {
        return exception;
    }

    public static class Builder {

        public static LoginResult success() {
            LoginResult rslt = new LoginResult();
            rslt.success = true;
            return rslt;
        }

        public static LoginResult success(SsoUser user) {
            LoginResult rslt = new LoginResult();
            rslt.success = true;
            rslt.user = user;
            return rslt;
        }

        public static LoginResult error(Exception error) {
            LoginResult rslt = new LoginResult();
            rslt.success = false;
            rslt.exception = error;
            return rslt;
        }
    }
}
