package ru.bio4j.spring.model.transport.jstore;

import java.io.Serializable;

/**
 * Агрегат
 */
public class Total implements Serializable {

    public static final String TOTALCOUNT_FIELD_NAME = "TOTAL_COUNTER_SFIELD";

    public enum Aggregate {
        UNDEFINED, COUNT, SUM, AVG, MAX, MIN;
    }

    private Total(String fieldName, Aggregate aggregate, Class<?> fieldType, Object fact) {
        this.fieldName = fieldName;
        this.aggregate = aggregate;
        this.fieldType = fieldType;
        this.fact = fact;
    }

    /**
     * Имя поля, по которому необходимо вычислить агрегат
     */
    private String fieldName;
    /**
     * Агрегат
     */
    private Aggregate aggregate;
    /**
     * Тип возвращаемого значения
     */
    private Class<?> fieldType;
    /**
     * Значение
     */
    private Object fact;

    public String getFieldName() {
        return fieldName;
    }
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Aggregate getAggregate() {
        return aggregate;
    }
    public void setAggregate(Aggregate aggregate) {
        this.aggregate = aggregate;
    }
    public Class<?> getFieldType() {
        return fieldType;
    }
    public void setFieldType(Class<?> fieldType) {
        this.fieldType = fieldType;
    }
    public Object getFact() {
        return fact;
    }
    public void setFact(Object fact) {
        this.fact = fact;
    }

    public static class Builder {
        private String fieldName;
        private Aggregate aggregate = Aggregate.COUNT;
        private Class<?> fieldType;
        private Object fact;

        public Builder fieldName(String value) {
            fieldName = value;
            return this;
        }
        public Builder aggrigate(Aggregate value) {
            aggregate = value;
            return this;
        }
        public Builder fieldType(Class<?> value) {
            fieldType = value;
            return this;
        }
        public Builder fact(Object value) {
            fact = value;
            return this;
        }
        public Total build() {
            return new Total(fieldName, aggregate, fieldType, fact);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
