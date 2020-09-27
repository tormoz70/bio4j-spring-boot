package ru.bio4j.spring.model.transport.errors;

public class AccessToBeanFieldException extends BioError {
    public AccessToBeanFieldException() {
        super();
        field = null;
    }
    private final String field;

    public AccessToBeanFieldException(Exception parentException) {
        super(parentException);
        this.field = null;
    }

    public AccessToBeanFieldException(String field, String message, Exception parentException) {
        super(message, parentException);
        this.field = field;
    }

    public AccessToBeanFieldException(String field, String message) {
        this(field, message, null);
    }

    public String getField() {
        return field;
    }
}
