package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.common.util.NumberValidationUtil;
import com.cheche365.cheche.core.model.CustomerField;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.manage.common.util.ExcelUtil;
import com.cheche365.cheche.scheduletask.model.ActivityMonitorDataInfo;
import com.cheche365.cheche.scheduletask.model.BusinessActivityInfo;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guoweifu on 2015/12/4.
 */
@Service("cpsChannelTaskService")
public class CPSChannelService {

    private static Logger logger = LoggerFactory.getLogger(CPSChannelService.class);


    public void addAttachment(EmailInfo emailInfo, BusinessActivityInfo businessActivityInfo) {
        Map<String, String> attachmentFileMap = createExcel(businessActivityInfo);
        emailInfo.addAttachment(attachmentFileMap.get("fileName"), attachmentFileMap.get("filePath"));
    }


    /**
     * 创建系统提醒Excel文件
     *
     * @param businessActivityInfo
     * @throws IOException
     */
    public Map<String, String> createExcel(BusinessActivityInfo businessActivityInfo) {
        Map<String, String> returnMap = new HashMap<>();
        FileOutputStream fileOut = null;
        try {
            // 创建excel工作簿
            HSSFWorkbook workbook = new HSSFWorkbook();
            // 添加Excel数据
            addExcelData(workbook, businessActivityInfo);
            // 文件目录地址
            returnMap = getFilePath(businessActivityInfo);
            //创建一个文件，把上面创建的工作簿输出到文件中
            fileOut = new FileOutputStream(returnMap.get("filePath"));
            workbook.write(fileOut);
            //关闭输出流
            fileOut.flush();
            fileOut.close();
        } catch (Exception ex) {
            logger.error("create excel error", ex);
        } finally {
            try {
                if (fileOut != null)
                    fileOut.close();
            } catch (Exception ex) {
                logger.error("create excel and close OutputStream has error", ex);
            }
        }
        return returnMap;
    }

    private static Map<String, String> getFilePath(BusinessActivityInfo businessActivityInfo) {
        Map<String, String> returnMap = new HashMap<>();
        try {
            // 当前日期
            String currentDateStr = DateUtils.getCurrentDateString("yyyy年MM月dd日");
            String fileName = businessActivityInfo.getName() + "-" + currentDateStr + "的商务活动监控数据列表.xls";
            File dicFile = new File(System.getProperty("java.io.tmpdir"));
            File file = new File(dicFile, fileName);
            String filePath = file.getAbsolutePath();

            returnMap.put("fileName", fileName);
            returnMap.put("filePath", filePath);
        } catch (Exception ex) {
            logger.error("create temp excel file error.", ex);
        }
        return returnMap;
    }

    private static void addExcelData(HSSFWorkbook workbook, BusinessActivityInfo businessActivityInfo) {
        // 创建带边框的CellStyle样式
        HSSFFont font = ExcelUtil.createFont(workbook, "宋体", (short) 13);
        HSSFCellStyle cellStyle = ExcelUtil.createCellStyle(workbook,font);
        HSSFSheet sheet = workbook.createSheet("sheet1");

        //商务活动监控数据列表
        createBusinessActivityMonitorData(businessActivityInfo, sheet, cellStyle);
    }

    private static void createBusinessActivityMonitorData(BusinessActivityInfo businessActivityInfo, HSSFSheet sheet, HSSFCellStyle cellStyle) {
        // 商务活动基本信息
        createStrCellValues(sheet, 0, supplyHourBasicExcelTitle(sheet), cellStyle);
        createStrCellValues(sheet, 1, supplyHourBasicExcelContent(businessActivityInfo), cellStyle);

        // 商务活动监控数据信息
        List<CustomerField> customerFieldList = businessActivityInfo.getCustomerFieldList();
        createStrCellValues(sheet, 2, supplyHourListExcelTitle(sheet, customerFieldList), cellStyle);
        Integer index = 3;
        List<ActivityMonitorDataInfo> monitorDataList = businessActivityInfo.getMonitorDataList();
        if (!CollectionUtils.isEmpty(monitorDataList)) {
            for (ActivityMonitorDataInfo data : monitorDataList) {
                createStrCellValues(sheet, index, supplyHourListExcelContent(data, customerFieldList), cellStyle);
                index++;
            }
        }
    }

