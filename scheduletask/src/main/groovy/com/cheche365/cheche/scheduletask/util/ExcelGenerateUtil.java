package com.cheche365.cheche.scheduletask.util;

import com.cheche365.cheche.common.util.NumberValidationUtil;
import com.cheche365.cheche.scheduletask.model.ColumnData;
import com.cheche365.cheche.scheduletask.model.ExcelInfoData;
import com.cheche365.cheche.scheduletask.model.ExcelSheetData;
import com.cheche365.cheche.scheduletask.model.ExcelSheetHeader;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生成Excel工具类
 * Created by sunhuazhong on 2015/6/10.
 */
public class ExcelGenerateUtil {

    /**
     * 创建excel文件
     * 返回文件名称和路径map
     *
     * @param excelInfoData 封装的excel数据对象
     * @return
     */
    public static Map<String, String> createExcel(ExcelInfoData excelInfoData) throws IOException {

        Map<String, String> returnMap = new HashMap<>();
        List<ExcelSheetData> excelSheetDataList = excelInfoData.getSheetDatas();
        // 创建excel工作簿
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFDataFormat dataFormat = wb.createDataFormat();
        // 创建带边框的CellStyle样式
        CellStyle cellStyle = createBorderCellStyle(wb);
        SXSSFWorkbook swb = new SXSSFWorkbook(wb, 10000);
        if (!CollectionUtils.isEmpty(excelSheetDataList)) {
            for (int i = 0; i < excelSheetDataList.size(); i++) {
                ExcelSheetData excelSheetData = excelSheetDataList.get(i);
                // 创建sheet（页）
                SXSSFSheet sheet = (SXSSFSheet) swb.createSheet();
                swb.setSheetName(i, excelSheetData.getSheetName());
                createSheetData(excelSheetData, dataFormat, sheet, cellStyle);
            }
            // 文件目录地址
            File dicFile = new File(System.getProperty("java.io.tmpdir"));
            File file = new File(dicFile, excelInfoData.getFileName());
            String filePath = file.getAbsolutePath();
            //创建一个文件
            FileOutputStream fileOut = new FileOutputStream(filePath);

            // 把上面创建的工作簿输出到文件中
            swb.write(fileOut);

            //关闭输出流
            fileOut.close();

            returnMap.put("fileName", excelInfoData.getFileName());
            returnMap.put("filePath", filePath);
        }

        return returnMap;
    }

    private static void createSheetData(ExcelSheetData excelSheetData, XSSFDataFormat dataFormat, SXSSFSheet sheet, CellStyle cellStyle) {
        List<ColumnData> columnDataList = excelSheetData.getColumnDataList();
        if (!CollectionUtils.isEmpty(columnDataList)) {
            int firstRow = 0;
            int columnNum = 0;
            for (int i = 0; i < columnDataList.size(); i++) {
                ColumnData columnData = columnDataList.get(i);
                //设置列头数据
                ExcelSheetHeader[] headers = columnData.getHeaders();
                if (columnNum < headers.length) {
                    columnNum = headers.length;
                }
            }

            int[] columnWidthArray = new int[columnNum];
            for (int i = 0; i < columnDataList.size(); i++) {
                ColumnData columnData = columnDataList.get(i);
                //设置列头数据
                ExcelSheetHeader[] headers = columnData.getHeaders();
                Row subjectRow = sheet.createRow(firstRow);
                firstRow = firstRow + 1;
                for (int j = 0; j < headers.length; j++) {
                    // 在row行上创建一个方格，并设置方格的显示
                    createCell(subjectRow, j, headers[j].getName(), cellStyle, dataFormat);
                    if (headers[j].getName().getBytes().length > columnWidthArray[j]) {
                        columnWidthArray[j] = headers[j].getName().getBytes().length;
                    }
                }
                //设置行数据
                List<String[]> rowList = columnData.getRows();
                if (!CollectionUtils.isEmpty(rowList)) {
                    for (int j = 0; j < rowList.size(); j++) {
                        // 创建一行，在页sheet上，Row 和 Cell 都是从0开始计数的
                        Row row = sheet.createRow(j + firstRow);
                        String[] rowValueArray = rowList.get(j);
                        for (int n = 0; n < rowValueArray.length; n++) {
                            // 在row行上创建一个方格，并设置方格的显示
                            createCell(row, n, rowValueArray[n], cellStyle, dataFormat);
                            if (rowValueArray[n] != null) {
                                if (rowValueArray[n].getBytes().length > columnWidthArray[n]) {
                                    columnWidthArray[n] = rowValueArray[n].getBytes().length;
                                }
                            }
                        }
                    }
                    firstRow = firstRow + rowList.size();
                }

            }

            // 设置列宽
            for (int i = 0; i < columnWidthArray.length; i++) {
                sheet.setColumnWidth(i, columnWidthArray[i] * 256 + 1024);
            }
        }
    }

