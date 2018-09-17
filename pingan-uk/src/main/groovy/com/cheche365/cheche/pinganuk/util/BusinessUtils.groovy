package com.cheche365.cheche.pinganuk.util

import com.cheche365.cheche.core.model.InsurancePackage
import com.cheche365.cheche.parser.util.BusinessUtils as parserBusinessUtils
import groovy.util.logging.Slf4j
import org.apache.commons.lang.time.DateUtils

import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1
import static com.cheche365.cheche.core.model.GlassType.Enum.IMPORT_2
import static com.cheche365.cheche.parser.Constants._DAMAGE
import static com.cheche365.cheche.parser.Constants._DAMAGE_IOP
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT2
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT2
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT5
import static com.cheche365.cheche.parser.Constants._GLASS
import static com.cheche365.cheche.parser.Constants._INSURANCE_KIND_NAME_COMMERCIAL
import static com.cheche365.cheche.parser.Constants._INSURANCE_KIND_NAME_COMPULSORY
import static com.cheche365.cheche.parser.Constants._INSURANCE_MAPPINGS
import static com.cheche365.cheche.parser.Constants._SCRATCH_AMOUNT
import static com.cheche365.cheche.parser.Constants._SCRATCH_IOP
import static com.cheche365.cheche.parser.Constants._SPONTANEOUS_LOSS
import static com.cheche365.cheche.parser.Constants._SPONTANEOUS_LOSS_IOP
import static com.cheche365.cheche.parser.Constants._THEFT
import static com.cheche365.cheche.parser.Constants._THEFT_IOP
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_AMOUNT
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_IOP
import static com.cheche365.cheche.parser.util.BusinessUtils.adjustInsurancePackageItem
import static com.cheche365.cheche.parser.util.InsuranceUtils._ADJUST_TIME_POLICY_BASE
import static com.cheche365.cheche.parser.util.InsuranceUtils._CHECK_ADVICE_WITH_TRUE
import static com.cheche365.cheche.parser.util.InsuranceUtils._ERROR_MESSAGE_POLICY
import static com.cheche365.cheche.parser.util.InsuranceUtils._FORBID_KINDS_TIME_CAUSE_POLICY_BASE
import static com.cheche365.cheche.parser.util.InsuranceUtils._JUDGE_SINGLE_ADVICE_BASE
import static com.cheche365.cheche.parser.util.InsuranceUtils._RENEW_QUOTE_POLICY
import static com.cheche365.cheche.parser.util.InsuranceUtils._SINGLE_ALLOWED_POLICY_BASE
import static com.cheche365.cheche.pinganuk.flow.Handlers._KIND_ITEM_CONVERTERS_CONFIG
import static java.time.LocalDate.now as today



/**
 * 业务相关工具类
 */
@Slf4j
class BusinessUtils {

    //<editor-fold defaultstate="collapsed" desc="处理套餐建议">

    private static final _POLICY_ERROR_ADVICE = 0L
    private static final _POLICY_CODE_ADJUST_START_DATE = _POLICY_ERROR_ADVICE + 1
    private static final _POLICY_CODE_FORBID_KIND_TYPE = _POLICY_CODE_ADJUST_START_DATE + 1
    private static final _POLICY_CODE_FORBID_SCRATCH = _POLICY_CODE_FORBID_KIND_TYPE + 1
    private static final _POLICY_CODE_FORBID_SPONTANEOUS = _POLICY_CODE_FORBID_SCRATCH + 1
    private static final _POLICY_CODE_ADJUST_KIND_COMBINE = _POLICY_CODE_FORBID_SPONTANEOUS + 1
    private static final _POLICY_CODE_FORBID_THEFT = _POLICY_CODE_ADJUST_KIND_COMBINE + 1
    private static final _POLICY_ERROR_READ_TIME_OUT = _POLICY_CODE_FORBID_THEFT + 1

