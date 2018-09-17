package com.cheche365.cheche.externalpayment.service

import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.serializer.converter.ArrayFieldsGenerator
import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.web.service.payment.PaymentService
import static com.cheche365.cheche.core.serializer.converter.ArrayFieldsGenerator.*
import static com.cheche365.cheche.externalpayment.util.BaoXianCode.QR_COPY_FIELDS
import static com.cheche365.cheche.externalpayment.util.BaoXianCode.INSURANCE_COPY_FIELDS
import static com.cheche365.cheche.externalpayment.util.BaoXianCode.newByTemplate
/**
 * Created by Administrator on 2018/3/7.
 */
class BotpyMergeInsurance {

    static final BOTPY_TO_CHECHE_INSURANCE_TYPE_MAPPING=[
        damage : 'damage',
        third : 'thirdParty',
        driver : 'driver',
        passenger : 'passenger',
        pilfer : 'theft',
        glass : 'glass',
        scratch : 'scratch',
        combust : 'spontaneous',
        third_party : 'unableFindThirdParty'
    ]


    static syncInsureBills(OrderRelatedService.OrderRelated or, Map insureInfo) {
        def newQR = newByTemplate(or.qr, QR_COPY_FIELDS)
        def icNos=insureInfo.ic_nos
        if(insureInfo?.biz_info?.total as Double){
             Insurance newInsurance = newByTemplate(or.insurance, INSURANCE_COPY_FIELDS )
             this.toInsurance(newInsurance, insureInfo?.biz_info)
             this.toInsurance(newQR, insureInfo?.biz_info)
             newInsurance.proposalNo = icNos?.biz_prop
             newInsurance.quoteRecord.quoteSourceId = icNos?.biz_quote
             or.toBePersist << newInsurance
        }

        if((insureInfo.force_info?.premium as Double) || (insureInfo.force_info?.tax as Double)) {
            def newCI = newByTemplate(or.ci, INSURANCE_COPY_FIELDS)
            toCI(newCI, insureInfo.force_info)
            toCI(newQR, insureInfo.force_info)
            newCI.proposalNo = icNos?.force_prop
            newCI.quoteRecord.quoteSourceId = icNos?.force_quote
            or.toBePersist << newCI
        }

        or.toBePersist << newQR

        or.persist()
    }

    static handleCommonFields(internalBill, externalBill){
        externalBill.start_date?.with{
            if(internalBill.properties.containsKey('effectiveDate')){
                internalBill.effectiveDate = Date.parse("yyyy-MM-dd", it)
            }

        }
        externalBill.end_date?.with{
            if(internalBill.properties.containsKey('expireDate')){
                internalBill.expireDate = Date.parse("yyyy-MM-dd", it)
            }

        }
        externalBill.policyNo?.with{
            if(internalBill.properties.containsKey('policyNo')){
                internalBill.policyNo = it as String
            }
        }

        internalBill.discount = externalBill.discount as Double
    }

    static toInsurance(internalModel, externalInsurance){
        handleCommonFields(internalModel, externalInsurance)
        internalModel.premium = externalInsurance.total as Double
        //internalModel.iopTotal = externalInsurance.nfcPremium as Double   //不计免赔待确认
        externalInsurance?.detail.each { externalFieldObj ->
            def internalName = BOTPY_TO_CHECHE_INSURANCE_TYPE_MAPPING.get(externalFieldObj.code)
            if(internalName){
                internalModel[toFullName(internalName, ArrayFieldsGenerator.FieldType.Premium)] = externalFieldObj.premium as Double

                if(hasAmount(internalName)){
                    internalModel[toFullName(internalName, ArrayFieldsGenerator.FieldType.Amount)] = externalFieldObj.amount as Double
                }

                if(hasIop(internalName)){
                    internalModel[toFullName(internalName, ArrayFieldsGenerator.FieldType.Iop)] = externalFieldObj.ncfPremium as Double
                }
            } else {
                logger.warn("金斗云回调非预期险种: ${externalFieldObj.code}")
            }
        }
        internalModel
    }

    static toCI(internalModel, externalCI){
        handleCommonFields(internalModel, externalCI)
        internalModel.compulsoryPremium = externalCI.premium as Double
        internalModel.autoTax = externalCI.tax as Double
    }

}
