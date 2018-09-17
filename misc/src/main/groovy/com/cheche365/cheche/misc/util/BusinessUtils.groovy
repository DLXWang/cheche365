package com.cheche365.cheche.misc.util

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.InsurancePackage
import com.cheche365.cheche.core.model.OrderSourceType
import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.repository.InsuranceCompanyRepository
import groovy.util.logging.Slf4j
import org.apache.commons.lang.math.RandomUtils

import static TimeZone.getDefault as timeZone
import static com.cheche365.cheche.common.Constants._DATETIME_FORMAT2
import static com.cheche365.cheche.common.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.common.Constants._DATE_FORMAT3
import static com.cheche365.cheche.common.Constants._DATE_FORMAT5
import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.misc.service.InsuranceRules.damageIOPPremium
import static com.cheche365.cheche.misc.service.InsuranceRules.damagePremium
import static com.cheche365.cheche.misc.service.InsuranceRules.driverIOPPremium
import static com.cheche365.cheche.misc.service.InsuranceRules.driverPremium
import static com.cheche365.cheche.misc.service.InsuranceRules.engineIOPPremium
import static com.cheche365.cheche.misc.service.InsuranceRules.enginePremium
import static com.cheche365.cheche.misc.service.InsuranceRules.glassPremium
import static com.cheche365.cheche.misc.service.InsuranceRules.passengerIOPPremium
import static com.cheche365.cheche.misc.service.InsuranceRules.passengerPremium
import static com.cheche365.cheche.misc.service.InsuranceRules.scratchIOPPremium
import static com.cheche365.cheche.misc.service.InsuranceRules.scratchPremium
import static com.cheche365.cheche.misc.service.InsuranceRules.spontaneousLossPremium
import static com.cheche365.cheche.misc.service.InsuranceRules.theftIOPPremium
import static com.cheche365.cheche.misc.service.InsuranceRules.theftPremium
import static com.cheche365.cheche.misc.service.InsuranceRules.thirdPartyIOPPremium
import static com.cheche365.cheche.misc.service.InsuranceRules.thirdPartyPremium
import static java.math.BigDecimal.ROUND_DOWN
import static java.time.LocalDateTime.of
import static java.time.LocalDateTime.ofInstant
import static java.time.ZoneId.systemDefault
import static java.time.ZoneOffset.ofHours
import static java.time.ZonedDateTime.now
import static java.time.ZonedDateTime.ofInstant as date
import static java.time.temporal.ChronoUnit.DAYS
import static java.time.temporal.ChronoUnit.MONTHS
import static java.util.Date.from
import static org.apache.commons.lang3.RandomUtils.nextDouble as randomDouble
import static org.apache.commons.lang3.RandomUtils.nextInt as randomInt

class BusinessUtils {

    private static final _PID_CITY_DISTINCT_NUMBER_MAPPINGS = [
        110: [101, 102, 105, 106, 107, 108, 109, 111, 112, 113, 114, 115, 116, 117, 228, 229]
    ]

    private static final _PID_CITY_CODES = _PID_CITY_DISTINCT_NUMBER_MAPPINGS.keySet()

