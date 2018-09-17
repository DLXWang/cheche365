package com.cheche365.cheche.picc.util

import com.cheche365.cheche.core.model.InsurancePackage
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.parser.util.BusinessUtils as parserBU
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getObjectByCityCode
import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1
import static com.cheche365.cheche.core.model.GlassType.Enum.IMPORT_2
import static com.cheche365.cheche.parser.Constants._AUTO_TAX
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT1
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT2
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT1
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT2
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT5
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT6
import static com.cheche365.cheche.parser.Constants.get_DAMAGE
import static com.cheche365.cheche.parser.Constants.get_DAMAGE_IOP
import static com.cheche365.cheche.parser.Constants.get_GLASS
import static com.cheche365.cheche.parser.Constants.get_GLASS_TYPE
import static com.cheche365.cheche.parser.Constants.get_SCRATCH_AMOUNT
import static com.cheche365.cheche.parser.Constants.get_SCRATCH_IOP
import static com.cheche365.cheche.parser.Constants.get_THEFT
import static com.cheche365.cheche.parser.Constants.get_THEFT_IOP
import static com.cheche365.cheche.parser.util.BusinessUtils.addQFSMessage
import static com.cheche365.cheche.parser.util.BusinessUtils.resolveNewQuoteRecordInContext
import static com.cheche365.cheche.parser.util.InsuranceUtils._JUDGE_SINGLE_ADVICE_BASE
import static com.cheche365.cheche.parser.util.InsuranceUtils.get_CHECK_ADVICE_WITH_TRUE
import static com.cheche365.cheche.parser.util.InsuranceUtils.get_COMPOSITE_ALLOWED_POLICY_BASE
import static com.cheche365.cheche.parser.util.InsuranceUtils.get_SINGLE_ALLOWED_POLICY_BASE
import static com.cheche365.cheche.picc.util.CityCodeParamsMappings._CITY_CODE_PARAMS_MAPPINGS
import static com.cheche365.cheche.picc.util.EarlyDayMappings._EARLY_DAY_MAPPINGS
import static java.time.LocalDate.now as today




/**
 * 业务相关的工具
 */
@Slf4j
class BusinessUtils {

    private static final _KIND_CODE_MAPPING_DOUBLE_TYPE = [
        [ '050600', 'thirdPartyAmount', 'thirdPartyPremium' ],              // 第三者责任险
        [ '050701', 'driverAmount', 'driverPremium' ],                      // 车上人员责任险-司机
        [ '050702', 'passengerAmount', 'passengerPremium',                  // 车上人员责任险-乘客
          { passengerCount, amount -> amount / passengerCount }
        ],
        [ '050200', 'damageAmount', 'damagePremium' ],                      // 机动车辆损失险
        [ '050500', 'theftAmount', 'theftPremium' ],                        // 盗抢险
        [ '050310', 'spontaneousLossAmount', 'spontaneousLossPremium' ],    // 自燃损失险
        [ '050210', 'scratchAmount', 'scratchPremium' ],                    // 车身划痕损失险
    ]

    private static final _KIND_CODE_MAPPING_BOOLEAN_TYPE = [
        [ '050291', 'enginePremium' ],                                      // 发动机特别损失险
        [ '050451', 'unableFindThirdPartyPremium' ],                        // 无法找到第三方特约险
        [ '050911', 'damageIop' ],                                          // 机动车损失保险IOP
        [ '050912', 'thirdPartyIop' ],                                      // 第三者责任保险IOP
        [ '050921', 'theftIop' ],                                           // 盗抢险IOP
        [ '050935', 'spontaneousLossIop' ],                                 // 自燃损失险IOP
        [ '050922', 'scratchIop' ],                                         // 车身划痕损失险IOP
        [ '050924', 'engineIop' ],                                          // 发动机特别损失险IOP
        [ '050928', 'driverIop' ],                                          // 车上人员责任险-司机IOP
        [ '050929', 'passengerIop']                                         // 车上人员责任险-乘客IOP
    ]

