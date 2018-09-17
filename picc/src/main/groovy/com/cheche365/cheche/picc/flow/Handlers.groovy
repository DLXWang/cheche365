package com.cheche365.cheche.picc.flow

import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.flow.Constants._ROUTE_FLAG_DONE
import static com.cheche365.cheche.common.util.ContactUtils.getAgeByIdentity
import static com.cheche365.cheche.common.util.ContactUtils.getGenderByIdentity
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1
import static com.cheche365.cheche.core.model.GlassType.Enum.IMPORT_2
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT1
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.adjustInsureAmount
import static com.cheche365.cheche.parser.util.BusinessUtils.getSelectedCarModelFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV
import static com.cheche365.cheche.picc.flow.Constants._REAL_TIME_CITIES
import static com.cheche365.cheche.picc.flow.Constants._STATUS_CODE_PICC_CAR_MODEL_NOT_FOUND
import static com.cheche365.cheche.picc.util.BusinessUtils.getAssignDriverFlag
import static com.cheche365.cheche.picc.util.BusinessUtils.getAutoEngineNo
import static com.cheche365.cheche.picc.util.BusinessUtils.getAutoVinNo
import static com.cheche365.cheche.picc.util.BusinessUtils.getCarEnrollDate
import static com.cheche365.cheche.picc.util.BusinessUtils.getCarModelCode
import static com.cheche365.cheche.picc.util.BusinessUtils.getCarPurchasePrice
import static com.cheche365.cheche.picc.util.BusinessUtils.getCarSeat
import static com.cheche365.cheche.picc.util.BusinessUtils.getDefaultInsurancePeriodText
import static com.cheche365.cheche.picc.util.BusinessUtils.getNewInsurancePeriodText
import static com.cheche365.cheche.picc.util.BusinessUtils.getPackageName
import static com.cheche365.cheche.picc.util.BusinessUtils.getRangeDate
import static com.cheche365.cheche.picc.util.BusinessUtils.getRunAreaCodeName

/**
 * 请求生成器（RPG）和响应处理器（RH）
 */
@Slf4j
class Handlers {

    //<editor-fold defaultstate="collapsed" desc="RequestParametersGenerators(RPGs)">

    // 计算车辆购置价
    private static final _CALC_PURCHASE_PRICE_DEFAULT = { context ->
        getCarPurchasePrice context
    }

    // 大连（TODO：在人保的JS中，厦门也有独立的处理分支，后续要注意）
    private static final _CALC_PURCHASE_PRICE_210200 = { context ->
        context.carPrice?.purchasePriceDefault
    }

    //获取流程唯一标识
    static final _GET_UNIQUE_ID_RPG_DEFAULT = { context ->
        def areaCode = context.areaCode
        def cityCode = context.cityCode
        def auto = context.auto

        [
            areaCode    : areaCode,
            cityCode    : cityCode,
            licenseno   : auto.licensePlateNo
        ]
    }

    //检查车辆黑名单新接口
    static final _CAR_BLACK_LIST_CAR_RPG_DEFAULT = { context ->
        def areaCode = context.areaCode
        def cityCode = context.cityCode
        def auto = context.auto

        [
            areaCode    : areaCode,
            cityCode    : cityCode,
            frameNo     : getAutoVinNo(context),
            vinCode     : getAutoVinNo(context),
            engineNo    : getAutoEngineNo(context),
            licenseno   : auto.licensePlateNo
        ]
    }

    //检查车辆黑名单旧接口
    static final _CAR_BLACK_LIST_RPG_DEFAULT = { context ->
        def areaCode = context.areaCode
        def cityCode = context.cityCode
        Auto auto = context.auto

        [
            areaCode    : areaCode,
            cityCode    : cityCode,
            frameNo     : getAutoVinNo(context),
            engineNo    : getAutoEngineNo(context),
            licenseno   : auto.licensePlateNo,
            cartype     : 1
        ]
    }

    //获取车辆使用年数新接口
    static final _GET_NEW_USE_YEARS_RPG_DEFAULT = { context ->
        def areaCode = context.areaCode
        def cityCode = context.cityCode
        def (startDateText, _2) = getNewInsurancePeriodText(context)
        def carEnrollDate = getCarEnrollDate context

        [
            areaCode    : areaCode,
            cityCode    : cityCode,
            startDate   : startDateText,
            enrollDate  : carEnrollDate
        ]
    }

    //获取车辆使用年数
    static final _GET_USE_YEARS_RPG_DEFAULT = { context ->
        def areaCode = context.areaCode
        def cityCode = context.cityCode
        def (startDateText, _2) = getNewInsurancePeriodText(context)
        def carEnrollDate = getCarEnrollDate context

        [
            areaCode    : areaCode,
            cityCode    : cityCode,
            startDate   : startDateText,
            enrollDate  : carEnrollDate
        ]
    }

    //获取车型价格
    static final _CHECK_PRICE_FOR_CAR_RPG_DEFAULT = { context ->
        def areaCode = context.areaCode
        def cityCode = context.cityCode
        def comCode = context.comCode
        def uniqueID = context.uniqueID
        def carUseYears = context.autoUseYears
        def handlerCode = context.handlerCode
        Auto auto = context.auto
        def carEnrollDate = getCarEnrollDate context
        def carModelCode = getCarModelCode context
        def (startDateText, endDateText) = getNewInsurancePeriodText(context)
        def carSeat = getCarSeat context
        def transferDate = context.extendedAttributes?.transferDate

        [
            'prpcmain.uniqueID'             : uniqueID,
            'prpcmain.cityCode'             : cityCode,
            'prpcmain.areaCode'             : areaCode,
            'prpcmain.handlercode'          : handlerCode,
            'prpcmain.comcode'              : comCode,
            'prpcmain.makecom'              : comCode,
            'prpcitemCar.licenseno'         : auto.licensePlateNo,
            'prpcitemCar.vinno'             : getAutoVinNo(context),
            'prpcitemCar.frameno'           : getAutoVinNo(context),
            'prpcitemCar.useyears'          : carUseYears,
            'prpcitemCar.engineno'          : getAutoEngineNo(context),
            'prpcitemCar.enrolldate'        : carEnrollDate,
            'prpcitemCar.runmiles'          : '10000',
            'prpcinsureds[0].insuredname'   : context.renewalInfo?.carOwner?.insuredname ?: auto.owner,
            'prpcinsureds[0].insuredflag'   : '0010000',
            'prpcinsureds[1].insurednature' : '3',
            'prpcinsureds[1].insuredflag'   : '1000000',
            'prpcinsureds[2].sex'           : getGenderByIdentity(auto.identity, ['2', '1']),
            'prpcinsureds[2].insuredflag'   : '0100000',
            'prpcinsureds[2].insuredname'   : context.renewalInfo?.insured?.insuredname ?: auto.owner,
            'prpcinsureds[2].identifytype'  : '01',
            'prpcinsureds[2].identifynumber': auto.identity,
            'prpcitemCar.modelcode'         : carModelCode,
            'prpcmain.startdate'            : startDateText,
            'prpcmain.enddate'              : endDateText,
            startDateBI                     : startDateText,
            endDateBI                       : endDateText,
            'prpcitemCar.seatcount'         : carSeat,
            guohuselect                     : transferDate ? 1 : 0,
            'prpcitemCar.transferdate'      : transferDate ? _DATE_FORMAT1.format(transferDate) : ''
        ]
    }