    private static String[] supplyHourBasicExcelTitle(HSSFSheet sheet) {
        sheet.setColumnWidth(0, 18 * 20);//ID
        sheet.setColumnWidth(1, 18 * 600);//商务活动名字
        sheet.setColumnWidth(2, 18 * 400);//合作商
        sheet.setColumnWidth(3, 18 * 180);//合作方式
        sheet.setColumnWidth(4, 18 * 250);//佣金
        sheet.setColumnWidth(5, 18 * 250);//城市
        sheet.setColumnWidth(6, 18 * 250);//预算
        sheet.setColumnWidth(7, 18 * 450);//活动开始时间
        sheet.setColumnWidth(8, 18 * 450);//活动结束时间
        sheet.setColumnWidth(9, 18 * 200);//落地页
        sheet.setColumnWidth(10, 18 * 400);//备注
        sheet.setColumnWidth(11, 18 * 400);//数据更新时间
        return new String[]{
                "ID", "商务活动名字", "合作商", "合作方式", "佣金",
                "城市", "预算", "活动开始", "活动结束", "落地页", "备注", "数据更新时间"
        };
    }

    private static String[] supplyHourBasicExcelContent(BusinessActivityInfo data) {
        DecimalFormat format = new DecimalFormat("#.##");
        return new String[]{
                data.getId() + "",                   //ID
                data.getName(),                      //商务活动名字
                data.getPartnerName(),               //合作商
                data.getCooperationModeName(),       //合作方式
                data.getRebate(),//佣金
                data.getCity() + "",                 //城市
                data.getBudget(),//预算
                data.getStartTime(),                 //活动开始时间
                data.getEndTime(),                   //活动结束时间
                data.getLandingPage(),               //落地页
                data.getComment(),                   //备注
                data.getRefreshTime()                //数据更新时间
        };
    }

    private static String[] supplyHourListExcelTitle(HSSFSheet sheet, List<CustomerField> customerFieldList) {
        sheet.setColumnWidth(0, 18 * 180);//日期
        sheet.setColumnWidth(1, 18 * 250);//PV
        sheet.setColumnWidth(2, 18 * 250);//UV
        sheet.setColumnWidth(3, 18 * 250);//注册
        sheet.setColumnWidth(4, 18 * 250);//试算
        sheet.setColumnWidth(5, 18 * 250);//提交订单数
        sheet.setColumnWidth(6, 18 * 250);//提交订单总额
        sheet.setColumnWidth(7, 18 * 250);//支付订单数
        sheet.setColumnWidth(8, 18 * 250);//支付订单总额
        sheet.setColumnWidth(9, 18 * 350);//支付订单总额
        sheet.setColumnWidth(10, 18 * 250);//特殊监控
        if (!CollectionUtils.isEmpty(customerFieldList)) {
            for (int i = 0; i < customerFieldList.size(); i++) {
                sheet.setColumnWidth(11 + i, 18 * 250);//自定义字段
            }
        }

        List<String> titleList = new ArrayList<>();
        titleList.add("日期");
        titleList.add("PV");
        titleList.add("UV");
        titleList.add("注册");
        titleList.add("试算");
        titleList.add("提交订单数");
        titleList.add("提交订单总额");
        titleList.add("支付订单数");
        titleList.add("支付订单总额");
        titleList.add("不包含车船税总额");
        titleList.add("特殊监控");
        if (!CollectionUtils.isEmpty(customerFieldList)) {
            customerFieldList.forEach(customerField -> {
                titleList.add(customerField.getName());
            });
        }
        String[] titles = new String[titleList.size()];
        return titleList.toArray(titles);
    }