    // 可能会禁用的险种的相关信息
    private static final _DISABLED_KIND_CODE_MAPPING = [
        '050210' : [
            scratchAmount       : [null, '该车无法上车身划痕损失险'],
            scratchIop          : [false, '该车无法上车身划痕损失险（不计免赔）']
        ],
        '050200' : [
            damage              : [false, '该车无法上机动车辆损失险'],
            damageIop           : [false, '该车无法上机动车辆损失险（不计免赔）']
        ],
        '050600' : [
            thirdPartyAmount    : [null, '该车无法上第三者责任险'],
            thirdPartyIop       : [false, '该车无法上第三者责任险（不计免赔）']
        ],
        '050500' : [
            theft               : [false, '该车无法上盗抢险'],
            theftIop            : [false, '该车无法上盗抢险（不计免赔）']
        ],
        '050701' : [
            driverAmount        : [null, '该车无法上车上人员责任险-司机'],
            driverIop           : [false, '该车无法上车上人员责任险-司机（不计免赔）']
        ],
        '050702' : [
            passengerAmount     : [null, '该车无法上车上人员责任险-乘客'],
            passengerIop        : [false, '该车无法上车上人员责任险-乘客（不计免赔）']
        ],
        '050291' : [
            engine              : [false, '该车无法上发动机特别损失险'],
            engineIop           : [false, '该车无法上发动机特别损失险（不计免赔）']
        ],
        '050231' : [
            glass               : [false, '该车无法上玻璃单独破碎险']
        ],
        '050310' : [
            spontaneousLoss     : [false, '该车无法上自燃损失险'],
            spontaneousLossIop  : [false, '该车无法上自燃损失险(不计免赔)']
        ],
        '050451' : [
            unableFindThirdParty     : [false, '该车无法上无法找到第三方']
        ]
    ]

    // 下面这个映射是那些无论是否选中必须更新保额的险种
    private static final _AMOUNT_PROPERTY_NAMES = [
        damageAmount            : 'purPurchasePrice',
        theftAmount             : 'purActualvalue',
        spontaneousLossAmount   : 'purActualvalue'
    ]



    /**
     * 用精准报价的json组装成QuoteRecord
     * @param accurateQuote
     * @param packageName
     * @param context
     * @return
     */
    static populateQuoteRecord(accurateQuote, packageName, context) {
        def newQuoteRecord = resolveNewQuoteRecordInContext(context)

        newQuoteRecord.with { quoteRecord ->
            // 乘客数
            def passengerCount = getCarSeat(context) - 1
            quoteRecord.passengerCount = passengerCount

            // 先更新下面这些无论是否选择都要有保额的险种
            _AMOUNT_PROPERTY_NAMES.each { amountPropName, jsonPropName ->
                def amount = accurateQuote[jsonPropName]
                quoteRecord[amountPropName] = amount
            }

            // 先从原始json中获取到package的详细列表，然后转换为[kindCode: [险种]]格式的mapping
            def accurateQuoteMapping = accurateQuote[packageName].collectEntries { insuranceTypeDetail ->
                [(insuranceTypeDetail.kindCode): insuranceTypeDetail]
            }

            // double型险种
            _KIND_CODE_MAPPING_DOUBLE_TYPE.each { kindCode, amountName, premiumName, amountConverter = null ->
                def kindItem = accurateQuoteMapping[kindCode]

                def insured = kindItem?.isTouBao // 某些险种可能根本就没有在列表中，比如划痕险，如果车龄超过3年就没有。
                quoteRecord[amountName] = insured ? amountConverter?.curry(passengerCount)?.call(kindItem.amount) ?: kindItem.amount : 0
                quoteRecord[premiumName] = insured ? kindItem.premium : 0

                // 将那些不能上的险种在套餐中设置为禁用
                disableUnavailableKindItems kindCode, kindItem, quoteRecord
            }

            // boolean型险种
            _KIND_CODE_MAPPING_BOOLEAN_TYPE.each { kindCode, premiumName ->
                def kindItem = accurateQuoteMapping[kindCode]

                def insured = kindItem?.isTouBao
                quoteRecord[premiumName] = insured ? kindItem.premium : 0

                // 将那些不能上的险种在套餐中设置为禁用
                disableUnavailableKindItems kindCode, kindItem, quoteRecord
            }

            // 玻璃单独破碎险
            def glassKindItem = accurateQuoteMapping.'050231'
//            GlassType glazzType = 10 == glassKindItem.amount ? glassTypeEnum.DOMESTIC_1
//                : 20 == glassKindItem.amount ? glassTypeEnum.IMPORT_2
//                : null
            quoteRecord.glassPremium = glassKindItem?.isTouBao ? glassKindItem.premium : 0
            if (!glassKindItem?.isTouBao) {
                disableUnavailableKindItems '050231', glassKindItem, quoteRecord
            }

            // 商业险总保费
            quoteRecord.premium = accurateQuote.CommonPackage.find { insurancePackageSummary ->
                packageName == insurancePackageSummary.kindCode
            }.premium
            //不计免赔总额计算
            quoteRecord.iopTotal = quoteRecord.sumIopItems()

            quoteRecord
        }
    }

