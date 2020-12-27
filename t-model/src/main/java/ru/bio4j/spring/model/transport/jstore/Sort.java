package ru.bio4j.spring.model.transport.jstore;

//import com.thoughtworks.xstream.annotations.XStreamAlias;
//import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.io.Serializable;

/**
 * Сортировка
 */
//@XStreamAlias("sort")
public class Sort implements Serializable {

    public enum NullsPosition {
        DEFAULT, NULLLAST, NULLFIRST;
        public int getCode() {
            return ordinal();
        }
    }

    public enum TextLocality {
        UNDEFINED, DEFAULT, RUSSIAN;
        public int getCode() {
            return ordinal();
        }
    }

    public enum Direction {
        ASC, DESC;
        public int getCode() {
            return ordinal();
        }
    }
    public Sort() {
    }

    private Sort(String fieldName, Direction direction, NullsPosition nullsPosition, TextLocality textLocality) {
        this.fieldName = fieldName;
        this.direction = direction;
        this.nullsPosition = nullsPosition;
        this.textLocality = textLocality;
    }

    private String fieldName;
    private Direction direction = Direction.ASC;
    private NullsPosition nullsPosition = NullsPosition.DEFAULT;
    private TextLocality textLocality = TextLocality.UNDEFINED;

    public String getFieldName() {
        return fieldName;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public NullsPosition getNullsPosition() {
        return nullsPosition;
    }

    public void setNullsPosition(NullsPosition nullsPosition) {
        this.nullsPosition = nullsPosition;
    }

    public TextLocality getTextLocality() {
        return textLocality;
    }

    public void setTextLocality(TextLocality textLocality) {
        this.textLocality = textLocality;
    }

    public static class Builder {
        private String fieldName;
        private Direction direction = Direction.ASC;
        private NullsPosition nullsPosition = NullsPosition.DEFAULT;
        private TextLocality textLocality = TextLocality.UNDEFINED;

        public Builder fieldName(String value) {
            fieldName = value;
            return this;
        }
        public Builder direction(Direction value) {
            direction = value;
            return this;
        }
        public Builder nullsPosition(NullsPosition value) {
            nullsPosition = value;
            return this;
        }
        public Builder textLocality(TextLocality value) {
            textLocality = value;
            return this;
        }
        public Sort build() {
            return new Sort(fieldName, direction, nullsPosition, textLocality);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
