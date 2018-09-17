package com.cheche365.cheche.manage.common.util;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.common.util.NumberValidationUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;

/**
 * Created by wangfei on 2015/5/30.
 */
public class ExcelUtil {

    public static final String EXTENSION_XLS = "xls";
    public static final String EXTENSION_XLSX = "xlsx";

    public static HSSFWorkbook getHSSFWorkbook(File file) throws Exception {
        if (file == null)
            return null;

        HSSFWorkbook wb;
        try {
            InputStream is = new FileInputStream(file);
            wb = new HSSFWorkbook(new POIFSFileSystem(is));
        } catch (Exception ex) {
            throw ex;
        }

        return wb;
    }

    public static Integer getRowCount(Sheet sheet) {
        if (sheet == null)
            return 0;

        return sheet.getLastRowNum();
    }

    public static Integer getColumnCount(Sheet sheet) {
        if (sheet == null)
            return 0;

        return sheet.getRow(0).getPhysicalNumberOfCells();
    }

    public static Integer getColumnCount(Sheet sheet, Integer rowNum) {
        if (sheet == null)
            return 0;

        if (rowNum == null || rowNum < 1)
            return 0;

        return sheet.getRow(rowNum).getPhysicalNumberOfCells();
    }

    public static String getStringCellValue(Cell cell) {
        if (cell == null)
            return "";

        String cellValue;
        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_STRING:
                cellValue = cell.getStringCellValue();
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case HSSFCell.CELL_TYPE_ERROR:
                cellValue = "";
                break;
            default:
                cellValue = "";
                break;
        }

        return StringUtils.trimToEmpty(cellValue);
    }

    public static String getCellValue(Cell cell) {
        String ret;
        if (cell == null) return "";
        CellType cellType = cell.getCellTypeEnum();
        switch (cellType) {
            case BLANK:
                ret = "";
                break;
            case BOOLEAN:
                ret = String.valueOf(cell.getBooleanCellValue());
                break;
            case ERROR:
                ret = null;
                break;
            case FORMULA:
                Workbook wb = cell.getSheet().getWorkbook();
                CreationHelper crateHelper = wb.getCreationHelper();
                FormulaEvaluator evaluator = crateHelper.createFormulaEvaluator();
                ret = getCellValue(evaluator.evaluateInCell(cell));
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date theDate = cell.getDateCellValue();
                    ret = DateUtils.getDateString(theDate, DateUtils.DATE_LONGTIME24_PATTERN);
                } else {
                    double numericCellValue = cell.getNumericCellValue();
                    if (numericCellValue % 1 != 0) {
                        ret = DoubleUtils.displayStringWithDecimal(numericCellValue, 2);
                    } else {
                        ret = NumberToTextConverter.toText(numericCellValue);
                    }
                }
                break;
            case STRING:
                ret = cell.getRichStringCellValue().getString();
                break;
            default:
                ret = null;
        }

        return StringUtils.trimToEmpty(ret); //有必要自行trim
    }

    public static HSSFCell createStrCellValue(HSSFRow row, Integer columnNum, String value, HSSFCellStyle style) {
        HSSFCell cell = createStrCellValue(row, columnNum, value);
        if (style != null) {
            if (NumberValidationUtil.isRealNumber(value)) {
                style.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,#0.00"));
            }
            cell.setCellStyle(style);
        }
        return cell;
    }

    public static HSSFCell createStrCellValue(HSSFRow row, Integer columnNum, String value) {
        HSSFCell cell = row.createCell(columnNum);
        if (NumberValidationUtil.isRealNumber(value)) {
            cell.setCellValue(Double.parseDouble(value));
            return cell;
        }
        cell.setCellValue(value);
        return cell;
    }

    public static HSSFRow createStrCellValues(HSSFSheet sheet, Integer rowNum, String[] values, HSSFCellStyle style) {
        HSSFRow row = sheet.createRow(rowNum);
        Integer index = 0;
        for (String value : values) {
            createStrCellValue(row, index, value, style);
            index++;
        }

        return row;
    }

    public static HSSFCellStyle createCellStyle(HSSFWorkbook workbook, HSSFFont font) {
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);


        return cellStyle;
    }

    public static HSSFFont createFont(HSSFWorkbook workbook, String fontName, short fontHeightInPoints) {
        HSSFFont font = workbook.createFont();

        font.setFontName(fontName);
        font.setFontHeightInPoints(fontHeightInPoints);

        return font;
    }

    public static Workbook upload(MultipartFile file) throws IOException {
        Workbook book;
        String fileName = file.getOriginalFilename();
        String extension = fileName.lastIndexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1);
        InputStream inputstream = file.getInputStream();
        if (ExcelUtil.EXTENSION_XLS.equals(extension)) {
            book = new HSSFWorkbook(inputstream);
        } else if (ExcelUtil.EXTENSION_XLSX.equals(extension)) {
            book = new XSSFWorkbook(inputstream);
        } else {
            return null;
        }
        return book;
    }

    public static Workbook uploadByFile(MultipartFile file) throws IOException, InvalidFormatException {
        Workbook book;
        String fileName = file.getOriginalFilename();
        String extension = fileName.lastIndexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1);

        InputStream inputstream = file.getInputStream();
        if (ExcelUtil.EXTENSION_XLS.equals(extension)) {
            book = new HSSFWorkbook(inputstream);
        } else if (ExcelUtil.EXTENSION_XLSX.equals(extension)) {
            book = new XSSFWorkbook(OPCPackage.open(inputstream));
            } else {
            return null;
        }
        return book;
    }



}