    //注册UniqueID到流程
    static final _CHECK_PROFIT_RPG_DEFAULT = { context ->
        def areaCode = context.areaCode
        def cityCode = context.cityCode
        def comCode = context.comCode
        def uniqueID = context.uniqueID
        def carSeat = getCarSeat context
        Auto auto = context.auto
        def carEnrollDate = getCarEnrollDate context
        def (startDateText, endDateText) = getNewInsurancePeriodText(context)
        def renewal = context.renewable ? 1 : 0
        def oldPolicyNo = context.renewalInfo?.renewalPolicyNo
        def autoUseYears = context.autoUseYears
        def transferDate = context.extendedAttributes?.transferDate

        [
            'prpcmain.areaCode'             : areaCode,
            'prpcmain.cityCode'             : cityCode,
            'prpcmain.comcode'              : comCode,
            'prpcmain.makecom'              : comCode,
            'prpcmain.uniqueID'             : uniqueID,
            'prpcitemCar.licenseno'         : auto.licensePlateNo,
            'prpcitemCar.frameno'           : getAutoVinNo(context),
            'prpcitemCar.vinno'             : getAutoVinNo(context),
            'prpcitemCar.engineno'          : getAutoEngineNo(context),
            'prpcitemCar.enrolldate'        : carEnrollDate,
            'prpcitemCar.seatcount'         : carSeat,
            'prpcitemCar.useyears'          : autoUseYears,
            'prpcinsureds[0].insuredname'   : auto.owner,
            'prpcinsureds[0].insuredflag'   : '0010000',
            'prpcinsureds[1].insuredflag'   : '1000000',
            'prpcinsureds[2].insuredflag'   : '0100000',
            'prpcinsureds[1].insurednature' : 3,
            'prpcmain.startdate'            : startDateText,
            'prpcmain.enddate'              : endDateText,
            'startDateBI'                   : startDateText,
            'endDateBI'                     : endDateText,
            'prpcmain.renewal'              : renewal,
            'oldPolicyNo'                   : oldPolicyNo,
            guohuselect                     : transferDate ? 1 : 0,
            'prpcitemCar.transferdate'      : transferDate ? _DATE_FORMAT1.format(transferDate) : ''
        ]
    }

    //注册UniqueID到流程
    static final _CHECK_PROFIT_RPG_TYPE2_AND_TYPE3 = { context ->
        def areaCode = context.areaCode
        def cityCode = context.cityCode
        def comCode = context.comCode
        def uniqueID = context.uniqueID
        def carSeat = getCarSeat context
        Auto auto = context.auto
        def carEnrollDate = getCarEnrollDate context
        def (startDateText, endDateText) = getNewInsurancePeriodText(context)
        def renewal = context.renewable ? 1 : 0
        def oldPolicyNo = context.renewalInfo?.renewalPolicyNo
        def autoUseYears = context.autoUseYears
        def carPurchasePrice = getCarPurchasePrice context
        def transferDate = context.extendedAttributes?.transferDate

        [
            'prpcmain.areaCode'             : areaCode,
            'prpcmain.cityCode'             : cityCode,
            'prpcmain.comcode'              : comCode,
            'prpcmain.makecom'              : comCode,
            'prpcmain.uniqueID'             : uniqueID,
            'prpcitemCar.licenseno'         : auto.licensePlateNo,
            'prpcitemCar.frameno'           : getAutoVinNo(context),
            'prpcitemCar.vinno'             : getAutoVinNo(context),
            'prpcitemCar.engineno'          : getAutoEngineNo(context),
            'prpcitemCar.enrolldate'        : carEnrollDate,
            'prpcitemCar.seatcount'         : carSeat,
            'prpcitemCar.purchaseprice'     : carPurchasePrice,
            'prpcitemCar.useyears'          : autoUseYears,
            'prpcinsureds[0].insuredname'   : auto.owner,
            'prpcinsureds[0].insuredflag'   : '0010000',
            'prpcinsureds[1].insuredflag'   : '1000000',
            'prpcinsureds[2].insuredflag'   : '0100000',
            'prpcinsureds[1].insurednature' : 3,
            'prpcmain.startdate'            : startDateText,
            'prpcmain.enddate'              : endDateText,
            'startDateBI'                   : startDateText,
            'endDateBI'                     : endDateText,
            'prpcmain.renewal'              : renewal,
            'oldPolicyNo'                   : oldPolicyNo,
            'lastcarownername'              : renewal ? auto.owner : '',
            guohuselect                     : transferDate ? 1 : 0,
            'prpcitemCar.transferdate'      : transferDate ? _DATE_FORMAT1.format(transferDate) : ''
        ]
    }

    //计算非续保全险报价
    static final _CALCULATE_FOR_BATCH_RPG_BASE = { calculatePurchasePrice, context ->
        def areaCode = context.areaCode
        def cityCode = context.cityCode
        def uniqueID = context.uniqueID
        def comCode = context.comCode
        def autoUseYears = context.autoUseYears
        Auto auto = context.auto
        def packageName = 'ComprehensivePackage'
        def (startDateText, endDateText) = getNewInsurancePeriodText(context)
        def purchasePrice = calculatePurchasePrice context
        def carEnrollDate = getCarEnrollDate context
        def carModelCode = getCarModelCode context
        def carSeat = getCarSeat context
        def transferDate = context.extendedAttributes?.transferDate

        [
            'prpcmain.areaCode'             : areaCode,
            'prpcmain.cityCode'             : cityCode,
            'prpcmain.comcode'              : comCode,
            'prpcmain.makecom'              : comCode,
            'prpcmain.uniqueID'             : uniqueID,
            'prpcmain.packageName'          : packageName,
            'prpcitemCar.licenseno'         : auto.licensePlateNo,
            'prpcitemCar.frameno'           : getAutoVinNo(context),
            'prpcitemCar.engineno'          : getAutoEngineNo(context),
            'prpcitemCar.modelcode'         : carModelCode,
            'prpcitemCar.purchaseprice'     : purchasePrice,
            'prpcitemCar.useyears'          : autoUseYears,
            'prpcitemCar.enrolldate'        : carEnrollDate,
            'prpcitemCar.seatcount'         : carSeat,
            'prpcmain.startdate'            : startDateText,
            'prpcmain.enddate'              : endDateText,
            'prpcinsureds[0].insuredflag'   : '0010000',
            'prpcinsureds[0].insuredname'   : auto.owner,
            'prpcinsureds[1].insuredflag'   : '1000000',
            'prpcinsureds[1].insurednature' : 3,
            'prpcinsureds[2].insuredflag'   : '0100000',
            'prpcinsureds[2].sex'           : '1',
            'FullAmountName'                : '8',
            guohuselect                     : transferDate ? 1 : 0,
            'prpcitemCar.transferdate'      : transferDate ? _DATE_FORMAT1.format(transferDate) : ''
        ]
    }

