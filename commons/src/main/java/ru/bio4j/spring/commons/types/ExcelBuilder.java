package ru.bio4j.spring.commons.types;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import ru.bio4j.spring.database.api.SQLDefinition;
import ru.bio4j.spring.model.transport.ABean;

import java.util.List;

public interface ExcelBuilder {
    HSSFWorkbook toExcel(List<ABean> rows, SQLDefinition sqlDefinition, boolean rnumEnabled);
    HSSFWorkbook toExcel(final List<ABean> rows, final SQLDefinition sqlDefinition);
    HSSFWorkbook toExcel(List<ABean> rows, String bioCode, boolean rnumEnabled);
    HSSFWorkbook toExcel(final List<ABean> rows, final String bioCode);
}
