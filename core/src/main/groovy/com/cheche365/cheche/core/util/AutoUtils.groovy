package com.cheche365.cheche.core.util

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.InsuranceBasicInfo
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.repository.AreaRepository
import org.apache.commons.lang3.StringUtils

import java.beans.PropertyDescriptor

import static com.cheche365.cheche.core.constants.ModelConstants._CHARTERED_CITIES_OF_CHINA
import static com.cheche365.cheche.core.model.AreaType.Enum.CITY_3
import static com.cheche365.cheche.core.model.AreaType.Enum.MUNICIPALITY_2
import static com.cheche365.cheche.core.model.Area.Enum.findByTypeAndShortCode
import static com.cheche365.cheche.core.model.PaymentStatus.Enum.*
import static com.cheche365.cheche.core.model.PaymentType.Enum.PAY_TYPES
import static com.cheche365.cheche.core.model.PaymentType.Enum.REFUND_TYPES

/**
 * auto工具类
 * Created by liheng on 2016/11/9.
 */
class AutoUtils {

    static DISPLAY_TEXT_MAPPING

    static  ENCRYPT_CONFIG = [
        "owner"      : [
            minLength: 1,
            between : 1..-2
        ],
        "identity"   :  [
            minLength: 6,
            between : 3..-4
        ],
        "engineNo"   :  [
            minLength: 4,
            between : 2..-3
        ],
        "vinNo"      : [
            minLength: 6,
            between : 3..-4
        ],
        "insuredIdNo"      : [
            minLength: 13,
            between : 9..13
        ]
    ]


    static AUTO_ENCRYPT_PROPS = ["identity", "engineNo", "vinNo", "insuredIdNo"]

    static VEHICLE_LICENSE_ENCRYPT_PROPS = ["engineNo", "vinNo"] //行驶证查询用户输入车牌、姓名、身份证，只需加密车架号、发动机号
    static VEHICLE_LICENSE_ENCRYPT_PROPS_NEW = ["engineNo", "vinNo","owner","identity"] //行驶证查询用户输入车牌、姓名、身份证，只需加密车架号、发动机号、车主姓名、身份证号  ;v1.6版本以后的加密字段

    static ORDER_AUTO = ["applicantIdNo":"identity",
                         "applicantName":"owner",
                         "insuredIdNo":"identity",
                         "insuredName":"owner"]
    static ALL_POSSIBLE_FIELDS = (AUTO_ENCRYPT_PROPS + VEHICLE_LICENSE_ENCRYPT_PROPS_NEW).unique()

    static PERSONNEL_INFO_PROPS = ['applicantIdNo', 'applicantName', 'insuredIdNo', 'insuredName']

    /**
     * 加密信息(中间以*代替)
     */
    static void encrypt(properties, object, PropertyDescriptor[] propertyDescriptors) {
        if (object == null) {
            return
        }

        properties.each { prop ->
            if (object instanceof Map) {
                object.put(prop, encryptionWithStar(object.get(prop), prop))
            } else {
                propertyDescriptors.find { it.name == prop }.with { descriptor ->
                    String original = descriptor.getReadMethod().invoke(object)
                    if (original) {
                        descriptor.getWriteMethod().invoke(object, encryptionWithStar(original, prop))
                    }
                }
            }
        }
    }

    /**
     * 解密信息
     */
    static void decrypt(Auto unencryptedAuto, Auto encryptedAuto) {
        VEHICLE_LICENSE_ENCRYPT_PROPS_NEW.each { prop ->
            Auto.PROPERTIES.find { it.name == prop }.with { descriptor ->
                String originalProp = descriptor.getReadMethod().invoke(unencryptedAuto)
                String encryptedProp = descriptor.getReadMethod().invoke(encryptedAuto)
                if (encryptedProp?.contains("*") && encryptedProp == encryptionWithStar(originalProp, prop)) {
                    descriptor.getWriteMethod().invoke(encryptedAuto, originalProp)
                }
            }
        }
    }