    static final _CALCULATE_FOR_BATCH_RPG_DEFAULT = _CALCULATE_FOR_BATCH_RPG_BASE.curry _CALC_PURCHASE_PRICE_DEFAULT

    //若carReduceFlag==1，carPurchasePrice=purchasePriceDefault；carReduceFlag==0，大连carPurchasePrice=purchasePriceDefault，即大连总是carPurchasePrice=purchasePriceDefault
    static final _CALCULATE_FOR_BATCH_RPG_210200 = _CALCULATE_FOR_BATCH_RPG_BASE.curry _CALC_PURCHASE_PRICE_210200

    //校验验证码
    static final _VERIFY_CAPTCHA_RPG_DEFAULT = { context ->
        def areaCode = context.areaCode
        def cityCode = context.cityCode
        def uniqueID = context.uniqueID
        def comCode = context.comCode
        Auto auto = context.auto
        def captchaText = context.captchaText

        [
            renewalRandom  : captchaText,
            tokenNo        : auto.insuredIdNo ?: auto.identity,
            carowner       : auto.owner,
            uniqueID       : uniqueID,
            renewalPackage : 'renewalPackage',
            areaCode       : areaCode,
            cityCode       : cityCode,
            comCode        : comCode,
            priceConfigKind: 2
        ]
    }

    /***************************************************************************************************/

    private static final _AMOUNT_CONVERTER_FROM_INSURANCE_PACKAGE = { propName, kindCode, context,
                                                                      insurancePackage, allKindItems ->
        def expectedAmount = insurancePackage[propName]
        def amountList = allKindItems.find { item ->
            kindCode == item.kindCode
        }?.amountList?.tokenize('|')?.minus('0')?.reverse()

        def actualAmount = expectedAmount ?
            (context.quoting ?
                (adjustInsureAmount(expectedAmount, amountList, { it as double }, { it as double }) ?: -1)
                : expectedAmount)
            : -1

        insurancePackage[propName] = -1 == actualAmount ? 0 : actualAmount
        actualAmount as int
    }

    private static final _AMOUNT_CONVERTER_FROM_JSON = { propName, kindCode, context, insurancePackage, allKindItems ->
        insurancePackage[propName] ?
            allKindItems.find { item ->
                kindCode == item.kindCode
            }?.amountList?.tokenize('|')?.get(1) ?: -1 :
            -1
    }

    private static final _AMOUNT_CONVERTER_BOOLEAN = { propName, context, insurancePackage, allKindItems ->
        insurancePackage[propName] ? 1 : -1
    }

    private static final _AMOUNT_CONVERTER_UNSUPPORTED = { context, insurancePackage, allKindItems ->
        -1
    }

    private static final _KIND_CODE_CONVERTERS = [
        // 下面是直接从套餐中获取保额的
        ['050600', _AMOUNT_CONVERTER_FROM_INSURANCE_PACKAGE.curry('thirdPartyAmount', '050600')], // 第三者责任险
        ['050701', _AMOUNT_CONVERTER_FROM_INSURANCE_PACKAGE.curry('driverAmount', '050701')],     // 车上人员责任险-司机
        ['050702', _AMOUNT_CONVERTER_FROM_INSURANCE_PACKAGE.curry('passengerAmount', '050702')],  // 车上人员责任险-乘客
        // 下面三个的保额需要从前面步骤中取得的全险JSON中获取
        ['050200', _AMOUNT_CONVERTER_FROM_JSON.curry('damage', '050200')],              // 机动车辆损失险
        ['050500', _AMOUNT_CONVERTER_FROM_JSON.curry('theft', '050500')],               // 盗抢险
        ['050310', _AMOUNT_CONVERTER_FROM_JSON.curry('spontaneousLoss', '050310')],     // 自燃损失险
        // 下面的都是boolean型的（1或者－1）
        ['050912', _AMOUNT_CONVERTER_BOOLEAN.curry('thirdPartyIop')],                   // 第三者责任保险IOP
        ['050928', _AMOUNT_CONVERTER_BOOLEAN.curry('driverIop')],                       // 车上人员责任险-司机IOP
        ['050929', _AMOUNT_CONVERTER_BOOLEAN.curry('passengerIop')],                    // 车上人员责任险-乘客IOP
        ['050911', _AMOUNT_CONVERTER_BOOLEAN.curry('damageIop')],                       // 机动车损失保险IOP
        ['050921', _AMOUNT_CONVERTER_BOOLEAN.curry('theftIop')],                        // 盗抢险IOP
        ['050451', _AMOUNT_CONVERTER_BOOLEAN.curry('unableFindThirdParty')],            // 无法找到第三方特约险
        ['050291', _AMOUNT_CONVERTER_BOOLEAN.curry('engine')],                          // 发动机特别损失险
        ['050924', _AMOUNT_CONVERTER_BOOLEAN.curry('engineIop')],                       // 发动机特别损失险IOP
        ['050922', _AMOUNT_CONVERTER_BOOLEAN.curry('scratchIop')],                      // 车身划痕损失险IOP
        ['050935', _AMOUNT_CONVERTER_BOOLEAN.curry('spontaneousLossIop')],              // 自燃损失险IOP

        // 套餐的顺序会影响险种的报价，如仅投车损，因为划痕在车损后面可能划痕就被投了
        ['050210', _AMOUNT_CONVERTER_FROM_INSURANCE_PACKAGE.curry('scratchAmount', '050210')],    // 车身划痕损失险

        ['050252', _AMOUNT_CONVERTER_UNSUPPORTED],                                       // 指定专修厂特约条款
        ['050919', _AMOUNT_CONVERTER_UNSUPPORTED],                                       // 精神损害抚慰金责任险（三者险）IOP
        ['050641', _AMOUNT_CONVERTER_UNSUPPORTED],                                       // 精神损害抚慰金责任险（三者险）
        ['050918', _AMOUNT_CONVERTER_UNSUPPORTED],                                       // 精神损害抚慰金责任险（车上人员）IOP
        ['050642', _AMOUNT_CONVERTER_UNSUPPORTED],                                       // 精神损害抚慰金责任险（车上人员）
        ['050643', _AMOUNT_CONVERTER_UNSUPPORTED],                                       // 精神损害抚慰金责任险（武汉，没有具体主险）
        ['050917', _AMOUNT_CONVERTER_UNSUPPORTED]                                        // 精神损害抚慰金责任险IOP
    ]

