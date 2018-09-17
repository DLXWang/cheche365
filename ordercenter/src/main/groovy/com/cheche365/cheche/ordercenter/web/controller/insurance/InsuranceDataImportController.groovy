package com.cheche365.cheche.ordercenter.web.controller.insurance

import com.cheche365.cheche.core.annotation.VisitorPermission
import com.cheche365.cheche.core.message.InsuranceImportResultMessage
import com.cheche365.cheche.core.service.ResourceService
import com.cheche365.cheche.manage.common.exception.FileUploadException
import com.cheche365.cheche.manage.common.model.OfflineInsuranceCompanyImportData
import com.cheche365.cheche.manage.common.model.OfflineOrderImportHistory
import com.cheche365.cheche.manage.common.service.BaseService
import com.cheche365.cheche.manage.common.util.ResponseOutUtil
import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel
import com.cheche365.cheche.manage.common.web.model.PageInfo
import com.cheche365.cheche.ordercenter.service.insurance.InsuranceDataImportService
import com.cheche365.cheche.ordercenter.web.model.insurance.OfflineInsuranceImportDataModel
import com.cheche365.cheche.ordercenter.web.model.insurance.OfflineInsuranceSubListModel
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

import javax.servlet.http.HttpServletResponse

/**
 * 线下保单导入
 * Created by yinJianBin on 2017/9/19.
 */
@RestController
@RequestMapping("/orderCenter/insurance/import")
public class InsuranceDataImportController {

    private Logger logger = LoggerFactory.getLogger(InsuranceDataImportController.class);
    private static final String SUCCESS = "success";
    private static final String TEMPLATE_FILE_NAME_FANHUA = "泛华线下数据导入模版.xlsx";
    private static final String TEMPLATE_FILE_NAME_FANHUA_ADDED = "泛华线下补充数据导入模版.xlsx";
    private static final String TEMPLATE_FILE_NAME_COMPANY = "保险公司线下数据导入模版.xlsx";
    private static final String TEMPLATE_FILE_NAME_FANHUA_TEMP = "泛华时代财险台账数据导入模版.xlsx";


    @Autowired
    private InsuranceDataImportService insuranceDataImportService
    @Autowired
    private StringRedisTemplate stringRedisTemplate
    @Autowired
    private ResourceService resourceService
    @Autowired
    private BaseService baseService
    /**
     * 保险公司导入列表
     */
    @RequestMapping(value = "/subList", method = RequestMethod.GET)
    public OfflineInsuranceSubListModel subList(@RequestParam(value = "policyNo", required = true) String policyNo) {
        return insuranceDataImportService.subList(policyNo)
    }

    /**
     * 财务对账查询
     */
    @RequestMapping(value = "/offlineInsuranceList", method = RequestMethod.GET)
    public DataTablePageViewModel getList(OfflineInsuranceImportDataModel reqParams) {
        Page<Object[]> insuranceList = insuranceDataImportService.findDataBySpecAndPaginate(reqParams)
        List<Object[]> contentList = insuranceList.getContent()
        List<OfflineInsuranceCompanyImportData> returnList = new ArrayList<>()
        for (Object[] content : contentList) {
            returnList.add(insuranceDataImportService.formatData(content))
        }
        PageInfo pageInfo = baseService.createPageInfo(insuranceList)
        return new DataTablePageViewModel<OfflineInsuranceCompanyImportData>(pageInfo.getTotalElements(), pageInfo.getTotalElements(), reqParams.getDraw(), returnList)
    }
    /**
     * 财务对账查询统计总数
     */
    @RequestMapping(value = "/offlineInsuranceCount", method = RequestMethod.GET)
    public OfflineInsuranceImportDataModel countAll(
            @RequestParam(value = "area", required = false) String area,
            @RequestParam(value = "institution", required = false) String institution,
            @RequestParam(value = "insuranceComp", required = false) String insuranceComp,
            @RequestParam(value = "balanceStartTime", required = false) String balanceStartTime,
            @RequestParam(value = "balanceEndTime", required = false) String balanceEndTime,
            @RequestParam(value = "policyNo", required = false) String policyNo,
            @RequestParam(value = "orderNo", required = false) String orderNo,
            @RequestParam(value = "licensePlateNo", required = false) String licensePlateNo,
            @RequestParam(value = "issueStartTime", required = false) String issueStartTime,
            @RequestParam(value = "issueEndTime", required = false) String issueEndTime,
            @RequestParam(value = 'status', required = false) Integer status) {
        OfflineInsuranceImportDataModel reqParams = new OfflineInsuranceImportDataModel();
        reqParams.setArea(area)
        reqParams.setInstitution(institution)
        reqParams.setInsuranceComp(insuranceComp)
        reqParams.setBalanceStartTime(balanceStartTime)
        reqParams.setBalanceEndTime(balanceEndTime)
        reqParams.setPolicyNo(policyNo)
        reqParams.setOrderNo(orderNo)
        reqParams.setLicensePlateNo(licensePlateNo)
        reqParams.setIssueStartTime(issueStartTime)
        reqParams.setIssueEndTime(issueEndTime)
        reqParams.setStatus(status)
        return insuranceDataImportService.countAll(reqParams)

    }

