package ru.bio4j.spring.dba;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.spring.commons.converter.Converter;
import ru.bio4j.spring.commons.types.ExcelBuilder;
import ru.bio4j.spring.commons.utils.ABeans;
import ru.bio4j.spring.commons.utils.Utl;
import ru.bio4j.spring.database.api.SQLContext;
import ru.bio4j.spring.database.api.SQLDefinition;
import ru.bio4j.spring.database.api.SelectSQLDef;
import ru.bio4j.spring.database.commons.CrudReaderApi;
import ru.bio4j.spring.database.commons.CursorParser;
import ru.bio4j.spring.model.transport.ABean;
import ru.bio4j.spring.model.transport.BeansPage;
import ru.bio4j.spring.model.transport.BioSQLException;
import ru.bio4j.spring.model.transport.Param;
import ru.bio4j.spring.model.transport.jstore.Field;
import ru.bio4j.spring.model.transport.jstore.Sort;
import ru.bio4j.spring.model.transport.jstore.filter.Filter;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ExcelBuilderImpl implements ExcelBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(ExcelBuilderImpl.class);

    private static HSSFCellStyle createHeaderStyle(HSSFWorkbook wb) {
        HSSFCellStyle rslt = wb.createCellStyle();
        HSSFFont headerFont = wb.createFont();
        headerFont.setFontHeightInPoints((short)12);
        headerFont.setBold(true);
        rslt.setFont(headerFont);
        rslt.setAlignment(HorizontalAlignment.CENTER);
        rslt.setVerticalAlignment(VerticalAlignment.CENTER);
        rslt.setBorderBottom(BorderStyle.THIN);
        rslt.setBorderLeft(BorderStyle.THIN);
        rslt.setBorderRight(BorderStyle.THIN);
        rslt.setBorderTop(BorderStyle.THIN);
        rslt.setLocked(true);
        rslt.setWrapText(true);
        return rslt;
    }

    private static void _addRNUMStyle(Map<String, HSSFCellStyle> rslt, HSSFWorkbook wb, HSSFFont cellFont) {
        HSSFCellStyle style = wb.createCellStyle();
        style.setFont(cellFont);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setWrapText(true);
        rslt.put("RNUM", style);
    }

    private static Map<String, HSSFCellStyle> createRowStyle(HSSFWorkbook wb, SelectSQLDef sqlDef, ABean firstRow, boolean rnumEnabled) {
        Map<String, HSSFCellStyle> rslt = new HashMap<>();
        HSSFFont cellFont = wb.createFont();
        cellFont.setFontHeightInPoints((short) 10);
        if(rnumEnabled)
            _addRNUMStyle(rslt, wb, cellFont);
        for (Field fld : sqlDef.getFields()) {
            boolean fieldExistsInData = firstRow.containsKey(fld.getAttrName()) || firstRow.containsKey(fld.getName());
            if(fld.getExpEnabled() && fieldExistsInData) {
                HSSFCellStyle style = wb.createCellStyle();
                style.setFont(cellFont);
                style.setAlignment(HorizontalAlignment.CENTER);
                HorizontalAlignment ha = Enum.valueOf(HorizontalAlignment.class, fld.getAlign().name().toUpperCase());
                if (ha != null)
                    style.setAlignment(ha);
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                style.setBorderBottom(BorderStyle.THIN);
                style.setBorderLeft(BorderStyle.THIN);
                style.setBorderRight(BorderStyle.THIN);
                style.setBorderTop(BorderStyle.THIN);
                style.setWrapText(true);
                rslt.put(fld.getName(), style);
            }
        }
        return rslt;
    }

    private static int _addRNUMHeader(HSSFCellStyle headerStyle, HSSFRow row, int celNum) {
        HSSFCell cellRNUM = row.createCell(celNum);
        cellRNUM.setCellValue("№ пп");
        cellRNUM.setCellStyle(headerStyle);
        celNum++;
        return celNum;
    }

    private static void addHeader(HSSFSheet ws, SelectSQLDef sqlDef, ABean firstRow, boolean rnumEnabled) {
        if(sqlDef != null && sqlDef.getFields()!= null && sqlDef.getFields().size() > 0) {
            HSSFRow r = ws.createRow(0);
            int celNum = 0;
            HSSFCellStyle headerStyle = createHeaderStyle(ws.getWorkbook());
            if(rnumEnabled)
                celNum = _addRNUMHeader(headerStyle, r, celNum);
            for (Field fld : sqlDef.getFields()) {
                boolean fieldExistsInData = firstRow.containsKey(fld.getAttrName()) || firstRow.containsKey(fld.getName());
                if(fld.getExpEnabled() && fieldExistsInData){
                    HSSFCell c = r.createCell(celNum);
                    int colWidth = Converter.toType(fld.getExpWidth(), int.class);
                    if(colWidth == 0) colWidth = 4700;
                    ws.setColumnWidth(celNum, colWidth);
                    c.setCellStyle(headerStyle);
                    c.setCellValue(Utl.nvl(fld.getTitle(), Utl.nvl(fld.getAttrName(), fld.getName())));
                    celNum++;
                }
            }
        }
    }

    private static int _addRNUMCell(Map<String, HSSFCellStyle> rowStyles, HSSFRow row, int rowNum, int celNum) {
        HSSFCell cellRNUM = row.createCell(celNum);
        cellRNUM.setCellValue(rowNum);
        cellRNUM.setCellStyle(rowStyles.get("RNUM"));
        celNum++;
        return celNum;
    }

    private static void addRow(HSSFSheet ws, Map<String, HSSFCellStyle> rowStyles, SelectSQLDef sqlDef, int rowNum, ABean rowData, boolean rnumEnabled) {
        HSSFRow r = ws.createRow(rowNum);
        int celNum = 0;
        if(rnumEnabled)
            celNum = _addRNUMCell(rowStyles, r, rowNum, celNum);
        for (Field fld : sqlDef.getFields()) {
            boolean fieldExistsInData = rowData.containsKey(fld.getAttrName()) || rowData.containsKey(fld.getName());
            if(fld.getExpEnabled() && fieldExistsInData){
                HSSFCell c = r.createCell(celNum);
                c.setCellStyle(rowStyles.get(fld.getName()));
                c.setCellValue(ABeans.extractAttrFromBean(rowData, Utl.nvl(fld.getAttrName(), fld.getName()), String.class, null));
                celNum++;
            }
        }
    }

    @Override
    public HSSFWorkbook toExcel(List<ABean> rows, SQLDefinition sqlDefinition, boolean rnumEnabled) {
        HSSFWorkbook wb = null;
        if(rows != null && rows != null && rows.size() > 0) {
            wb = new HSSFWorkbook();
            HSSFSheet ws = wb.createSheet();
            addHeader(ws, sqlDefinition.getSelectSqlDef(), rows.get(0), rnumEnabled);
            Map<String, HSSFCellStyle> rowStyles = createRowStyle(wb, sqlDefinition.getSelectSqlDef(), rows.get(0), rnumEnabled);
            int rowNum = 1;
            for (ABean bean : rows) {
                addRow(ws, rowStyles, sqlDefinition.getSelectSqlDef(), rowNum, bean, rnumEnabled);
                rowNum++;
            }
        }
        return wb;
    }

    @Override
    public HSSFWorkbook toExcel(List<ABean> rows, SQLDefinition sqlDefinition) {
        return toExcel(rows, sqlDefinition, false);
    }

    @Override
    public HSSFWorkbook toExcel(List<ABean> rows, String bioCode, boolean rnumEnabled) {
        SQLDefinition sqlDefinition = CursorParser.pars(bioCode);
        return toExcel(rows, sqlDefinition, rnumEnabled);
    }

    @Override
    public HSSFWorkbook toExcel(List<ABean> rows, String bioCode) {
        SQLDefinition sqlDefinition = CursorParser.pars(bioCode);
        return toExcel(rows, sqlDefinition);
    }


}
