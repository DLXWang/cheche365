package com.cheche365.cheche.ordercenter.web.controller;

import com.cheche365.cheche.common.excel.ExcelExportUtil;
import com.cheche365.cheche.common.excel.entity.params.ExcelExportEntity;
import com.cheche365.cheche.common.excel.entity.params.ExportParams;
import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.abao.InsurancePerson;
import com.cheche365.cheche.core.model.abao.InsurancePolicy;
import com.cheche365.cheche.manage.common.exception.FileUploadException;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.util.ExcelUtil;
import com.cheche365.cheche.manage.common.util.ResponseOutUtil;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.ordercenter.model.PublicQuery;
import com.cheche365.cheche.ordercenter.service.healthOrder.HealthOrderService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by xu.yelong on 2016/12/27.
 */
@Controller
@RequestMapping(value = "/orderCenter/health/excel")
public class HealthInsuranceExcelController {

    @Autowired
    private HealthOrderService healthOrderService;
    @Autowired
    private BaseService baseService;

    private Logger logger = LoggerFactory.getLogger(HealthInsuranceExcelController.class);

    @RequestMapping(value = "/export/all", method = RequestMethod.GET)
    public ResultModel exportAll(PublicQuery publicQuery,
                                 @RequestParam(value = "orderNo", required = false) String orderNo,
                                 @RequestParam(value = "orderStatus", required = false) Long orderStatus,
                                 @RequestParam(value = "channel", required = false) Long channel,
                                 @RequestParam(value = "effectiveDate", required = false) String effectiveDate,
                                 @RequestParam(value = "expireDate", required = false) String expireDate,
                                 @RequestParam(value = "mobile", required = false) String mobile, HttpServletResponse response) {
        publicQuery.setOrderNo(orderNo);
        publicQuery.setOrderStatus(orderStatus);
        publicQuery.setChannel(channel);
        publicQuery.setMobile(mobile);
        Page<InsurancePolicy> insurancePolicyPage = healthOrderService.findInsurancePolicyBySpecAndPaginate(baseService.buildPageable(1, 99999, Sort.Direction.DESC, "id"), publicQuery, effectiveDate, expireDate);
        logger.debug("按条件需导出数据数量[{}]条", insurancePolicyPage.getTotalElements());
        return export(insurancePolicyPage.getContent(), response);
    }

