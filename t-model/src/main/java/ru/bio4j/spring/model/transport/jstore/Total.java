package ru.bio4j.spring.model.transport.jstore;

/**
 * Агрегат
 */
public class Total {

    public static final String TOTALCOUNT_FIELD_NAME = "TOTAL_COUNTER_SFIELD";

    public enum Aggrigate {
        UNDEFINED, COUNT, SUM, AVG, MAX, MIN;
    }

    private Total(String fieldName, Aggrigate aggrigate, Class<?> fieldType, Object fact) {
        this.fieldName = fieldName;
        this.aggrigate = aggrigate;
        this.fieldType = fieldType;
        this.fact = fact;
    }

    private String fieldName;
    private Aggrigate aggrigate;
    private Class<?> fieldType;
    private Object fact;

    public String getFieldName() {
        return fieldName;
    }
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Aggrigate getAggrigate() {
        return aggrigate;
    }
    public void setAggrigate(Aggrigate aggrigate) {
        this.aggrigate = aggrigate;
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
        private Aggrigate aggrigate = Aggrigate.COUNT;
        private Class<?> fieldType;
        private Object fact;

        public Builder fieldName(String value) {
            fieldName = value;
            return this;
        }
        public Builder aggrigate(Aggrigate value) {
            aggrigate = value;
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
            return new Total(fieldName, aggrigate, fieldType, fact);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