    private static getChangedKindItems(context, packageName) {
        def insurancePackage = context.accurateInsurancePackage
        def defaultPackageJson = context.defaultPackageJson
        def allKindItems = defaultPackageJson[packageName]

        // 除了玻璃之外的所有险种
        def nonGlazzKinds = _KIND_CODE_CONVERTERS.collectEntries { kindCodeConverter ->
            def (kindCode, converter) = kindCodeConverter
            [ (kindCode) : converter(context, insurancePackage, allKindItems) ]
        }
        // 玻璃险
        if (insurancePackage.glass) {
            def amountList = allKindItems.find { item ->
                '050231' == item.kindCode
            }?.amountList?.tokenize('|')?.minus('0')?.collect { amount ->
                if ('10' == amount) {
                    1
                } else if ('20' == amount) {
                    2
                }
            }
            if (!((insurancePackage.glassType.id as int) in amountList)) {
                if (!amountList) { // 无法投保玻璃险
                    insurancePackage.glass = false
                    insurancePackage.glassType = null
                } else {
                    def actualGlassType = amountList[0]
                    insurancePackage.glassType = actualGlassType == 1 ? DOMESTIC_1 : IMPORT_2
                }
            }
        }
        def glazzKind = ['050231' : insurancePackage.glass ? (DOMESTIC_1 == insurancePackage.glassType ? 10 : 20) : -1]

        (nonGlazzKinds + glazzKind).collect { kindCode, amount ->
            "$kindCode:$amount"
        }.join ','
    }
    //计算套餐
    static final _CALCULATE_FOR_CHANGE_KIND_RPG_BASE = { calcPurchasePrice, context ->
        def areaCode = context.areaCode
        def cityCode = context.cityCode
        def uniqueID = context.uniqueID
        def comCode = context.comCode
        def handlerCode = context.handlerCode
        def carEnrollDate = getCarEnrollDate context
        def carModelCode = getCarModelCode context
        def carSeat = getCarSeat context
        def purchasePrice = calcPurchasePrice context
        def autoUseYears = context.autoUseYears
        def (startDateText, endDateText) = getNewInsurancePeriodText(context)
        def beforeProposalNo = context.renewalInfo?.renewalPolicyNo
        Auto auto = context.auto
        def renewal = context.renewable ? 1 : 0
        def packageName = getPackageName context
        def changedKindItems = getChangedKindItems context, packageName
        def assignDriverFlag = getAssignDriverFlag context
        def runAreaCodeName = getRunAreaCodeName context
        def adjustedPrice = context.adjustedPrice
        def transferDate = context.extendedAttributes?.transferDate

        def params = [
            'prpcmain.uniqueID'             : uniqueID,
            'prpcmain.areaCode'             : areaCode,
            'prpcmain.cityCode'             : cityCode,
            'prpcmain.comcode'              : comCode,
            'prpcmain.makecom'              : comCode,
            'prpcmain.handlercode'          : handlerCode,
            'prpcmain.renewal'              : renewal,
            'prpcmain.packageName'          : packageName,
            'prpcmain.startdate'            : startDateText,
            'prpcmain.enddate'              : endDateText,
            'prpcitemCar.licenseno'         : auto.licensePlateNo,
            'prpcitemCar.frameno'           : getAutoVinNo(context),
            'prpcitemCar.engineno'          : getAutoEngineNo(context),
            'prpcitemCar.modelcode'         : carModelCode,
            'prpcitemCar.purchaseprice'     : adjustedPrice?.purchasePrice ?: purchasePrice,
            'prpcitemCar.useyears'          : autoUseYears,
            'prpcitemCar.enrolldate'        : carEnrollDate,
            'prpcitemCar.seatcount'         : carSeat,
            'prpcitemCar.runmiles'          : '10000',
            'prpcinsureds[0].insuredflag'   : '0010000',
            'prpcinsureds[0].insuredname'   : auto.owner,
            'prpcinsureds[1].insuredflag'   : '1000000',
            'prpcinsureds[1].insurednature' : 3,
            'prpcinsureds[2].insuredname'   : auto.owner,
            'prpcinsureds[2].identifytype'  : '01',
            'prpcinsureds[2].identifynumber': auto.identity,
            'prpcinsureds[2].insuredflag'   : '0100000',
            'prpcinsureds[2].sex'           : getGenderByIdentity(auto.identity, ['2', '1']),
            'prpcinsureds[2].age'           : getAgeByIdentity(auto.identity),
            beforeProposalNo                : beforeProposalNo,
            mrPurchasePrice                 : purchasePrice,
            changeItemKind                  : changedKindItems, // sample: '050500:149096.80,050701:-1'
            startDateBI                     : startDateText,
            endDateBI                       : endDateText,
            starthourBI                     : '0',
            endhourBI                       : '24',
            assignDriver                    : assignDriverFlag,
            RunAreaCodeName                 : runAreaCodeName,
            FullAmountName                  : '8',
            lastHas050500                   : context.renewalInfo?.lastHas050500 ?: '0',
            purchasePriceMax                : adjustedPrice?.purchasePriceMax ?: purchasePrice,
            guohuselect                     : transferDate ? 1 : 0,
            'prpcitemCar.transferdate'      : transferDate ? _DATE_FORMAT1.format(transferDate) : ''

        ]

        if (assignDriverFlag) {
            def renewalInfo = context.renewalInfo
            if (renewalInfo?.carDrivers && renewalInfo.carDrivers.size() > 0) {
                renewalInfo.carDrivers.eachWithIndex { carDriver, idx ->
                    def firstCarDriver = renewalInfo.carDrivers[idx]
                    def assignDriverName = firstCarDriver.insuredname
                    def assignDriverAge = firstCarDriver.age
                    def assignDriverYears = firstCarDriver.drivingyears
                    def driverIdx = idx + 3

                    params += [
                        "prpcinsureds[$driverIdx].insuredname"      : assignDriverName,
                        "prpcinsureds[$driverIdx].age"              : assignDriverAge,
                        "prpcinsureds[$driverIdx].drivingyears"     : assignDriverYears,
                        "prpcinsureds[$driverIdx].sex"              : firstCarDriver.sex,
                        "prpcinsureds[$driverIdx].drivinglicenseno" : firstCarDriver.drivinglicenseno,
                    ]
                }
            }
        }
        params
    }

    static final _CALCULATE_FOR_CHANGE_KIND_RPG_210200 = _CALCULATE_FOR_CHANGE_KIND_RPG_BASE.curry _CALC_PURCHASE_PRICE_210200

    static final _CALCULATE_FOR_CHANGE_KIND_RPG_DEFAULT = _CALCULATE_FOR_CHANGE_KIND_RPG_BASE.curry _CALC_PURCHASE_PRICE_DEFAULT

    /***************************************************************************************************/