    public static CellStyle createBorderCellStyle(XSSFWorkbook wb) {
        XSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        // 设置单元格内容垂直对齐方式
//        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        // 设置单元格内容水平对齐方式
//        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFillBackgroundColor(HSSFColor.CORAL.index);
        cellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
        // 设置单元格边框样式
        // CellStyle.BORDER_DOUBLE      双边线
        // CellStyle.BORDER_THIN        细边线
        // CellStyle.BORDER_MEDIUM      中等边线
        // CellStyle.BORDER_DASHED      虚线边线
        // CellStyle.BORDER_HAIR        小圆点虚线边线
        // CellStyle.BORDER_THICK       粗边线
//        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
//        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
//        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
//        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
//        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        // 设置单元格边框颜色
        cellStyle.setBottomBorderColor(new XSSFColor(java.awt.Color.BLACK));
        cellStyle.setTopBorderColor(new XSSFColor(java.awt.Color.BLACK));
        cellStyle.setLeftBorderColor(new XSSFColor(java.awt.Color.BLACK));
        // 创建字体对象
        Font ztFont = wb.createFont();
        ztFont.setColor(Font.COLOR_NORMAL);// 字体
        ztFont.setFontHeightInPoints((short) 13);// 字体大小
        ztFont.setFontName("宋体");// 将“宋体”字体应用到当前单元格上
//        ztFont.setItalic(true);// 设置字体为斜体字
//        ztFont.setUnderline(Font.U_DOUBLE);// 添加（Font.U_SINGLE单条下划线/Font.U_DOUBLE双条下划线）
//        ztFont.setStrikeout(true);// 是否添加删除线
        cellStyle.setFont(ztFont);// 将字体应用到样式上面
        return cellStyle;
    }

    public static void createCell(Row row, int cellNum, String cellValue, CellStyle cellStyle, XSSFDataFormat dataFormat) {
        Cell cell = setCellValue(row, cellNum, cellValue);
        setCellStyle(cell, cellValue, cellStyle, dataFormat);
    }

    private static Cell setCellValue(Row row, int cellNum, String cellValue) {
        Cell cell = row.createCell(cellNum);
        if (NumberValidationUtil.isDecimal(cellValue)) {
            cell.setCellValue(Double.parseDouble(cellValue));
            return cell;
        }
        cell.setCellValue(cellValue);
        return cell;
    }

    private static void setCellStyle(Cell cell, String cellValue, CellStyle cellStyle, XSSFDataFormat dataFormat) {
        if (cellStyle != null) {
            if (NumberValidationUtil.isDecimal(cellValue)) {
                cellStyle.setDataFormat(dataFormat.getFormat("#,#0.00"));
            }
            //该格式的情况下，会导致该列之前的列如果是小于1的浮点型数，展示不出来；所以将该格式注释掉
            /*else if (NumberValidationUtil.isWholeNumber(cellValue)) {
                cellStyle.setDataFormat(dataFormat.getFormat("#"));
            }*/
            cell.setCellStyle(cellStyle);
        }
    }
}
