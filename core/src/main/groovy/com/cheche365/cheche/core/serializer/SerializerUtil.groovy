package com.cheche365.cheche.core.serializer

import com.cheche365.cheche.common.util.DoubleUtils
import com.cheche365.cheche.core.model.DailyInsurance
import com.cheche365.cheche.core.model.InsurancePackage
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.VehicleLicense

import java.text.DecimalFormat

/**
 * Created by zhengwei on 2/23/17.
 */
class SerializerUtil {

    static final Closure STRING_CONVERTER =   { originalValue ->  if( Integer.valueOf(originalValue) == 1)  "国产"    else if(Integer.valueOf(originalValue) == 2) "进口"   else   "未知" }
    static final Closure BOOLEAN_CONVERTER =   { originalValue ->  Integer.valueOf(originalValue)   ? '是'  : "否"}
    static final Closure DOUBLE_CONVERTER_10000 =   { originalValue, Double y=10000->  Double.valueOf(originalValue)  * y }
    static final Closure DOUBLE_CONVERTER_1000 =   { originalValue,Double y=1000 -> Double.valueOf(originalValue) * y}


    static final INSURE_FIELD_US_MAPPING = [
        compulsory          : [
            index    : [0, 1],
            type     : 'boolean',
            converter: BOOLEAN_CONVERTER
        ],
        autoTax             : [
            index    : [1, 2],
            type     : 'boolean',
            converter: BOOLEAN_CONVERTER
        ],
        thirdPartyAmount    : [
            index    : [2, 5],
            type     : 'double',
            converter: DOUBLE_CONVERTER_10000
        ],
        damage              : [
            index    : [5, 6],
            type     : 'boolean',
            converter: BOOLEAN_CONVERTER
        ],
        theft               : [
            index    : [6, 7],
            type     : 'boolean',
            converter: BOOLEAN_CONVERTER
        ],
        driverAmount        : [
            index    : [7, 9],
            type     : 'double',
            converter: DOUBLE_CONVERTER_10000
        ],
        passengerAmount     : [
            index    : [9, 11],
            type     : 'double',
            converter: DOUBLE_CONVERTER_10000
        ],
        engine              : [
            index    : [11, 12],
            type     : 'boolean',
            converter: BOOLEAN_CONVERTER
        ],
        scratchAmount       : [
            index    : [12, 14],
            type     : 'double',
            converter: DOUBLE_CONVERTER_1000
        ],
        spontaneousLoss     : [
            index    : [14, 15],
            type     : 'boolean',
            converter: BOOLEAN_CONVERTER
        ],
        glass               : [
            index    : [15, 16],
            type     : 'boolean',
            converter: BOOLEAN_CONVERTER
        ],
        glassType           : [
            index    : [16, 17],
            type     : 'boolean',
            converter: STRING_CONVERTER
        ],
        thirdPartyIop       : [
            index    : [17, 18],
            type     : 'boolean',
            converter: BOOLEAN_CONVERTER
        ],
        damageIop           : [
            index    : [18, 19],
            type     : 'boolean',
            converter: BOOLEAN_CONVERTER
        ],
        theftIop            : [
            index    : [19, 20],
            type     : 'boolean',
            converter: BOOLEAN_CONVERTER
        ],
        driverIop           : [
            index    : [20, 21],
            type     : 'boolean',
            converter: BOOLEAN_CONVERTER
        ],
        passengerIop        : [
            index    : [21, 22],
            type     : 'boolean',
            converter: BOOLEAN_CONVERTER
        ],
        engineIop           : [
            index    : [22, 23],
            type     : 'boolean',
            converter: BOOLEAN_CONVERTER
        ],
        scratchIop          : [
            index    : [23, 24],
            type     : 'boolean',
            converter: BOOLEAN_CONVERTER
        ],
        spontaneousLossIop  : [
            index    : [24, 25],
            type     : 'boolean',
            converter: BOOLEAN_CONVERTER
        ],
        unableFindThirdParty: [
            index    : [25, 26],
            type     : 'boolean',
            converter: BOOLEAN_CONVERTER
        ],
        designatedRepairShop: [
            index    : [26, 27],
            type     : 'boolean',
            converter: BOOLEAN_CONVERTER
        ]
    ]