    /**
     * 获得新的保险周期（起止时间）
     * @param context
     */
    static getNewInsurancePeriodText(context) {
        def (accurateStartDateText, accurateEndDateText) = getDateTextFromPeriod(context.period)
        if (!accurateStartDateText) {
            (accurateStartDateText, accurateEndDateText) = getDateTextFromReinsurance(context.reinsurance)
        }

        def renewalInfo = context.renewalInfo
        // 优先使用续保信息中的起保日期；非续保客户使用明天至第二年的今天作为起止时间，否则就从renewalInfo中直接取出起止时间
        def startDateText = renewalInfo?.prpcmain?.startdateStr ?: accurateStartDateText
        def endDateText = renewalInfo?.prpcmain?.enddateStr ?: accurateEndDateText

        def (newStartDateText, newEndDateText) = getDefaultInsurancePeriodText(context)
        if (startDateText) {
            def startDate = getLocalDate(_DATE_FORMAT1.parse(startDateText))
            def today = today()
            def earlyDays = getEarlyDays4Commercial context //续保取earlyDays4Insure

            if (today >= startDate.minusDays(earlyDays)) {
                newStartDateText = startDateText
                newEndDateText = endDateText
            } else { //续保T+5，转保T+1
                if (context.renewable) {
                    newStartDateText = _DATETIME_FORMAT1.format(today.plusDays(5))
                    newEndDateText = _DATETIME_FORMAT1.format(today.plusYears(1).plusDays(4))
                }
            }
        }

        new Tuple2(newStartDateText, newEndDateText)
    }

    //从reinsurance中获取新的起保周期
    static getDateTextFromReinsurance(reinsurance) {
        String startDateText = reinsurance?.startdate
        def needPlus1Day = startDateText ? '/23' == startDateText[startDateText.lastIndexOf('/')..-1] : false // i.e.:'2017/01/17/23' 需要增加一天
        def accurateStartDateText = startDateText ? startDateText[0..startDateText.lastIndexOf('/') - 1] : null
        def accurateEndDateText
        if (accurateStartDateText) {
            def accurateStartDate = getLocalDate(_DATE_FORMAT1.parse(accurateStartDateText))
            if (needPlus1Day) {
                accurateStartDate = accurateStartDate.plusDays(1)
                accurateStartDateText = _DATETIME_FORMAT1.format accurateStartDate
            }
            def accurateEndDate = accurateStartDate.plusYears(1).minusDays(1)
            accurateEndDateText = _DATETIME_FORMAT1.format accurateEndDate
        }

        new Tuple2(accurateStartDateText, accurateEndDateText)
    }

    //从period中获取新的起保周期
    static getDateTextFromPeriod(period) {
        def periodMessage = period?.message
        def m = periodMessage =~ /.*(\d{4}-\d{2}-\d{2}).*/
        def accurateStartDateText, accurateEndDateText
        if (m) {
            def lastEndDate = getLocalDate(_DATE_FORMAT3.parse(m[0][1]))
            def startDate = lastEndDate.plusDays(1)
            def endDate = lastEndDate.plusYears(1)
            accurateStartDateText = _DATETIME_FORMAT1.format startDate
            accurateEndDateText = _DATETIME_FORMAT1.format endDate
        }

        new Tuple2(accurateStartDateText, accurateEndDateText)
    }

