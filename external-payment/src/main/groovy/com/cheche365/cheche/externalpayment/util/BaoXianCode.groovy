package com.cheche365.cheche.externalpayment.util

import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.web.service.payment.PaymentService.OrderRelated
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static com.cheche365.cheche.core.serializer.converter.ArrayFieldsGenerator.*

/**
 * Created by tongsong on 2017/4/10 0010.
 */
public class BaoXianCode {

    private static final Logger logger = LoggerFactory.getLogger(BaoXianCode.class);

    def static final INSURANCE_COPY_FIELDS =  ['id', 'applicant', 'auto', 'quoteRecord', 'insuranceCompany', 'insurancePackage', 'createTime', 'insuredName', 'applicantName', 'applicantIdNo', 'applicantMobile', 'applicantEmail', 'insuredIdNo', 'insuredMobile', 'insuredEmail']
    def static final QR_COPY_FIELDS = ['id', 'applicant', 'auto', 'insuranceCompany', 'area', 'insurancePackage', 'createTime', 'channel', 'quoteFlowType', 'quoteSourceId', 'type']
    static final INSURE_FIELD_MAPPING = [
        VehicleDemageIns:'damage',
        ThirdPartyIns:'thirdParty',
        DriverIns:'driver',
        PassengerIns:'passenger',
        TheftIns:'theft',
        GlassIns:'glass',
        CombustionIns: 'spontaneousLoss',
        ScratchIns: 'scratch',
        WadingIns:'engine',
        VehicleDemageMissedThirdPartyCla:'unableFindThirdParty'
    ]

    def static toInsurance(internalModel, externalInsurance){
        handleCommonFields(internalModel, externalInsurance)
        internalModel.premium = externalInsurance.premium as Double
        internalModel.iopTotal = externalInsurance.nfcPremium as Double

        externalInsurance.riskKinds.each { externalFieldObj ->
            def internalName = INSURE_FIELD_MAPPING.get(externalFieldObj.riskCode)
            if(internalName){
                internalModel[toFullName(internalName, FieldType.Premium)] = externalFieldObj.premium as Double

                if(hasAmount(internalName)){
                    internalModel[toFullName(internalName, FieldType.Amount)] = externalFieldObj.amount as Double
                }

                if(hasIop(internalName)){
                    internalModel[toFullName(internalName, FieldType.Iop)] = externalFieldObj.ncfPremium as Double
                }
            } else {
                logger.warn("泛华回调非预期险种: ${externalFieldObj.riskCode}")
            }
        }
        internalModel
    }

    def static toCI(internalModel, externalCI, externalAT){
        if(externalCI){
            handleCommonFields(internalModel, externalCI)
            internalModel.compulsoryPremium = externalCI.premium as Double
        }
        if(externalAT){
            internalModel.autoTax = (externalAT.taxFee as Double)  + (externalAT.lateFee as Double)
        }
    }

    def static handleCommonFields(internalBill, externalBill){
        externalBill.startDate?.with{
            if(internalBill.properties.containsKey('effectiveDate')){
                internalBill.effectiveDate = Date.parse("yyyy-MM-dd", it)
            }

        }
        externalBill.endDate?.with{
            if(internalBill.properties.containsKey('expireDate')){
                internalBill.expireDate = Date.parse("yyyy-MM-dd", it)
            }

        }
        externalBill.policyNo?.with{
            if(internalBill.properties.containsKey('policyNo')){
                internalBill.policyNo = it as String
            }
        }

        internalBill.discount = externalBill.discountRate as Double
    }

    def static syncBills(insureInfo, OrderRelated or) {
        def newQR = newByTemplate(or.qr, QR_COPY_FIELDS)
        if(insureInfo.bizInsureInfo?.premium as Double){
            def newInsurance = newByTemplate(or.insurance, INSURANCE_COPY_FIELDS)
            toInsurance(newInsurance, insureInfo.bizInsureInfo)
            toInsurance(newQR, insureInfo.bizInsureInfo)
            or.toBePersist << newInsurance
        }

        if((insureInfo.efcInsureInfo?.premium as Double) || (insureInfo.taxInsureInfo?.taxFee as Double)) {
            def newCI = newByTemplate(or.ci, INSURANCE_COPY_FIELDS)
            toCI(newCI, insureInfo.efcInsureInfo, insureInfo.taxInsureInfo)
            toCI(newQR, insureInfo.efcInsureInfo, insureInfo.taxInsureInfo)
            or.toBePersist << newCI
        }

        or.toBePersist << newQR

        or.persist()
    }

    def static newByTemplate(def template, List fields){
        template.getClass().newInstance().with { newObj ->
            fields.each {
                newObj[it] = template[it]
            }
            newObj
        }
    }

    public static void setExpress(def delivery,PurchaseOrder purchaseOrder){
        DeliveryInfo deliveryInfo = purchaseOrder.deliveryInfo
        logger.info("泛华快递地址为:{}",delivery.name);
        logger.info("泛华快递姓名为:{}",delivery.address);
        logger.info("泛华快递电话为:{}",delivery.phone);
        Date nowTime = new Date();
        purchaseOrder.trackingNo = delivery.expressNo
        if(null==deliveryInfo){
            logger.info("泛华订单:{} 创建deliveryInfo",purchaseOrder.orderNo);
            deliveryInfo = new DeliveryInfo();
            deliveryInfo.createTime = nowTime;
        }
        purchaseOrder.setDeliveryInfo(deliveryInfo);
        deliveryInfo.expressCompany = delivery.expressCompanyName;
        deliveryInfo.trackingNo = delivery.expressNo;
        deliveryInfo.updateTime = nowTime;
        deliveryInfo.operator = purchaseOrder.operator;
    }

}