    static Map resolveUniqueString(String uniqueString) {

        def result=[:]
        Iterator keys=INSURE_FIELD_US_MAPPING.keySet().iterator()
        while(keys.hasNext()){
            def key=keys.next()
            def type=INSURE_FIELD_US_MAPPING.get(key)
            int subBegin = type.index[0];
            int subEnd = type.index[1];
            result.put(key,type.converter (uniqueString.substring (subBegin, subEnd)))
        }

        result
    }


    static Map toMap(object, List discardFields = []) {

        if(!discardFields.contains('class')) {
            discardFields = ['class'] + discardFields  //Arrays.asList方法生成的list不支持add方法，所以不能用discardFields.add
        }

        object?.properties.findAll{ ((!discardFields.contains(it.key)) && it.value) }.collectEntries {
            [it.key, it.value]
        }
    }

    static Map toMapExceptClass(object){
        object?.properties.findAll{ (('class' != it.key)) }.collectEntries {
            [it.key, it.value]
        }
    }

    static Map toMapKeepFields(object, String keepFields, boolean reserveNonNullValue = false) {

        def fields = (keepFields.split(',') as List).collect { it.trim() }
        object.properties.findAll { (fields.contains(it.key) && (reserveNonNullValue ?: it.value)) }.collectEntries {
            [it.key, it.value]
        }
    }

    static Map toMap(object, String discardFields) {
        toMap(object, (discardFields.split(',') as List).collect {it.trim()})
    }

    def static formatVehicleLicense(VehicleLicense vehicleLicense){
        Map licenseMap = toMap(vehicleLicense, ['class','id'])

        if(licenseMap?.brandCode) {
            licenseMap.autoType = [code : licenseMap?.brandCode]
        }
        licenseMap
    }

    static Map convertDailyInsurance(DailyInsurance dailyInsurance){
        def discardFields = ['class', 'purchaseOrder','restartDate','discountAmount','bankCard','status','insurancePackage']
        toMap(dailyInsurance, discardFields)

    }




    public static String generateQuoteDetail(QuoteRecord qr) {
        DecimalFormat format = new DecimalFormat("#.##");
        StringBuffer sb = new StringBuffer(); 
        if (qr.getDamagePremium() != null && qr.getDamagePremium() != 0.00) {
            sb.append("、车损").append(format.format(qr.getDamagePremium())).append("元");
        }
        if (qr.getThirdPartyPremium() != null && qr.getThirdPartyPremium() != 0.00) {
            sb.append("、三者").append("（").append(format.format(qr.getThirdPartyAmount() / 10000))
                .append("万）").append(format.format(qr.getThirdPartyPremium())).append("元");
        }
        if (qr.getTheftPremium() != null && qr.getTheftPremium() != 0.00) {
            sb.append("、盗抢").append(format.format(qr.getTheftPremium())).append("元");
        }
        if (qr.getScratchPremium() != null && qr.getScratchPremium() != 0.00) {
            sb.append("、划痕").append("（").append(format.format(qr.getScratchAmount()))
                .append("元）").append(format.format(qr.getScratchPremium())).append("元");
        }
        if (qr.getGlassPremium() != null && qr.getGlassPremium() != 0.00) {
            sb.append("、玻璃").append("（").append(qr.getInsurancePackage().getGlassType().getName()).append("）")
                .append(format.format(qr.getGlassPremium())).append("元");
        }
        if (qr.getSpontaneousLossPremium() != null && qr.getSpontaneousLossPremium() != 0.00) {
            sb.append("、自燃").append(format.format(qr.getSpontaneousLossPremium())).append("元");
        }
        if (qr.getDriverPremium() != null && qr.getDriverPremium() != 0.00) {
            sb.append("、司机人员险").append("（").append(format.format(qr.getDriverAmount() / 10000))
                .append("万）").append(format.format(qr.getDriverPremium())).append("元");
        }
        if (qr.getPassengerPremium() != null && qr.getPassengerPremium() != 0.00) {
            sb.append("、乘客人员险").append("（").append(format.format(qr.getPassengerAmount() / 10000))
                .append("万）").append(format.format(qr.getPassengerPremium())).append("元");
        }
        if (qr.getEnginePremium() != null && qr.getEnginePremium() != 0.00) {
            sb.append("、发动机").append(format.format(qr.getEnginePremium())).append("元");
        }
        if (qr.getIopTotal() != null && qr.getIopTotal() != 0.00) {
            sb.append("及相关不计免赔").append(format.format(qr.getIopTotal())).append("元");
        }
        sb.append("，商业险小计：").append(format.format(qr.getPremium())).append("元");
        if (qr.getCompulsoryPremium() != null && qr.getCompulsoryPremium() != 0.00) {
            sb.append("，交强险：").append(format.format(qr.getCompulsoryPremium())).append("元");
        }
        if (qr.getAutoTax() != null && qr.getAutoTax() != 0.00) {
            sb.append("，车船税：").append(format.format(qr.getAutoTax())).append("元");
        }
        if(qr.getUnableFindThirdPartyPremium() != null && qr.getUnableFindThirdPartyPremium() != 0.00) {
            sb.append("、第三方特约险").append(format.format(qr.getUnableFindThirdPartyPremium())).append("元");
        }
        if(qr.getDesignatedRepairShopPremium() != null && qr.getDesignatedRepairShopPremium() != 0.00) {
            sb.append("、指定专修厂险").append(format.format(qr.getDesignatedRepairShopPremium())).append("元");
        }
        sb.append("，保险总计：").append(format.format(DoubleUtils.add(qr.getPremium(), qr.getCompulsoryPremium(), qr.getAutoTax()))).append("元");
        String result = sb.toString();
        return result.substring(1);
    }