    /**
     * 获取从明天到明年今天的日期
     */
    static getDefaultInsurancePeriodText(context) {
        def earlyDayMappings = getEarlyDays context
        def nextDays4Start = earlyDayMappings.nextDays4Start ?: 1

        def today = today()
        def startDateText = _DATETIME_FORMAT1.format(today.plusDays(nextDays4Start))
        def endDateText = _DATETIME_FORMAT1.format(today.plusYears(1))
        new Tuple2(startDateText, endDateText)
    }

    /**
     *套餐不能投(kindItem为空或kindItem?.isTouBao==false)时，且投了此险种，设为不能投
     */
    private static void disableUnavailableKindItems(kindCode, kindItem, quoteRecord) {
        if (!kindItem?.isTouBao && _DISABLED_KIND_CODE_MAPPING.containsKey(kindCode)) {
            def insurancePackage = quoteRecord.insurancePackage
            def disabledFieldNames = _DISABLED_KIND_CODE_MAPPING[kindCode]
            disabledFieldNames.each { entry ->
                def name = entry.key
                def (disabledValue, _1) = entry.value
                if (insurancePackage[name]) {
                    insurancePackage[name] = disabledValue
                }
            }
        }
    }

    /**
     * selectedCarModel：北京上海即为arInfo.CarModels[0], 非续保中科软车型地区为选择中间价的车型，非续保精友车型为选取中间价后注册车型后返回的body
     * 所以北京（carInfo?.limitLoadPerson）上海（selectedCarModel.seat）,中科软精友续保 （selectedCarModel.seat），非续保（selectedCarModel.seat）
     * @param context
     * @return
     */
    static int getCarSeat(context) {
        def carSeat = context.vehicleInfo?.SeatCount
        if (!carSeat) {
            def carModel = context.selectedCarModel
            carSeat = carModel?.seat ?: context.renewalInfo?.prpcitemCar?.seatcount
        }

        (carSeat ?: 5) as int
    }

    /**
     * 北京上海从carInfo中取初登日期；其他 续保从续保信息中取，非续保用户输入
     * 优先从续保日期中取，然后从carInfo中取。两者不一致优先取续保信息中的
     * @param context
     */
    static getCarEnrollDate(context) {
        def carEnrollDate = context.carInfo?.enrollDate

        if (!carEnrollDate) {
            if (context.trafficInfo?.cicarinfowxlist && context.trafficInfo.cicarinfowxlist[0].enrolldate) {
                // 在未调用VerifyNewCaptcha之前，context.trafficInfo是null
                carEnrollDate = _DATETIME_FORMAT1.format(_DATETIME_FORMAT2.parse(context.trafficInfo.cicarinfowxlist[0].enrolldate))
            } else if (context.renewalInfo?.prpcitemCar?.enrolldate) {
                carEnrollDate = _DATETIME_FORMAT1.format(_DATETIME_FORMAT2.parse(context.renewalInfo.prpcitemCar.enrolldate))
            } else {
                carEnrollDate = (context.auto.enrollDate ? _DATE_FORMAT1.format(context.auto.enrollDate)
                    : context.historicalVehicleInfo?.enrolldate ? _DATETIME_FORMAT1.format(_DATETIME_FORMAT2.parse(context.historicalVehicleInfo?.enrolldate))
                    : _DATE_FORMAT1.format(new Date()))
            }
        }

        carEnrollDate
    }

    /**
     * selectedCarModel：北京上海即为arInfo.CarModels[0], 非续保中科软车型地区为选择中间价的车型，非续保精友车型为选取中间价后注册车型后返回的body
     * 所以北京（selectedCarModel.price）上海（selectedCarModel.price）,中科软精友（selectedCarModel.price）
     * 价格优先从注册的价格中取(北京上海目前没有注册价格),然后从车型中选取
     * @param context
     * @return
     */
    static getCarPurchasePrice(context) {
        def carPrice = context.carPrice
        //根据人保官网js代码这样取值
        def carPurchasePrice = ('1' == carPrice?.carReduceFlag ? carPrice?.purchasePriceDefault : carPrice?.actualPrice)

        if (!carPurchasePrice) {
            def carModel = context.selectedCarModel
            carPurchasePrice = carModel?.price ?: context.renewalInfo?.prpcitemCar?.purchaseprice
        }

        carPurchasePrice
    }

