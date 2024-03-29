package ru.bio4j.spring.database.api;

import ru.bio4j.spring.model.transport.jstore.Sort;

import java.util.List;

public interface SelectSQLDef extends SQLDef {

    void setWrapMode(byte wrapMode);
    byte getWrapMode();

    List<Sort> getDefaultSort();
    void setDefaultSort(List<Sort> defaultSort);

    boolean isReadonly();
    void setReadonly(boolean readonly);

    boolean isMultySelection();
    void setMultySelection(boolean multySelection);

    String getTotalsSql();
    void setTotalsSql(String totalsSql);

    String getLocateSql();

    void setLocateSql(String locateSql);
}