    private static final _ADJUST_KIND_COMBINE_POLICY = { advice, context, others ->
        def insurancePackage = context.accurateInsurancePackage
        if (insurancePackage[_THIRD_PARTY_AMOUNT] == _THIRD_PARTY_AMOUNT_LIST.last()) {
            // 三者险调至最大限度时仍不能报价成功，需禁用车损险
            adjustInsurancePackageItem context, _THIRD_PARTY_AMOUNT, _THIRD_PARTY_IOP, context.insurancePackage[_THIRD_PARTY_AMOUNT], null
            adjustInsurancePackageItem context, _DAMAGE, _DAMAGE_IOP, false, false
        } else { // 调整三者险，步幅为3
            def index = _THIRD_PARTY_AMOUNT_LIST.findIndexOf {
                it == insurancePackage[_THIRD_PARTY_AMOUNT]
            }
            def propValue = _THIRD_PARTY_AMOUNT_LIST[index + 3] ?: _THIRD_PARTY_AMOUNT_LIST.last()
            adjustInsurancePackageItem context, _THIRD_PARTY_AMOUNT, _THIRD_PARTY_IOP, propValue, null
        }

        context.newQuoteRecord = null
        getLoopContinueFSRV insurancePackage, advice
    }

    private static final _ADVICE_POLICY_MAPPINGS = [
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_ADJUST_START_DATE))  : _ADJUST_TIME_POLICY_BASE.curry(_DATETIME_FORMAT2),
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_KIND_TYPE))   : _FORBID_KINDS_TIME_CAUSE_POLICY_BASE.curry(_DATE_FORMAT5),
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_SCRATCH))     : _SINGLE_ALLOWED_POLICY_BASE.curry(_SCRATCH_AMOUNT, _SCRATCH_IOP, 0, false),
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_SPONTANEOUS)) : _SINGLE_ALLOWED_POLICY_BASE.curry(_SPONTANEOUS_LOSS, _SPONTANEOUS_LOSS_IOP, false, false),
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_THEFT))       : _SINGLE_ALLOWED_POLICY_BASE.curry(_THEFT, _THEFT_IOP, false, false),
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_ADJUST_KIND_COMBINE)): _ADJUST_KIND_COMBINE_POLICY,
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_ERROR_READ_TIME_OUT))     : _RENEW_QUOTE_POLICY,
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_ERROR_ADVICE))            : _ERROR_MESSAGE_POLICY,
    ]

    private static final _CHECK_ADVICE_TYPE1_BASE = { advice, context, others ->
        def m = advice =~ /.*C51.*保险期限是\[(\d{8})-(\d{8})\].*/
        if (m.matches()) {
            def (startDateText, endDateText) = [m[0][1], m[0][2]]
            def startDate = _DATE_FORMAT5.format(DateUtils.addYears(_DATE_FORMAT2.parse(startDateText), 1))
            def endDate = _DATE_FORMAT5.format(DateUtils.addYears(_DATE_FORMAT2.parse(endDateText), 1))
            others << [
                bzStartDate: startDate,
                bzEndDate  : endDate,
                kindType   : _INSURANCE_KIND_NAME_COMPULSORY
            ]
        }

        m.matches()
    }

    private static final _CHECK_ADVICE_TYPE2_BASE = { advice, context, others ->
        def m = advice =~ /.*不能晚于当前日期＋(\d*)天.*/
        def m1 = advice =~ /.*保险止期必须大于保险起期且保险止期－保险起期必须小于等于1年.*/
        def m2 = advice =~ /.*保险起期超过提前投保最大天数(\d*)天.*/
        others << [
            earlyDays: m.matches() ? m[0][1] as int : m2.matches() ? m2[0][1] as int : null,
            kindType : m2.matches() ? _INSURANCE_KIND_NAME_COMMERCIAL : _INSURANCE_KIND_NAME_COMPULSORY
        ]

        m.matches() || m1.matches() || m2.matches()
    }

    private static final _CHECK_ADVICE_TYPE3_BASE = { flowCode, advice, context, others ->
        flowCode == advice.split(',').first()
    }

    private static final _CHECK_ADVICE_TYPE4_BASE = { advice, context, others ->
        (advice =~ /.*险别组合不符合核保政策.*/).with { m ->
            m.find()
        }
    }

    private static final _CHECK_ADVICE_TYPE5_BASE = { advice, context, others ->
        (advice =~ /.*Read timed out.*/).with { m ->
            m.find()
        }
    }

    private static final _COMMON_REGULATOR = { policyCode, advice, context, others ->
        [(policyCode): advice.value]
    }


    static final _CITY_ADVICE_POLICY_MAPPINGS = [
        default: _ADVICE_POLICY_MAPPINGS
    ]

    static final _ADVICE_REGULATOR_MAPPINGS = [
        (_CHECK_ADVICE_TYPE1_BASE)            : _COMMON_REGULATOR.curry(_POLICY_CODE_ADJUST_START_DATE),
        (_CHECK_ADVICE_TYPE2_BASE)            : _COMMON_REGULATOR.curry(_POLICY_CODE_FORBID_KIND_TYPE),
        (_CHECK_ADVICE_TYPE3_BASE.curry('12')): _COMMON_REGULATOR.curry(_POLICY_CODE_FORBID_SCRATCH),
        (_CHECK_ADVICE_TYPE3_BASE.curry('31')): _COMMON_REGULATOR.curry(_POLICY_CODE_FORBID_SPONTANEOUS),
        (_CHECK_ADVICE_TYPE3_BASE.curry('29')): _COMMON_REGULATOR.curry(_POLICY_CODE_FORBID_THEFT),
        (_CHECK_ADVICE_TYPE4_BASE)            : _COMMON_REGULATOR.curry(_POLICY_CODE_ADJUST_KIND_COMBINE),
        (_CHECK_ADVICE_TYPE5_BASE)            : _COMMON_REGULATOR.curry(_POLICY_ERROR_READ_TIME_OUT),
        (_CHECK_ADVICE_WITH_TRUE)             : _COMMON_REGULATOR.curry(_POLICY_ERROR_ADVICE)
    ]

    // 获取商业险套餐建议
    static final _GET_EFFECTIVE_ADVICES = { advices, context, others ->
        def m = advices =~ /.*流程号：(.*)/
        if (m.find()) {
            return m.collect { advice ->
                advice[1].split('@@@')
            }.flatten()
        }

        m = advices =~ /.*申请报价出错:\n(.*)/
        if (m.find()) {
            return [m[0][1]]
        }

        m = advices =~ /.*平台返回信息:(.*)/
        if (m.find()) {
            return [m[0][1]]
        }

        [advices]
    }

    //</editor-fold>

    static getAllBaseKindItems(context, convertersConfig) {
        convertersConfig.collectEntries { outerKindCode, _1, _2, extConfig, _4, _5 ->
            [
                (outerKindCode): [
                    amountList: extConfig,
                    amount    : context.theftAmount ?: context.selectedCarModel.vehicleLossInsuredValue as double
                ]
            ]
        }
    }

    static getAllKindItems(dutyList) {
        dutyList.collectEntries { duty ->
            [
                (duty.dutyCode): [
                    amount : duty.insuredAmount,
                    premium: duty.totalActualPremium,
                    seats  : duty.seats
                ]
            ]
        }
    }

    /**
     * 获取续保套餐
     */
    static generateRenewalPackage(originRenewalPackage) {
        def renewalPackage = new InsurancePackage()
        renewalPackage.compulsory = true
        renewalPackage.autoTax = true
        def kindItems = originRenewalPackage.collectEntries { item ->
            [(item.dutyCode): item]
        }

        _KIND_ITEM_CONVERTERS_CONFIG.each { kindCode, fieldName, inner2OuterConverter, extConfig, _4, _5 ->
            def item = kindItems[kindCode]
            if (fieldName) {
                renewalPackage[fieldName] = item ? (_INSURANCE_MAPPINGS[fieldName].isAmount ? item.insuredAmount : true) : 0
                if (item && fieldName == _GLASS) {
                    renewalPackage.glassType = (0 == item.seats ? DOMESTIC_1 : IMPORT_2)
                }
            }
        }

        renewalPackage
    }

    /**
     * 车牌号加"-"处理
     */
    static resolveAutoLicensePlate(autoLicensePlate) {
        "${autoLicensePlate[0..1]}-${autoLicensePlate[2..-1]}" as String
    }

    /**
     * 获取初登日期   优先级：用户输入、续保、转保
     * @param context
     * @param dateFormat
     * @return
     */
    static getEnrollDate(context, dateFormat = _DATE_FORMAT3) {
        dateFormat.format(
            (context.voucher?.vehicleTarget ? _DATE_FORMAT5.parse(context.voucher.vehicleTarget.firstRegisterDate as String) : null)
                ?: (context.vehicleDataList?.size() > 0 ? _DATE_FORMAT3.parse(context.vehicleDataList.first().firstRegisterDate as String) : null)
                ?: context.auto.enrollDate
                ?: new Date()
        )
    }

    /**
     * 获取从明天到明年今天的日期
     * @param context
     * @param earlyDaysStart 默认最近的可投保天数
     * @return
     */
    static getDefaultInsurancePeriodTexts(datetimeFormat = _DATETIME_FORMAT3, earlyDaysStart = 1) {
        def today = today()
        def startDateText = datetimeFormat.format(today.plusDays(earlyDaysStart).atTime(0, 0, 0))
        def endDateText = datetimeFormat.format(today.plusYears(1).atTime(23, 59, 59))
        new Tuple2(startDateText, endDateText)
    }

    /**
     * 设置商业险起保日期（日期大于今天）
     * @param context
     * @param startDateText 商业险起保日期
     * @param datetimeFormat 日期格式，默认_DATETIME_FORMAT2
     * @return
     */
    static setCommercialInsurancePeriodTexts(context, startDateText, datetimeFormat = _DATETIME_FORMAT2) {
        if (new Date() < _DATE_FORMAT3.parse(startDateText)) {
            parserBusinessUtils.setCommercialInsurancePeriodTexts context, startDateText, datetimeFormat
        }
    }

    /**
     * 获取商业险起保日期（getCommercialInsurancePeriod中获取的商业险止保日期时间为'00:00:00'，此处置为'23:59:59'）
     * @param context
     * @return
     */
    static getCommercialInsurancePeriodTexts(context) {
        parserBusinessUtils.getCommercialInsurancePeriodTexts(context, _DATETIME_FORMAT2)
    }

    /**
     * 设置交强险起保日期（日期大于今天）
     * @param context
     * @param startDateText 交强险起保日期
     * @param datetimeFormat 日期格式，默认_DATETIME_FORMAT2
     * @return
     */
    static setCompulsoryInsurancePeriodTexts(context, startDateText, datetimeFormat = _DATETIME_FORMAT2) {
        if (new Date() < _DATE_FORMAT3.parse(startDateText)) {
            parserBusinessUtils.setCompulsoryInsurancePeriodTexts context, startDateText, datetimeFormat
        }
    }

    /**
     * 获取交强险起保日期
     * @param context
     * @return
     */
    static getCompulsoryInsurancePeriodTexts(context) {
        parserBusinessUtils.getCompulsoryInsurancePeriodTexts(context, _DATETIME_FORMAT2)
    }

    /**
     * 获取验证码Base64值
     */
    static getImageBase64(imageBase64) {
        if (imageBase64.startsWith('data')) {
            imageBase64 = imageBase64.split('base64,')[-1]
        }
        imageBase64
    }

}
