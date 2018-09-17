package com.cheche365.cheche.scheduletask.service.insurance

import com.cheche365.cheche.common.math.NumberUtils
import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.model.Agent
import com.cheche365.cheche.core.model.AutoType
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.util.BigDecimalUtil
import com.cheche365.cheche.manage.common.exception.FileUploadException
import com.cheche365.cheche.manage.common.model.InsuranceOfflineDataModel
import com.cheche365.cheche.manage.common.model.OfflineOrderImportHistory
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.Row
import org.springframework.stereotype.Service

import static com.cheche365.cheche.common.util.DateUtils.getDate
import static com.cheche365.cheche.manage.common.service.offlinedata.OfflineOrderDataConvertHandler.*

/**
 * 泛华补充数据读取,校验,处理
 * Created by yinJianBin on 2017/1/15
 */
@Service
@Slf4j
class FanhuaAddedDataHandler extends AbstractFanhuaDataHandler {


    def singleUpdate(map, history, admin, InsuranceOfflineDataModel model) {
        PurchaseOrder purchaseOrder = model.purchaseOrder
        def purchaseOrderRebate = insurancePurchaseOrderRebateRepository.findFirstByPurchaseOrder(purchaseOrder)
        OfflineDataVersionModel versionModel = new OfflineDataVersionModel()
        versionModel.setPolicyNo(model.policyNo)
        versionModel.setLicensePlateNo(model.getLicenseNo())
        versionModel.setCode(model.getCode())
        versionModel.setTotalPremium(NumberUtils.toDouble(model.getTotalPremium().toString()))
        versionModel.setIdentity(model.getAgentIdentity())
        versionModel.setDownCommercialAmount(purchaseOrderRebate.getDownCommercialAmount())
        versionModel.setDownCompulsoryAmount(purchaseOrderRebate.getDownCompulsoryAmount())

        String version = OfflineDataVersionGenerator.generate(versionModel)
        if (version.equals(model.offlineDataHistory.dataVersion)) {
            log.debugEnabled && log.debug("数据没有更新,跳过该条数据,行号:${model.order},保单号:${model.policyNo}")
            return
        }

        AutoType autoType = new AutoType()
        autoType.code = model.code
        autoType.seats = model.seats
        autoType.newPrice = model.newPrice
        autoTypeRepository.save(autoType)

        def auto = purchaseOrder.auto
        auto.autoType = autoType
        if (model.kilometerPerYear) auto.kilometerPerYear = model.kilometerPerYear
        if (model.enrollDate) auto.enrollDate = model.enrollDate
        auto.updateTime = new Date()
        autoRepository.save(auto)

        Agent agent = agentRepository.findByIdentity(model.agentIdentity)
        if (agent && model.cardNumber) {
            agent.cardNumber = model.cardNumber
            agent.updateTime = new Date()
            agentRepository.save(agent)
        }

        versionModel.setCode(autoType.getCode())
//        saveOfflineDataHistory(model, history, purchaseOrder, model.offlineDataHistory, versionModel)
    }