    @VisitorPermission("or0804")
    @RequestMapping(value = "/Fanhua", method = RequestMethod.POST)
    public void uploadFanhuaReport(
            @RequestParam(value = "codeFile", required = false) MultipartFile file,
            @RequestParam(value = 'area', required = false, defaultValue = '') Long area,
            @RequestParam String description,
            HttpServletResponse response) throws IOException {
        try {
            String runningFlag = InsuranceImportResultMessage.getRunningFlag(stringRedisTemplate);
            if (StringUtils.isNotBlank(runningFlag)) {
                ResponseOutUtil.outPrint(response, "当前任务正在运行,请等待!");
                return;
            }
            insuranceDataImportService.saveFile(file, area, description, OfflineOrderImportHistory.TYPE_FANHUA);
            ResponseOutUtil.outPrint(response, SUCCESS);
        } catch (FileUploadException | IllegalArgumentException fe) {
            logger.error("导入保单数据校验发生异常", fe.getMessage());
            InsuranceImportResultMessage.resetProcessFlag(stringRedisTemplate);
            ResponseOutUtil.outPrint(response, fe.getMessage());
        } catch (Exception e) {
            logger.error("导入保单数据发生异常", e);
            InsuranceImportResultMessage.resetProcessFlag(stringRedisTemplate);
            ResponseOutUtil.outPrint(response, "上传错误!");
        }
    }

    @VisitorPermission("or0804")
    @RequestMapping(value = "/FanhuaTemp", method = RequestMethod.POST)
    public void uploadFanhuaTempReport(
            @RequestParam(value = "codeFile", required = false) MultipartFile file,
            @RequestParam(value = 'area', required = false, defaultValue = '') Long area,
            @RequestParam String description,
            HttpServletResponse response) throws IOException {
        try {
            String runningFlag = InsuranceImportResultMessage.getRunningFlag(stringRedisTemplate);
            if (StringUtils.isNotBlank(runningFlag)) {
                ResponseOutUtil.outPrint(response, "当前任务正在运行,请等待!");
                return;
            }
            insuranceDataImportService.saveFile(file, area, description, OfflineOrderImportHistory.TYPE_FANHUA_TEMP);
            ResponseOutUtil.outPrint(response, SUCCESS);
        } catch (FileUploadException | IllegalArgumentException fe) {
            logger.error("导入保单数据校验发生异常", fe.getMessage());
            InsuranceImportResultMessage.resetProcessFlag(stringRedisTemplate);
            ResponseOutUtil.outPrint(response, fe.getMessage());
        } catch (Exception e) {
            logger.error("导入保单数据发生异常", e);
            InsuranceImportResultMessage.resetProcessFlag(stringRedisTemplate);
            ResponseOutUtil.outPrint(response, "上传错误!");
        }
    }

    @VisitorPermission("or0804")
    @RequestMapping(value = "/Company", method = RequestMethod.POST)
    public void uploadCompanyReport(
            @RequestParam(value = "codeFile", required = false) MultipartFile file,
            @RequestParam(value = 'area', required = false, defaultValue = '') Long area,
            @RequestParam String description,
            HttpServletResponse response) throws IOException {
        try {
            String runningFlag = InsuranceImportResultMessage.getRunningFlag(stringRedisTemplate);
            if (StringUtils.isNotBlank(runningFlag)) {
                ResponseOutUtil.outPrint(response, "当前任务正在运行,请等待!");
                return;
            }
            insuranceDataImportService.saveFile(file, area, description, OfflineOrderImportHistory.TYPE_COMPANY);
            ResponseOutUtil.outPrint(response, SUCCESS);
        } catch (FileUploadException | IllegalArgumentException fe) {
            logger.error("导入保单数据校验发生异常", fe.getMessage());
            InsuranceImportResultMessage.resetProcessFlag(stringRedisTemplate);
            ResponseOutUtil.outPrint(response, fe.getMessage());
        } catch (Exception e) {
            logger.error("导入保单数据发生异常", e);
            InsuranceImportResultMessage.resetProcessFlag(stringRedisTemplate);
            ResponseOutUtil.outPrint(response, "上传错误!");
        }
    }

    @VisitorPermission("or0804")
    @RequestMapping(value = "/template/url", method = RequestMethod.GET)
    public Map getTemplateUrl() {
        String templatePath = resourceService.getResourceAbsolutePath(resourceService.getProperties().getTemplatePath());
        String fanhuaUrl = resourceService.absoluteUrl(templatePath, TEMPLATE_FILE_NAME_FANHUA);
        String fanhuaAddedUrl = resourceService.absoluteUrl(templatePath, TEMPLATE_FILE_NAME_FANHUA_ADDED);
        String fanhuaTempUrl = resourceService.absoluteUrl(templatePath, TEMPLATE_FILE_NAME_FANHUA_TEMP);
        String companyUrl = resourceService.absoluteUrl(templatePath, TEMPLATE_FILE_NAME_COMPANY);
        return ['Fanhua': fanhuaUrl, 'Company': companyUrl, 'FanhuaAdded': fanhuaAddedUrl, 'FanhuaTemp': fanhuaTempUrl]
    }


}