    /**
     * 解密信息
     */
    static void decrypt(PurchaseOrder po,Auto unencryptedAuto) {
        ORDER_AUTO.each {key,value ->
                PurchaseOrder.PO_DESCRIPTOR.find{it.name == key}.with {descriptor ->

                String encryptedProp = descriptor.getReadMethod().invoke(po)

                if(encryptedProp?.contains("*")){
                    Auto.PROPERTIES.find{it.name ==value }.with { prop ->
                        String original = prop.getReadMethod().invoke(unencryptedAuto)
                        descriptor.getWriteMethod().invoke(po,original)
                    }
                }
             }
        }

    }

    /**
     * 加密信息
     */
    static void encrypt(InsuranceBasicInfo insuranceBasicInfo) {
        PERSONNEL_INFO_PROPS.each {
            insuranceBasicInfo[it] = encryptionWithStar insuranceBasicInfo[it], ORDER_AUTO[it]
        }
    }

    /**
     * 解密信息
     */
    static void decrypt(PurchaseOrder po, InsuranceBasicInfo insuranceBasicInfo) {
        PERSONNEL_INFO_PROPS.each {
            po[it] = po[it]?.contains('*') ? insuranceBasicInfo[it] : po[it]
        }
    }

    static String encryptionWithStar(String original, String property) {
        def key = ENCRYPT_CONFIG.get(property)
        if (StringUtils.isBlank(original) || original.length() < key.minLength || containStarChars(original)  ) {
            return original
        }

        return (original[0..key.between[0]-1]
                +  '*' * (original.length()-key.minLength)
                + (key.between[-1]==-2  ?  '' : original[key.between[-1]+1..-1]))
    }

    static boolean isAutoContainStarChars(Auto auto) {
        if (auto == null) {
            return false
        }
        for (prop in ALL_POSSIBLE_FIELDS) {
            Boolean containStarChar = Auto.PROPERTIES.find { it.name == prop }.with { descriptor ->
                (descriptor.getReadMethod().invoke(auto) as String)?.contains("*")
            }
            if (containStarChar) {
                return true
            }
        }
        return false
    }

    static boolean isContainStrChars(PurchaseOrder po){

        for (prop in ORDER_AUTO.keySet()) {
            Boolean containStarChar = PurchaseOrder.PO_DESCRIPTOR.find { it.name == prop }.with { descriptor ->
                (descriptor.getReadMethod().invoke(po) as String)?.contains("*")
            }
            if (containStarChar) {
                return true
            }
        }
        return false
    }
    static boolean containStarChars(String param) {
        return param?.contains("*")
    }

    /**
     * 返回车辆的所属地区
     */
    static Area getAreaOfAuto(String licensePlateNo) {
        if (!licensePlateNo) {
            return null
        }
        def charteredCities = _CHARTERED_CITIES_OF_CHINA
        def type = CITY_3.id
        def shortCode = licensePlateNo[0..1]
        if (licensePlateNo[0] in charteredCities) {
            type = MUNICIPALITY_2.id
            shortCode = licensePlateNo[0]
        }

        findByTypeAndShortCode(type, shortCode)
    }

    static void toDisplayText(payment) {
        if (!DISPLAY_TEXT_MAPPING) {  //由于依赖PaymentStatus和PaymentType初始化结束后才能初始化这部分，所以不能写到static块中
            synchronized (AutoUtils.class) {
                if (!DISPLAY_TEXT_MAPPING) {  //加锁后检查
                    DISPLAY_TEXT_MAPPING = [
                        (PAY_TYPES)   : [
                            (NOTPAYMENT_1)    : '等待付款',
                            (PAYMENTSUCCESS_2): '已支付',
                            (PAYMENTFAILED_3) : '支付失败'
                        ],
                        (REFUND_TYPES): [
                            (NOTPAYMENT_1)    : '退款中',
                            (PAYMENTSUCCESS_2): '退款成功',
                            (PAYMENTFAILED_3) : '退款失败'
                        ]
                    ]
                }
            }

        }

        def displayText = DISPLAY_TEXT_MAPPING.find {it.key.id.contains(payment.paymentType.id)}?.value.find{it.key.id == payment.status.id}?.value
        if(displayText){
            payment.status = payment.status.clone()
            payment.status.status = displayText
        }
    }

}
