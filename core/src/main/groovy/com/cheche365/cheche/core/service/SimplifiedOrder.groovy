package com.cheche365.cheche.core.service

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.serializer.ModelFieldsCopier
import com.cheche365.cheche.core.serializer.converter.ArrayQuoteRecord
import com.cheche365.cheche.core.util.AutoUtils
import org.springframework.stereotype.Service

import java.text.DecimalFormat

import static com.cheche365.cheche.core.model.OrderStatus.Enum.REFUNDED_9
import static com.cheche365.cheche.core.model.OrderStatus.Enum.REFUNDING_10

/**
 * Created by tongsong on 2016/11/30 0030.
 * 简化后的订单对象，包括订单、保单、支付信息
 */
@Service
class SimplifiedOrder extends ModelFieldsCopier {

    static final CALCULATE_COPY_FIELDS = ["fields", "total", "discounts"];
    static final DOUBLE_SERIALIZER = {value->  value ?  new DecimalFormat("0.00").format(value) : ''}

    static final FIELDS_MAPPING = [
        [
            'sourcePath': ['purchaseOrder'],
            'targetPath': 'purchaseOrder',
            'fields'    : ['createTime', 'updateTime', 'status',
                           [
                               name : 'payableAmount',
                               formatter : DOUBLE_SERIALIZER,
                           ],[
                               name : 'paidAmount',
                               formatter : DOUBLE_SERIALIZER,
                           ],
                           'channel', 'orderNo', 'trackingNo',
                           'insuredName', 'insuredIdNo', 'insuredIdentityType','applicantName', 'applicantIdNo', 'applicantIdentityType','ownerMobile', 'deliveryInfo', 'expireTime']
        ],
        [
            'sourcePath': ['purchaseOrder.auto.area'],
            'targetPath': 'purchaseOrder.auto.area',
            'fields'    : ['id', 'name', 'shortCode', 'postalCode']
        ],
        [
            'sourcePath': ['purchaseOrder.area'],
            'targetPath': 'purchaseOrder.area',
            'fields'    : ['id', 'name', 'shortCode', 'postalCode']
        ],
        [
            'sourcePath': ['purchaseOrder.auto'],
            'targetPath': 'purchaseOrder.auto',
            'fields'    : ['id', 'licensePlateNo', 'engineNo', 'owner', 'vinNo', 'enrollDate', 'identity', 'insuredIdNo', 'fuelType', 'useCharacter']
        ],
        [
            'sourcePath': ['purchaseOrder.auto.identityType'],
            'targetPath': 'purchaseOrder.auto.identityType',
            'fields'    : ['id', 'name', 'description']
        ],
        [
            'sourcePath': ['purchaseOrder.auto.autoType'],
            'targetPath': 'purchaseOrder.auto.autoType',
            'fields'    : ['seats', 'code', 'supplementInfo']
        ],
        [
            'sourcePath': ['purchaseOrder.deliveryAddress'],
            'targetPath': 'purchaseOrder.deliveryAddress',
            'fields'    : ['id', 'area', 'street', 'district', 'districtName', 'city', 'cityName', 'province', 'provinceName', 'name', 'telephone', 'mobile']
        ],
        [
            'sourcePath': ['quoteRecord.insuranceCompany'],
            'targetPath': 'quoteRecord.insuranceCompany',
            'fields'    : ['id', 'name', 'code', 'logoUrl']
        ],
        [
            'sourcePath': ['quoteRecord'],
            'targetPath': 'quoteRecord',
            'fields'    : ['insurancePackage']
        ],

        [
            'sourcePath': ['customerPayments'],
            'targetPath': 'purchaseOrder.payments',
            'fields'    : ['id', 'createTime', 'updateTime', 'amount', 'channel', 'status', 'paymentType']
        ],
        [
            'sourcePath': ['gifts'],
            'targetPath': 'purchaseOrder.gifts',
            'fields'    : ['giftAmount', 'giftDisplay', 'giftType']
        ],
        [
            'sourcePath': [''],
            'targetPath': 'purchaseOrder',
            'fields'    : [
                            'orderAmended',
                            'amendAmount',
                            'needUploadImage',
                            'showImageTab',
                            'showDailyInsurance',
                            'reinsure',
                            'customer',
                            'innerPay',
                            'supportRequote',
                            'canRenewal',
                            'specialAgreement',
                            [
                                name: 'signLink',
                                ignore: {source -> !(source?.signLink)}

                            ],
            ]
        ],
        [
            'sourcePath': ['dateInfo'],
            'targetPath': 'quoteRecord.base',
            'fields'    : ['effectiveDate', 'expireDate']
        ],
        [
            'sourcePath': ['dateInfo'],
            'targetPath': 'quoteRecord.compulsory',
            'fields'    : ['compulsoryEffectiveDate->effectiveDate', 'compulsoryExpireDate->expireDate']
        ],
        [
            'sourcePath': [''],
            'targetPath': 'purchaseOrder',
            'fields'    : ['payWarningSupport', 'needPayWarning']
        ]
    ]

    SimplifiedOrder convert(Map<String, Object> map) {
        copyFields(map)
        ArrayQuoteRecord arrayGenerator = new ArrayQuoteRecord().convert(map["quoteRecord"])
        CALCULATE_COPY_FIELDS.each { this["quoteRecord"][it] = arrayGenerator[it] }

        AutoUtils.encrypt(AutoUtils.AUTO_ENCRYPT_PROPS, this.purchaseOrder.auto, Auto.PROPERTIES)
        this.purchaseOrder.customerPayments?.each { AutoUtils.toDisplayText(it) }

        def answernRestart = map.purchaseOrder.answernFinished(map.quoteRecord)
        this.purchaseOrder.expireTime = answernRestart ? null : (DateUtils.getDateString(map.purchaseOrder.expireTime, DateUtils.DATE_LONGTIME24_PATTERN))

        if (map.purchaseOrder.statusDisplay) {//泛华订单前端展示
            this.purchaseOrder.status = map.purchaseOrder.statusDisplay()
        }
        return this;
    }

    @Override
    def fieldsMapping() {
        return FIELDS_MAPPING
    }
}
