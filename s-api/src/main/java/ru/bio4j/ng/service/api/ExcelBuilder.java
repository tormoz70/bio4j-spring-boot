package ru.bio4j.ng.service.api;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.model.transport.ABean;
import ru.bio4j.ng.model.transport.ABeanPage;
import ru.bio4j.ng.model.transport.FilterAndSorter;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.StoreMetadata;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ExcelBuilder {
    HSSFWorkbook toExcel(List<ABean> rows, SQLDefinition sqlDefinition, boolean rnumEnabled);
    HSSFWorkbook toExcel(final List<ABean> rows, final SQLDefinition sqlDefinition);
    HSSFWorkbook toExcel(List<ABean> rows, String bioCode, boolean rnumEnabled);
    HSSFWorkbook toExcel(final List<ABean> rows, final String bioCode);
}