    /**
     * selectedCarModel：北京上海即为arInfo.CarModels[0], 非续保中科软车型地区为选择中间价的车型，非续保精友车型为选取中间价后注册车型后返回的body
     * 北京上海selectedCarModel.modelCode中取车型编号；其他 续保selectedCarModel.modelCode中取，非续保selectedCarModel.modelCode(是根据用户输入的品牌型号得到的code)
     * @param context
     * @return
     */
    static getCarModelCode(context) {
        def carModel = context.selectedCarModel
        def carModelCode = carModel?.modelCode
        if (!carModelCode) {
            carModelCode = context.renewalInfo?.prpcitemCar?.modelcode ?: context.historicalVehicleInfo?.modelcode
        }

        carModelCode?.trim()
    }

    /**
     * 获取套餐名称
     * @param context
     * @return
     */
    static getPackageName(context) {
        context.renewable ? 'OptionalPackage' : 'ComprehensivePackage'
    }

    /**
     * 获取是否指定了驾驶人标志，1表示指定了。
     */
    static getAssignDriverFlag(context) {
        context.renewalInfo?.carDrivers ? '1' : ''
    }

    /**
     * 获取是否指定驾驶区域，11表示不指定 03，表示指定
     */
    static getRunAreaCodeName(context) {
        context.renewalInfo?.prpcitemCar?.runareacode ?: '11'
    }

    /**
     * 组装交强险信息到QuoteRecord
     * @param quote
     * @param context
     * @return
     */
    static populateQuoteRecordBZ(quote, context) {
        QuoteRecord quoteRecord = resolveNewQuoteRecordInContext(context)

        quoteRecord.with {
            compulsoryPremium = quote.premiumBZ as double
            // 车船税 ＝ 今年车船税 + 去年欠费 + 滞纳金
            autoTax = (quote.thisPayTax as double) + (quote.prePayTax as double) + (quote.delayPayTax as double)

            def hasQuoteAutoTax = quoteRecord.insurancePackage.autoTax
            def autoTaxEnabled = autoTax ? true : false //目前0表示不支持车船税

            if (hasQuoteAutoTax && !autoTaxEnabled) {
                insurancePackage.autoTax = false
                addQFSMessage context, _AUTO_TAX, '不支持投保车船税'
            } else if (!hasQuoteAutoTax && autoTaxEnabled) {
                insurancePackage.autoTax = true
                addQFSMessage context, _AUTO_TAX, '投保交强,车船税改为投保'
            }

            it
        }
    }

    /**
     * 从交强险返回的错误信息信息中获取新的交强险起止日期
     */
    static getNewCalculateBZDateText(errorMsg) {
        def patternErrorMessage1 = /.*(\d{8})[-|－](\d{8}).*/           //北京(-)上海(－) 保险期限是[20150604-20160603]
        def patternErrorMessage2 = /.*终保日期 (\d{4}-\d{2}-\d{2}).*/    //广州，四川   起保日期 2014-09-25 00;终保日期 2015-09-25 00
        def patternErrorMessage3 = /.*－(\d{4}年\d{2}月\d{2}日).*/       //上海 0101010128_重复投保[该车已在大地保险公司存在有效保单记录，保险期限是2015年07月31日 00:00－2016年07月30日 23:59[保险起期-保险止期]，请核对.]

        def dateFormatFrom = _DATE_FORMAT2
        def dateFormatTo = 'yyyy/MM/dd'

        def plusOneDay = true
        def m = errorMsg =~ patternErrorMessage1
        if (!m.matches()) {
            m = errorMsg =~ patternErrorMessage2
            dateFormatFrom = _DATE_FORMAT3
            plusOneDay = false
        }
        if (!m.matches()) {
            m = errorMsg =~ patternErrorMessage3
            dateFormatFrom = _DATE_FORMAT6
            plusOneDay = true
        }

        if (m.matches()) {
            def lastEndDateCIText = dateFormatFrom.parse(m[0][-1]).format(dateFormatTo)
            def startDate = getLocalDate(_DATE_FORMAT1.parse(lastEndDateCIText))
            if (plusOneDay) {
                startDate = startDate.plusDays(1)
            }
            def newStartDateCIText = _DATETIME_FORMAT1.format startDate
            def newEndDateCIText = _DATETIME_FORMAT1.format getLocalDate(_DATE_FORMAT1.parse(lastEndDateCIText)).plusYears(1)

            new Tuple2(newStartDateCIText, newEndDateCIText)
        } else {
            new Tuple2(null, null)
        }
    }