    @Override
    InsuranceOfflineDataModel readExcelRow(Row row) {
        StringBuilder sb = new StringBuilder()
        InsuranceOfflineDataModel model = new InsuranceOfflineDataModel();
        Integer rowNum = row.rowNum + 1
        try {
            //序号
            model.setOrder(rowNum);
            //出单时间
            def createTime = ''
            try {
                createTime = getCellValue(row.getCell(0));
                def date = getDate(createTime, DateUtils.DATE_LONGTIME24_PATTERN)
                if (date) model.setCreateTime(date)
            } catch (Exception e) {
                model.setCreateTime(createTime)
                log.debugEnabled && log.error("线下数据导入异常:第{}行数据出单时间解析出错，应为时间(yyyy-MM-dd)格式，请修正!异常原因:{} ", rowNum, e.getMessage());
                sb.append("出单时间解析出错，应为时间(yyyy-MM-dd)格式，请修正！")
            }
            //出单机构
            String institution = getCellValue(row.getCell(1));
            model.setInstitution(institution);
            //保险公司
            String insuranceCompanyName = getCellValue(row.getCell(3));
            model.setInsuranceCompanyName(insuranceCompanyName);
            // 平台/来源 代理人姓名
            String agentName = getCellValue(row.getCell(4));
            model.setAgentName(agentName);
            //代理人身份证
            String agentIdentity = getCellValue(row.getCell(5));
            model.setAgentIdentity(agentIdentity);
            //收款银行卡号
            String cardNumber = getCellValue(row.getCell(6))
            model.cardNumber = cardNumber
            //投保人姓名
            String applicantName = getCellValue(row.getCell(7))
            model.applicantName = applicantName
            //车牌号
            String licenseNo = getCellValue(row.getCell(8));
            model.setLicenseNo(licenseNo);
            //发动机号
            String engineNo = getCellValue(row.getCell(9));
            model.setEngineNo(engineNo);
            //车架号
            String vinNo = getCellValue(row.getCell(10));
            model.setVinNo(vinNo);
            //车型
            String code = getCellValue(row.getCell(11))
            if (code) model.setCode(code)
            //初登日期
            String enrollDate = getCellValue(row.getCell(12))
            if (enrollDate) model.setEnrollDate(getDate(enrollDate, DateUtils.DATE_LONGTIME24_PATTERN))
            //座位数
            String seats = getCellValue(row.getCell(22))
            if (seats) model.setSeats(NumberUtils.toInt(seats))
            //新车购置价
            String newPrice = getCellValue(row.getCell(23))
            if (newPrice) model.setNewPrice(BigDecimalUtil.bigDecimalValue(NumberUtils.toDouble(newPrice)).doubleValue() * 10000)
            //平均行驶里程
            String kilometerPerYear = getCellValue(row.getCell(26))
            if (kilometerPerYear) model.setKilometerPerYear(kilometerPerYear)
            //保费总额
            String totalPremium = getCellValue(row.getCell(28));
            //车船税
            String autoTax = getCellValue(row.getCell(30));
            //保单号
            String policyNo = getCellValue(row.getCell(27));
            model.setPolicyNo(policyNo);
            //保险类型("商业险"或者"交强险")
            String insuranceType = getInsuranceType(getCellValue(row.getCell(29)));
            model.setInsuranceType(insuranceType)
            if (InsuranceOfflineDataModel.INSURANCE_TYPE_COMMERCIAL.equals(insuranceType)) {
                model.setPremium(totalPremium);
                model.setTotalPremium(totalPremium);
            } else if (InsuranceOfflineDataModel.INSURANCE_TYPE_COMPULSORY.equals(insuranceType)) {
                model.setAutoTax(autoTax);
                model.setTotalPremium(totalPremium)
                model.setCompulsory(BigDecimalUtil.subtract(NumberUtils.toDouble(totalPremium), NumberUtils.toDouble(autoTax)))
            }

            def effectiveDate
            try {
                effectiveDate = getCellValue(row.getCell(32));
                def date = getDate(effectiveDate, DateUtils.DATE_LONGTIME24_PATTERN)
                if (date) model.setEffectiveDate(date)
            } catch (Exception e) {
                model.setEffectiveDate(effectiveDate)
                log.debugEnabled && log.error("线下数据导入异常:第{}行数据起保日期解析出错，应为时间(yyyy-MM-dd)格式，请修正!异常原因:{} ", rowNum, e.getMessage());
                sb.append("起保日期解析出错，应为时间(yyyy-MM-dd)格式，请修正！")
            }
        } catch (RuntimeException re) {
            sb.append("model转换异常! ")
            log.error('第{}行数据模型转换异常', rowNum, re)
        } catch (Exception e) {
            throw new FileUploadException(e.getMessage(), e)
        }
        if (StringUtils.isNotBlank(sb.toString())) {
            model.setErrorMessage(sb.toString())
        }
        model
    }

    @Override
    InsuranceOfflineDataModel checkRow(InsuranceOfflineDataModel model, Set<String> policyNoSet, OfflineOrderImportHistory history) {
        StringBuilder sb = new StringBuilder(StringUtils.defaultString(model.getErrorMessage()))
        Integer rowNum = model.order + 1
        assertNotNull(model.createTime, rowNum, "出单时间", sb);
        assertNotBlank(model.institution, rowNum, "出单机构", sb);
        assertNotBlank(model.insuranceCompanyName, rowNum, "保险公司", sb);
        assertNotBlank(model.agentIdentity, rowNum, "代理人身份证号", sb);
        assertNotBlank(model.applicantName, rowNum, "投保人姓名", sb);
        assertNotBlank(model.licenseNo, rowNum, "车牌号", sb);
        assertNotBlank(model.totalPremium, rowNum, "保费总额", sb);
        Double totalPremiumDouble = toDouble(model.totalPremium, rowNum, "保费总额", sb)
        model.setTotalPremiumDouble(totalPremiumDouble)
        assertNotBlank(model.policyNo, rowNum, "保单号", sb);
        String insuranceType = model.insuranceType
        if (InsuranceOfflineDataModel.INSURANCE_TYPE_COMMERCIAL.equals(insuranceType)) {
            //do nothing
        } else if (InsuranceOfflineDataModel.INSURANCE_TYPE_COMPULSORY.equals(insuranceType)) {
            if (model.autoTax) toDouble(model.autoTax, rowNum, "车船税", sb);
        } else {
            sb.append("不能识别的险种:" + insuranceType)
        }
        if (StringUtils.isNotBlank(sb.toString())) {
            model.setErrorMessage(sb.toString())
        }
        model
    }


    static String getInsuranceType(String insuranceType) {
        if ('机动车商业险'.equals(insuranceType)) {
            return InsuranceOfflineDataModel.INSURANCE_TYPE_COMMERCIAL
        } else if ('机动车交强险'.equals(insuranceType)) {
            return InsuranceOfflineDataModel.INSURANCE_TYPE_COMPULSORY
        }
        return "请确定该条数据的保险类型! "
    }

}
