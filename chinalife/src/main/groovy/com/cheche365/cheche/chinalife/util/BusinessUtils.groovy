package com.cheche365.cheche.chinalife.util

import com.cheche365.cheche.core.model.InsurancePackage
import com.cheche365.cheche.core.model.QuoteRecord
import groovy.util.logging.Slf4j
import net.sf.json.groovy.JsonSlurper

import java.sql.Date

import static com.cheche365.cheche.chinalife.flow.Constants._BOOLEAN_KINDCODES
import static com.cheche365.cheche.chinalife.flow.Constants._UNIT_AMOUNT_KIND_ITEMS
import static com.cheche365.cheche.common.util.DateUtils.getDaysUntil
import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1
import static com.cheche365.cheche.core.model.GlassType.Enum.IMPORT_2
import static com.cheche365.cheche.parser.Constants._DAMAGE
import static com.cheche365.cheche.parser.Constants._DAMAGE_IOP
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT2
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.Constants._DRIVER_AMOUNT
import static com.cheche365.cheche.parser.Constants._DRIVER_IOP
import static com.cheche365.cheche.parser.Constants._ENGINE
import static com.cheche365.cheche.parser.Constants._ENGINE_IOP
import static com.cheche365.cheche.parser.Constants._GLASS
import static com.cheche365.cheche.parser.Constants._INSURANCE_COMMERCIAL_MAPPINGS
import static com.cheche365.cheche.parser.Constants._INSURANCE_MAPPINGS
import static com.cheche365.cheche.parser.Constants._PASSENGER_AMOUNT
import static com.cheche365.cheche.parser.Constants._PASSENGER_IOP
import static com.cheche365.cheche.parser.Constants._SCRATCH_AMOUNT
import static com.cheche365.cheche.parser.Constants._SCRATCH_IOP
import static com.cheche365.cheche.parser.Constants._SPONTANEOUS_LOSS
import static com.cheche365.cheche.parser.Constants._SPONTANEOUS_LOSS_IOP
import static com.cheche365.cheche.parser.Constants._THEFT
import static com.cheche365.cheche.parser.Constants._THEFT_IOP
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_AMOUNT
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_IOP
import static com.cheche365.cheche.parser.Constants._UNABLE_FIND_THIRDPARTY
import static com.cheche365.cheche.parser.util.BusinessUtils.adjustInsureAmount
import static com.cheche365.cheche.parser.util.BusinessUtils.updateAutoSeats
import static com.cheche365.flow.business.flow.util.BusinessUtils.getVehicleOption
import static java.lang.Math.ceil
import static java.time.LocalDate.now as today



/**
 * 业务相关工具类
 */
@Slf4j
class BusinessUtils {

    private static
    final _AMOUNT_CONVERTER_FROM_INSURANCE_PACKAGE = { propName, kindCode, context, insurancePackage, allKindItems, needCheckExpectedAmount = true ->
        def expectedAmount = insurancePackage[propName]
        def amountList = allKindItems[kindCode]?.amountList?.reverse()

        def actualAmount = needCheckExpectedAmount ? (expectedAmount ?
            (adjustInsureAmount(expectedAmount, amountList, { it as double }, { it as double }) ?: 0)
            : 0)
            : (adjustInsureAmount(expectedAmount, amountList, { it as double }, { it as double }) ?: 0)

        insurancePackage[propName] = actualAmount
        actualAmount as int
    }

    private static final _AMOUNT_CONVERTER_FROM_JSON = { propName, kindCode, context, insurancePackage, allKindItems ->
//        insurancePackage[propName] ? allKindItems[kindCode]?.amountList?.get(0) : ''
        //TODO:要确定除广州外北京深圳是否也是这么改
        insurancePackage[propName] ? context.carInfo.actualValue : ''
    }