    /**
     * 获取商业险的投保日期限制
     * @param context
     */
    static getEarlyDays4Insuring(context) {
        def earlyDayMappings = getEarlyDays context
        context.renewable ? earlyDayMappings.earlyDays4Renewal : earlyDayMappings.earlyDays4Insure
    }

    /**
     * 获取商业险起保日期限制
     * @param context
     */
    static getEarlyDays4Commercial(context) {
        def earlyDayMappings = getEarlyDays context
        earlyDayMappings.earlyDays4Insure
    }

    /**
     * 获取交强险起保日期限制
     * @param context
     * @return
     */
    static getEarlyDays4Compulsory(context) {
        def earlyDayMappings = getEarlyDays context
        earlyDayMappings.earlyDays4InsureBZ
    }

    /**
     * 获取商业险最早起保时间
     * @param context
     * @return
     */
    static getNextDays4Commercial(context) {
        def earlyDayMappings = getEarlyDays context
        earlyDayMappings.nextDays4Start >= 0 ? earlyDayMappings.nextDays4Start : _EARLY_DAY_MAPPINGS.default.nextDays4Start
    }

    private static getEarlyDays(context) {
        getObjectByCityCode context.area, _EARLY_DAY_MAPPINGS, true
    }

    /**
     * 能否单投交强险
     */
    static isBZSingle(context) {
        1 == _CITY_CODE_PARAMS_MAPPINGS[context.area.id].BZSingle
    }

    static getAutoVinNo(context) {
        context.renewalInfo?.prpcitemCar?.frameno?.trim() ?: context.auto.vinNo
    }

    static getAutoEngineNo(context) {
        context.renewalInfo?.prpcitemCar?.engineno?.trim() ?: context.auto.engineNo
    }

    /**
     * 获取商业险起保时间（MAX(period, reinsure)）
     */
    static getActualStartDate(context) {
        def periodStartDateText = getDateTextFromPeriod(context.period).first
        def reinsuranceStartDateText = getDateTextFromReinsurance(context.reinsurance).first
        def periodStartDate = periodStartDateText ? getLocalDate(_DATE_FORMAT1.parse(periodStartDateText)) : null
        def reinsuranceStartDate = reinsuranceStartDateText ? getLocalDate(_DATE_FORMAT1.parse(reinsuranceStartDateText)) : null

        reinsuranceStartDate ?: periodStartDate
    }

    private static final _KIND_CODE_INSURANCE_PACKAGE_MAPPING = [
        ['050600', 'thirdPartyAmount', false], // 第三者责任险
        ['050701', 'driverAmount', false],     // 车上人员责任险-司机
        ['050702', 'passengerAmount', false, false,
         { passengerCount, amount -> amount / passengerCount }
        ],                                     // 车上人员责任险-乘客
        ['050210', 'scratchAmount', false],    // 车身划痕损失险
        ['050200', 'damage', true],            // 机动车辆损失险
        ['050500', 'theft', true],             // 盗抢险
        ['050310', 'spontaneousLoss', true],   // 自燃损失险
        ['050291', 'engine', true],            // 发动机特别损失险
        ['050451', 'unableFindThirdParty', true], //无法找到第三方特约险
        ['050911', 'damageIop', true],         // 机动车损失保险IOP
        ['050912', 'thirdPartyIop', true],     // 第三者责任保险IOP
        ['050921', 'theftIop', true],          // 盗抢险IOP
        ['050922', 'scratchIop', true],        // 车身划痕损失险IOP
        ['050935', 'spontaneousLossIop', true],// 自燃损失险IOP
        ['050924', 'engineIop', true],         // 发动机特别损失险IOP
        ['050928', 'driverIop', true],         // 车上人员责任险-司机IOP
        ['050929', 'passengerIop', true],      // 车上人员责任险-乘客IOP
        ['050231', 'glass', true, true]        // 玻璃险
    ]

