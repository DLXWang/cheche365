package com.cheche365.cheche.manage.common.service.reverse.step

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.constants.AnswernConstant
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.service.ResourceService
import com.cheche365.cheche.core.util.BeanUtil
import com.cheche365.cheche.manage.common.service.reverse.OrderReverse
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * Created by yellow on 2017/11/6.
 */
@Service
@Slf4j
class GenerateInsurance implements TPlaceInsuranceStep {
    @Transactional
    @Override
    Object run(Object context) {
        log.debug("------生成保单------")
        OrderReverse model = context.model
        InsuranceRepository insuranceRepository = context.insuranceRepository
        CompulsoryInsuranceRepository compulsoryInsuranceRepository = context.compulsoryInsuranceRepository
        QuoteRecord quoteRecord = context.quoteRecord
        PurchaseOrder purchaseOrder = context.purchaseOrder
        ResourceService resourceService = context.resourceService
        Insurance insurance = insuranceRepository.findFirstByQuoteRecordOrderByCreateTimeDesc(quoteRecord)
        CompulsoryInsurance compulsoryInsurance = compulsoryInsuranceRepository.findFirstByQuoteRecordOrderByCreateTimeDesc(quoteRecord)

        if (model.getCommercialPremium() > 0) {
            insurance = insurance == null ? new Insurance() : insurance
            this.saveInsurance(model, insurance, purchaseOrder, quoteRecord, insuranceRepository, resourceService)
        } else {
            if (null != insurance)
                insuranceRepository.delete(insurance.id)
        }

        if (model.getCompulsoryPremium() > 0) {
            compulsoryInsurance = compulsoryInsurance == null ? new CompulsoryInsurance() : compulsoryInsurance
            this.saveCompulsoryInsurance(model, compulsoryInsurance, purchaseOrder, quoteRecord, compulsoryInsuranceRepository, resourceService)
        } else {
            if (null != compulsoryInsurance)
                compulsoryInsuranceRepository.delete(compulsoryInsurance.id)
        }
        getContinueFSRV true
    }


    def saveInsurance(OrderReverse model, Insurance insurance, PurchaseOrder purchaseOrder, QuoteRecord quoteRecord, InsuranceRepository insuranceRepository, ResourceService resourceService) {

        def contains = ["thirdPartyPremium",
                        "thirdPartyAmount", "damagePremium", "damageAmount", "theftPremium", "theftAmount", "enginePremium",
                        "engineAmount", "driverPremium", "driverAmount", "passengerPremium", "passengerAmount", "passengerCount", "spontaneousLossPremium",
                        "spontaneousLossAmount", "glassPremium", "glassAmount", "scratchAmount", "scratchPremium", "unableFindThirdPartyPremium", "damageIop", "thirdPartyIop",
                        "theftIop", "engineIop", "driverIop", "passengerIop", "spontaneousLossIop", "scratchIop", "insuredIdNo", "insuredName", "insuranceImage", "discount",
                        "applicantName", "applicantIdNo", "designatedRepairShopPremium"] as String[]
        BeanUtil.copyPropertiesContain(model, insurance, contains)
        insurance.setPremium(model.getCommercialPremium())
        insurance.setPolicyNo(model.getCommercialPolicyNo())
        insurance.setEffectiveDate(DateUtils.getDate(model.getCommercialEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN))
        insurance.setEffectiveHour(model.getCommercialEffectiveHour())
        insurance.setExpireDate(DateUtils.getDate(model.getCommercialExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN))
        insurance.setExpireHour(model.getCommercialExpireHour())
        InsuranceCompany insuranceCompany = quoteRecord.getInsuranceCompany()
        insurance.setInsuranceCompany(insuranceCompany)
        insurance.setInsuranceImage(getInsuranceImageUrl(model.getInsuranceImage(), resourceService))
        insurance.setIopTotal(model.getIop())
        insurance.setInsuredIdentityType(getIdType(model.getInsuredIdType()))
        insurance.setApplicantIdentityType(getIdType(model.getApplicantIdType()))
        /**
         * 如果是安心的话，需要设置停驶返现比例，否则停复驶失败
         * **/
        if (insuranceCompany == InsuranceCompany.Enum.ANSWERN_65000)
            insurance.setProportion(AnswernConstant.PROPORTION)
        if (insurance.getId() != null) {
            insurance.setUpdateTime(new Date())
        } else {
            insurance.setCreateTime(model.getApplicantDate())
        }
        Insurance.setInsuranceReferences(purchaseOrder, quoteRecord, purchaseOrder.getOperator(), insurance)

        insuranceRepository.save(insurance)
    }

    def saveCompulsoryInsurance(OrderReverse model, CompulsoryInsurance insurance, PurchaseOrder purchaseOrder, QuoteRecord quoteRecord, CompulsoryInsuranceRepository compulsoryInsuranceRepository, ResourceService resourceService) {
        def contains = ["compulsoryPremium", "autoTax", "insuredIdNo", "insuredName", "insuranceImage", "applicantName", "applicantIdNo"] as String[]
        BeanUtil.copyPropertiesContain(model, insurance, contains)
        insurance.setPolicyNo(model.getCompulsoryPolicyNo())
        insurance.setEffectiveDate(DateUtils.getDate(model.getCompulsoryEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN))
        insurance.setEffectiveHour(model.getCompulsoryEffectiveHour())
        insurance.setExpireDate(DateUtils.getDate(model.getCompulsoryExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN))
        insurance.setExpireHour(model.getCompulsoryExpireHour())
        insurance.setInsuranceImage(getInsuranceImageUrl(model.getCompulsoryInsuranceImage(), resourceService))
        insurance.setStamp(getInsuranceImageUrl(model.getCompulsoryStampFile(), resourceService))
        InsuranceCompany insuranceCompany = quoteRecord.getInsuranceCompany()
        insurance.setInsuranceCompany(insuranceCompany)
        insurance.setDiscount(model.getDiscountCI())
        insurance.setInsuredIdentityType(getIdType(model.getInsuredIdType()))
        insurance.setApplicantIdentityType(getIdType(model.getApplicantIdType()))
        if (insurance.getId() != null) {
            insurance.setUpdateTime(new Date())
        } else {
            insurance.setCreateTime(new Date())
        }
        CompulsoryInsurance.setCompulsoryInsuranceReferences(purchaseOrder, quoteRecord, purchaseOrder.getOperator(), insurance)
        compulsoryInsuranceRepository.save(insurance)
    }


    def getInsuranceImageUrl(String resourceAbsoluteUrl, ResourceService resourceService) {
        if (StringUtils.isBlank(resourceAbsoluteUrl)) {
            return null
        }
        String insurancePath = resourceService.getProperties().getInsurancePath()
        int index
        if ((index = resourceAbsoluteUrl.indexOf(insurancePath)) > -1) {
            return resourceAbsoluteUrl.substring(index, resourceAbsoluteUrl.length())
        }
        return null
    }

    def static getIdType(idType) {
        return idType ? idType : IdentityType.Enum.IDENTITYCARD
    }
}