    private static final _AMOUNT_CONVERTER_BOOLEAN = { propName, context, insurancePackage, allKindItems ->
        if (_GLASS == propName) {
            if (insurancePackage.glass) {
                def kindItem = allKindItems.F
                if (kindItem?.amountList && ((insurancePackage.glassType.id as int) in kindItem.amountList)) {
                    DOMESTIC_1 == insurancePackage.glassType ? 1 : 2
                } else if (kindItem?.amountList) { //只有一种选择,改套餐
                    def actualGlassType = kindItem.amountList[0]
                    insurancePackage.glassType = actualGlassType == 1 ? DOMESTIC_1 : IMPORT_2
                    actualGlassType
                }
            }
        } else {
            insurancePackage[propName] ? 1 : null
        }
    }

    private static final _AMOUNT_CONVERTER_BOOLEAN_IOP = { propName, context, insurancePackage, kindItem ->
        insurancePackage[propName] && kindItem.iopEnabled ? 1 : 0
    }

    private static final _AMOUNT_CONVERTER_BOOLEAN_IOP_UNSUPPORTED = { context, insurancePackage, kindItem ->
        0
    }

    //顺序对报价有微小影响
    static final _KIND_CODE_CONVERTERS = [
        ['A', _DAMAGE, _AMOUNT_CONVERTER_FROM_JSON.curry(_DAMAGE, 'A'),
         _AMOUNT_CONVERTER_BOOLEAN_IOP.curry(_DAMAGE_IOP)],                                         // 机动车辆损失险

        ['B', _THIRD_PARTY_AMOUNT, _AMOUNT_CONVERTER_FROM_INSURANCE_PACKAGE.curry(_THIRD_PARTY_AMOUNT, 'B'),
         _AMOUNT_CONVERTER_BOOLEAN_IOP.curry(_THIRD_PARTY_IOP)],                                      // 第三者责任险

        ['G', _THEFT, _AMOUNT_CONVERTER_FROM_JSON.curry(_THEFT, 'G'),
         _AMOUNT_CONVERTER_BOOLEAN_IOP.curry(_THEFT_IOP)],                                           // 盗抢险

        ['D11', _DRIVER_AMOUNT, _AMOUNT_CONVERTER_FROM_INSURANCE_PACKAGE.curry(_DRIVER_AMOUNT, 'D11'),
         _AMOUNT_CONVERTER_BOOLEAN_IOP.curry(_DRIVER_IOP)],                                          // 车上人员责任险-司机

        ['D12', _PASSENGER_AMOUNT, _AMOUNT_CONVERTER_FROM_INSURANCE_PACKAGE.curry(_PASSENGER_AMOUNT, 'D12'),
         _AMOUNT_CONVERTER_BOOLEAN_IOP.curry(_PASSENGER_IOP)],                                       // 车上人员责任险-乘客

        ['L', _SCRATCH_AMOUNT, _AMOUNT_CONVERTER_FROM_INSURANCE_PACKAGE.curry(_SCRATCH_AMOUNT, 'L'),
         _AMOUNT_CONVERTER_BOOLEAN_IOP.curry(_SCRATCH_IOP)],                                         // 车身划痕损失险

        ['Z', _SPONTANEOUS_LOSS, _AMOUNT_CONVERTER_FROM_JSON.curry(_SPONTANEOUS_LOSS, 'Z'),
         _AMOUNT_CONVERTER_BOOLEAN_IOP.curry(_SPONTANEOUS_LOSS_IOP)],                                 // 自燃损失险

        ['X1', _ENGINE, _AMOUNT_CONVERTER_BOOLEAN.curry(_ENGINE),
         _AMOUNT_CONVERTER_BOOLEAN_IOP.curry(_ENGINE_IOP)],                                          // 发动机特别损失险

        ['F', _GLASS, _AMOUNT_CONVERTER_BOOLEAN.curry(_GLASS),
         _AMOUNT_CONVERTER_BOOLEAN_IOP_UNSUPPORTED],                                                 // 玻璃单独破碎险

        ['N5', _UNABLE_FIND_THIRDPARTY, _AMOUNT_CONVERTER_BOOLEAN.curry(_UNABLE_FIND_THIRDPARTY),
         _AMOUNT_CONVERTER_BOOLEAN_IOP_UNSUPPORTED],                                                 // 无法找到第三方特约险
    ]

