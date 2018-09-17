package com.cheche365.cheche.partner.serializer

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.PartnerOrder
import com.cheche365.cheche.core.serializer.ModelFieldsCopier
import com.cheche365.cheche.core.serializer.converter.ArrayBillsGenerator
import com.cheche365.cheche.core.serializer.converter.ArrayFieldsGenerator
import com.cheche365.cheche.core.service.PurchaseOrderService
import com.cheche365.cheche.core.service.QuoteSupplementInfoService

/** *************************************************************************/
/*                              CommonBills.groovy                 */
/*   文  件 名: CommonBills.groovy                                 */
/*   模  块： 车险ToB平台                                                */
/*   功  能:  通用订单同步序列化结构                                   */
/*   初始创建:2016/7/12                                             */
/*   版本更新:V1.0                                                         */
/*   版权所有:北京车与车科技有限公司                                       */
/** *************************************************************************/

class CommonBills extends ModelFieldsCopier {


    static final CALCULATE_COPY_FIELDS = ["fields", "total"];


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
            'fields'    : ['orderNo', 'paidAmount', 'payableAmount', 'expireTime', 'status', 'applicant.mobile->mobile']
        ],

        [
            'sourcePath': ['partnerOrder'],
            'targetPath': '',
            'fields'    : ['partnerUser.partnerId->uid','state']
        ],

        [
            'sourcePath': [''],
            'targetPath': '',
            'fields'    : ['payUrl']
        ]
        ,
        [
            'sourcePath': ['partnerOrder.purchaseOrder.deliveryInfo'],
            'targetPath': 'deliveryInfo',
            'fields'    : ['expressCompany', 'trackingNo']
        ]
    ]

    private PurchaseOrderService purchaseOrderService
    private QuoteSupplementInfoService supplementInfoService

    CommonBills(PurchaseOrderService purchaseOrderService, QuoteSupplementInfoService supplementInfoService) {
        this.purchaseOrderService = purchaseOrderService
        this.supplementInfoService = supplementInfoService
    }

    CommonBills convert(PartnerOrder partnerOrder, rawData = null) {

        Insurance insurance = rawData?.insurance ?: this.purchaseOrderService.getInsuranceBillsByOrder(partnerOrder.getPurchaseOrder())
        CompulsoryInsurance ci = rawData?.compulsoryInsurance ?: this.purchaseOrderService.getCIBillByOrder(partnerOrder.getPurchaseOrder())

        ArrayBillsGenerator arrayGenerator = new ArrayBillsGenerator();
        arrayGenerator.toArray(ci, insurance, ArrayFieldsGenerator.GroupPolicy.Three);
        CALCULATE_COPY_FIELDS.each { this[it] = arrayGenerator[it] }

        def source = ["insurance": insurance, "ci": ci, "partnerOrder": partnerOrder]
        copyFields(source)
        this.expireTime = DateUtils.getDateString(partnerOrder.purchaseOrder.expireTime, DateUtils.DATE_LONGTIME24_PATTERN)
        this.createTime = DateUtils.getDateString(partnerOrder.purchaseOrder.createTime, DateUtils.DATE_LONGTIME24_PATTERN)

        return this
    }


    @Override
    fieldsMapping() {
        return FIELDS_MAPPING
    }

}