    public static String generatePremiumDetail(QuoteRecord qr) {
        DecimalFormat format = new DecimalFormat("#.##");
        StringBuffer sb = new StringBuffer();
        InsurancePackage insurancePackage = qr.getInsurancePackage();
        if (insurancePackage.isDamage()) {
            sb.append("/车损险");
            if (qr.getDamagePremium() != null && qr.getDamagePremium() != 0.00) {
                sb.append("(").append(format.format(qr.getDamageAmount())).append("元)");
            }
        }
        if (insurancePackage.getThirdPartyAmount() != null && insurancePackage.getThirdPartyAmount() > 0) {
            sb.append("/三者险");
            if (qr.getThirdPartyPremium() != null && qr.getThirdPartyPremium() != 0.00) {
                sb.append("(").append(format.format(qr.getThirdPartyAmount() / 10000)).append("万元)");
            }
        }
        if (insurancePackage.isTheft()) {
            sb.append("/盗抢险");
            if (qr.getTheftPremium() != null && qr.getTheftPremium() != 0.00) {
                sb.append("(").append(format.format(qr.getTheftAmount())).append("元)");
            }
        }
        if (insurancePackage.getScratchAmount() != null && insurancePackage.getScratchAmount() > 0) {
            sb.append("/划痕险");
            if (qr.getScratchPremium() != null && qr.getScratchPremium() != 0.00) {
                sb.append("(").append(format.format(qr.getScratchAmount())).append("元)");
            }
        }
        if (insurancePackage.isGlass()) {
            sb.append("/玻璃险").append("(").append(qr.getInsurancePackage().getGlassType().getName()).append(")");
        }
        if (insurancePackage.isSpontaneousLoss()) {
            sb.append("/自燃险");
            if (qr.getSpontaneousLossPremium() != null && qr.getSpontaneousLossPremium() != 0.00) {
                sb.append("(").append(format.format(qr.getSpontaneousLossAmount())).append("元)");
            }
        }
        if (insurancePackage.getDriverAmount() != null && insurancePackage.getDriverAmount() > 0) {
            sb.append("/司机人员险");
            if (qr.getDriverPremium() != null && qr.getDriverPremium() != 0.00) {
                sb.append("(").append(format.format(qr.getDriverAmount() / 10000)).append("万元)");
            }
        }
        if (insurancePackage.getPassengerAmount() != null && insurancePackage.getPassengerAmount() > 0) {
            sb.append("/乘客人员险");
            if (qr.getPassengerPremium() != null && qr.getPassengerPremium() != 0.00) {
                sb.append("(").append(format.format(qr.getPassengerAmount() / 10000)).append("万元)");
            }
        }
        if (insurancePackage.isEngine()) {
            sb.append("/发动机险");
        }

        String result = sb.toString();
        result = result.length() > 0 ? result.substring(1) : "";

        return result;
    }
    
    
}