    /**
     * 返回必须投却没有投保的险种
     */
    static checkMandatoryKindItems(context, mandatoryKindItems) {
        def insurancePackage = context.insurancePackage

        _KIND_CODE_CONVERTERS.findResults { kindCode, propName, _2, _3 ->
            kindCode in mandatoryKindItems && !insurancePackage[propName] ? context.allKindItems[kindCode] + [propName: propName] : null
        }
    }
    /**
     * 修改必须投保的险种
     * @param context
     * @param propName
     * @return
     */
    static changeInsurancePackageOption(context, kindItemWithPropName) {
        def kindCode = kindItemWithPropName.kindCode
        def propName = kindItemWithPropName.propName

        if (_BOOLEAN_KINDCODES.any { it == kindCode }) {
            changeBooleanPackageOption context, propName
        } else {
            _AMOUNT_CONVERTER_FROM_INSURANCE_PACKAGE propName, kindCode, context, context.accurateInsurancePackage, context.allKindItems, false
        }

        if (kindItemWithPropName.iopEnabled && context.iopEnabled) {
            context.accurateInsurancePackage[_INSURANCE_MAPPINGS[propName].iopPropName] = true
        }
    }
    /**
     * 将必须投保的却未投保的boolean型的险种改成投保
     * @param context
     * @param propName
     * @return
     */
    private static void changeBooleanPackageOption(context, propName) {
        context.accurateInsurancePackage[propName] = true
    }

    /**
     * 获取计算套餐险种参数
     */
    static getCustomPremiumParams(context) {
        def insurancePackage = context.accurateInsurancePackage
        def allKindItems = context.allKindItems

        def idx = 0
        def premiumFeeParams = (_KIND_CODE_CONVERTERS.collect { kindCode, propName, converter, iopConverter ->
            def converterResult = converter(context, insurancePackage, allKindItems)

            def kindItem = allKindItems[kindCode]
            if (null == kindItem || !insurancePackage[propName]) {
                return null
            }
            def kindName = allKindItems[kindCode].kindName
            def unitAmount = allKindItems[kindCode].unitAmount
            def unitAmountCount = allKindItems[kindCode].unitAmountCount
            def iopConverterResult = iopConverter(context, insurancePackage, kindItem)

            def params = [
                "temporary.quoteMain.geQuoteItemkinds[$idx].kindCode"    : kindCode,
                "temporary.quoteMain.geQuoteItemkinds[$idx].kindName"    : kindName,
                "temporary.quoteMain.geQuoteItemkinds[$idx].unitAmount"  : unitAmount ? converterResult : '',
                "temporary.quoteMain.geQuoteItemkinds[$idx].amount"      : unitAmount ? converterResult * unitAmountCount : converterResult,
                "temporary.quoteMain.geQuoteItemkinds[$idx].nodeductflag": iopConverterResult
            ]

            idx++

            params
        } - null).sum()

        def iopParams = [ //不计免赔参数
            "temporary.quoteMain.geQuoteItemkinds[$idx].kindCode"    : 'M',
            "temporary.quoteMain.geQuoteItemkinds[$idx].kindName"    : '不计免赔',
            "temporary.quoteMain.geQuoteItemkinds[$idx].amount"      : '0',
            "temporary.quoteMain.geQuoteItemkinds[$idx].nodeductflag": '0',
        ]

        premiumFeeParams ? premiumFeeParams + iopParams : [:]
    }

    /**
     * 获取基础套餐
     */
    static getBaseKindItems(context, sourceKindItems) {
        def kindItems = (sourceKindItems.attachKinds.collect {
            it.kindType = 2
            it
        } + sourceKindItems.mainKinds.collect {
            it.kindType = 1
            it
        })
        def deductKindItems = sourceKindItems.deductKinds

        def allKindItems = [:]
        kindItems.each { item ->
            def kindCode = item.id.kindCode
            def value = [
                kindName       : item.kindCName,
                kindCode       : kindCode,
                codeType       : item.codeType,
                iopEnabled     : (deductKindItems.any { kindCode == it.id.kindCode }) && '1' == item.nodeductflag,
                amountList     : 'F' == kindCode ? null : extractAmountList(item.valuerange),
                unitAmount     : _UNIT_AMOUNT_KIND_ITEMS.contains(kindCode),
                unitAmountCount: 'D12' == kindCode ? (getCarSeat(context) - 1) : 1,
                kindType       : item.kindType  // 主险:1 or 附加险:2

            ]
            allKindItems << [("$kindCode".toString()): value]
        }

        amendAmountList(context, allKindItems)
    }