    //计算交强险
    static final _BZ_RPG_BASE = { taxType, calcPurchasePrice, context ->
        def areaCode = context.areaCode
        def cityCode = context.cityCode
        def uniqueID = context.uniqueID
        def packageName = getPackageName context
        def comCode = context.comCode
        def handlerCode = context.handlerCode
        def handlerCodeUni = context.handlercode_uni
        def carModelCode = getCarModelCode context
        def purchasePrice = calcPurchasePrice context
        def carSeat = getCarSeat context
        String carEnrollDate = getCarEnrollDate context
        def autoUseYears = context.autoUseYears
        Auto auto = context.auto
        def identityType = (auto.identityType.id as String).padLeft(2, '0')
        def ciCarInfo = context?.trafficInfo?.cicarinfowxlist?.first() // 江苏地区交强险车辆相关信息

        /**
         * TODO
         * 张华彬，2016-08-02：
         * 在修复#5834的过程中，发现保险公司新的交强险起止日期和商业险是同样的，
         * 即不是getDefaultInsurancePeriodText()返回值，而是getNewInsurancePeriodText()返回值。
         * 所以保险公司官网仅仅请求了一次就报出了交强险，而我们的交强险步骤有“根据errorMsg更正起止日期”的逻辑，
         * 所以我们要报两次才能报出交强险。
         * 出于安全考虑，在修复该bug时我没有改动这里，但是后续需要观察是否可能此处得到最准确的起止日期来防止多余的调用
         */
        def (startDateCIText, endDateCIText) = getDefaultInsurancePeriodText(context)
        def transferDate = context.extendedAttributes?.transferDate
        def (startDateText, endDateText) = getNewInsurancePeriodText(context)

        [
            'prpcmain.areaCode'             : areaCode,
            'prpcmain.cityCode'             : cityCode,
            'prpcmain.comcode'              : comCode,
            'prpcmain.makecom'              : comCode,
            'prpcmain.handlercode'          : handlerCode,
            'prpcmain.handlercode_uni'      : handlerCodeUni,
            'prpcmain.operatorcode'         : handlerCode,
            'prpcmain.uniqueID'             : uniqueID,
            'prpcmain.packageName'          : packageName,
            'prpcitemCar.licenseno'         : auto.licensePlateNo,
            'prpcitemCar.frameno'           : getAutoVinNo(context),
            'prpcitemCar.vinno'             : getAutoVinNo(context),
            'prpcitemCar.engineno'          : getAutoEngineNo(context),
            'prpcitemCar.modelcode'         : carModelCode,
            'prpcitemCar.purchaseprice'     : purchasePrice,
            'prpcitemCar.useyears'          : autoUseYears,
            'prpcitemCar.enrolldate'        : carEnrollDate,
            'prpcitemCar.seatcount'         : carSeat,
            'prpcinsureds[0].insuredflag'   : '0010000',
            'prpcinsureds[0].insuredname'   : auto.owner,
            'prpcinsureds[1].insuredflag'   : '1000000',
            'prpcinsureds[1].insurednature' : 3,
            'prpcinsureds[2].insuredflag'   : '0100000',
            'carShipTax.taxpayername'       : ciCarInfo?.carowner ?: auto.owner, // 车主和投保人可能不同
            'carShipTax.taxpayertype'       : identityType,
            'carShipTax.taxpayeridentno'    : auto.identity,
            'paytax'                        : 'on',
            'CarOwerIdentifyType'           : identityType,
            'CarOwnerIdentifyNumber'        : auto.identity,
            'prpcmain.startdate'            : startDateCIText,
            'prpcmain.enddate'              : endDateCIText,
            startDateBI                     : startDateText,
            endDateBI                       : endDateText,
            'carShipTax.taxtype'            : taxType,
            'TAX_FLAG'                      : taxType,
            'taxEnable'                     : '1',
            'prpcinsureds[2].sex'           : '1',
            guohuselect                     : transferDate ? 1 : 0,
            'prpcitemCar.transferdate'      : transferDate ? _DATE_FORMAT1.format(transferDate) : ''
        ]
    }

    //计算交强险 大连
    static final _CALCULATE_FOR_BZ_RPG_210200 = _BZ_RPG_BASE.curry '', _CALC_PURCHASE_PRICE_210200

    //计算交强险 北京 北京不上送交强险类型
    static final _CALCULATE_FOR_BZ_RPG_DEFAULT = _BZ_RPG_BASE.curry '', _CALC_PURCHASE_PRICE_DEFAULT

    //计算交强险 上海 上海先上送补充并纳税（B）
    static final _CALCULATE_FOR_BZ_RPG_TAX_TYPE_B_DEFAULT = _BZ_RPG_BASE.curry 'B', _CALC_PURCHASE_PRICE_DEFAULT
    //上海再上送仅纳税（N）
    static final _CALCULATE_FOR_BZ_RPG_TAX_TYPE_N_DEFAULT = _BZ_RPG_BASE.curry 'N', _CALC_PURCHASE_PRICE_DEFAULT

    //检查交强险 北京 北京不上送交强险类型
    static final _CHECK_BZ_RPG_DEFAULT = _BZ_RPG_BASE.curry '', _CALC_PURCHASE_PRICE_DEFAULT

    //检查交强险 上海
    static final _CHECK_BZ_RPG_310000 = { context ->   //taxType 已在计算交强险时放入上下文
        _BZ_RPG_BASE.call(context.taxType ?: 'B', _CALC_PURCHASE_PRICE_DEFAULT, context)
    }

    //查找车型
    private static final _FIND_CAR_MODEL_RPG_BASE = { requestType, withQueryCode, context ->
        def areaCode = context.areaCode
        def cityCode = context.cityCode
        def uniqueID = context.uniqueID
        def comCode  = context.comCode
        Auto auto = context.auto
        def carEnrollDate = getCarEnrollDate context
        def carModel = context.renewalInfo?.prpcitemCar?.brandname ?: context.historicalVehicleInfo?.brandname ?: '1'   //为1是因为上海非空即可
        def queryCarModelCode = context.queryCarModelCode ?: context.renewalInfo?.prpcitemCar?.modelcode //某些地区续保要求上送， 20160704上海一定不上送

        def params = [
            'carModelQuery.requestType' : requestType,
            'carModelQuery.areaCode'    : areaCode,
            'carModelQuery.cityCode'    : cityCode,
            'carModelQuery.uniqueId'    : uniqueID,
            'carModelQuery.licenseNo'   : auto.licensePlateNo,
            'carModelQuery.carOwner'    : auto.owner,
            'carModelQuery.frameNo'     : getAutoVinNo(context),
            'carModelQuery.engineNo'    : getAutoEngineNo(context),
            'carModelQuery.comCode'     : comCode,
            'carModelQuery.carModel'    : carModel,     //上海需要品牌型号非空即可
            'carModelQuery.licenseType' : '02',         //上海必须
            'carModelQuery.enrollDate'  : carEnrollDate
        ]

        withQueryCode ? params + ['carModelQuery.queryCode': queryCarModelCode] : params
    }

    // 目前逻辑北京无论转保续保历史车辆都是用01
    // 上海都是用02，虽然上海续保官网是用04，但响应是500.点击查询是用的是02
    // 重庆等其他地区 续保使用04，转保等已不再是用这个接口
    static final _FIND_CAR_MODEL_RPG_01 = _FIND_CAR_MODEL_RPG_BASE.curry '01', true
    static final _FIND_CAR_MODEL_RPG_02 = _FIND_CAR_MODEL_RPG_BASE.curry '02', true
    static final _FIND_CAR_MODEL_RPG_04 = _FIND_CAR_MODEL_RPG_BASE.curry '04', true
    static final _FIND_CAR_MODEL_RPG_02_310000 = _FIND_CAR_MODEL_RPG_BASE.curry '02', false
    static final _FIND_CAR_MODEL_RPG_04_310000 = _FIND_CAR_MODEL_RPG_BASE.curry '04', false