    @RequestMapping(value = "/export/new", method = RequestMethod.GET)
    @Transactional
    public ResultModel exportNews(HttpServletResponse response) {
        List<InsurancePolicy> insurancePolicyList = healthOrderService.findByExportNotExists();
        logger.debug("导出最新的数据数量[{}]条", insurancePolicyList.size());
        return export(insurancePolicyList, response);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public void upload(@RequestParam(value = "codeFile", required = false) MultipartFile file, HttpServletResponse response) {
        logger.debug("import abao Excel start");
        Workbook book;
        try {
            book = ExcelUtil.upload(file);
            if(book==null){
                throw new FileUploadException("文件转换错误 !");
            }
        } catch (IOException e) {
            logger.error("导入excel数据失败", e);
            return;
        }
        Sheet sheet = book.getSheetAt(0);
        List<List<String>> dataList = new ArrayList<>();
        int coloumNum = ExcelUtil.getColumnCount(sheet);
        for (int i = 4; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            List<String> rowList = new ArrayList<>();
            for (int c = 0; c < coloumNum; c++) {
                Cell cell = row.getCell(c);
                rowList.add(ExcelUtil.getCellValue(cell));
            }
            dataList.add(rowList);
        }

        try {
            healthOrderService.importDatas(dataList);
            ResponseOutUtil.outPrint(response, "success");
        } catch (Exception e) {
            logger.error("上传保单失败", e);
            return;
        }
    }

    public ResultModel export(List<InsurancePolicy> insurancePolicyList, HttpServletResponse response) {
        ExportParams params = new ExportParams();
        params.setIsCreateHeadRows(true);
        params.setTitle("车车科技医疗险清单");
        params.setTitleHeight((short) 500);
        params.setSecondTitle("发送日期:" + DateUtils.getDateString(new Date(), DateUtils.DATE_SHORTDATE_PATTERN));
        params.setSecondTitleHeight((short) 500);
        List<ExcelExportEntity> entities = new ArrayList<>();
        List<ExcelExportEntity> entitiesApplicantPerson = new ArrayList<>();
        List<ExcelExportEntity> entitiesInsuredPerson = new ArrayList<>();
        List<ExcelExportEntity> entitiesInsured = new ArrayList<>();

        ExcelExportEntity entityApplicantPerson = new ExcelExportEntity("投保人信息", "applicantPerson", HSSFColor.GOLD.index);
        ExcelExportEntity entityInsuredPerson = new ExcelExportEntity("被保险人信息", "insuredPerson", HSSFColor.LIME.index);
        ExcelExportEntity entityInsured = new ExcelExportEntity("保险信息", "insured", HSSFColor.WHITE.index);
        entitiesApplicantPerson.add(new ExcelExportEntity("姓名", "applicantPersonName", 20));
        entitiesApplicantPerson.add(new ExcelExportEntity("证件类型", "applicantPersonIdentityTypeName", 20));
        entitiesApplicantPerson.add(new ExcelExportEntity("证件号码", "applicantPersonIdentity", 30));
        entitiesApplicantPerson.add(new ExcelExportEntity("性别", "applicantPersonSex", 20));
        entitiesApplicantPerson.add(new ExcelExportEntity("出生日期", "applicantPersonBirthday", 20));
        entitiesApplicantPerson.add(new ExcelExportEntity("手机号码", "applicantPersonMobile", 20));
        entitiesApplicantPerson.add(new ExcelExportEntity("E-mail地址", "applicantPersonEmail", 20));
        entitiesInsuredPerson.add(new ExcelExportEntity("投被保险人关系", "relation", 20));
        entitiesInsuredPerson.add(new ExcelExportEntity("姓名", "insuredPersonName", 20));
        entitiesInsuredPerson.add(new ExcelExportEntity("证件类型", "insuredPersonIdentityTypeName", 20));
        entitiesInsuredPerson.add(new ExcelExportEntity("证件号码", "insuredPersonIdentity", 30));
        entitiesInsuredPerson.add(new ExcelExportEntity("性别", "insuredPersonSex", 20));
        entitiesInsuredPerson.add(new ExcelExportEntity("出生日期", "insuredPersonBirthday", 20));
        entitiesInsuredPerson.add(new ExcelExportEntity("手机号码", "insuredPersonMobile", 20));
        entitiesInsuredPerson.add(new ExcelExportEntity("E-mail地址", "insuredPersonEmail", 20));
        entitiesInsuredPerson.add(new ExcelExportEntity("职业", "insuredPersonIndustry", 20));
        entitiesInsured.add(new ExcelExportEntity("起保日期(格式如2017/1/1)", "effectiveDate", 35));
        entitiesInsured.add(new ExcelExportEntity("单份保额", "amount", 20));
        entitiesInsured.add(new ExcelExportEntity("购买份数", "num", 20));
        entitiesInsured.add(new ExcelExportEntity("合计保费", "premium", 20));
        entitiesInsured.add(new ExcelExportEntity("订单号", "orderNo", 20));
        entitiesInsured.add(new ExcelExportEntity("保单号", "policyNo", 20));
        entityApplicantPerson.setList(entitiesApplicantPerson);
        entityInsuredPerson.setList(entitiesInsuredPerson);
        entityInsured.setList(entitiesInsured);
        entities.add(entityApplicantPerson);
        entities.add(entityInsuredPerson);
        entities.add(entityInsured);
        Map DataMap = new HashMap();

        List applicantPersonList = new ArrayList<>();
        List insuredPersonList = new ArrayList<>();
        List insureList = new ArrayList<>();
        for (InsurancePolicy insurancePolicy : insurancePolicyList) {
            Map applicantPersonMap = new HashMap<String, Object>();
            Map insuredPersonMap = new HashMap<String, Object>();
            Map insureMap = new HashMap<String, Object>();
            InsurancePerson applicantPerson = insurancePolicy.getApplicantPerson();
            InsurancePerson insuredPerson = insurancePolicy.getInsuredPerson();
            applicantPersonMap.put("applicantPersonName", applicantPerson.getName());
            applicantPersonMap.put("applicantPersonIdentityTypeName", applicantPerson.getIdentityType() == null ? "" : applicantPerson.getIdentityType().getName());
            applicantPersonMap.put("applicantPersonIdentity", applicantPerson.getIdentity());
            applicantPersonMap.put("applicantPersonSex", applicantPerson.getGender() == null ? "" : applicantPerson.getGender().getName());
            applicantPersonMap.put("applicantPersonBirthday", DateUtils.getDateString(applicantPerson.getBirthday(), DateUtils.DATE_SHORTDATE_PATTERN));
            applicantPersonMap.put("applicantPersonMobile", applicantPerson.getMobile());
            applicantPersonMap.put("applicantPersonEmail", applicantPerson.getEmail());
            insuredPersonMap.put("relation", applicantPerson.getRelationship() == null ? "" : applicantPerson.getRelationship().getName());
            insuredPersonMap.put("insuredPersonName", insuredPerson.getName());
            insuredPersonMap.put("insuredPersonIdentityTypeName", insuredPerson.getIdentityType() == null ? "" : insuredPerson.getIdentityType().getName());
            insuredPersonMap.put("insuredPersonIdentity", insuredPerson.getIdentity());
            insuredPersonMap.put("insuredPersonSex", insuredPerson.getGender() == null ? "" : insuredPerson.getGender().getName());
            insuredPersonMap.put("insuredPersonBirthday", DateUtils.getDateString(insuredPerson.getBirthday(), DateUtils.DATE_SHORTDATE_PATTERN));
            insuredPersonMap.put("insuredPersonMobile", insuredPerson.getMobile());
            insuredPersonMap.put("insuredPersonEmail", insuredPerson.getEmail());
            insuredPersonMap.put("insuredPersonIndustry", insuredPerson.getIndustry() == null ? "" : insuredPerson.getIndustry().getName());
            insureMap.put("effectiveDate", DateUtils.getDateString(insurancePolicy.getEffectiveDate(), DateUtils.DATE_SHORTDATE2_PATTERN));
            insureMap.put("amount", insurancePolicy.getInsuranceQuote() == null ? "" : insurancePolicy.getInsuranceQuote().getPremium());
            insureMap.put("num", "1");
            insureMap.put("premium", insurancePolicy.getInsuranceQuote().getPremium().doubleValue() * 1);
            insureMap.put("orderNo", insurancePolicy.getPurchaseOrder().getOrderNo());
            insureMap.put("policyNo", insurancePolicy.getPolicyNo());
            applicantPersonList.add(applicantPersonMap);
            insuredPersonList.add(insuredPersonMap);
            insureList.add(insureMap);
        }
        DataMap.put("applicantPerson", applicantPersonList);
        DataMap.put("insuredPerson", insuredPersonList);
        DataMap.put("insured", insureList);
        List list = new ArrayList<>();
        list.add(DataMap);
        Workbook workbook = ExcelExportUtil.exportExcel(params, entities, list);
        return ResponseOutUtil.excelExport(workbook, response, "车车科技医疗险清单.xls");
    }

}
