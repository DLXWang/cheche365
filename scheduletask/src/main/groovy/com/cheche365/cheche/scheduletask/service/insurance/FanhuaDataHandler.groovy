package com.cheche365.cheche.scheduletask.service.insurance

import com.cheche365.cheche.common.math.NumberUtils
import com.cheche365.cheche.common.util.DateUtils
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
 * 泛华数据读取,校验,处理
 * Created by yinJianBin on 2017/12/1.
 */
@Service
@Slf4j
class FanhuaDataHandler extends AbstractFanhuaDataHandler {


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
                createTime = getCellValue(row.getCell(2));
                def date = getDate(createTime, DateUtils.DATE_LONGTIME24_PATTERN)
                if (date) model.setCreateTime(date)
            } catch (Exception e) {
                model.setCreateTime(createTime)
                log.debugEnabled && log.error("线下数据导入异常:第{}行数据出单时间解析出错，应为时间(yyyy-MM-dd)格式，请修正!异常原因:{} ", rowNum, e.getMessage());
                sb.append("出单时间解析出错，应为时间(yyyy-MM-dd)格式，请修正！")
            }
            //出单机构
            String institution = getCellValue(row.getCell(3));
            model.setInstitution(institution);
            //保险公司
            String insuranceCompanyName = getCellValue(row.getCell(5));
            model.setInsuranceCompanyName(insuranceCompanyName);
            // 平台/来源
            String agentName = getCellValue(row.getCell(6));
            model.setAgentName(agentName);
            //代理人身份证
            String agentIdentity = getCellValue(row.getCell(7));
            model.setAgentIdentity(agentIdentity);
            //车主
            String owner = getCellValue(row.getCell(8));
            model.setOwner(owner);
            //车牌号
            String licenseNo = getCellValue(row.getCell(9));
            model.setLicenseNo(licenseNo);
            //保费总额
            String totalPremium = getCellValue(row.getCell(10));
            //交强险保费
            String compulsoryPremium = getCellValue(row.getCell(11));
            //车船税
            String autoTax = getCellValue(row.getCell(12));
            //商业险
            String premium = getCellValue(row.getCell(13));
            //保单号
            String policyNo = getCellValue(row.getCell(17));
            model.setPolicyNo(policyNo);
            //保险类型("商业险"或者"交强险")
            String insuranceType = getInsuranceType(compulsoryPremium, autoTax, premium);
            model.setInsuranceType(insuranceType);
            if (InsuranceOfflineDataModel.INSURANCE_TYPE_COMMERCIAL.equals(insuranceType)) {
                model.setPremium(premium);
                model.setTotalPremium(totalPremium);
                //商业点位
                String upCommercialRebate = getCellValue(row.getCell(15));
                model.setUpCommercialRebate(upCommercialRebate);
                //渠优（保酷）(上游商业险佣金)
                String upCommercialAmount = getCellValue(row.getCell(16));
                model.setUpCommercialAmount(upCommercialAmount);
                //泛华商业险点位
                String downCommercialRebate = getCellValue(row.getCell(21));
                model.setDownCommercialRebate(downCommercialRebate);
                //天道佣金(下游商业险佣金)
                String downCommercialAmount = getCellValue(row.getCell(22));
                model.setDownCommercialAmount(downCommercialAmount);
            } else if (InsuranceOfflineDataModel.INSURANCE_TYPE_COMPULSORY.equals(insuranceType)) {
                model.setCompulsory(compulsoryPremium);
                model.setAutoTax(autoTax);
                model.setTotalPremium(totalPremium);
                //交强点位
                String upCompulsoryRebate = getCellValue(row.getCell(14));
                model.setUpCompulsoryRebate(upCompulsoryRebate);
                //渠优（保酷）(上游交强险佣金)
                String upCompulsoryAmount = getCellValue(row.getCell(16));
                model.setUpCompulsoryAmount(upCompulsoryAmount);
                //泛华交强险点位
                String downCompulsoryRebate = getCellValue(row.getCell(20));
                model.setDownCompulsoryRebate(downCompulsoryRebate);
                //天道佣金(下游交强险佣金)
                String downCompulsoryAmount = getCellValue(row.getCell(22));
                model.setDownCompulsoryAmount(downCompulsoryAmount);
            }
            //发动机号
            String engineNo = getCellValue(row.getCell(18));
            model.setEngineNo(engineNo);
            //车架号
            String vinNo = getCellValue(row.getCell(19));
            model.setVinNo(vinNo);
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
        assertNotBlank(model.agentIdentity, rowNum, "代理人身份证", sb);
        assertNotBlank(model.owner, rowNum, "车主姓名", sb);
        assertNotBlank(model.licenseNo, rowNum, "车牌号", sb);
        assertNotBlank(model.totalPremium, rowNum, "保费总额", sb);
        Double totalPremiumDouble = toDouble(model.totalPremium, rowNum, "保费总额", sb)
        model.setTotalPremiumDouble(totalPremiumDouble)
        assertNotBlank(model.policyNo, rowNum, "保单号", sb);
        String insuranceType = model.insuranceType
        model.setInsuranceType(insuranceType);
        if (InsuranceOfflineDataModel.INSURANCE_TYPE_COMMERCIAL.equals(insuranceType)) {
            assertNotBlank(model.premium, rowNum, "商业险保费", sb);
            toDouble(model.premium, rowNum, "商业险值", sb);
            toDouble(model.upCommercialRebate, rowNum, "商业点位", sb);
            toDouble(model.upCommercialAmount, rowNum, "渠优（保酷）", sb);
            toDouble(model.downCommercialRebate, rowNum, "商业点位", sb);
            toDouble(model.downCommercialAmount, rowNum, "天道佣金", sb);
        } else if (InsuranceOfflineDataModel.INSURANCE_TYPE_COMPULSORY.equals(insuranceType)) {
            assertNotBlank(model.compulsory, rowNum, "交强险保费", sb);
            toDouble(model.compulsory, rowNum, "交强险保费", sb);
            if (model.autoTax) toDouble(model.autoTax, rowNum, "车船税值", sb);
            toDouble(model.upCompulsoryRebate, rowNum, "交强点位", sb);
            toDouble(model.upCompulsoryAmount, rowNum, "渠优（保酷）", sb);
            toDouble(model.downCompulsoryRebate, rowNum, "交强点位", sb);
            toDouble(model.downCompulsoryAmount, rowNum, "天道佣金", sb);
        } else {
            sb.append("不能识别的险种:" + insuranceType)
        }
        assertNotBlank(model.engineNo, rowNum, "发动机号", sb);
        assertNotBlank(model.vinNo, rowNum, "车架号", sb);
        if (StringUtils.isNotBlank(sb.toString())) {
            model.setErrorMessage(sb.toString())
        }
        model
    }

    static String getInsuranceType(String compulsory, String autoTax, String premium) {
        boolean isCompulsoryEmpty = StringUtils.isEmpty(compulsory) || NumberUtils.toDouble(compulsory, 0.0) == 0.0d
        boolean isAutoTaxEmpty = StringUtils.isEmpty(autoTax) || NumberUtils.toDouble(autoTax, 0.0) == 0.0d
        boolean isPremiumEmpty = StringUtils.isEmpty(premium) || NumberUtils.toDouble(premium, 0.0) == 0.0d
        if (isCompulsoryEmpty && isAutoTaxEmpty && isPremiumEmpty) {
            return "请确定该条数据的保险类型! ";
        } else if (!isCompulsoryEmpty && !isAutoTaxEmpty && !isPremiumEmpty) {
            return "请确定该条数据的保险类型! ";
        } else if (!isPremiumEmpty) {
            return InsuranceOfflineDataModel.INSURANCE_TYPE_COMMERCIAL;
        } else {
            return InsuranceOfflineDataModel.INSURANCE_TYPE_COMPULSORY;
        }
    }

}