    //注册UniqueId到车型
    static final _REGISTER_UNIQUE_ID_RPG_DEFAULT = { context ->
        def areaCode = context.areaCode
        def cityCode = context.cityCode
        def comCode = context.comCode
        def uniqueID = context.uniqueID
        def selectedCarModel = context.selectedCarModel

        [
            'carModelQuery.requestType' : '03',
            'carModelQuery.areaCode'    : areaCode,
            'carModelQuery.cityCode'    : cityCode,
            'carModelQuery.comCode'     : comCode,
            'carModelQuery.uniqueId'    : uniqueID,
            'carModelQuery.serialno'    : selectedCarModel.serialno
        ]
    }
    //注册UniqueId到车型 精友
    static final _REGISTER_UNIQUE_ID_RPG_JY = { context, otherArgs = null ->
        def areaCode = context.areaCode
        def cityCode = context.cityCode
        def comCode = context.comCode
        def uniqueID = context.uniqueID
        Auto auto = context.auto
        def queryCarModelCode = auto?.autoType?.code
        def jYCarModelParentId = context.vehicleInfo?.parentId
        def jYCarModelQueryCode = context.vehicleInfo?.vehicleFgwCode

        [
            'carModelQuery.requestType' : '03',
            'carModelQuery.areaCode'    : areaCode,
            'carModelQuery.cityCode'    : cityCode,
            'carModelQuery.comCode'     : comCode,
            'carModelQuery.uniqueId'    : uniqueID,
            'carModelQuery.carModel'    : queryCarModelCode,
            'carModelQuery.parentId'    : otherArgs?.jYCarModelParentId ?: jYCarModelParentId,
            'carModelQuery.queryCode'   : otherArgs?.jYCarModelQueryCode ?: jYCarModelQueryCode
        ]
    }

    //注册UniqueID到车型 杭州、南京等
    static final _REGISTER_UNIQUE_ID_RPG_NON_JY = { context, otherArgs = null ->
        def areaCode = context.areaCode
        def cityCode = context.cityCode
        def comCode = context.comCode
        def uniqueID = context.uniqueID
        def queryCarModelCode = context.auto?.autoType?.code
        def selectedCarModel = context.selectedCarModel
        def carModelParentId = selectedCarModel.parentId
        def carModelQueryCode = selectedCarModel?.vehicleFgwCode

        [
            'carModelQuery.requestType' : '03',
            'carModelQuery.areaCode'    : areaCode,
            'carModelQuery.cityCode'    : cityCode,
            'carModelQuery.comCode'     : comCode,
            'carModelQuery.uniqueId'    : uniqueID,
            'carModelQuery.carModel'    : queryCarModelCode,
            'carModelQuery.parentId'    : carModelParentId,
            'carModelQuery.queryCode'   : otherArgs?.carModelQueryCode ?: carModelQueryCode
        ]
    }

    //检查再保信息（基础闭包）
    static final _CHECK_REINSURANCE_RPG_BASE = { startHourSy, context ->
        def areaCode = context.areaCode
        def cityCode = context.cityCode
        def uniqueID = context.uniqueID
        def renewal = context.renewable ? 1 : 0
        def renewalInfo = context.renewalInfo
        // 非续保客户使用明天至第二年的今天作为起止时间，否则就从renewalInfo中直接取出起止时间
        def candidateStartDateText = renewalInfo?.prpcmain?.startdateStr ?: getDefaultInsurancePeriodText(context).first
        def oldPolicyNo = renewalInfo?.renewalPolicyNo

        [
            areacode    : areaCode,
            citycode    : cityCode,
            uniqueID    : uniqueID,
            startDate   : candidateStartDateText,
            StartHourSY : renewalInfo?.lastStartHourBI ?: startHourSy, // 此参数不可省略
            isRenewal   : renewal,
            oldPolicyNo : oldPolicyNo
        ]
    }

    //检查再保信息（默认）
    static final _CHECK_REINSURANCE_RPG_DEFAULT = _CHECK_REINSURANCE_RPG_BASE.curry 0


    // 检查人员黑名单RPG
    static final _INSURED_BLACK_LIST_RPG_DEFAULT = { context ->
        def areaCode = context.areaCode
        def cityCode = context.cityCode
        Auto auto = context.auto

        [
            areaCode        : areaCode,
            cityCode        : cityCode,
            insurename      : auto.owner,
            identifyType    : auto.identityType.id,
            identifyNumber  : auto.identity,
            isBusiness      : 1
        ]
    }

    // 计算续保全险报价
    static final _CALCULATE_FOR_XUBAO_RPG_DEFAULT = { context ->
        def areaCode = context.areaCode
        def cityCode = context.cityCode
        def uniqueID = context.uniqueID
        def comCode = context.comCode
        def handlerCode = context.handlerCode
        def runMiles = context.RunMiles
        def carModelCode = getCarModelCode context
        def carPrice = getCarPurchasePrice context
        def carSeat = getCarSeat context
        def carEnrollDate = getCarEnrollDate context
        def autoUseYears = context.autoUseYears
        def (startDateText, endDateText) = getNewInsurancePeriodText (context)
        Auto auto = context.auto
        def packageName = 'ComprehensivePackage'

        [
            'prpcmain.areaCode'             : areaCode,
            'prpcmain.cityCode'             : cityCode,
            'prpcmain.comcode'              : comCode,
            'prpcmain.makecom'              : comCode,
            'prpcmain.handlercode'          : handlerCode,
            'prpcmain.uniqueID'             : uniqueID,
            'prpcmain.packageName'          : packageName,
            'prpcmain.startdate'            : startDateText,
            'prpcmain.enddate'              : endDateText,
            'prpcitemCar.licenseno'         : auto.licensePlateNo,
            'prpcitemCar.frameno'           : getAutoVinNo(context),
            'prpcitemCar.engineno'          : getAutoEngineNo(context),
            'prpcitemCar.modelcode'         : carModelCode,
            'prpcitemCar.purchaseprice'     : carPrice,
            'prpcitemCar.useyears'          : autoUseYears,
            'prpcitemCar.enrolldate'        : carEnrollDate,
            'prpcitemCar.seatcount'         : carSeat,
            'prpcitemCar.runmiles'          : context.renewalInfo?.prpcitemCar?.runmiles ?: runMiles,
            'prpcinsureds[0].insuredflag'   : '0010000',
            'prpcinsureds[0].insuredname'   : context.renewalInfo?.carOwner?.insuredname ?: auto.owner,
            'prpcinsureds[1].insuredflag'   : '1000000',
            'prpcinsureds[1].insurednature' : 3,
            'prpcinsureds[2].insuredflag'   : '0100000',
            'prpcinsureds[2].identifynumber': context.renewalInfo?.insured?.identifynumber ?: auto.identity,
            renewalPackage                  : 'renewalPackage',
            'prpcinsureds[2].sex'           : '1',
            'FullAmountName'                : '8'
        ]
    }

