package ru.bio4j.spring.commons.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogWrapper {

    private Logger logger;

    private LogWrapper(Logger logger){
        this.logger = logger;
    }

    public static LogWrapper getLogger(Class<?> clazz){
        return new LogWrapper(LoggerFactory.getLogger(clazz));
    }

    public void info(String msg) {
        logger.info(msg);
    }

    public void info(String msg, Object ... vars) {
        logger.info(msg, vars);
    }

    public void debug(String msg) {
        if(logger.isDebugEnabled())
            logger.debug(msg);
    }

    public void debug(String msg, Object ... vars) {
        if(logger.isDebugEnabled())
            logger.debug(msg, vars);
    }

    public void trace(String msg, Object ... vars) {
        if(logger.isDebugEnabled())
            logger.trace(msg, vars);
    }

    public void trace(String msg) {
        if(logger.isDebugEnabled())
            logger.trace(msg);
    }

    public void error(String msg, Exception e) {
        logger.error(msg, e);
    }

    public void error(Exception e) {
        logger.error(null, e);
    }

    public void error(String msg) {
        logger.error(msg);
    }

}
