package ru.bio4j.spring.common.errors;


public class ConvertValueException extends BioError {

	public ConvertValueException(Object value, Class<?> valueType, Class<?> targetType, String message) {
        super(String.format("Error on conver value %s (type %s) to type %s. Message %s", value, valueType, targetType, message));
    }

    public ConvertValueException(Object value, Class<?> valueType, Class<?> targetType) {
        super(String.format("Error on conver value %s (type %s) to type %s. Message %s", value, valueType, targetType, null));
    }

}