    // 宁波计算续保全险报价
    static final _CALCULATE_FOR_XUBAO_RPG_330200 = { context ->
        def areaCode = context.areaCode
        def cityCode = context.cityCode
        def uniqueID = context.uniqueID
        def comCode = context.comCode
        def carModelCode = getCarModelCode context
        def carPrice = getCarPurchasePrice context
        def carSeat = getCarSeat context
        def carEnrollDate = getCarEnrollDate context
        def autoUseYears = context.autoUseYears
        def (startDateText, endDateText) = getRangeDate(context)
        Auto auto = context.auto
        def packageName = 'ComprehensivePackage'

        [
            'prpcmain.areaCode'             : areaCode,
            'prpcmain.cityCode'             : cityCode,
            'prpcmain.comcode'              : comCode,
            'prpcmain.makecom'              : comCode,
            'prpcmain.uniqueID'             : uniqueID,
            'prpcmain.packageName'          : packageName,
            'prpcmain.startdate'            : startDateText,
            'prpcmain.enddate'              : endDateText,
            'prpcitemCar.licenseno'         : auto.licensePlateNo,
            'prpcitemCar.frameno'           : getAutoVinNo(context),
            'prpcitemCar.engineno'          : getAutoEngineNo(context),
            'prpcitemCar.modelcode'         : carModelCode,
            'prpcitemCar.purchaseprice'     : carPrice,
            'prpcitemCar.useyears'          : autoUseYears,
            'prpcitemCar.enrolldate'        : carEnrollDate,
            'prpcitemCar.seatcount'         : carSeat,
            'prpcinsureds[0].insuredflag'   : '0010000',
            'prpcinsureds[0].insuredname'   : auto.owner,
            'prpcinsureds[1].insuredflag'   : '1000000',
            'prpcinsureds[1].insurednature' : 3,
            'prpcinsureds[2].insuredflag'   : '0100000',
            renewalPackage                  : 'renewalPackage',
            'prpcinsureds[2].sex'           : '1',
            'FullAmountName'                : '8'
        ]
    }

    // 检查保险周期
    static final _CHECK_PERIOD_RPG_DEFAULT = { context ->
        def areaCode = context.areaCode
        def cityCode = context.cityCode
        def uniqueID = context.uniqueID
        def comCode = context.comCode
        def carSeat = getCarSeat context
        String carEnrollDate = getCarEnrollDate context
        Auto auto = context.auto

        def (startDateText, endDateText) = getNewInsurancePeriodText(context)
        def hasRealTimeFlag = context.area.id in _REAL_TIME_CITIES ? '1' : '0'

        [
            'prpcmain.areaCode'      : areaCode,
            'prpcmain.cityCode'      : cityCode,
            'prpcmain.comcode'       : comCode,
            'prpcmain.makecom'       : comCode,
            'prpcmain.uniqueID'      : uniqueID,
            'prpcitemCar.licenseno'  : auto.licensePlateNo,
            'prpcitemCar.frameno'    : getAutoVinNo(context),
            'prpcitemCar.engineno'   : getAutoEngineNo(context),
            'prpcitemCar.enrolldate' : carEnrollDate,
            'prpcitemCar.seatcount'  : carSeat,
            isBusiness               : 1,
            'startDateBI'            : startDateText,
            'endDateBI'              : endDateText,
            'hasRealTime'            : hasRealTimeFlag
        ]
    }

    // 检查承保政策
    static final _CHECK_POLICY_RPG_DEFAULT = { context ->
        def areaCode = context.areaCode
        def cityCode = context.cityCode
        def uniqueID = context.uniqueID
        def comCode = context.comCode
        def handlerCode = context.handlerCode
        Auto auto = context.auto
        String carEnrollDate = getCarEnrollDate context
        def carPurchasePrice = getCarPurchasePrice context
        def bzSelected = context.accurateInsurancePackage?.compulsory ? 1 : 0
        def (startDateText, endDateText) = getNewInsurancePeriodText(context)
        def (startDateTextDefault, _2) = getDefaultInsurancePeriodText(context)
        def packageName = getPackageName context
        def carSeat = getCarSeat context
        def autoUseYears = context.autoUseYears
        def transferDate = context.extendedAttributes?.transferDate

        [
            'prpcmain.areaCode'             : areaCode,
            'prpcmain.cityCode'             : cityCode,
            'prpcmain.comcode'              : comCode,
            'prpcmain.makecom'              : comCode,
            'prpcmain.handlercode'          : handlerCode,
            'prpcmain.operatorcode'         : handlerCode,
            'prpcmain.uniqueID'             : uniqueID,
            tokenNo                         : auto.identity,
            'prpcitemCar.nonlocalflag'      : 0,
            'prpcitemCar.licenseno'         : auto.licensePlateNo,
            'prpcitemCar.frameno'           : getAutoVinNo(context),
            'prpcitemCar.engineno'          : getAutoEngineNo(context),
            'prpcitemCar.enrolldate'        : carEnrollDate,
            'prpcitemCar.useyears'          : autoUseYears,
            'prpcitemCar.seatcount'         : carSeat,
            'prpcinsureds[0].insuredflag'   : '0010000',
            'prpcinsureds[1].insuredflag'   : '1000000',
            'prpcinsureds[1].insurednature' : 3,
            'prpcinsureds[2].insuredflag'   : '0100000',
            'prpcitemCar.purchaseprice'     : carPurchasePrice,
            'prpcmain.startdate'            : startDateText,
            'prpcmain.enddate'              : endDateText,
            'BZ_selected'                   : bzSelected,
            'startDateBI'                   : startDateText,
            'endDateBI'                     : endDateText,
            'startDateCI'                   : context.bzStartDateText ?: startDateTextDefault,
            'isBusiness'                    : '1',
            'prpcmain.packageName'          : packageName,
            'guohuselect'                   : transferDate ? 1 : 0
        ]
    }