    /**
     * 获取续保套餐
     */
    static generateRenewalPackage(quoteItemKinds) {
        new InsurancePackage().with { insurancePackage ->
            compulsory = true
            autoTax = true

            quoteItemKinds.inject(insurancePackage, { ip, item ->
                def kindCode = item.kindCode
                if ((item.amount as double) > 0) {
                    def propName = _KIND_CODE_CONVERTERS.find {
                        kindCode == it.first()
                    }?.get(1)
                    if (propName) {
                        ip[propName] = item.amount as double
                        if (_GLASS == propName){
                            ip.glassType = (1 == item.amount ? DOMESTIC_1 : IMPORT_2)
                        }
                        if ('1' == item.nodeductflag && _GLASS != propName && _SPONTANEOUS_LOSS != propName) {
                            def iopPropName = _INSURANCE_COMMERCIAL_MAPPINGS[propName].iopPropName
                            ip[iopPropName] = true
                        }
                    }
                }
                ip
            })
        }
    }

    //根据网页js逻辑修正amountList
    private static amendAmountList(context, allKindItems) {
        def params = context.carVerify
        def driverOption = params.UInewCarValueDriverOption
        def driverOptionMessage = params.UInewCarValueDriverOptionMessage?.tokenize(';')?.inject([:]) { prev, item ->
            def (kindCode, amount) = item.tokenize(',')
            prev[kindCode] ? prev[kindCode] << amount : (prev[kindCode] = [amount])

            prev
        }
        def glassChoiceSchemeFlag = params.UInewCarGlassChoiceSchemeFlag
        def carValueRangeOption = params.UInewCarvaluerangeOption

        allKindItems.collect { key, item ->
            if ('0' == item.codeType) {
                if (driverOptionMessage?.containsKey(item.kindCode)
                    && ((1 == item.kindType && driverOption in ['0', '2']) || (2 == item.kindType && driverOption in ['1', '2']))) {
                    item.amountList = item.amountList?.findAll {
                        !driverOptionMessage[item.kindCode].contains(it)
                    }
                }
            }
            if ('F' == item.kindCode) { //玻璃险
                def importFlag = context.carInfo?.importFlag ?: context.vehicleInfo?.importFlag
                item.amountList = [1, 2]
                if ('1' == carValueRangeOption) {
                    if (importFlag in ['进口', 'A']) {
                        item.amountList = [2]
                    }
                } else {
                    if (importFlag in ['进口', 'A']) {
                        item.amountList = [1, 2]
                    }
                }
                if ('1' == glassChoiceSchemeFlag) {
                    if (importFlag in ['国产', 'B']) {
                        item.amountList = [1]
                    }
                } else if ('2' == glassChoiceSchemeFlag) {
                    if (importFlag in ['国产', 'B', '合资', 'C']) {
                        item.amountList = [1, 2]
                    }
                } else {
                    if (importFlag in ['国产', 'B', '合资', 'C']) {
                        item.amountList = [1]
                    }
                }
            }
            item
        }

        allKindItems
    }

    /**
     * 获取精准报价套餐
     */
    static getQuoteKindItems(sourceKindItems) {
        def allKindItems = [:]
        sourceKindItems.each { item ->
            def kindCode = item.kindCode
            def value = [
                kindName    : item.kindName,
                kindCode    : kindCode,
                amountOrFlag: 'D12' == kindCode ? item.unitAmount : item.amount,
                premium     : item.premium,
                iopEnabled  : '1' == item.nodeductflag
            ]
            allKindItems << [("$kindCode".toString()): value]
        }

        allKindItems
    }

