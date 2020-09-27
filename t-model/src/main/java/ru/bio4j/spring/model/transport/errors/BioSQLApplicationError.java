package ru.bio4j.spring.model.transport.errors;

public class BioSQLApplicationError extends BioSQLException {
    public BioSQLApplicationError(String msg, Exception parentException){
        super(msg, parentException);
    }

    public BioSQLApplicationError(Exception parentException){
        super(parentException);
    }

    public BioSQLApplicationError(int errorCode, String msg){
        super(errorCode, msg);
    }

    public BioSQLApplicationError(String msg){
        super(msg);
    }

}
