package com.cheche365.cheche.externalpayment.handler

import com.cheche365.cheche.core.serializer.converter.ArrayFieldsGenerator
import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.externalpayment.model.BotpyBodyInsurance
import com.cheche365.cheche.externalpayment.model.BotpyCallBackBody
import com.cheche365.cheche.web.service.payment.PaymentService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.serializer.converter.ArrayFieldsGenerator.*

/**
 * Created by Administrator on 2018/3/28.
 */
@Service
class BotpyMergeInsuranceHandler {

    private static final Logger logger = LoggerFactory.getLogger(BotpyMergeInsuranceHandler.class);

    List insuranceFieldsCopy() {
        ['id', 'applicant', 'auto', 'quoteRecord', 'insuranceCompany', 'insurancePackage', 'createTime', 'insuredName', 'applicantName', 'applicantIdNo', 'applicantMobile', 'applicantEmail', 'insuredIdNo', 'insuredMobile', 'insuredEmail']
    }
    List qrFieldsCopy(){
        ['id', 'applicant', 'auto', 'insuranceCompany', 'area', 'insurancePackage', 'createTime', 'channel', 'quoteFlowType', 'quoteSourceId', 'type']
    }
    Map insuranceType(){
        [
            damage:'damage',
            third:'thirdParty',
            driver:'driver',
            passenger:'passenger',
            pilfer:'theft',
            glass:'glass',
            scratch:'scratch',
            combust: 'spontaneousLoss',
            third_party:'unableFindThirdParty',
            water:'engine',
            exempt_damage : 'damage',
            exempt_third : 'thirdParty',
            exempt_driver : 'driver',
            exempt_passenger : 'passenger',
            exempt_pilfer : 'theft',
            exempt_scratch : 'scratch',
            exempt_combust : 'spontaneousLoss'
        ]
    }

    def syncBills(BotpyCallBackBody callBackBody, OrderRelatedService.OrderRelated or) {
        def newQR = newByTemplate(or.qr, qrFieldsCopy())
        if(callBackBody.insurance() && callBackBody.premium() && callBackBody.premium()!=or.insurance.premium){
            logger.info("商业险保费发生变化，更新本地商业险保单信息")
            def newInsurance = newByTemplate(or.insurance, insuranceFieldsCopy())
            toInsurance(newInsurance, callBackBody)
            toInsurance(newQR, callBackBody)
            or.toBePersist << newInsurance
        }

        if(callBackBody.ci() && (callBackBody.ciPremium() && or.ci.compulsoryPremium || callBackBody.ciAutoTax()) ) {
            logger.info("交强险保费发生变化，更新本地交强险保单信息")
            def newCI = newByTemplate(or.ci, insuranceFieldsCopy())
            toCI(newCI, callBackBody)
            toCI(newQR, callBackBody)
            or.toBePersist << newCI
        }


        or.toBePersist << newQR

        or.persist()
    }

    def handleCommonFields(internalBill, BotpyCallBackBody callBackBody){
        callBackBody.startDate()?.with{
            if(internalBill.properties.containsKey('effectiveDate')) {
                internalBill.effectiveDate = callBackBody.startDate()
            }
        }
        callBackBody.endDate()?.with{
            if(internalBill.properties.containsKey('expireDate')) {
                internalBill.expireDate = callBackBody.endDate()
            }
        }
        callBackBody.insurancePolicyNo()?.with{
            if(internalBill.properties.containsKey('policyNo')) {
                internalBill.policyNo = callBackBody.insurancePolicyNo()
            }
        }

        internalBill.discount = callBackBody.discount()
    }



    def toInsurance(internalModel, BotpyCallBackBody callBackBody){
        handleCommonFields(internalModel, callBackBody)
        internalModel.premium = callBackBody.premium()
        internalModel.iopTotal = callBackBody.premium()

        callBackBody.insures().each { BotpyBodyInsurance bodyInsurance ->
            def internalName = insuranceType().get(bodyInsurance.code())
            if(internalName){
                internalModel[toFullName(internalName, ArrayFieldsGenerator.FieldType.Premium)] = bodyInsurance.premium()
                if(hasAmount(internalName)){
                    internalModel[toFullName(internalName, ArrayFieldsGenerator.FieldType.Amount)] = bodyInsurance.amount()
                }

                if(hasIop(internalName)){
                    internalModel[toFullName(internalName, ArrayFieldsGenerator.FieldType.Iop)] = bodyInsurance.premium()
                }
            } else {
                logger.warn("金斗云回调非预期险种: ${bodyInsurance.code()}")
            }
        }
        internalModel
    }



    def toCI(internalModel, BotpyCallBackBody callBackBody){
        if(callBackBody.ci()){
            handleCommonFields(internalModel, callBackBody)
            internalModel.compulsoryPremium = callBackBody.ciPremium()
        }
        if(callBackBody.ciAutoTax()){
            internalModel.autoTax = callBackBody.ciAutoTax()
        }
    }

    def newByTemplate(def template, List fields){
        template.getClass().newInstance().with { newObj ->
            fields.each {
                newObj[it] = template[it]
            }
            newObj
        }
    }
}