    /**
     * 获取amountList
     *  选值类型的 [1000,2000,5000]
     *  boolean类型的['']，即不上送保额 如盗抢
     */
    private static extractAmountList(String sourceText) {
        if (sourceText) {
            if (-1 != sourceText.indexOf('@')) {
                sourceText = sourceText.substring(sourceText.indexOf('@') + 1)
                def amountListMap = new JsonSlurper().parseText(sourceText) as Map
                amountListMap.keySet().toList().flatten()
            } else {
                ['']
            }
        }
    }

    private static final _KIND_CODE_MAPPING_DOUBLE_TYPE = [
        ['B', 'thirdPartyAmount', 'thirdPartyPremium', 'thirdPartyIop', 0.15],  // 第三者责任险
        ['D11', 'driverAmount', 'driverPremium', 'driverIop', 0.15],              // 车上人员责任险-司机
        ['D12', 'passengerAmount', 'passengerPremium', 'passengerIop', 0.15,      // 车上人员责任险-乘客
         { passengerCount, amount -> amount / passengerCount }
        ],
        ['A', 'damageAmount', 'damagePremium', 'damageIop', 0.15],               // 机动车辆损失险
        ['G', 'theftAmount', 'theftPremium', 'theftIop', 0.2],                  // 盗抢险
        ['Z', 'spontaneousLossAmount', 'spontaneousLossPremium', 'spontaneousLossIop', 0.15],   // 自燃损失险
        ['L', 'scratchAmount', 'scratchPremium', 'scratchIop', 0.15]           // 车身划痕损失险
    ]

    private static final _KIND_CODE_MAPPING_BOOLEAN_TYPE = [
        ['X1', 'enginePremium', null, 'engineIop'],  // 发动机特别损失险
        ['F', 'glassPremium', null, null],    // 玻璃险
        ['N5', 'unableFindThirdPartyPremium', null, null]    // 无法找到第三方特约险
    ]

    // 可能会禁用的险种的相关信息
    private static final _DISABLED_KIND_CODE_MAPPING = [
        'L' : [
            scratchAmount: [null, '该车无法上车身划痕损失险'],
            scratchIop   : [false, '该车无法上车身划痕损失险（不计免赔）']
        ],
        'A' : [
            damage   : [false, '该车无法上机动车辆损失险'],
            damageIop: [false, '该车无法上机动车辆损失险（不计免赔）']
        ],
        'B' : [
            thirdPartyAmount: [null, '该车无法上第三者责任险'],
            thirdPartyIop   : [false, '该车无法上第三者责任险（不计免赔）']
        ],
        'G1': [
            theft   : [false, '该车无法上盗抢险'],
            theftIop: [false, '该车无法上盗抢险（不计免赔）']
        ],
        'D3': [
            driverAmount: [null, '该车无法上车上人员责任险-司机'],
            driverIop   : [false, '该车无法上车上人员责任险-司机（不计免赔）']
        ],
        'D4': [
            passengerAmount: [null, '该车无法上车上人员责任险-乘客'],
            passengerIop   : [false, '该车无法上车上人员责任险-乘客（不计免赔）']
        ],
        'X1': [
            engine   : [false, '该车无法上发动机特别损失险'],
            engineIop: [false, '无法上发动机特别损失险（不计免赔）']
        ],
        'F' : [
            glass: [false, '该车无法上玻璃单独破碎险']
        ],
        'Z' : [
            spontaneousLoss: [false, '该车无法上自燃损失险'],
            spontaneousLossIop: [false, '该车无法上自燃损失险（不计免赔）']
        ],
        'N5': [
            unableFindThirdParty: [false, '该车无法上无法找到第三方']
        ]
    ]

