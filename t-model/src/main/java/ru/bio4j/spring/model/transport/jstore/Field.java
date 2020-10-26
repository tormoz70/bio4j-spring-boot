package ru.bio4j.spring.model.transport.jstore;

//import flexjson.JSON;
import ru.bio4j.spring.model.transport.MetaType;

/**
 * Описание поля пакета данных
 */
public class Field {

    private int id;

    /** Имя поля */
    private String name;

    /** Имя атрибута */
    private String attrName;

    /** Формат отображения */
    private String format;

    /** Выравнивание */
    private Alignment align;

    /** Заголовок */
    private String title;

    /** Отображать всплывающую подсказаку с полным текстом ячейки */
    private boolean showTooltip;

    /** Не отображать */
    private boolean hidden;

    /** Фильтрация */
    private boolean filter;

    /** Только чтение */
    private boolean readonly;

    /** Требуется заполнение пользоватлем */
    private boolean mandatory;

    /** Первичный ключ */
    private boolean pk;

    /** На клиенте не приводить значение null к типу колонки */
    private boolean useNull;

    /** Ширина колонки */
    private String width;

    /** Значение по умолчанию */
    private Object defaultVal;

    /** Имя поля, по которому будет сортировка, если null, то sorter == name */
    private String sorter;

    /** Кудп помнстить NULLS при сортировке */
    private Sort.NullsPosition nullsPosition;

    /** Имя поля, из которого будет взято значение для отображения tooltip, если null, то tooltip == name */
    private String tooltip;

    /** Тип колонки */
    private MetaType metaType = MetaType.UNDEFINED;

    /** Включить в экспорт */
    private Boolean expEnabled;
    /** Формат при экспорте */
    private String expFormat;
    /** Ширина при экспорте */
    private String expWidth;

    /** Использовать локализованную сортировку при сортировке по текстовому полю */
    private Sort.TextLocality textLocality = Sort.TextLocality.UNDEFINED;


    /** Агригруемое поле */
    private Total.Aggregate aggregate;

    @Override
    public String toString() {
        return String.format("{name: \"%s\", metaType: \"%s\", pk: \"%s\"}", name, metaType, pk);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Alignment getAlign() {
        return align;
    }

    public void setAlign(Alignment align) {
        this.align = align;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean value) {
        this.hidden = value;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean value) {
        this.readonly = value;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isPk() {
        return pk;
    }

    public void setPk(boolean value) {
        this.pk = value;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public Object getDefaultVal() {
        return defaultVal;
    }

    public void setDefaultVal(Object defaultVal) {
        this.defaultVal = defaultVal;
    }

    public MetaType getMetaType() {
        return metaType;
    }

    public void setMetaType(MetaType value) {
        this.metaType = value;
    }

    /**
     * Field id starts from 1
     * @return id
     */
    public int getId() { return id; }

    /**
     * Field index starts from 0
     * @return index
     */
    public int getIndex() { return id-1; }

    public void setId(int id) { this.id = id; }

    public boolean isUseNull() {
        return useNull;
    }

    public void setUseNull(boolean useNull) {
        this.useNull = useNull;
    }

    public boolean isShowTooltip() {
        return showTooltip;
    }

    public void setShowTooltip(boolean showTooltip) {
        this.showTooltip = showTooltip;
    }

    public boolean isFilter() {
        return filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }

    public Boolean getExpEnabled() {
        return expEnabled;
    }

    public void setExpEnabled(Boolean expEnabled) {
        this.expEnabled = expEnabled;
    }

    public String getExpFormat() {
        return expFormat;
    }

    public void setExpFormat(String expFormat) {
        this.expFormat = expFormat;
    }

    public String getExpWidth() {
        return expWidth;
    }

    public void setExpWidth(String expWidth) {
        this.expWidth = expWidth;
    }

    public String getSorter() {
        return sorter;
    }

    public void setSorter(String sorter) {
        this.sorter = sorter;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public Sort.NullsPosition getNullsPosition() {
        return nullsPosition;
    }

    public void setNullsPosition(Sort.NullsPosition nullsPosition) {
        this.nullsPosition = nullsPosition;
    }

    public Sort.TextLocality getTextLocality() {
        return textLocality;
    }

    public void setTextLocality(Sort.TextLocality textLocality) {
        this.textLocality = textLocality;
    }

    public Total.Aggregate getAggregate() {
        return aggregate;
    }

    public void setAggregate(Total.Aggregate aggregate) {
        this.aggregate = aggregate;
    }
}
