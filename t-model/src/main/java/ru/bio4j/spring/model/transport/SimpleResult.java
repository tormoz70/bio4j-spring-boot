package ru.bio4j.spring.model.transport;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

public class SimpleResult {
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

        public static SimpleResult success() {
            SimpleResult rslt = new SimpleResult();
            rslt.success = true;
            return rslt;
        }

        public static SimpleResult success(SsoUser user) {
            SimpleResult rslt = new SimpleResult();
            rslt.success = true;
            rslt.user = user;
            return rslt;
        }

        public static SimpleResult error(Exception error) {
            SimpleResult rslt = new SimpleResult();
            rslt.success = false;
            rslt.exception = error;
            return rslt;
        }
    }
}