    /**
     * 组装报价结果、禁用不能投保的险种
     */
    static populateQuoteRecord(context, accurateQuote) {
        def newQuoteRecord = resolveNewQuoteRecordInContext context

        newQuoteRecord.with { quoteRecord ->

            def seats = getCarSeat(context)
            // 更新座位数
            updateAutoSeats(context, seats)
            // 乘客数
            def passengerCount = seats - 1
            quoteRecord.passengerCount = passengerCount

            // double型险种
            _KIND_CODE_MAPPING_DOUBLE_TYPE.each { kindCode, amountName, premiumName, iopPropName, iopRate, amountConverter = null ->
                def kindItem = accurateQuote[kindCode]

                def insured = (null != kindItem)
                quoteRecord[amountName] = insured ? (kindItem.amountOrFlag as double) : 0
                quoteRecord[premiumName] = insured ? (kindItem.premium as double) : 0

                // 将那些不能上的险种在套餐中设置为禁用
                disableUnavailableKindItems kindCode, kindItem, quoteRecord

                //不计免赔
                def iopEnabled = (insured && kindItem.iopEnabled)
                if (iopPropName && quoteRecord.insurancePackage[iopPropName] && iopEnabled) {
                    quoteRecord[iopPropName] = -1
                }
                disableUnavailableIopOnlyKindItems kindCode, insured, !iopEnabled, iopPropName, quoteRecord
            }

            // boolean型险种
            _KIND_CODE_MAPPING_BOOLEAN_TYPE.each { kindCode, premiumName, iopRate, iopPropName ->
                def kindItem = accurateQuote[kindCode]

                def insured = (null != kindItem)
                quoteRecord[premiumName] = insured ? (kindItem.premium as double) : 0

                // 将那些不能上的险种在套餐中设置为禁用
                disableUnavailableKindItems kindCode, kindItem, quoteRecord

                //不计免赔
                def iopEnabled = (insured && kindItem.iopEnabled)
                if (iopPropName && quoteRecord.insurancePackage[iopPropName] && iopEnabled) {
                    quoteRecord[iopPropName] = -1
                }
                disableUnavailableIopOnlyKindItems kindCode, insured, !iopEnabled, iopPropName, quoteRecord
            }

            // 商业险总保费
            quoteRecord.premium = accurateQuote.premium
            // 商业险不计免赔总保费
            quoteRecord.iopTotal = new BigDecimal(accurateQuote['M']?.premium ?: 0).setScale(2, BigDecimal.ROUND_HALF_UP)
            quoteRecord
        }
    }

    /**
     * 创建context中的newQuoteRecord,已有则返回
     */
    static resolveNewQuoteRecordInContext(context) {
        if (!context.newQuoteRecord) {
            def today = today()
            def createTime = Date.valueOf today
            context.newQuoteRecord = new QuoteRecord(
                applicant: context.applicant,
                auto: context.auto,
                insuranceCompany: context.insuranceCompany,
                insurancePackage: context.accurateInsurancePackage,
                createTime: createTime
            )
        }

        context.newQuoteRecord
    }

    /**
     * 禁用的套餐加入到quoteFieldStatus中
     */
    private static void disableUnavailableKindItems(kindCode, kindItem, quoteRecord) {
        if (!kindItem && _DISABLED_KIND_CODE_MAPPING.containsKey(kindCode)) {
            def insurancePackage = quoteRecord.insurancePackage
            def disabledFieldNames = _DISABLED_KIND_CODE_MAPPING[kindCode]
            disabledFieldNames.each { entry ->
                def name = entry.key
                def (disabledValue, errorMessage) = entry.value
                if (insurancePackage[name]) {
                    insurancePackage[name] = disabledValue
                }
            }
        }
    }

    /**
     * 能上主险但上不了对应的iop,加入到quoteFieldStatus中
     */
    private static void disableUnavailableIopOnlyKindItems(kindCode, insured, iopDisabled, iopPropName, quoteRecord) {
        if (insured && iopDisabled && _DISABLED_KIND_CODE_MAPPING.containsKey(kindCode)) {
            def insurancePackage = quoteRecord.insurancePackage
            def disabledIop = _DISABLED_KIND_CODE_MAPPING[kindCode][iopPropName]
            if (disabledIop && insurancePackage[iopPropName]) {
                def (disabledValue, errorMessage) = disabledIop
                insurancePackage[iopPropName] = disabledValue
            }
        }
    }

    static getCarSeat(context) {
        (context.carRenewalInfo?.seatCount ?: context.vehicleInfo?.seatCount ?: context.vehicleInfo?.seat ?: context.carInfo?.seat ?: context.auto.autoType?.seats ?: 5) as int
    }