    private static final _PID_CHECK_CODE_DIGIT_WEIGHTS = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2]


    private static final _VIN_NO_VALID_CHARS = (0..9) + ('A'..'Z') - ['I', 'O', 'Q']

    private static final _VIN_NO_CHECK_CODE_DIGIT_WEIGHTS = [8, 7, 6, 5, 4, 3, 2, 10, 0, 9, 8, 7, 6, 5, 4, 3, 2]

    private static final _VIN_NO_ALPHA_NUMBER_MAPPINGS = [
        1: 1, 2: 2, 3: 3, 4: 4, 5: 5, 6: 6, 7: 7, 8: 8, 9: 9, 0: 0,
        A: 1, B: 2, C: 3, D: 4, E: 5, F: 6, G: 7, H: 8, J: 1, K: 2,
        L: 3, M: 4, N: 5, P: 7, R: 9, S: 2, T: 3, U: 4, V: 5, W: 6,
        X: 7, Y: 8, Z: 9
    ]

    private static final _VIN_NO_MODEL_YEAR_CODE_MAPPINGS = [
        2004: 4, 2005: 5, 2006: 6, 2007: 7, 2008: 8, 2009: 9, 2010: 'A', 2011: 'B', 2012: 'C', 2013: 'D'
    ]

    private static final _VIN_NO_MODEL_CODE_YEAR_MAPPINGS = _VIN_NO_MODEL_YEAR_CODE_MAPPINGS.collect { k, v -> [(v): k] }.inject { p, c -> p + c }

    private static final _ENGINE_NO_VALID_CHARS = (0..9) + ('A'..'Z')

    private static final _AUTO_TAX_VALID_PRICES = [
        { 0 <= it && it < 10 }   : 200,
        { 10 <= it && it < 30 }  : 260,
        { 30 <= it && it < 50 }  : 320,
        { 50 <= it && it < 70 }  : 380,
        { 70 <= it && it < 85 }  : 600,
        { 85 <= it && it < 90 }  : 750,
        { 90 <= it && it < 95 }  : 1500,
        { 95 <= it && it < 96 }  : 1800,
        { 96 <= it && it < 99 }  : 2400,
        { 99 <= it && it <= 100 }: 4400
    ]

    /**
     * 随机个人身份证
     * @return
     */
    static getRandomPid() {
        "$randomPidCityDistrictCode$randomPidBirthDay$randomPidSequenceCode".with { first17Digits ->
            def checkCode = (first17Digits.collect { ch ->
                ch as int
            }.withIndex().collect { num, idx ->
                num * _PID_CHECK_CODE_DIGIT_WEIGHTS[idx]
            }.sum() % 11).with { t ->
                def r = (12 - t) % 11
                10 == r ? 'X' : r
            }
            "$first17Digits$checkCode"
        }
    }

    /**
     * 随机个人身份证城市区域编码
     * @return
     */
    static getRandomPidCityDistrictCode() {
        def cityCode = _PID_CITY_CODES[randomInt(0, _PID_CITY_CODES.size() - 1)]
        def distinctCodes = _PID_CITY_DISTINCT_NUMBER_MAPPINGS[cityCode]
        "$cityCode${distinctCodes[randomInt(0, distinctCodes.size() - 1)]}"
    }

    /**
     * 随机个人身份证生日
     * @return
     */
    static getRandomPidBirthDay() {
        "${randomInt(1965, 1995)}${(randomInt(1, 12) as String).padLeft(2, '0')}${(randomInt(1, 28) as String).padLeft(2, '0')}"
    }

    /**
     * 随机个人身份证序列号
     * @return
     */
    static getRandomPidSequenceCode() {
        randomInt 110, 850
    }

    /**
     * 随机车架号
     */
    static getRandomVinNo() {
        (['L'] + (2..7).collect {
            _VIN_NO_VALID_CHARS[randomInt(0, _VIN_NO_VALID_CHARS.size() - 1)]
        } + [_VIN_NO_MODEL_YEAR_CODE_MAPPINGS[randomInt(2004, 2013)]] + (9..17).collect {
            _VIN_NO_VALID_CHARS[randomInt(0, _VIN_NO_VALID_CHARS.size() - 1)]
        }).with { alphaList ->
            def checkCode = (alphaList.withIndex().collect { alpha, idx ->
                _VIN_NO_ALPHA_NUMBER_MAPPINGS[alpha] * _VIN_NO_CHECK_CODE_DIGIT_WEIGHTS[idx]
            }.sum() % 11).with { r ->
                10 == r ? 'X' : r
            }

            alphaList[8] = checkCode
            alphaList
        }.join ''
    }

    /**
     * 随机引擎号
     */
    static getRandomEngineNo() {
        (1..randomInt(6, 12)).collect {
            _ENGINE_NO_VALID_CHARS[randomInt(0, _ENGINE_NO_VALID_CHARS.size() - 1)]
        }.join ''
    }

    /**
     * 随机初登日期
     * 初登日期不能小于车型年度
     * @return
     */
    static getRandomEnrollDate(String vinNo) {
        _DATE_FORMAT3.parse "${randomInt(2004, 2013)}-${(randomInt(1, 12) as String).padLeft(2, '0')}-${(randomInt(1, 28) as String).padLeft(2, '0')}"
    }

    /**
     * 计算折旧价（每月千分之六）
     * @param originalPrice
     * @param enrollDate
     */
    static getDepreciatedPrice(double originalPrice, Date enrollDate) {
        def pricePerMonth = originalPrice * 0.006
        def months = date(enrollDate.toInstant(), systemDefault()).until now(), MONTHS
        originalPrice - pricePerMonth * months
    }

    /**
     * 根据套餐生成报价记录
     * @param insurancePackage
     * @param generateDamageAmount
     * @param auto
     * @param user
     * @param area
     * @param icEnum
     * @param qfTypeEnum
     * @return
     */
    static getQuoteRecord(InsurancePackage insurancePackage, generateDamageAmount, Auto auto, User user, Area area, InsuranceCompanyRepository icRepo, qfType) {
        def ic
//        if (110000L != area.id) { // 外地的单子 平安100%
//            ic = icRepo.findByCode('PINGAN')
//
////        } else if (PARTNER_BAIDU.name == user.registerChannel.name) {
////            ic = randomInt(0, 100).with { dice ->
////                0 <= dice && dice < 85 ? icRepo.findByCode('PICC')
////                    : 85 <= dice && dice < 95 ? icRepo.findByCode('CHINALIFE')
////                    : icRepo.findByCode('CIC')
////            }
//        } else {
//
//            // 北京的百度渠道，没有平安、太平洋、阳光、国寿的
//            if('PARTNER_BAIDU' == user.registerChannel.getName()) {
//                ic = randomInt(0, 100).with { dice ->
//                    0 <= dice && dice < 70 ? icRepo.findByCode('PICC')
//                        : 70 <= dice && dice < 90 ? icRepo.findByCode('CHINALIFE')
//                        : icRepo.findByCode('CIC')
//                }
//            } else {
//                ic = randomInt(0, 100).with { dice ->
//                    0 <= dice && dice < 62 ? icRepo.findByCode('PICC')
//                        : 62 <= dice && dice < 77 ? icRepo.findByCode('CPIC')
//                        : 77 <= dice && dice < 87 ? icRepo.findByCode('PINGAN')
//                        : 87 <= dice && dice < 91 ? icRepo.findByCode('CIC')
//                        : 91 <= dice && dice < 96 ? icRepo.findByCode('SINOSIG')
//                        : icRepo.findByCode('CHINALIFE')
//                }
//
//            }
//        }


        def r = org.apache.commons.lang3.RandomUtils.nextInt(1, 100)
        if (1 <= r && r < 40) {
            ic = icRepo.findByCode('PICC')
        } else if (40 <= r && r < 60) {
            ic = icRepo.findByCode('CPIC')
        } else if (60 <= r && r < 80) {
            ic = icRepo.findByCode('PINGAN')
        } else {
            ic = icRepo.findByCode('SINOSIG')
        }


        def discountRate = randomDouble 0.7, 1
        def premium = 0.0

        double damageAmount = generateDamageAmount()
        double depreciatedPrice = getDepreciatedPrice damageAmount, auto.enrollDate


        double damageP = insurancePackage.damage ? damagePremium(area, auto, damageAmount) : 0.0
        double damageIop = insurancePackage.damageIop ? damageIOPPremium(area, auto, damageP) : 0.0
        premium += damageP + damageIop

        double thirdPartyAmount = insurancePackage.thirdPartyAmount
        double thirdPartyP = thirdPartyAmount ? thirdPartyPremium(area, auto, thirdPartyAmount) : 0.0
        double thirdPartyIop = insurancePackage.thirdPartyIop ? thirdPartyIOPPremium(area, auto, thirdPartyP) : 0.0
        premium += thirdPartyP + thirdPartyIop

        double theftAmount = depreciatedPrice
        double theftP = insurancePackage.theft ? theftPremium(area, auto, theftAmount) : 0.0
        double theftIop = insurancePackage.theftIop ? theftIOPPremium(area, auto, theftP) : 0.0
        premium += theftP + theftIop

        double engineP = insurancePackage.engine ? enginePremium(area, auto, damageP) : 0.0
        def engineIop = insurancePackage.engineIop ? engineIOPPremium(area, auto, engineP) : 0.0
        premium += engineP + engineIop

        double driverAmount = insurancePackage.driverAmount
        double driverP = driverAmount ? driverPremium(area, auto, driverAmount) : 0.0
        double driverIop = insurancePackage.driverIop ? driverIOPPremium(area, auto, driverP) : 0.0
        premium += driverP + driverIop

        double passengerCount = (auto.seats ?: 5) - 1
        double passengerAmount = insurancePackage.passengerAmount
        double passengerP = passengerAmount ? passengerPremium(area, auto, passengerAmount) : 0.0
        double passengerIop = insurancePackage.passengerIop ? passengerIOPPremium(area, auto, passengerP) : 0.0
        premium += passengerP + passengerIop

        double spontaneousLossAmount = depreciatedPrice
        double spontaneousLossP = insurancePackage.spontaneousLoss ? spontaneousLossPremium(area, auto, spontaneousLossAmount) : 0.0
        premium += spontaneousLossP

        double scratchAmount = insurancePackage.scratchAmount
        double scratchP = scratchAmount ? scratchPremium(area, auto, damageAmount, scratchAmount) : 0.0
        double scratchIop = insurancePackage.scratchIop ? scratchIOPPremium(area, auto, scratchP) : 0.0
        premium += scratchP + scratchIop

        double glassP = insurancePackage.glass ? glassPremium(area, auto, insurancePackage.glassType, damageAmount) : 0.0
        premium += glassP

        double compulsoryPremium = 880
        int dice = randomInt 0, 100
        double autoTax = _AUTO_TAX_VALID_PRICES.findResult { hitCheck, price ->
            hitCheck(dice) ? price : null
        }


        new QuoteRecord(
            discount: discountRate,

            compulsoryPremium: compulsoryPremium * discountRate,
            autoTax: autoTax,

            premium: premium * discountRate,

            damageAmount: damageAmount,
            damagePremium: damageP * discountRate,
            damageIop: damageIop * discountRate,

            thirdPartyAmount: thirdPartyAmount,
            thirdPartyPremium: thirdPartyP * discountRate,
            thirdPartyIop: thirdPartyIop * discountRate,

            theftAmount: theftAmount,
            theftPremium: theftP * discountRate,
            theftIop: theftIop * discountRate,

            enginePremium: engineP * discountRate,
            engineIop: engineIop * discountRate,

            driverAmount: driverAmount,
            driverPremium: driverP * discountRate,
            driverIop: driverIop * discountRate,

            passengerAmount: passengerAmount,
            passengerPremium: passengerP * discountRate,
            passengerIop: passengerIop * discountRate,
            passengerCount: passengerCount,

            spontaneousLossAmount: spontaneousLossAmount,
            spontaneousLossPremium: spontaneousLossP * discountRate,

            scratchAmount: scratchAmount,
            scratchPremium: scratchP * discountRate,
            scratchIop: scratchIop * discountRate,

            glassPremium: glassP * discountRate,


            insurancePackage: insurancePackage,
            insuranceCompany: ic,
            area: area,
            applicant: user,
            auto: auto,

            quoteFlowType: qfType
        )
    }

    /**
     * 返回传入日期的当日起始和终止时间（精确到秒）
     * 即当日的00:00:00和23:59:59
     * @param date
     * @return
     */
    static getStartAndEndDateTime(Date date) {
        def time = ofInstant(date.toInstant(), timeZone().toZoneId())
        def startTime = time.withHour(0).withMinute(0).withSecond(0).withNano(0)
        def endTime = time.withHour(23).withMinute(59).withSecond(59).withNano(0)

        [from(startTime.toInstant(ofHours(8))), from(endTime.toInstant(ofHours(8)))]
    }

    /**
     * 根据期望数据落入的起始、终止时间范围以及数据总量和当前数据的偏移，
     * 返回当前数据对应的日期
     * @param startDate
     * @param endDate
     * @param totalCount
     * @param index
     * @return
     */
    static getDateInSpecificRange(startDate, endDate, totalCount, index) {
        def numberOfDays = startDate.until(endDate, DAYS)
        def onNumberOfDays = new BigDecimal(numberOfDays / totalCount * index).setScale(0, ROUND_DOWN)
        def createDate = startDate.plusDays(onNumberOfDays as long)
        //得到创建时间，时分秒随机得到
        Random rand = new Random();
        def localDateTime = of(
            createDate.getYear(),
            createDate.getMonth(),
            createDate.getDayOfMonth(),
            (rand.nextInt(18) + 6) as int,
            rand.nextInt(60) as int,
            rand.nextInt(60) as int)
        _DATE_FORMAT5.parse(_DATETIME_FORMAT2.format(localDateTime))
    }

    /**
     * 根据订单创建时间获取随机之后40天内的起保时间
     * @param date 订单创建时间
     */
    static getInsuranceEffectiveDate(date) {
        def delayDays = new Random().nextInt(40)

        def localDate = getLocalDate(date)
        def s = localDate.plusDays(delayDays)
        def e = s.plusYears(1).minusDays(1)
        def sDate = _DATE_FORMAT3.parse(_DATETIME_FORMAT3.format(s))
        def eDate = _DATE_FORMAT3.parse(_DATETIME_FORMAT3.format(e))

        [sDate, eDate]
    }

    private static PREMIUM_0 = { premium ->
        premium <= 0
    }
    private static PREMIUM_0_100 = { premium ->
        0 < premium && premium <= 100
    }
    private static PREMIUM_100_1000 = { premium ->
        100 < premium && premium <= 1000
    }
    private static PREMIUM_1000_3000 = { premium ->
        1000 < premium && premium <= 3000
    }
    private static PREMIUM_3000_5000 = { premium ->
        3000 < premium && premium <= 5000
    }
    private static PREMIUM_5000 = { premium ->
        premium > 5000
    }

    // 根据保费多少构造固定的套餐
    private static _FIXED_INSURANCE_PACKAGE = [
        (PREMIUM_0)        : [
            [damage: false],
            [damage: 1]
        ],
        (PREMIUM_0_100)    : [
            [damage: true],
            [damage: 1]
        ],
        (PREMIUM_100_1000) : [
            [thirdPartyAmount: 100000L, damage: true],
            [damage: 1]
        ],
        (PREMIUM_1000_3000): [
            [thirdPartyAmount: 500_000L, damage: true, theft: true],
            [damage: 0.81, theft: 0.19]
        ],
        (PREMIUM_3000_5000): [
            [thirdPartyAmount: 1_000_000L, driverAmount: 100_000L, passengerAmount: 100_000L, damage: true, theft: true],
            [damage: 0.81, theft: 0.19]
        ],
        (PREMIUM_5000)     : [
            [thirdPartyAmount: 1500000L, driverAmount: 200_000L, passengerAmount: 200_000L, damage: true, theft: true, spontaneousLoss: true],
            [damage: 0.64, theft: 0.19, spontaneousLoss: 0.17]
        ]
    ]


    private static AMOUNT_PREMIUM_MAPPING = [
        thirdPartyAmount: [
            50000.00  : 440,
            100000.00 : 604,
            150000.00 : 837,
            200000.00 : 740,
            300000.00 : 965,
            500000.00 : 1046,
            1000000.00: 1322,
            1500000.00: 1527,
            2000000.00: 1727,
            2500000.00: 1711,
            3000000.00: 2391,
        ],
        driverAmount    : [
            10000.00 : 26,
            15000.00 : 38,
            20000.00 : 56,
            30000.00 : 77,
            50000.00 : 98,
            80000.00 : 192,
            100000.00: 230,
            200000.00: 480,
            300000.00: 1071,

        ],
        passengerAmount : [
            10000.00 : 70,
            14000.00 : 92,
            20000.00 : 141,
            30000.00 : 112,
            50000.00 : 211,
            80000.00 : 109,
            100000.00: 569,
            200000.00: 1233,
            300000.00: 1599,
            800000.00: 1217,
        ]
    ]

    private static _POPULATE_QUOTE_RECORD_AMOUNT = { specialProp, insurancePackage, quoteRecord, premium ->
        def amountName = specialProp + 'Amount'

        if (insurancePackage[amountName]) {
            def propAmount = insurancePackage[amountName] as long
            propAmount = AMOUNT_PREMIUM_MAPPING[amountName].keySet().min { Math.abs(propAmount - it) }
            def propPremium = AMOUNT_PREMIUM_MAPPING[amountName][propAmount]

            if (propPremium > premium - quoteRecord.calculatePremium()) {
                propPremium = premium - quoteRecord.calculatePremium()
            }

            def propIop = propPremium * 0.15
            def actualPropPremium = propPremium - propIop

            quoteRecord[amountName] = propAmount
            quoteRecord[specialProp + 'Premium'] = actualPropPremium

            insurancePackage[specialProp + 'Iop'] = true
            quoteRecord[specialProp + 'Iop'] = propIop
        }

        quoteRecord
    }

    private static _POPULATE_QUOTE_RECORD_BOOLEAN = { specialProp, proportion, insurancePackage, quoteRecord, remainPremium ->

        if (insurancePackage[specialProp]) {

            if (remainPremium > 0) {

                def propPremium = remainPremium * proportion



                def propIop = propPremium * 0.15
                def actualPropPremium = propPremium - propIop

                def damageAmount = quoteRecord.damageAmount ?: (actualPropPremium / 0.0153220933)  // 肯定有车损，且在最前面

                quoteRecord[specialProp + 'Amount'] = damageAmount
                quoteRecord[specialProp + 'Premium'] = actualPropPremium

                insurancePackage[specialProp + 'Iop'] = true
                quoteRecord[specialProp + 'Iop'] = propIop

            }
        }

        quoteRecord
    }

    private static _POPULATE_QUOTE_RECORD = [
        thirdParty     : _POPULATE_QUOTE_RECORD_AMOUNT.curry('thirdParty'),
        driver         : _POPULATE_QUOTE_RECORD_AMOUNT.curry('driver'),
        passenger      : _POPULATE_QUOTE_RECORD_AMOUNT.curry('passenger'),
        scratch        : _POPULATE_QUOTE_RECORD_AMOUNT.curry('scratch'),

        damage         : _POPULATE_QUOTE_RECORD_BOOLEAN.curry('damage'),
        theft          : _POPULATE_QUOTE_RECORD_BOOLEAN.curry('theft'),
        spontaneousLoss: _POPULATE_QUOTE_RECORD_BOOLEAN.curry('spontaneousLoss'),
    ]


    static fakeQuoteInfoByPremium(premium, compulsoryPremium, autoTax) {
        def qr = new QuoteRecord()

        def (insurancePackageProps, proportion) = _FIXED_INSURANCE_PACKAGE.findResult { getPremiumGrade, value ->
            getPremiumGrade(premium) ? value : null
        }

        def insurancePackage = new InsurancePackage(insurancePackageProps)
        if (compulsoryPremium) {
            insurancePackage.compulsory = true
        }
        if (autoTax) {
            insurancePackage.autoTax = true
        }
        insurancePackage.calculateUniqueString()

        if (compulsoryPremium) {
            qr.compulsoryPremium = compulsoryPremium
        }
        if (autoTax) {
            qr.autoTax = autoTax
        }


        qr = ['thirdParty', 'driver', 'passenger', 'scratch'].inject qr, { quoteRecord, specialProp ->
            _POPULATE_QUOTE_RECORD[specialProp] insurancePackage, quoteRecord, premium
        }
        def remainPremium = premium - qr.calculatePremium()

        qr = ['damage', 'theft', 'spontaneousLoss'].inject qr, { quoteRecord, specialProp ->
            _POPULATE_QUOTE_RECORD[specialProp] proportion[specialProp], insurancePackage, quoteRecord, remainPremium

        }
        qr.calculatePremium()

        [insurancePackage, qr]
    }

    /**
     * 如果文件不存在则创建目录和文件
     */
    static checkAndAmendFile(file) {
        if (!file.exists()) {
            def fileDir = (file.absolutePath - file.name) as File
            if (!fileDir.exists()) {
                fileDir.mkdirs()
            }
            file.createNewFile()
        }
    }
    /**
     * 如果文件目录不存在则创建目录
     */
    static checkFileDirs(file) {
        if (!file.exists()) {
            def dataFileDir = (file.absolutePath - file.name) as File
            if (!dataFileDir.exists()) {
                dataFileDir.mkdirs()
            }
        }
    }

    static getInsuranceCompanyByFuzzyName(name) {
        [
            InsuranceCompany.Enum.PICC,
            InsuranceCompany.Enum.SINOSIG,
            InsuranceCompany.Enum.PINGAN,
            InsuranceCompany.Enum.CPIC,
            InsuranceCompany.Enum.CHINALIFE,
            InsuranceCompany.Enum.CIC,
            InsuranceCompany.Enum.ZHONGAN,
            InsuranceCompany.Enum.AXATP,
            InsuranceCompany.Enum.FUNDINS,
            InsuranceCompany.Enum.ANSWERN,


            InsuranceCompany.Enum.TAIPING,
            InsuranceCompany.Enum.allianz,
            InsuranceCompany.Enum.starr,
            InsuranceCompany.Enum.tk,
            InsuranceCompany.Enum.minanins,
            InsuranceCompany.Enum.liberty,
            InsuranceCompany.Enum.alltrust,
            InsuranceCompany.Enum.TIAN,
            InsuranceCompany.Enum.HUATAI,
            InsuranceCompany.Enum.NEWCHINA,
            InsuranceCompany.Enum.FORESEA,
            InsuranceCompany.Enum.GENERALI,
            InsuranceCompany.Enum.DICC,
            InsuranceCompany.Enum.AIG,
            InsuranceCompany.Enum.ABLIFE,
            InsuranceCompany.Enum.FUBON,
            InsuranceCompany.Enum.BPIC,
            InsuranceCompany.Enum.CHIC,
            InsuranceCompany.Enum.YDPIC,
            InsuranceCompany.Enum.URTRUST,

        ].findResult {
            if (name.contains('太平洋')) {
                InsuranceCompany.Enum.CPIC
            } else if (name.contains('太平')) {
                InsuranceCompany.Enum.TAIPING
            } else if (it.name.contains(name)) {
                it
            }
        }
    }

    static getChannelByFuzzyName(name) {
        switch (name) {
            case '世纪通保':
                Channel.Enum.PARTNER_NCI_25
                break
            case '人人优品':
                Channel.Enum.PARTNER_RRYP_40
                break
            case '保险师':
                Channel.Enum.PARTNER_BXS_30
                break
            case '支付宝加油服务':
                Channel.Enum.ALIPAY_10
                break
            case '支付宝服务窗':
                Channel.Enum.ALIPAY_FUWUCHUANG_21
                break
            case '汽车之家':
                Channel.Enum.PARTNER_AUTOHOME_13
                break
            case '百度地图':
                Channel.Enum.PARTNER_BAIDU_15
                break
            case '途虎养车':
                Channel.Enum.PARTNER_TUHU_203
                break
            case '兼业代理':
                randomChannel
                break
            case 'IOS':
                Channel.Enum.IOS_4
                break
            case '自有':
                randomChannel
                break
            case '微车':
                randomChannel
                break
            default:
                Channel.Enum.UNKNOWN_9
        }
    }

    private static getRandomChannel() {
        def r = randomInt(0, 100)
        if (r <= 14) {
            Channel.Enum.WE_CHAT_3
        } else if (r <= 16) {
            Channel.Enum.IOS_4
        } else if (r <= 98) {
            Channel.Enum.WEB_5
        } else {
            Channel.Enum.ANDROID_6
        }
    }

    static getPayChannelByFuzzyName(name) {
        if (name?.contains('支付宝')) {
            PaymentChannel.Enum.ALIPAY_1
        } else if (name?.contains('微信')) {
            PaymentChannel.Enum.WECHAT_4
        } else if (name?.contains('银联')) {
            PaymentChannel.Enum.UNIONPAY_3
        } else {
            PaymentChannel.Enum.OFFLINEBYCARD_6
        }
    }

    static isCPS(sourceChannelName) {
        ['微车'].find {
            sourceChannelName?.contains it
        }
    }

    static getRandomId() {
        def s = [
            362202, 610125, 360123, 440106, 450102, 132902, 530113, 130625, 110105, 211422, 320923, 140102, 330121,
            430602, 130903, 110228, 220524, 130632, 132201, 110108, 210281, 120224, 110222, 430124, 130681, 110103,
            231005, 230103, 110107, 432828, 612525, 440301, 120107, 440225, 441823, 362102, 130723, 230205, 110106,
            342422, 370205, 152127, 411329, 430181, 430381, 420111
        ]
        def x = [
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9
        ]*.toString()

        def x1 = [
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 'X'
        ]*.toString()

        def xx = x[org.apache.commons.lang3.RandomUtils.nextInt(0, x.size())] + x[org.apache.commons.lang3.RandomUtils.nextInt(0, x.size())] + x[org.apache.commons.lang3.RandomUtils.nextInt(0, x.size())] + x1[org.apache.commons.lang3.RandomUtils.nextInt(0, x1.size())]

        "${s[org.apache.commons.lang3.RandomUtils.nextInt(0, s.size())]}${org.apache.commons.lang3.RandomUtils.nextInt(1975, 1995)}${(org.apache.commons.lang3.RandomUtils.nextInt(1, 13) as String).padLeft(2, '0')}${(org.apache.commons.lang3.RandomUtils.nextInt(1, 29) as String).padLeft(2, '0')}${xx}"
    }
}
