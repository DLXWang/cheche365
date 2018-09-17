package com.cheche365.cheche.scheduletask.service.insurance

import com.cheche365.cheche.manage.common.exception.FileUploadException
import com.cheche365.cheche.manage.common.model.InsuranceOfflineDataModel
import com.cheche365.cheche.manage.common.model.OfflineFanhuaTempDataModel
import com.cheche365.cheche.manage.common.model.OfflineOrderImportHistory
import com.cheche365.cheche.manage.common.repository.OfflineFanhuaTempDataModelRepository
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.Row
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

import static com.cheche365.cheche.manage.common.service.offlinedata.OfflineOrderDataConvertHandler.getCellValue

/**
 * 泛华补充数据读取,校验,处理
 * Created by yinJianBin on 2017/1/15
 */
@Service
@Slf4j
class FanhuaTempDataHandler extends AbstractFanhuaDataHandler {

    @Autowired
    OfflineFanhuaTempDataModelRepository offlineFanhuaTempDataModelRepository

    @Transactional
    void save(Collection models) {
        offlineFanhuaTempDataModelRepository.save(models)
    }


    InsuranceOfflineDataModel readExcelRow(Row row) {
        return null;
    }

    static OfflineFanhuaTempDataModel readTempExcelRow(Row row) {
        StringBuilder sb = new StringBuilder("")
        OfflineFanhuaTempDataModel model = new OfflineFanhuaTempDataModel();
        Integer rowNum = row.rowNum + 1
        try {
            model.setOrderNo(getCellValue(row.getCell(0)))
            model.setInstitution(getCellValue(row.getCell(1)))
            model.setOwnerOrganizationName(getCellValue(row.getCell(2)))
            model.setGroupName(getCellValue(row.getCell(3)))
            model.setAgentName(getCellValue(row.getCell(4)))
            model.setAgentNo(getCellValue(row.getCell(5)))
            model.setOrderCreateTime(getCellValue(row.getCell(6)))
            model.setEffectiveDate(getCellValue(row.getCell(7)))
            model.setReviewDate(getCellValue(row.getCell(8)))
            model.setPolicyNo(getCellValue(row.getCell(9)))
            model.setPolicyBatchNo(getCellValue(row.getCell(10)))
            model.setWarrantyType(getCellValue(row.getCell(11)))
            model.setBusinessType(getCellValue(row.getCell(12)))
            model.setCarType(getCellValue(row.getCell(13)))
            model.setSellType(getCellValue(row.getCell(14)))
            model.setLicensePlateNo(getCellValue(row.getCell(15)))
            model.setInsuredName(getCellValue(row.getCell(16)))
            model.setPremium(getCellValue(row.getCell(17)))
            model.setPremiumAfterTax(getCellValue(row.getCell(18)))
            model.setInsuranceCompanyName(getCellValue(row.getCell(19)))
            model.setInsuranceType(getCellValue(row.getCell(20)))
            model.setRebateLevel(getCellValue(row.getCell(21)))
            model.setSumIncomeRebate(getCellValue(row.getCell(22)))
            model.setSumIncomeAmount(getCellValue(row.getCell(23)))
            model.setIncomeAfterTax(getCellValue(row.getCell(24)))
            model.setCommissionChargeRebate1(getCellValue(row.getCell(25)))
            model.setCommissionChargeAmount1(getCellValue(row.getCell(26)))
            model.setCommissionChargeRebateAdded1(getCellValue(row.getCell(27)))
            model.setBillingDate1(getCellValue(row.getCell(28)))
            model.setInvoiceNo1(getCellValue(row.getCell(29)))
            model.setPaymentTime1(getCellValue(row.getCell(30)))
            model.setBalanceBatchNo1(getCellValue(row.getCell(31)))
            model.setCommissionChargeRebate2(getCellValue(row.getCell(32)))
            model.setCommissionChargeAmount2(getCellValue(row.getCell(33)))
            model.setCommissionChargeRebateAdded2(getCellValue(row.getCell(34)))
            model.setBillingDate2(getCellValue(row.getCell(35)))
            model.setInvoiceNo2(getCellValue(row.getCell(36)))
            model.setPaymentTime2(getCellValue(row.getCell(37)))
            model.setBalanceBatchNo2(getCellValue(row.getCell(38)))
            model.setCommissionChargeRebate3(getCellValue(row.getCell(39)))
            model.setCommissionChargeAmount3(getCellValue(row.getCell(40)))
            model.setCommissionChargeRebateAdded3(getCellValue(row.getCell(41)))
            model.setBillingDate3(getCellValue(row.getCell(42)))
            model.setInvoiceNo3(getCellValue(row.getCell(43)))
            model.setPaymentTime3(getCellValue(row.getCell(44)))
            model.setBalanceBatchNo3(getCellValue(row.getCell(45)))
            model.setCommissionChargeRebate4(getCellValue(row.getCell(46)))
            model.setCommissionChargeAmount4(getCellValue(row.getCell(47)))
            model.setCommissionChargeRebateAdded4(getCellValue(row.getCell(48)))
            model.setBillingDate4(getCellValue(row.getCell(49)))
            model.setInvoiceNo4(getCellValue(row.getCell(50)))
            model.setPaymentTime4(getCellValue(row.getCell(51)))
            model.setBalanceBatchNo4(getCellValue(row.getCell(52)))
            model.setNoCommissionChargeAmount(getCellValue(row.getCell(53)))
            model.setSumRebate(getCellValue(row.getCell(54)))
            model.setSumRebateAmount(getCellValue(row.getCell(55)))
            model.setRebate(getCellValue(row.getCell(56)))
            model.setRebateAmount(getCellValue(row.getCell(57)))
            model.setPayRebateDate1(getCellValue(row.getCell(58)))
            model.setCostDate1(getCellValue(row.getCell(59)))
            model.setRebateChangedRebate(getCellValue(row.getCell(60)))
            model.setRebateChangedAmount(getCellValue(row.getCell(61)))
            model.setPayRebateChangedDate(getCellValue(row.getCell(62)))
            model.setCostDateChangedDate1(getCellValue(row.getCell(63)))
            model.setPromoteRebate(getCellValue(row.getCell(64)))
            model.setPromoteAmount(getCellValue(row.getCell(65)))
            model.setPayTime2(getCellValue(row.getCell(66)))
            model.setCostDate2(getCellValue(row.getCell(67)))
            model.setRebateAddedPoint(getCellValue(row.getCell(68)))
            model.setRebateAddedAmount(getCellValue(row.getCell(69)))
            model.setPayTime(getCellValue(row.getCell(70)))
            model.setDeliveryFee(getCellValue(row.getCell(71)))
            model.setTrueRebateAmount(getCellValue(row.getCell(72)))
            model.setRecommendReward(getCellValue(row.getCell(73)))
            model.setRebatePoint4(getCellValue(row.getCell(74)))
            model.setRebateAmount4(getCellValue(row.getCell(75)))
            model.setPayTime3(getCellValue(row.getCell(76)))
            model.setAfterTaxMargin(getCellValue(row.getCell(77)))
            model.setAfterMaxIncome(getCellValue(row.getCell(78)))
            model.setComment1(getCellValue(row.getCell(79)))
            model.setComment2(getCellValue(row.getCell(80)))

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
        return new InsuranceOfflineDataModel()
    }

}