    /**
     * 在调用FindCarModelInfo之前，选取车型从vehicleClassCode中取；北京carBrandInfo中取，其他A0
     * 在调用FindCarModelInfo之后，从FindCarModelInfo返回的carInfo中取
     */
    static getCarKindCode(context) {
        context.carBrandInfo?.carKindCode ?: context.carInfo?.carKindCode ?: context.vehicleInfo?.vehicleClassCode ?: 'A0'
    }

    static getCarEnrollDate(context) {
        def auto = context.auto
        auto.enrollDate ? _DATE_FORMAT3.format(auto.enrollDate) : context.carRenewalInfo?.enrollDate ?:
            context.carBrandInfo?.enrollDate ?:
                context.carInfo?.enrollDate ?:
                    _DATE_FORMAT3.format(new java.util.Date())
    }

    /**
     * publishDate必须要在enrollDate之后
     * @param context
     * @return
     */
    static getDefaultPublishDate(context) {
        _DATE_FORMAT3.format(Date.parse('yyyy-MM-dd', getCarEnrollDate(context)).plus(3))
    }

    /**
     * 默认的起保日期T+1或T+2
     */
    static getDefaultInsurancePeriodText(UIStartDateMinMessage) {
        def today = today()
        def defaultPlusDays = ceil(UIStartDateMinMessage as float) as int
        def startDateText = _DATETIME_FORMAT3.format(today.plusDays(defaultPlusDays))
        startDateText
    }

    /**
     * 根据上年保单结束日期返回新保单起保日期和结束日期
     * @param lastBsPolicyEndDate
     * @return
     */
    static getNewStartDate(lastBsPolicyEndDate) {
        def correctStartDate = getLocalDate(_DATE_FORMAT3.parse(lastBsPolicyEndDate)).plusDays(1)
        def endDate = correctStartDate.plusYears(1).minusDays(1)
        new Tuple(_DATETIME_FORMAT3.format(correctStartDate), _DATETIME_FORMAT3.format(endDate))
    }

    /**
     * 从交强险返回的错误信息信息中获取新的交强险起止日期
     */
    static getNewCalculateBZDateText(errorMsg) {
        def patternErrorMessage1 = /.*(\d{8})[-|－](\d{8}).*/
        def patternErrorMessage2 = /.*终保日期 (\d{4}-\d{2}-\d{2}).*/
        def dateFormatFrom = _DATE_FORMAT2
        def plusOneDay = true

        def m = errorMsg =~ patternErrorMessage1
        if (!m.matches()) {
            m = errorMsg =~ patternErrorMessage2
            dateFormatFrom = _DATE_FORMAT3
            plusOneDay = false
        }

        if (m.matches()) {
            def startDate = getLocalDate(dateFormatFrom.parse(m[0][-1]))
            def endDate = startDate.plusYears(1)

            if (plusOneDay) {
                startDate = startDate.plusDays(1)
            } else {
                endDate = endDate.minusDays(1)
            }
            def newStartDateCIText = _DATETIME_FORMAT3.format startDate
            def newEndDateCIText = _DATETIME_FORMAT3.format endDate

            new Tuple2(newStartDateCIText, newEndDateCIText)
        } else {
            new Tuple2(null, null)
        }
    }

    /**
     * 判断商业险或交强险日期是否在投保期内
     * @param context
     * @param startDate
     */
    static startDateInPeriod(context, startDate, earlyDayType) {
        //今天与投保起始日期间隔天数，结果应该为一个正值（认定startDate为未来日期）
        def intervalDay = -getDaysUntil(_DATE_FORMAT3.parse(startDate))
        //该城市交强险投保期限
        def earlyDaysBZ = context.carVerify.get(earlyDayType) as int
        intervalDay - earlyDaysBZ <= 0
    }
    /**
     * 组装交强险信息到QuoteRecord
     * @param quote
     * @param context
     * @return
     */
    static populateQuoteRecordBZ(context, compulsoryFee, autoTaxFee) {
        QuoteRecord quoteRecord = context.newQuoteRecord

        quoteRecord.with {
            compulsoryPremium = compulsoryFee as double

            autoTax = autoTaxFee as double

            def hasQuoteAutoTax = context.insurancePackage.autoTax
            def autoTaxEnabled = autoTax ? true : false  //目前autoTax为0表示不能投车船

            // 当程序选择了车船税,而页面得到的车船税为0时,将是否选择车船税改成false,并添加提示.
            if (hasQuoteAutoTax && !autoTaxEnabled) {
                quoteRecord.insurancePackage.autoTax = false
                // 当程序没有选择车船税而页面得到的车船税不为0时,将是否选择车船税改成true,并添加
            } else if (!hasQuoteAutoTax && autoTaxEnabled) {
                quoteRecord.insurancePackage.autoTax = true
            }

            it
        }
    }

