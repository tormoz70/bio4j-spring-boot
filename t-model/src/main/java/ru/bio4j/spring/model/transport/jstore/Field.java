package ru.bio4j.spring.model.transport.jstore;

//import flexjson.JSON;
import ru.bio4j.spring.model.transport.MetaType;

import java.io.Serializable;
import java.util.Objects;

/**
 * Описание поля пакета данных
 */
public class Field implements Serializable {

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

    /** Документация для swagger */
    private String dtoDocumentation;

    /** Отображать всплывающую подсказаку с полным текстом ячейки */
    private boolean showTooltip;

    /** Не отображать */
    private boolean hidden;

    /** Не генерить в dto */
    private boolean dtoSkip;

    /** добавить в dto @JsonIgnore */
    private boolean dtoJsonIgnore;

    /** не публиковать в swagger */
    private boolean dtoApiHidden;

    /** генерить в dto как List of type */
    private boolean dtoAsList;

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

    /** Вкл сортировки по полю */
    private boolean sort;

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

    /** Поле является значением в combobox */
    private boolean looCaption;

    /** Максимальное кол-во символов при вводе в поле редактирования */
    private Integer editMaxLength;

    /** Вкл поле в форму редактирования */
    private Boolean editor;

    /** Код справочника для выбора значения с помощью combobox */
    private String looReference;

    /** Зафиксировать колонку в гриде слева или справа*/
    private Fixed fixed;

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

    public int getId() { return id; }

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

    public boolean isSort() {
        return sort;
    }

    public void setSort(boolean sort) {
        this.sort = sort;
    }

    public boolean isLooCaption() {
        return looCaption;
    }

    public void setLooCaption(boolean looCaption) {
        this.looCaption = looCaption;
    }

    public Integer getEditMaxLength() {
        return editMaxLength;
    }

    public void setEditMaxLength(Integer editMaxLength) {
        this.editMaxLength = editMaxLength;
    }

    public Boolean isEditor() {
        return editor;
    }

    public void setEditor(Boolean editor) {
        this.editor = editor;
    }

    public String getLooReference() {
        return looReference;
    }

    public void setLooReference(String looReference) {
        this.looReference = looReference;
    }

    public Fixed getFixed() {
        return fixed;
    }

    public void setFixed(Fixed fixed) {
        this.fixed = fixed;
    }

    public String getDtoDocumentation() {
        return dtoDocumentation;
    }

    public void setDtoDocumentation(String dtoDocumentation) {
        this.dtoDocumentation = dtoDocumentation;
    }

    public boolean isDtoSkip() {
        return dtoSkip;
    }

    public void setDtoSkip(boolean dtoSkip) {
        this.dtoSkip = dtoSkip;
    }

    public boolean isDtoJsonIgnore() {
        return dtoJsonIgnore;
    }

    public void setDtoJsonIgnore(boolean dtoJsonIgnore) {
        this.dtoJsonIgnore = dtoJsonIgnore;
    }

    public boolean isDtoApiHidden() {
        return dtoApiHidden;
    }

    public void setDtoApiHidden(boolean dtoApiHidden) {
        this.dtoApiHidden = dtoApiHidden;
    }

    public boolean isDtoAsList() {
        return dtoAsList;
    }

    public void setDtoAsList(boolean dtoAsList) {
        this.dtoAsList = dtoAsList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;
        return id == field.id &&
                showTooltip == field.showTooltip &&
                hidden == field.hidden &&
                filter == field.filter &&
                readonly == field.readonly &&
                mandatory == field.mandatory &&
                pk == field.pk &&
                useNull == field.useNull &&
                Objects.equals(name, field.name) &&
                Objects.equals(attrName, field.attrName) &&
                Objects.equals(format, field.format) &&
                align == field.align &&
                Objects.equals(title, field.title) &&
                Objects.equals(width, field.width) &&
                Objects.equals(defaultVal, field.defaultVal) &&
                sort == field.sort &&
                Objects.equals(sorter, field.sorter) &&
                nullsPosition == field.nullsPosition &&
                Objects.equals(tooltip, field.tooltip) &&
                metaType == field.metaType &&
                Objects.equals(expEnabled, field.expEnabled) &&
                Objects.equals(expFormat, field.expFormat) &&
                Objects.equals(expWidth, field.expWidth) &&
                textLocality == field.textLocality &&
                aggregate == field.aggregate &&
                Objects.equals(looCaption, field.looCaption) &&
                Objects.equals(editMaxLength, field.editMaxLength) &&
                editor == field.editor &&
                Objects.equals(looReference, field.looReference) &&
                fixed == field.fixed &&
                Objects.equals(dtoDocumentation, field.dtoDocumentation) &&
                dtoSkip == field.dtoSkip &&
                dtoJsonIgnore == field.dtoJsonIgnore &&
                dtoApiHidden == field.dtoApiHidden &&
                dtoAsList == field.dtoAsList;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, attrName, format, align, title, showTooltip, hidden, filter, readonly, mandatory, pk, useNull, width, defaultVal, sort, sorter,
                nullsPosition, tooltip, metaType, expEnabled, expFormat, expWidth, textLocality, aggregate, looCaption, editMaxLength, editor, looReference,
                dtoDocumentation, dtoSkip, dtoJsonIgnore, dtoApiHidden, dtoAsList);
    }
}