    /**
     * 获取续保套餐
     */
    static generateRenewalPackage(context, originRenewalPackage) {
        def renewalPackage = new InsurancePackage()
        renewalPackage.compulsory = true
        renewalPackage.autoTax = true

        _KIND_CODE_INSURANCE_PACKAGE_MAPPING.each { kindCode, fieldName, isBoolean,
                                                    isGlass = false, amountConverter = null ->
            def item = originRenewalPackage [kindCode]
            renewalPackage [fieldName] = isBoolean ? (-1.0 != item?.amount ? true : false)
                : (-1.0 != item?.amount ? (amountConverter?.curry (getCarSeat (context) - 1)?.call (item.amount) ?: item.amount)
                : 0)
            if (isGlass && ('10' == item?.amount || '20' == item?.amount)) {
                renewalPackage.glassType = (10 == item.amount ? DOMESTIC_1 : IMPORT_2)
            }
        }

        renewalPackage
    }

    /**
     * 去掉出厂年份晚于初登日期的车型的过滤器
     */
    static final _PICC_FILTER_VEHICLE_LIST = { ctx, item ->
        def m = item.parentVehName =~ /(\d{4})款/
        def manufactureDate = m ? m[0][1] + '0101' : _DATETIME_FORMAT3.format(today().minusYears(15))
        _DATE_FORMAT2.parse(manufactureDate) <= ctx.auto.enrollDate ?:
            ctx.renewalInfo?.prpcitemCar?.enrolldate ? _DATE_FORMAT5.parse(ctx.renewalInfo.prpcitemCar.enrolldate)
                : today()
    }

    /**
     * 返回保单开始日期和结束日期。
     * 当context中的开始日期大于今天算起的可投保日期范围时返回今天算起的最大可投保日期。
     * @param context
     * @return
     */
    static final getRangeDate(context) {
        def renewalInfo = context.renewalInfo
        def startDateText = renewalInfo.prpcmain.startdateStr
        def early = getEarlyDays4Insuring context
        def format = _DATETIME_FORMAT1
        def today = today()
        def startDate = format.parse(startDateText).date
        if (startDate > today.plusDays(early)) {
            new Tuple2(today.plusDays(early), today.plusDays(early - 1).plusYears(1))
        } else {
            new Tuple2(startDateText, renewalInfo.prpcmain.enddateStr)
        }

    }

    static final getCommercialCanInsureDate(context) {
        getActualStartDate(context)?.minusDays(getEarlyDays4Commercial(context))
    }

    /**
     * 获取商业险开始日期TEXT,首先从续保信息context.renewalInfo.prpcmain获取,获取不到再从period, reinsure中获取
     * @param context
     * @return
     */
    static getRealStartDateTextBI(context) {
        def actualStartDate = getActualStartDate(context)
        def periodText = actualStartDate ? _DATETIME_FORMAT1.format(actualStartDate) : null
        periodText ?: context.vehicleInfo?.startDateBI ?: context.renewalInfo?.prpcmain?.startdateStr
    }

    /**
     * 获取商业险开始日期TEXT,首先从context.dateInfo获取,如失败从补充信息中获取,
     * 再失败，从getNextDays4Commercial中获取相应的起保日期
     * @param context
     * @return 长度是2的元组
     */
    static getDefaultStartDateTextBI(context, datetimeFormat = _DATETIME_FORMAT1) {
        getDefaultStartDateText context, parserBU.&getCommercialInsurancePeriodTexts, datetimeFormat
    }

    /**
     * 获取交强险开始日期TEXT,首先从context.dateInfo获取,如失败从补充信息中获取,
     * 再失败，从getNextDays4Commercial中获取相应的起保日期
     * @param context
     * @return 长度是2的元组
     */
    static getDefaultStartDateTextCI(context, datetimeFormat = _DATETIME_FORMAT1) {
        getDefaultStartDateText context, parserBU.&getCompulsoryInsurancePeriodTexts, datetimeFormat
    }

