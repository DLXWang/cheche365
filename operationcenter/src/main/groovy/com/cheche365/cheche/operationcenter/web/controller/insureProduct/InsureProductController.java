package com.cheche365.cheche.operationcenter.web.controller.insureProduct;

import com.cheche365.cheche.common.excel.ExcelExportUtil;
import com.cheche365.cheche.common.excel.entity.enums.ExcelType;
import com.cheche365.cheche.common.excel.entity.params.ExcelExportEntity;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.ResultModel;
import com.cheche365.cheche.core.service.InsuranceProductService;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.manage.common.util.AssertUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by chenx on 2016/11/14.
 * 【阿保-非车险产品】EXCEL数据导入导出功能实现
 */
@RestController
@RequestMapping("/operationcenter/insureProduct")
public class InsureProductController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InsuranceProductService insuranceProductService;

    /**
     * 平台数据导入
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public void upload(@RequestParam(value = "codeFile", required = false) MultipartFile file,HttpServletResponse response) {
        logger.debug("import abao Excel start");
        AssertUtil.notNull(file, "文件不可为空");
        Map<String, List<Map<String, String>>> excelData = new HashMap();
        Workbook book=null;
        Integer nums = 0;

        try {
            InputStream inputstream = file.getInputStream();
            book = new XSSFWorkbook(OPCPackage.open(inputstream));
        } catch (InvalidFormatException | IOException e) {
            logger.error("阿宝导入excel数据失败", e);
        }
        for (int i = 0; i < book.getNumberOfSheets(); i++) {
            Sheet sheet = book.getSheetAt(i);
            List<Map<String, String>> excelList = new ArrayList<>();
            nums += sheet.getLastRowNum();
            for (int j = 1; j <= sheet.getLastRowNum(); j++) {
                Map currentRowMap = new HashMap<>();
                Row currentRow = sheet.getRow(j);
                for (int k = 0; k < currentRow.getLastCellNum(); k++) {
                    String cellValue = "";
                    if (currentRow.getCell(k) != null) {
                        cellValue = String.valueOf(currentRow.getCell(k));
                        if (cellValue.endsWith(".0")) {
                            cellValue = cellValue.substring(0, cellValue.indexOf("."));
                        }
                    }
                    currentRowMap.put(sheet.getRow(0).getCell(k).getStringCellValue(), cellValue);
                }
                excelList.add(currentRowMap);
            }
            excelData.put(sheet.getSheetName(), excelList);
        }

        try {
            insuranceProductService.persistExcelData(excelData);
        }catch (BusinessException e){
            outPrint(response, CacheUtil.doJacksonSerialize(new ResultModel(false,e.getMessage())));
        }
        logger.debug("import abao Excel end ,nums =>"+ nums );
        outPrint(response, CacheUtil.doJacksonSerialize(new ResultModel(true,String.valueOf(nums))));
    }

    private void outPrint(HttpServletResponse response, String ajaxString){
        PrintWriter out = null;
        try {
            response.setContentType("text/html; charset=utf-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("X-Frame-Options", "SAMEORIGIN");
            out = response.getWriter();
            out.write(ajaxString);
            out.flush();
        } catch (IOException | IllegalStateException e) {
            logger.error("阿宝导入excel数据失败", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    /**
     * 数据导出
     */
    @RequestMapping(value = "/outputExcels", method = RequestMethod.GET)
    public ResultModel outputExcels(HttpServletResponse response) {
        OutputStream out = null;
        try {
            Map<String, List<Map<String, String>>> excelData = insuranceProductService.getExcelDataFromDB();
            Iterator it = excelData.keySet().iterator();
            Map dataSet = new HashMap<>();
            Map entitiesSet = new HashMap();
            while (it.hasNext()) {
                List<ExcelExportEntity> entities = new ArrayList<>();
                String sheetKey = (String) it.next();
                List<Map<String, String>> excelRowsList = excelData.get(sheetKey);
                Map<String, String> rows = excelRowsList.get(0);
                Iterator iterator = rows.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    ExcelExportEntity entity=new ExcelExportEntity(key, key);
                    entity.setWidth(30);
                    entities.add(entity);
                }
                entitiesSet.put(sheetKey, entities);
                excelRowsList.remove(0);
                dataSet.put(sheetKey, excelRowsList);
            }
            Workbook workbook = ExcelExportUtil.exportExcel(entitiesSet, dataSet, ExcelType.HSSF);
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding("utf-8");
            response.setHeader("X-Frame-Options", "SAMEORIGIN");
            response.setHeader("Content-Disposition",
                "attachment; filename=" + new String(("阿宝产品.xls").getBytes(), "iso-8859-1"));
            out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            out.close();
            return null;
        } catch (IOException e) {
            logger.error("excel导出时出现IO异常", e);
            return new ResultModel(false, e.getMessage());
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException ex) {
                logger.error("export result, close OutputStream has error", ex);
            }
        }
    }
}
