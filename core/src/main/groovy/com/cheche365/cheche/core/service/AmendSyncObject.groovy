package com.cheche365.cheche.core.service

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.serializer.ModelFieldsCopier
import com.cheche365.cheche.core.serializer.converter.ArrayBillsGenerator
import com.cheche365.cheche.core.serializer.converter.ArrayFieldsGenerator
import com.cheche365.cheche.core.util.CacheUtil

import static com.cheche365.cheche.core.model.OrderStatus.Enum.PAID_3
import static com.cheche365.cheche.core.model.OrderStatus.Enum.PENDING_PAYMENT_1
import static com.cheche365.cheche.core.model.PaymentStatus.Enum.NOTPAYMENT_1
import static com.cheche365.cheche.core.model.PaymentType.Enum.ADDITIONALPAYMENT_2
import static com.cheche365.cheche.core.model.PaymentType.Enum.PARTIALREFUND_3

/**
 * Created by zhengwei on 2/10/17.
 */
class AmendSyncObject extends ModelFieldsCopier {

    static OrderStatus ADDITIONAL_PAYMENT = [id: 201, status: "增补待支付", description: "增补待支付"]
    private static OrderStatus PARTIAL_REFUND = [id: 202, status: "部分退款", description: "部分退款"]

    static final CALCULATE_COPY_FIELDS = ["fields", "total"]
    static final FIELDS_MAPPING = [

        [
            'sourcePath': ['ci.insuranceCompany', 'insurance.insuranceCompany'],
            'targetPath': 'insuranceCompany',
            'fields'    : ['id', 'name', 'code']
        ],

        [
            'sourcePath': ['ci.auto', 'insurance.auto'],
            'targetPath': 'auto',
            'fields'    : ['licensePlateNo', 'engineNo', 'owner', 'vinNo', 'enrollDate', 'area.id', 'area.name', 'autoType.code->model', 'identity']
        ],

        [
            'sourcePath': ['ci.auto.identityType', 'insurance.auto.identityType'],
            'targetPath': 'auto.identityType',
            'fields'    : ['id', 'name', 'description']
        ],

        [
            'sourcePath': ['insurance'],
            'targetPath': 'insurance',
            'fields'    : ['proposalNo', 'policyNo', 'effectiveDate', 'expireDate']
        ],

        [
            'sourcePath': ['ci'],
            'targetPath': 'compulsoryInsurance',
            'fields'    : ['proposalNo', 'policyNo', 'effectiveDate', 'expireDate']
        ],

        [
            'sourcePath': ['ci', 'insurance'],
            'targetPath': 'insuredPerson',
            'fields'    : ['insuredIdNo', 'insuredName', 'applicant.mobile->insuredMobile']
        ],

        [
            'sourcePath': ['ci.insuredIdentityType', 'insurance.insuredIdentityType'],
            'targetPath': 'insuredPerson.insuredIdentityType',
            'fields'    : ['id', 'name', 'description']
        ],

        [
            'sourcePath': ['ci', 'insurance'],
            'targetPath': 'applicant',
            'fields'    : ['applicantIdNo', 'applicantName', 'applicant.mobile->applicantMobile']
        ],

        [
            'sourcePath': ['ci.applicantIdentityType', 'insurance.applicantIdentityType'],
            'targetPath': 'applicant.applicantIdentityType',
            'fields'    : ['id', 'name', 'description']
        ],

        [
            'sourcePath': ['partnerOrder.purchaseOrder.deliveryAddress'],
            'targetPath': 'deliveryAddress',
            'fields'    : ['address', 'name', 'mobile']
        ],

        [
            'sourcePath': ['partnerOrder.purchaseOrder'],
            'targetPath': '',
            'fields'    : ['orderNo', 'paidAmount', 'payableAmount', 'expireTime', 'status']
        ],

        [
            'sourcePath': ['partnerOrder'],
            'targetPath': '',
            'fields'    : ['partnerUser.partnerId->uid','state']
        ],

        [
            'sourcePath': ['payments'],
            'targetPath': 'payments',
            'fields'    : ['id', 'amount', 'status', 'paymentType']
        ]
        ,
        [
            'sourcePath': ['partnerOrder.purchaseOrder.deliveryInfo'],
            'targetPath': 'deliveryInfo',
            'fields'    : ['expressCompany', 'trackingNo']
        ],
        [
            'sourcePath': [''],
            'targetPath': '',
            'fields'    : ['payUrl']
        ]
    ]

    PurchaseOrderService purchaseOrderService

    public AmendSyncObject(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService
    }

    def convert(PartnerOrder partnerOrder, List<Payment> payments,Object rawData=null) {

        Insurance insurance = getInsurance(partnerOrder.getPurchaseOrder(),rawData)
        CompulsoryInsurance ci = getCompulsoryInsurance(partnerOrder.getPurchaseOrder(),rawData)

        ArrayBillsGenerator arrayGenerator = new ArrayBillsGenerator();
        arrayGenerator.toArray(ci, insurance, ArrayFieldsGenerator.GroupPolicy.Three);
        CALCULATE_COPY_FIELDS.each { this[it] = arrayGenerator[it] }

        copyFields(["insurance": insurance, "ci": ci, "partnerOrder": partnerOrder, "payments": payments])

        this.expireTime = DateUtils.getDateString(partnerOrder.purchaseOrder.expireTime, DateUtils.DATE_LONGTIME24_PATTERN)
        processAmendOrderStatus(this, partnerOrder.purchaseOrder, payments)

        return this
    }

    Insurance getInsurance(PurchaseOrder purchaseOrder,Object rawData){

        if (rawData?.insurance&& CacheUtil.doJacksonDeserialize(CacheUtil.doJacksonSerialize(rawData.insurance),Insurance)){
            return  CacheUtil.doJacksonDeserialize(CacheUtil.doJacksonSerialize(rawData.insurance),Insurance)
        }
        return this.purchaseOrderService.getInsuranceBillsByOrder(purchaseOrder)
    }

    CompulsoryInsurance getCompulsoryInsurance(PurchaseOrder purchaseOrder,Object rawData){
        if(rawData?.compulsoryInsurance&& CacheUtil.doJacksonDeserialize(CacheUtil.doJacksonSerialize(rawData.compulsoryInsurance),CompulsoryInsurance)){
            return CacheUtil.doJacksonDeserialize(CacheUtil.doJacksonSerialize(rawData.compulsoryInsurance),CompulsoryInsurance)
        }
        return this.purchaseOrderService.getCIBillByOrder(purchaseOrder);
    }

    def processAmendOrderStatus(AmendSyncObject amendSyncObject, PurchaseOrder purchaseOrder, List<Payment> payments) {
        Boolean additionalPayment = (PENDING_PAYMENT_1 == purchaseOrder.status) && payments.findAll {
            ADDITIONALPAYMENT_2 == it.paymentType && NOTPAYMENT_1 == it.status
        }
        Boolean partialRefund = (PAID_3 == purchaseOrder.status) && payments.findAll {
            PARTIALREFUND_3 == it.paymentType && NOTPAYMENT_1 == it.status
        }
        amendSyncObject.status = additionalPayment ? ADDITIONAL_PAYMENT : (partialRefund ? PARTIAL_REFUND : amendSyncObject.status)
    }

    @Override
    def fieldsMapping() {
        FIELDS_MAPPING
    }
}
