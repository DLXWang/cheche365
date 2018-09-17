package com.cheche365.cheche.partner.serializer

import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.serializer.ModelFieldsCopier

import static com.cheche365.cheche.common.util.DateUtils.DATE_LONGTIME24_PATTERN
import static com.cheche365.cheche.common.util.DateUtils.getDateString
import static com.cheche365.cheche.common.util.DoubleUtils.displayDoubleValue
import static com.cheche365.cheche.common.util.DoubleUtils.sub
import static com.cheche365.cheche.core.model.OrderStatus.Enum.isInsureFailure
import static java.lang.Boolean.TRUE

/**
 * Created by liheng on 2018/9/6 0006.
 */
class APIPlaceOrder extends ModelFieldsCopier {

    APIPlaceOrder generatePlaceOrderResult(context, PurchaseOrder order = context.order) {
        copyFields context + [
            discountAmount: displayDoubleValue(sub(order.payableAmount, order.payableAmount)),
            insureFailure : isInsureFailure(order.status),
            payableAmount : displayDoubleValue(order.payableAmount),
            paidAmount    : displayDoubleValue(order.paidAmount),
            createTime    : getDateString(order.createTime, DATE_LONGTIME24_PATTERN),
            expireTime    : getDateString(order.expireTime, DATE_LONGTIME24_PATTERN),
            reinsure      : TRUE == context?.additionalParameters?.reInsure
        ]
        this
    }

    @Override
    def fieldsMapping() {
        [
            [
                sourcePath: ['insurance', 'compulsoryInsurance'],
                targetPath: '',
                fields    : ['applicantEmail', 'applicantIdNo', 'applicantMobile', 'applicantName', 'insuredEmail', 'insuredIdNo', 'insuredMobile', 'insuredName']
            ],
            [
                sourcePath: ['insurance.applicantIdentityType', 'compulsoryInsurance.applicantIdentityType'],
                targetPath: 'applicantIdentityType',
                fields    : ['id', 'name', 'description']
            ],
            [
                sourcePath: ['insurance.insuredIdentityType', 'compulsoryInsurance.insuredIdentityType'],
                targetPath: 'insuredIdentityType',
                fields    : ['id', 'name', 'description']
            ],
            [
                sourcePath: ['insurance.auto', 'compulsoryInsurance.auto'],
                targetPath: 'auto',
                fields    : ['autoType.code->autoType', 'engineNo', 'enrollDate', 'identity', 'licensePlateNo', 'owner', 'vinNo']
            ],
            [
                sourcePath: ['insurance.auto.area', 'compulsoryInsurance.auto.area'],
                targetPath: 'auto.area',
                fields    : ['id', 'name', 'shortCode']
            ],
            [
                sourcePath: ['insurance.auto.identityType', 'compulsoryInsurance.auto.identityType'],
                targetPath: 'auto.identityType',
                fields    : ['id', 'name', 'description']
            ],
            [
                sourcePath: ['insurance.insuranceCompany', 'compulsoryInsurance.insuranceCompany'],
                targetPath: 'insuranceCompany',
                fields    : ['id', 'code', 'name']
            ],
            [
                sourcePath: ['insurance.insurancePackage', 'compulsoryInsurance.insurancePackage'],
                targetPath: 'insurancePackage',
                fields    : ['autoTax', 'compulsory', 'damage', 'damageIop', 'designatedRepairShop', 'driverAmount', 'driverIop', 'engine', 'engineIop', 'glass', 'glassType', 'glassTypeId', 'iopTotal', 'passengerAmount', 'passengerIop', 'scratchAmount', 'scratchIop', 'spontaneousLoss', 'spontaneousLossIop', 'theft', 'theftIop', 'thirdPartyAmount', 'thirdPartyIop', 'unableFindThirdParty']
            ],
            [
                sourcePath: ['order'],
                targetPath: '',
                fields    : ['orderNo->purchaseOrderNo', 'status']
            ],
            [
                sourcePath: ['order.deliveryAddress'],
                targetPath: 'deliveryAddress',
                fields    : ['city', 'cityName', 'description', 'district', 'districtName', 'mobile', 'name', 'postalcode', 'province', 'provinceName', 'street']
            ],
            [
                sourcePath: ['insurance'],
                targetPath: 'insurance',
                fields    : ['damageAmount', 'damageIop', 'damagePremium', 'designatedRepairShopPremium', 'discount', 'driverAmount', 'driverIop', 'driverPremium', 'effectiveDate', 'effectiveHour', 'engineAmount', 'engineIop', 'enginePremium', 'expireDate', 'expireHour', 'glassAmount', 'glassPremium', 'iopTotal', 'originalPolicyNo', 'passengerAmount', 'passengerCount', 'passengerIop', 'passengerPremium', 'policyNo', 'premium', 'proportion', 'proposalNo', 'scratchAmount', 'scratchIop', 'scratchPremium', 'specialAgreement', 'spontaneousLossAmount', 'spontaneousLossIop', 'spontaneousLossPremium', 'status', 'theftAmount', 'theftIop', 'theftPremium', 'thirdPartyAmount', 'thirdPartyIop', 'thirdPartyPremium', 'unableFindThirdPartyPremium']
            ],
            [
                sourcePath: ['compulsoryInsurance'],
                targetPath: 'compulsoryInsurance',
                fields    : ['annotations', 'autoTax', 'ciStatus', 'compulsoryPremium', 'discount', 'effectiveDate', 'effectiveHour', 'expireDate', 'expireHour', 'institution', 'originalPolicyNo', 'policyNo', 'proposalNo', 'stamp', 'status']
            ],
            [
                sourcePath: [''],
                targetPath: '',
                fields    : ['discountAmount', 'insureFailure', 'payableAmount', 'paidAmount', 'createTime', 'expireTime', 'reinsure', 'payUrl']
            ]
        ]
    }
}
