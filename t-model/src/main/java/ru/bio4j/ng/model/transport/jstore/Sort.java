package ru.bio4j.ng.model.transport.jstore;

//import com.thoughtworks.xstream.annotations.XStreamAlias;
//import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Сортировка
 */
//@XStreamAlias("sort")
public class Sort {

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

//    @XStreamAsAttribute
    private String fieldName;
//    @XStreamAsAttribute
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

}