    private static getDefaultStartDateText(context, getInsurancePeriodTexts, datetimeFormat) {
        def (startDateText, endDateText) = getInsurancePeriodTexts(context, datetimeFormat, false)
        if (!startDateText) {
            def startDate = today().plusDays(getNextDays4Commercial(context))
            startDateText = datetimeFormat.format startDate
            endDateText = datetimeFormat.format startDate.plusYears(1).minusDays(getNextDays4Commercial(context) ? 1 : 0)
        }
        new Tuple2(startDateText, endDateText)
    }

    static isTextWithAsteriskSame(text, textWithAsterisk) {
        if (textWithAsterisk) {
            def lastTextWithoutAsterisk = textWithAsterisk.split('\\*')[-1]
            text[-lastTextWithoutAsterisk.size()..-1] == lastTextWithoutAsterisk
        } else {
            false
        }
    }

    //<editor-fold defaultstate="collapsed" desc="处理套餐建议">

    private static final _POLICY_IGNORABLE_ADVICE = 0L
    private static final _POLICY_CODE_FORBID_SCRATCH = _POLICY_IGNORABLE_ADVICE + 1
    private static final _POLICY_CODE_FORBID_DAMAGE_AND_THEFT = _POLICY_CODE_FORBID_SCRATCH + 1
    private static final _POLICY_CODE_ADJUST_GLASS = _POLICY_CODE_FORBID_DAMAGE_AND_THEFT + 1


    private static final _ERROR_MESSAGE_POLICY = { advice, context, others ->
        getFatalErrorFSRV advice.entrySet().first().value
    }

    private static final _ADVICE_POLICY_MAPPINGS = [
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_SCRATCH))         : _SINGLE_ALLOWED_POLICY_BASE.curry(_SCRATCH_AMOUNT, _SCRATCH_IOP, 0, false),
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_ADJUST_GLASS))           : _SINGLE_ALLOWED_POLICY_BASE.curry(_GLASS, _GLASS_TYPE, true, IMPORT_2),
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_CODE_FORBID_DAMAGE_AND_THEFT)): _COMPOSITE_ALLOWED_POLICY_BASE.curry([_DAMAGE, _THEFT], [_DAMAGE_IOP, _THEFT_IOP], false, false),
        (_JUDGE_SINGLE_ADVICE_BASE.curry(_POLICY_IGNORABLE_ADVICE))            : _ERROR_MESSAGE_POLICY
    ]

    // 保额类险种提示信息
    private static final _CHECK_ADVICE_BASE = { propName, advice, context, others ->
        (advice =~ /.*(?:不得承保|禁止承保).*$propName.*/).with { m ->
            m.find()
        }
    }
//    resultMsg=打回出单系统！！获取新核保返回信息：划痕险保额不高于2000元；
    private static final _CHECK_ADVICE_TEXT_BASE = { text, advice, context, others ->
        (advice =~ /.*(?:$text).*/).with { m ->
            m.find()
        }
    }

    private static final _COMMON_REGULATOR_BASE = { keyCode, advice, context, others ->
        [(keyCode): advice]
    }



    static final _ADVICE_REGULATOR_MAPPINGS = [
        (_CHECK_ADVICE_BASE.curry('划痕险'))                        : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_SCRATCH),
        (_CHECK_ADVICE_TEXT_BASE.curry('进口玻璃承保|进口车投保国产玻璃险'))     : _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_ADJUST_GLASS),
        (_CHECK_ADVICE_TEXT_BASE.curry('商业险脱保超过.*天，并且承保车损险或盗抢险')): _COMMON_REGULATOR_BASE.curry(_POLICY_CODE_FORBID_DAMAGE_AND_THEFT),
        (_CHECK_ADVICE_WITH_TRUE)                                : _COMMON_REGULATOR_BASE.curry(_POLICY_IGNORABLE_ADVICE)
    ]

    static final _GET_EFFECTIVE_ADVICES = { advices, context, others ->
//        def m = advices =~ /.*不通过原因为：(.*)/
//        if (m.find()) {
//            return m.collect { advice ->
//                advice[1].split(';')
//            }.flatten()
//        }
        [advices]
    }

    static final _CITY_ADVICE_POLICY_MAPPINGS = [
        default: _ADVICE_POLICY_MAPPINGS
    ]

    //</editor-fold>
}
