package com.cheche365.cheche.scheduletask.service.common;

import com.cheche365.cheche.scheduletask.model.*;
import com.cheche365.cheche.scheduletask.util.ExcelGenerateUtil;
import com.cheche365.cheche.scheduletask.util.ParameterUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by guoweifu on 2015/12/24.
 */
@Service("excelAttachmentService")
public class ExcelAttachmentService implements IAttachmentService {


    private Map<String, Method> methodMap = new HashMap<>();

    //创建附件
    public Map<String, String> createSimpleAttachment(List<? extends AttachmentData> attachmentDataList, Map<String, Object> paramMap, ExcelAttachmentConfig excelAttachmentConfig) throws IOException {
        if (excelAttachmentConfig == null
                || CollectionUtils.isEmpty(excelAttachmentConfig.getSheets())
                || CollectionUtils.isEmpty(excelAttachmentConfig.getSheets().get(0).getColumns())) {
            return null;
        }

        SheetConfig sheetConfig = excelAttachmentConfig.getSheets().get(0);
        Map<String, String> columnMap = sheetConfig.getColumns().get(0).getColumn();
        String columnType = sheetConfig.getColumns().get(0).getColumnType();
        if (columnMap == null) {
            return null;
        }
        ExcelSheetData excelSheetData = new ExcelSheetData();
        //设置sheet名称
        excelSheetData.setSheetName(sheetConfig.getSheetName());
        ColumnData columnData = new ColumnData();
        columnData.setColumnNum(columnMap.size());
        //设置列数

        Iterator<String> iterator = columnMap.keySet().iterator();
        //列值
        List<String> fieldNameList = new ArrayList<>();
        // 设置列头
        ExcelSheetHeader[] headers = new ExcelSheetHeader[columnData.getColumnNum()];
        int columnIndex = 0;
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = columnMap.get(key);
            fieldNameList.add(value);
            headers[columnIndex] = new ExcelSheetHeader(key);
            columnIndex++;
        }
        columnData.setHeaders(headers);

        //反射获取所有get方法
        setColumnData(columnType, columnData, fieldNameList, attachmentDataList);
        List<ColumnData> columnDataList = new ArrayList<>();
        columnDataList.add(columnData);
        excelSheetData.setColumnDataList(columnDataList);

        //Excel信息
        ExcelInfoData excelInfoData = new ExcelInfoData();
        excelInfoData.setFirstSheet(excelSheetData);
        String fileName = ParameterUtil.replaceParamForStr(paramMap, excelAttachmentConfig.getFileName());
        excelInfoData.setFileName(fileName);
        return ExcelGenerateUtil.createExcel(excelInfoData);
    }


    //创建附件
    public Map<String, String> createAttachment(ExcelAttachmentConfig excelAttachmentConfig, Map<String, Object> paramMap, Map<String, ? extends List<? extends AttachmentData>> dataSetMaps) throws IOException {
        if (excelAttachmentConfig == null
                || CollectionUtils.isEmpty(excelAttachmentConfig.getSheets())
                || CollectionUtils.isEmpty(excelAttachmentConfig.getSheets().get(0).getColumns())) {
            return null;
        }

        List<ExcelSheetData> excelSheetDataList = new ArrayList<>();

        List<SheetConfig> sheetConfigList = excelAttachmentConfig.getSheets();
        for (SheetConfig sheetConfig : sheetConfigList) {

            //设置sheet名称
            ExcelSheetData excelSheetData = new ExcelSheetData();
            excelSheetData.setSheetName(sheetConfig.getSheetName());
            List<ColumnData> columnDataList = new ArrayList<>();

            List<ColumnConfig> columnConfigList = sheetConfig.getColumns();
            for (ColumnConfig columnConfig : columnConfigList) {
                Map<String, String> columnMap = columnConfig.getColumn();
                //行数据
                ColumnData columnData = new ColumnData();
                //设置列数
                columnData.setColumnNum(columnMap.size());
                //设置列头
                ExcelSheetHeader[] headers = new ExcelSheetHeader[columnData.getColumnNum()];
                //列值名称
                List<String> fieldNameList = new ArrayList<>();

                Iterator<String> iterator = columnMap.keySet().iterator();
                int columnIndex = 0;
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = columnMap.get(key);
                    fieldNameList.add(value);
                    headers[columnIndex] = new ExcelSheetHeader(key);
                    columnIndex++;
                }
                columnData.setHeaders(headers);

                String columnType = columnConfig.getColumnType();

                //遍历数据
                if (dataSetMaps != null) {
                    if (dataSetMaps.containsKey(columnType)) {
                        List<? extends AttachmentData> attachmentDataList = dataSetMaps.get(columnType);
                        setColumnData(columnType, columnData, fieldNameList, attachmentDataList);
                        columnDataList.add(columnData);
                        break;
                    }
                }
                columnDataList.add(columnData);
            }
            excelSheetData.setColumnDataList(columnDataList);
            excelSheetDataList.add(excelSheetData);
        }

        //Excel信息
        ExcelInfoData excelInfoData = new ExcelInfoData();
        excelInfoData.setSheetDatas(excelSheetDataList);
        String fileName = ParameterUtil.replaceParamForStr(paramMap, excelAttachmentConfig.getFileName());
        excelInfoData.setFileName(fileName);
        return ExcelGenerateUtil.createExcel(excelInfoData);
    }

    private void setColumnData(String columnType, ColumnData columnData, List<String> fieldNameList, List<? extends AttachmentData> attachmentDataList) {
        //反射获取所有get方法
        String getMethodName = "";
        Method getMethod = null;
        if (attachmentDataList != null && attachmentDataList.size() > 0) {
            Class clazz = attachmentDataList.get(0).getClass();
            for (String fieldName : fieldNameList) {
                if (!methodMap.containsKey(columnType + "_" + fieldName)) {
                    try {
                        getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        getMethod = clazz.getMethod(getMethodName, new Class[]{});
                        methodMap.put(columnType + "_" + fieldName, getMethod);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }

            //设置行数据
            List<String[]> rowList = new ArrayList<>();
            for (AttachmentData attachmentData : attachmentDataList) {
                String[] rowValueArray = new String[columnData.getColumnNum()];
                for (int j = 0; j < fieldNameList.size(); j++) {
                    //获取值
                    rowValueArray[j] = (String) getFieldValueByName(columnType, fieldNameList.get(j), attachmentData);
                }
                rowList.add(rowValueArray);
            }

            columnData.setRows(rowList);
        }
    }

    //根据属性名称获取属性值
    private Object getFieldValueByName(String columnType, String fieldName, Object object) {
        Method getMethod = methodMap.get(columnType + "_" + fieldName);
        Object value = null;
        try {
            value = getMethod.invoke(object, new Object[]{});
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value == null ? "" : value;
    }
}