    /**
     * 破解验证码并获取正确的数据
     * @param sourceResult 带有验证问题的response
     * @param requestParamsClosure
     * @param responseObtainClosure
     * @return
     */
    static validateCheckCode(sourceResult, requestParamsClosure, responseObtainClosure) {
        def bsDemandNo = sourceResult.temporary.quoteMain.demandNo
        def bzDemandNo = sourceResult.temporary.quoteMain.bzDemandNo
        if ('4' == sourceResult?.temporary?.resultType) {
            ['A', 'B', 'C', 'D'].findResult { answer ->
                def args = requestParamsClosure(answer, bsDemandNo, bzDemandNo)
                def result = responseObtainClosure(args)

                '5' != result?.temporary?.resultType ? result : null
            }
        } else {
            sourceResult
        }
    }

    /**
     * 当年应缴/往年补缴/滞纳金 相加的到车船税金额
     */
    static getAutoTaxPremium(quoteCarTax) {
        def autoTaxPremium = ((quoteCarTax?.currentTax ?: 0 as double)
            + (quoteCarTax?.formerTax ?: 0 as double)
            + (quoteCarTax?.lateFee ?: 0 as double))
        autoTaxPremium ? new BigDecimal(autoTaxPremium).setScale(2, BigDecimal.ROUND_HALF_UP) : 0
    }

    /**
     * 通过身份证号码获取出生日期 1990-01-01
     */
    static getBirthdayById(id) {
        id[6..9] + '-' + id[10..11] + '-' + id[12..13]
    }

    static getAutoVinNo(context) {
        context.auto.vinNo ?: context.carRenewalInfo?.frameNo
    }

    static getAutoEngineNo(context) {
        context.auto.engineNo ?: context.carRenewalInfo?.engineNo
    }

    static getCarOwner(context) {
        context.auto.owner ?: context.carRenewalInfo?.carOwner
    }

    static getOldCustomerFlag(context) {
        context.renewable ? '1' : '0'
    }


    /**
     * FindCarModelInfo节点拼装车型选择的map
     */
    static final _CHINA_LIFE_GET_VEHICLE_OPTION = { context, vehicle ->
        def vehicleOptionInfo = [
            brand          : vehicle.vehicleBrand1,
            family         : '',
            gearbox        : vehicle.transmissionType,
            exhaustScale   : vehicle.engineDesc,
            model          : vehicle.fcvehicle,
            productionDate : vehicle.marketDate + ' ' + vehicle.vehicleAlias,
            seats          : vehicle.seatCount,
            newPrice       : vehicle.purchasePrice,
        ]
        getVehicleOption vehicle.RBCode, vehicleOptionInfo
    }

    /**
     * 创建获取续保套餐节点需要生成的参数.
     * 这些参数不传也可以生成续保套餐,但无法返回需要判断流程走势的 result.temporary.resultType.
     */
    static createOldProposalQuoteItemParam(quoteItemkinds) {
        def quoteItemParam = [:]
        quoteItemkinds?.eachWithIndex { entry, i ->
            entry.collect {
                def entryValue = it.value
                if (it.key in ['benchMarkPremium', 'discount', 'premium']) {
                    entryValue = entryValue.toDouble()
                }
                quoteItemParam += [('temporary.quoteMain.geQuoteItemkinds[' + i + '].' + it.key): entryValue]
            }
        }
        quoteItemParam
    }

}
