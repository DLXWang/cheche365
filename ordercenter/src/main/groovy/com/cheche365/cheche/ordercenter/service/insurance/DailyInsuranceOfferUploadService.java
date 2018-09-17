package com.cheche365.cheche.ordercenter.service.insurance;

import com.cheche365.cheche.manage.common.exception.FileUploadException;
import com.cheche365.cheche.manage.common.util.ExcelUtil;
import com.cheche365.cheche.ordercenter.web.model.insurance.DailyInsuranceOfferReport;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yinJianBin on 2017/3/3.
 */
@Service
public class DailyInsuranceOfferUploadService {

    private Logger logger = LoggerFactory.getLogger(DailyInsuranceOfferUploadService.class);


    public void importReport(MultipartFile file) throws Exception {
        Workbook book;
        try {
            String fileName = file.getOriginalFilename();
            String extension = fileName.lastIndexOf(".") == -1 ? "" : fileName.substring(fileName.lastIndexOf(".") + 1);
            InputStream inputstream = file.getInputStream();

            if (ExcelUtil.EXTENSION_XLS.equals(extension)) {
                book = new HSSFWorkbook(inputstream);
            } else if (ExcelUtil.EXTENSION_XLSX.equals(extension)) {
                book = new XSSFWorkbook(inputstream);
            } else {
                throw new FileUploadException("文件转换错误 !");
            }
        } catch (Exception e) {
            throw new FileUploadException("文件转换错误 !");
        }
        Sheet sheet = book.getSheetAt(0);
        List<DailyInsuranceOfferReport> dataList = new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null || row.getCell(0) == null) {
                continue;
            }
            DailyInsuranceOfferReport dailyInsuranceOfferReport = new DailyInsuranceOfferReport();

            String reportType = ExcelUtil.getCellValue(row.getCell(1));
            dailyInsuranceOfferReport.setReportType(reportType);

            String orderNo = ExcelUtil.getCellValue(row.getCell(2));
            dailyInsuranceOfferReport.setOrderNo(orderNo);
            String status = ExcelUtil.getCellValue(row.getCell(14));
            if (reportType.endsWith("月报表")) {
                status = ExcelUtil.getCellValue(row.getCell(15));
            }
            if (status == null || !status.trim().equals("是")) {
                logger.info("上传的excel文件第{}行,订单号{}的数据兑换状态无变化,忽略更新", i + 1, orderNo);
                continue;
            }
            dailyInsuranceOfferReport.setStatus(status);

            dataList.add(dailyInsuranceOfferReport);
        }

        if (dataList.size() == 0) {
            throw new FileUploadException("文件内无待更新数据 !");
        }
    }

}