    //保存承保
    static final _SAVE_PROPOSAL_RPG_DEFAULT = { context ->
        def (startDateText) = getNewInsurancePeriodText(context)
        def carEnrollDate   = getCarEnrollDate context
        def packageName     = getPackageName context
        def autoVinNo       = getAutoVinNo context
        def autoEngineNo    = getAutoEngineNo context
        def bzSelected      = context.accurateInsurancePackage?.compulsory ? 1 : 0

        def auto            = context.auto
        def order           = context.order
        def applicant       = order.applicant

        def autoId          = auto.identity                        // 车主身份证
        def applicantId     = applicant.identity ?: autoId         // 投保人身份证
        def insureId        = order.insuredIdNo ?: autoId          // 被保险人身份证
        def autoName        = auto.owner                           // 车主身份证
        def applicantName   = applicant.name ?: auto.owner         // 投保人身份证
        def insureName      = order.insuredName ?: auto.owner      // 被保险人身份证

        def deliveryAddress = order.deliveryAddress
        def address         = deliveryAddress.provinceName + deliveryAddress.cityName + deliveryAddress.districtName + deliveryAddress.street
        def userMobile      = applicant?.mobile ?: auto.mobile ?: deliveryAddress?.mobile  // 用户手机

        [
            'prpcmain.areaCode'             : context.areaCode,
            'prpcmain.cityCode'             : context.cityCode,
            'prpcitemCar.licenseno'         : auto.licensePlateNo,
            'prpcmain.comcode'              : context.comCode,
            'prpcmain.handlercode'          : context.handlerCode,
            'prpcmain.operatorcode'         : context.handlerCode,
            'prpcmain.uniqueID'             : context.uniqueID,
            //'prpcmain.renewal'              : context.renewable ? 1 : 0,
            //'prpcitemCar.licenseflag'       : '1',
            //'prpcinsureds[1].insurednature' : '3',
            'prpcinsureds[0].insuredflag'   : '0010000',
            'prpcinsureds[1].insuredflag'   : '1000000',
            'prpcinsureds[2].insuredflag'   : '0100000',
            'prpcmain.packageName'          : packageName,
            'prpcitemCar.frameno'           : autoVinNo,
            'prpcitemCar.engineno'          : autoEngineNo,
            'prpcitemCar.enrolldate'        : carEnrollDate,
            'startDateBI'                   : startDateText,
            'BZ_selected'                   : bzSelected,
            'isBusiness'                    : '1',
            //'CarOwnerPhoneNumber'           : userMobile,

            //车主
            'prpcinsureds[0].insuredname'   : autoName,
            'prpcinsureds[0].identifytype'  : '01',
            'prpcinsureds[0].identifynumber': autoId,
            'prpcinsureds[0].mobile'        : userMobile,
            'prpcinsureds[0].insuredaddress': address,
            //投保人
            'prpcinsureds[1].insuredname'   : applicantName,
            'prpcinsureds[1].identifytype'  : '01',
            'prpcinsureds[1].identifynumber': applicantId,
            'prpcinsureds[1].mobile'        : userMobile,
            //被保人
            'prpcinsureds[2].insuredname'   : insureName,
            'prpcinsureds[2].identifytype'  : '01',
            'prpcinsureds[2].identifynumber': insureId,
            'prpcinsureds[2].mobile'        : userMobile,
        ]
    }

    //根据品牌编号查找车型
    // TODO：应该将02、03、04的请求拆开，否则混在一起很难维护
    private static final _FIND_CAR_MODEL_BY_BRAND_NAME_RPG_BASE = { requestType, context ->
        def areaCode = context.areaCode
        def cityCode = context.cityCode
        def uniqueID = context.uniqueID
        def comCode  = context.comCode
        Auto auto = context.auto
        def carEnrollDate = getCarEnrollDate context
        def carModel = auto?.autoType?.code ?: context.historicalVehicleInfo.brandname
        def queryCode = context.selectedCarModel?.vehicleFgwCode ?: context.historicalVehicleInfo?.modelcode?.trim()
        def parentId = context.selectedCarModel?.parentId ?: context.extendedAttributes?.autoModel
        log.info '车型查询参数，requestType：{}，carModel：{}，queryCode：{}，parentId：{}', requestType, carModel, queryCode, parentId

        [
            'carModelQuery.requestType' : requestType,
            'carModelQuery.areaCode'    : areaCode,
            'carModelQuery.cityCode'    : cityCode,
            'carModelQuery.uniqueId'    : uniqueID,
            'carModelQuery.licenseNo'   : auto.licensePlateNo,
            'carModelQuery.carOwner'    : auto.owner,
            'carModelQuery.frameNo'     : getAutoVinNo(context),
            'carModelQuery.engineNo'    : getAutoEngineNo(context),
            'carModelQuery.comCode'     : comCode,
            'carModelQuery.licenseType' : '02',
            'carModelQuery.enrollDate'  : carEnrollDate,
            'carModelQuery.carModel'    : carModel,
            'carModelQuery.queryCode'   : queryCode,
            'carModelQuery.parentId'    : parentId
        ]

    }

    static final _FIND_CAR_MODEL_BY_BRAND_NAME_RPG_DEFAULT  = _FIND_CAR_MODEL_BY_BRAND_NAME_RPG_BASE.curry '02'
    static final _FIND_CAR_MODEL_BY_BRAND_NAME_RPG_03       = _FIND_CAR_MODEL_BY_BRAND_NAME_RPG_BASE.curry '03'

    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="ResponseHandlers(RHs)">

    // 根据品牌型号查找车型
    private static final _FIND_CAR_MODEL_BY_BRAND_NAME_02_BASE = { carModelsPropName, resp, context ->
        def brandModelList = resp.body?."$carModelsPropName"

        if (brandModelList) {
            getSelectedCarModelFSRV context, brandModelList, resp
        } else {
            log.error '无法获得车型，通常是车辆/人员信息有误导致的：{}', resp
            getValuableHintsFSRV(context,
                [
                    _VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING,
                    _VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING,
                    _VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING
                ])
        }
    }

    static final _FIND_CAR_MODEL_BY_BRAND_NAME_02_RH_DEFAULT    = _FIND_CAR_MODEL_BY_BRAND_NAME_02_BASE.curry 'carModels'
    // 某些费改地区（杭州、深圳）
    static final _FIND_CAR_MODEL_BY_BRAND_NAME_02_RH_TYPE2      = _FIND_CAR_MODEL_BY_BRAND_NAME_02_BASE.curry 'queryVehicle'

    // 直接选择车型的RH（requestType 03和04都属于这种响应处理方式）
    static final _FIND_CAR_MODEL_BY_BRAND_NAME_03_RH_DEFAULT = { resp, context ->
        if ('0000' == resp.head.errorCode && resp.body) {
            context.selectedCarModel = resp.body
            getContinueFSRV resp.body
        } else {
            log.error '无法获得车型，通常是车辆/人员信息有误导致的：{}', resp
            getValuableHintsFSRV(context,
                [
                    _VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING,
                    _VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING,
                    _VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING
                ])
        }
    }


    // 注册UniqueID到车型
    static final _REGISTER_UNIQUE_ID_RH_DEFAULT = { resp, context ->
        getContinueFSRV resp
    }

    // 注册UniqueID到车型 使用精友车型库的城市
    static final _REGISTER_UNIQUE_ID_RH_JY = { resp, context ->
        if ('成功' == resp.head.errorMsg) {
            context.selectedCarModel = resp.body
            getContinueFSRV resp
        } else {
            log.error '根据输入的品牌型号无法找到匹配车型'
            [_ROUTE_FLAG_DONE, _STATUS_CODE_PICC_CAR_MODEL_NOT_FOUND, null, '根据输入的品牌型号无法找到匹配车型']
        }
    }

    //</editor-fold>

}