    private static String[] supplyHourListExcelContent(ActivityMonitorDataInfo data, List<CustomerField> customerFieldList) {
        String monitorTime = data.getMonitorTime();
        DecimalFormat format = new DecimalFormat("#.##");
        List<String> valueList = new ArrayList<>();
        valueList.add(monitorTime);//日期
        valueList.add(data.getPv() == null ? "0" : data.getPv() + "");//PV
        valueList.add(data.getUv() == null ? "0" : data.getUv() + "");//UV
        valueList.add(data.getRegister() == null ? "0" : data.getRegister() + "");//注册
        valueList.add(data.getQuote() == null ? "0" : data.getQuote() + "");//试算
        valueList.add(data.getSubmitCount() == null ? "0" : data.getSubmitCount() + "");//提交订单数
        valueList.add(format.format(DoubleUtils.doubleValue(data.getSubmitAmount())));//提交订单总额
        valueList.add(data.getPaymentCount() == null ? "0" : data.getPaymentCount() + "");//支付订单数
        valueList.add(format.format(DoubleUtils.doubleValue(data.getPaymentAmount())));//支付订单总额
        valueList.add(format.format(DoubleUtils.doubleValue(data.getNoAutoTaxAmount())));//不包含车船税总额
        valueList.add(data.getSpecialMonitor() == null ? "0" : data.getSpecialMonitor() + "");//特殊监控
        if (!CollectionUtils.isEmpty(customerFieldList)) {
            for (int i = 0; i < customerFieldList.size(); i++) {
                try {
                    Method method = data.getClass().getMethod("getCustomerField" + (i + 1));
                    valueList.add(format.format(DoubleUtils.doubleValue((Double) method.invoke(data))));//自定义字段
                } catch (Exception ex) {
                    logger.error("reflect to get customer field value error", ex);
                }
            }
        }
        String[] values = new String[valueList.size()];
        return valueList.toArray(values);
    }

    private static HSSFCell createStrCellValue(HSSFRow row, Integer columnNum, String value, HSSFCellStyle style) {
        HSSFCell cell = createStrCellValue(row, columnNum, value);
        if (style != null) {
            if (NumberValidationUtil.isRealNumber(value)) {
                style.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,#0.00"));
            }
            cell.setCellStyle(style);
        }
        return cell;
    }

    private static HSSFCell createStrCellValue(HSSFRow row, Integer columnNum, String value) {
        HSSFCell cell = row.createCell(columnNum);
        if (NumberValidationUtil.isRealNumber(value)) {
            cell.setCellValue(Double.parseDouble(value));
            return cell;
        }
        cell.setCellValue(value);
        return cell;
    }

    private static HSSFRow createStrCellValues(HSSFSheet sheet, Integer rowNum, String[] values, HSSFCellStyle style) {
        HSSFRow row = sheet.createRow(rowNum);
        Integer index = 0;
        for (String value : values) {
            createStrCellValue(row, index, value, style);
            index++;
        }

        return row;
    }


//    private static HSSFCellStyle createCellStyle(HSSFWorkbook workbook) {
//        HSSFCellStyle cellStyle = workbook.createCellStyle();
//        // 设置单元格内容垂直对齐方式
//        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
//        // 设置单元格内容水平对齐方式
//        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//        cellStyle.setFillBackgroundColor(HSSFColor.CORAL.index);
//        cellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
//        // 设置单元格边框样式
//        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);//细边线
//        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
//        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
//        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
//        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
//        // 设置单元格边框颜色
//        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
//        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
//        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
//        // 创建字体对象
//        Font ztFont = workbook.createFont();
//        ztFont.setColor(Font.COLOR_NORMAL);// 字体
//        ztFont.setFontHeightInPoints((short) 13);// 字体大小
//        ztFont.setFontName("宋体");// 将“宋体”字体应用到当前单元格上
//        cellStyle.setFont(ztFont);
//
//        return cellStyle;
//    }
}
