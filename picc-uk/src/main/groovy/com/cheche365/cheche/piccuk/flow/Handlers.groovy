package com.cheche365.cheche.piccuk.flow

import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.ContactUtils.getRandomEmail
import static com.cheche365.cheche.common.util.ContactUtils.randomMobile
import static com.cheche365.cheche.common.util.DateUtils.getYearsUntil
import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1
import static com.cheche365.cheche.core.model.GlassType.Enum.IMPORT_2
import static com.cheche365.cheche.core.model.PaymentChannel.Enum.AGENT_PARSER_ALIPAY_62
import static com.cheche365.cheche.parser.Constants._DAMAGE
import static com.cheche365.cheche.parser.Constants._DAMAGE_IOP
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.Constants._DRIVER_AMOUNT
import static com.cheche365.cheche.parser.Constants._DRIVER_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants._DRIVER_IOP
import static com.cheche365.cheche.parser.Constants._ENGINE
import static com.cheche365.cheche.parser.Constants._ENGINE_IOP
import static com.cheche365.cheche.parser.Constants._GLASS
import static com.cheche365.cheche.parser.Constants._PASSENGER_AMOUNT
import static com.cheche365.cheche.parser.Constants._PASSENGER_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants._PASSENGER_IOP
import static com.cheche365.cheche.parser.Constants._SCRATCH_AMOUNT
import static com.cheche365.cheche.parser.Constants._SCRATCH_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants._SCRATCH_IOP
import static com.cheche365.cheche.parser.Constants._SPONTANEOUS_LOSS
import static com.cheche365.cheche.parser.Constants._SPONTANEOUS_LOSS_IOP
import static com.cheche365.cheche.parser.Constants._THEFT
import static com.cheche365.cheche.parser.Constants._THEFT_IOP
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_AMOUNT
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_IOP
import static com.cheche365.cheche.parser.Constants._UNABLE_FIND_THIRDPARTY
import static com.cheche365.cheche.parser.util.BusinessUtils.adjustInsureAmount
import static com.cheche365.cheche.parser.util.BusinessUtils.getCommercialInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.getCompulsoryInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.getQuoteKindItemParams
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted
import static com.cheche365.cheche.piccuk.util.BusinessUtils.getAddress
import static com.cheche365.cheche.piccuk.util.BusinessUtils.getAllBaseKindItems
import static com.cheche365.cheche.piccuk.util.BusinessUtils.getBICIFlag
import static com.cheche365.cheche.piccuk.util.BusinessUtils.getCarSeat
import static com.cheche365.cheche.piccuk.util.BusinessUtils.getEnrollDate
import static com.cheche365.cheche.piccuk.util.BusinessUtils.getEnrollDateText
import static com.cheche365.cheche.piccuk.util.BusinessUtils.getTaxDate
import static java.time.LocalDate.now as today



/**
 * 请求生成器（RPG）和响应处理器（RH）
 */
@Slf4j
class Handlers {

    static final _AMOUNT_CONVERTER_BOOLEAN = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        insurancePackage[propName] ? 'on' : null
    }

    static final _AMOUNT_CONVERTER_FROM_JSON = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        insurancePackage[propName] ? kindItem.amount : null
    }

    static final _GLASS_CONVERTER_FROM_AMOUNT = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        insurancePackage[propName] ? (DOMESTIC_1 == insurancePackage.glassType ? 10 : 20) : null
    }

    static final _AMOUNT_CONVERTER_FROM_AMOUNT_LIST = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        def expectedAmount = insurancePackage[propName]
        def amountList = kindItem?.amountList?.reverse()

        def amount = expectedAmount ?
            (adjustInsureAmount(expectedAmount, amountList, { it as double }, { it as double }) ?: null)
            : null

        amount
    }

    static final _AMOUNT_CONVERTER_UNSUPPORTED = { context, insurancePackage, kindItem, propName, iopPropName, extConfig ->
        null
    }

    /**
     * 内部的保额转换成外部的请求(内转外)
     */
    static final _I2O_PREMIUM_CONVERTER = { context, outerKindCode, kindItem, result ->
        if (result) {
            def params = [
                chooseFlag   : 'on',
                kindCode     : outerKindCode,
                amount       : 'on' != result ? result : null,
                calculateFlag: _KIND_ITEM_CONVERTER_MAPPINGS[outerKindCode].calculateFlag,
                clauseCode   : _KIND_ITEM_CONVERTER_MAPPINGS[outerKindCode].clauseCode,
                flag         : _KIND_ITEM_CONVERTER_MAPPINGS[outerKindCode].flag
            ]
            if ('050232' == outerKindCode) {
                params << [
                    modeCode: result,
                    amount  : null
                ]
            }
            if ('050712' == outerKindCode) {
                def quantity = ((context.vehiclePMCheckResult?.seatCount) ?: getCarSeat(context)) - 1
                params << [
                    quantity  : quantity,
                    unitAmount: result,
                    amount    : result * quantity
                ]
            }
            params
        }
    }

    /**
     * 获取报价后，将返回的报价转换成内部的结果（外转内）
     * 返回值有四项：保额，报价，iop的报价，其他的信息(比如玻璃险的类型，获取乘客险的座位数)
     */
    static final _O2I_PREMIUM_CONVERTER = { context, innerKindCode, kindItem, amountName, premiumName, isIop,
                                            iopPremiumName, extConfig ->
        def other
        if (_GLASS == innerKindCode) {
            other = ('10' == kindItem?.modeCode) ? DOMESTIC_1 : ('20' == kindItem?.modeCode) ? IMPORT_2 : null
        }
        if (_PASSENGER_AMOUNT == innerKindCode) {
            other = context?.selectedCarModel?.rateDPassengercapacity
            kindItem?.amount = kindItem?.unitAmount
        }

        [
            isIop ? null : kindItem?.amount,
            isIop ? null : kindItem?.premium,
            isIop ? kindItem?.premium : null,
            other
        ]

    }

    static final _KIND_ITEM_CONVERTERS_CONFIG = [
        ['050232', _GLASS, _GLASS_CONVERTER_FROM_AMOUNT, null, _O2I_PREMIUM_CONVERTER, null], //玻璃单独破碎险
        ['050202', _DAMAGE, _AMOUNT_CONVERTER_FROM_JSON, null, _O2I_PREMIUM_CONVERTER, null], //机动车损失保险
        ['050311', _SPONTANEOUS_LOSS, _AMOUNT_CONVERTER_FROM_JSON, null, _O2I_PREMIUM_CONVERTER, null], //自燃损失险
        ['050602', _THIRD_PARTY_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, _THIRD_PARTY_AMOUNT_LIST,
         _O2I_PREMIUM_CONVERTER, null], //机动车第三者责任保险
        ['050211', _SCRATCH_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, _SCRATCH_AMOUNT_LIST, _O2I_PREMIUM_CONVERTER, null], //车身划痕损失险
        ['050501', _THEFT, _AMOUNT_CONVERTER_FROM_JSON, null, _O2I_PREMIUM_CONVERTER, null], //机动车盗抢保险
        ['050461', _ENGINE, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //涉水发动机损坏险
        ['050711', _DRIVER_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, _DRIVER_AMOUNT_LIST, _O2I_PREMIUM_CONVERTER, null], //机动车车上人员责任保险（司机）
        ['050712', _PASSENGER_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, _PASSENGER_AMOUNT_LIST, _O2I_PREMIUM_CONVERTER, null], //机动车车上人员责任保险（乘客）
        ['050932', _THEFT_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（盗抢险）
        ['050938', _ENGINE_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（涉水发动机损坏险）
        ['050930', _DAMAGE_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（车损险）
        ['050931', _THIRD_PARTY_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（三者险）
        ['050937', _SCRATCH_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（车身划痕险）
        ['050935', _SPONTANEOUS_LOSS_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（自燃险）
        ['050933', _DRIVER_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（车上人员司机）
        ['050934', _PASSENGER_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（车上人员乘客）
        ['050451', _UNABLE_FIND_THIRDPARTY, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //车损险无法找到第三方
    ]

    static final _KIND_ITEM_CONVERTER_MAPPINGS = [
        // 主险
        '050711': [calculateFlag: 'Y21Y00', clauseCode: '050053', flag: '100000', detailsSize: 4, name: '车上人员责任险（司机）'],
        '050712': [calculateFlag: 'Y21Y00', clauseCode: '050053', flag: '100000', detailsSize: 4, name: '车上人员责任险（乘客）'],

        '050202': [calculateFlag: 'Y11Y000', clauseCode: '050051', flag: '100000', detailsSize: 4, name: '机动车损失保险'],
        '050501': [calculateFlag: 'N11Y000', clauseCode: '050054', flag: '100000', detailsSize: 4, name: '盗抢险'],
        '050602': [calculateFlag: 'Y21Y000', clauseCode: '050052', flag: '100000', detailsSize: 4, name: '第三者责任保险'],

        // 附加险
        '050211': [calculateFlag: 'N12Y000', clauseCode: '050059', flag: '200000', detailsSize: 0, name: '车身划痕损失险'],
        '050232': [calculateFlag: 'N32N000', clauseCode: '050056', flag: '200000', detailsSize: 0, name: '玻璃单独破碎险'],
        '050311': [calculateFlag: 'N12Y000', clauseCode: '050057', flag: '200000', detailsSize: 0, name: '自燃损失险'],
        '050461': [calculateFlag: 'N32Y000', clauseCode: '050060', flag: '200000', detailsSize: 0, name: '发动机涉水损失险'],
        '050451': [calculateFlag: 'N32N000', clauseCode: '050064', flag: '200000', detailsSize: 0, name: '机动车损失保险无法找到第三方特约险'],

        // IOP
        // 主险的IOP
        '050930': [calculateFlag: 'N33N000', clauseCode: '050066', flag: '200000', detailsSize: 4, name: '不计免赔险（车损险）'],
        '050931': [calculateFlag: 'N33N000', clauseCode: '050066', flag: '200000', detailsSize: 4, name: '不计免赔险（三者险）'],
        '050932': [calculateFlag: 'N33N000', clauseCode: '050066', flag: '200000', detailsSize: 4, name: '不计免赔险（盗抢险）'],
        '050933': [calculateFlag: 'N33N000', clauseCode: '050066', flag: '200000', detailsSize: 4, name: '不计免赔险（车上人员（司机））'],
        '050934': [calculateFlag: 'N33N000', clauseCode: '050066', flag: '200000', detailsSize: 4, name: '不计免赔险（车上人员（乘客））'],
        // 附加险的IOP
        '050935': [calculateFlag: 'N33N000', clauseCode: '050066', flag: '200000', detailsSize: 0, name: '不计免赔险（自燃损失险）'],
        '050936': [calculateFlag: 'N33N000', clauseCode: '050066', flag: '200000', detailsSize: 0, name: ''], //不计免赔险（新增设备损失险）
        '050937': [calculateFlag: 'N33N000', clauseCode: '050066', flag: '200000', detailsSize: 0, name: '不计免赔险（车身划痕损失险）'],
        '050938': [calculateFlag: 'N33N000', clauseCode: '050066', flag: '200000', detailsSize: 0, name: '不计免赔险（发动机涉水损失险）'],
        '050939': [calculateFlag: 'N33N000', clauseCode: '050066', flag: '200000', detailsSize: 0, name: ''], // 不计免赔险（车上货物责任险）
        '050917': [calculateFlag: 'N33N000', clauseCode: '050066', flag: '200000', detailsSize: 0, name: ''],//不计免赔险（精神损害抚慰金责任险）
    ]

    static final _QUERY_AND_QUOTE_RPG_BASE = { config, context ->
        context.kindItemConvertersConfig = config

        def allBaseKindItems = getAllBaseKindItems context, context.kindItemConvertersConfig
        def quoteParams = getQuoteKindItemParams context, allBaseKindItems, config, _I2O_PREMIUM_CONVERTER
        //将list转换成map型
        def bizParamsForMap = quoteParams.withIndex().collectEntries { bizParams, index ->
            bizParams.keySet().collectEntries { key ->
                [
                    "prpCitemKindsTemp[${index}].${key}": bizParams[key]
                ]
            }
        }

        def (biStartDateText, biEndDateText) = getCommercialInsurancePeriodTexts(context) //商业险起保日期
        def (ciStartDateText, ciEndDateText) = getCompulsoryInsurancePeriodTexts(context) //交强险起保日期
        Auto auto = context.auto
        def selectedCarModel = context.selectedCarModel
        def baseInfo = context.baseInfo

        context.angentInfo + baseInfo.subMap([
            'prpCitemKindCI.calculateFlag',
            'prpCitemKindCI.clauseCode',
            'prpCitemKindCI.disCount',
            'prpCitemKindCI.dutyFlag',
            'prpCitemKindCI.familyNo',
            'prpCitemKindCI.flag',
            'prpCitemKindCI.id.itemKindNo',
            'prpCitemKindCI.kindCode',
            'bIInsureFlag',
            'cIInsureFlag',
            'ciInsureDemandCheckCIVo.flag',
            'ciInsureDemandCheckVo.flag',
            'prpBatchVehicle.carId',
            'prpCcarShipTax.id.itemNo',
            'prpCcarShipTax.taxComCode', //税务机关代码
            'prpCitemCar.id.itemNo',
            'prpCitemCar.runMiles',
            'prpCitemCar.carProofNo',
            'prpCmain.agentCode',
            'prpCmain.businessNature',
            'prpCmain.comCode',
            'prpCmain.startHour',
            'prpCmain.endHour',
            'prpCmain.makeCom',
            'prpCmain.handler1Code',
            'prpCmain.handlerCode',
            'prpCmain.operateDate',
            'prpCinsureds[0].id.serialNo',
            'prpCinsureds[0].insuredFlag',
            'prpCmainCommon.clauseIssue',
            'prpCmainCI.endHour',
            'prpCmainCI.startHour',
            'bizType',
            'queryCarModelInfo',
            'riskCode',
            'is4SFlag'
        ]) + [
            'isBICI'                      : getBICIFlag(context),
            'ciStartDate'                 : ciStartDateText,
            'biStartDate'                 : biStartDateText,
            'qualificationName'           : baseInfo.prpCmainagentName,
            'qualificationNo'             : context.qualificationNo,
            'queryArea'                   : context.cityName,
            'prpCcarShipTax.model'        : 'A01',
            'prpCcarShipTax.taxPayerName' : selectedCarModel.owner,
            'prpCcarShipTax.taxType'      : '1',  //交税形式(1：正常缴税)
            'isNetProp'                   : '1',  // 投保形式(1：电子投保)
            'prpCmain.argueSolution'      : '1', // 合同争议方式：1：诉讼，2：仲裁。仲裁无法报价
            'prpCitemCar.carId'           : '',
            'prpCitemCar.carKindCode'     : 'A01', //车辆种类
            'prpCitemCar.clauseType'      : 'F42', //机动车综合条款（家庭自用汽车产品）
            'prpCitemCar.newCarFlag'      : '0',   // 新旧车标志
            'prpCitemCar.noNlocalFlag'    : '0',   //是否为外地车？1或0
            'prpCmainCar.agreeDriverFlag' : '0',
            'prpCmainCar.newDeviceFlag'   : '0',
            'prpCitemCar.useNatureCode'   : '211', //家庭自用汽车
            'prpCitemCar.licenseType'     : '02',
            'prpCitemCar.engineNo'        : auto.engineNo,
            'prpCitemCar.licenseNo'       : auto.licensePlateNo,
            'prpCitemCar.frameNo'         : auto.vinNo,
            'prpCitemCar.enrollDate'      : getEnrollDateText(context),
            'prpCitemCar.useYears'        : getYearsUntil(getEnrollDate(context)) ? getYearsUntil(getEnrollDate(context)) : 1, //这里可能有雷 (确实有雷，错误 #13603遇到一辆一年内的车，我们算出来的使用年限是0，折扣较低，官网传的是1，折扣较高，导致报价不一致)
            'prpCitemCar.fuelType'        : 'A', //'A'汽油
            'prpCitemCar.modelDemandNo'   : selectedCarModel.id?.pmQueryNo,
            'prpCitemCar.seatCount'       : getCarSeat(context),
            'prpCitemCar.purchasePrice'   : selectedCarModel.priceT,
            'prpCitemCar.brandName'       : selectedCarModel.vehicleName,
            'prpCitemCar.modelCode'       : selectedCarModel.vehicleId,
            'prpCitemCar.actualValue'     : context.carActualValue,
            //以下几个与代理有关
            'prpCmain.coinsFlag'          : '00', //'00':非联非共
            'prpCmain.startDate'          : biStartDateText,
            'prpCmain.endDate'            : biEndDateText,
            'prpCmain.operatorCode'       : baseInfo.operatorCode,
            'prpCmainCommon.queryArea'    : context.provinceCode,
            'prpCinsureds[0].insuredType' : '01', //1：个人，2：团体
            'prpCinsureds[0].identifyType': '01',
            'prpCinsureds[0].insuredName' : auto.owner,
            'prpCmainCI.endDate'          : ciEndDateText,
            'prpCmainCI.startDate'        : ciStartDateText,
            'prpCitemKindCI.amount'       : '122000', // TODO : 确认该值能否从前面的步骤中拿到
            'prpCitemCar.monopolyFlag'    : '1',
            'carModelPlatFlag'            : '0',
            'monopolyConfigsCount'        : '0',
            'prpCitemCar.id.itemNo'       : '1',
            //GuangdongSysFlag : 0,
        ] + bizParamsForMap
    }


    static final _QUERY_AND_QUOTE_RPG_BASE_DEFAULT = { config, partialParameter, context ->
        context.kindItemConvertersConfig = config

        def allBaseKindItems = getAllBaseKindItems context, context.kindItemConvertersConfig
        def quoteParams = getQuoteKindItemParams context, allBaseKindItems, config, _I2O_PREMIUM_CONVERTER
        //将list转换成map型
        def bizParamsForMap = quoteParams.withIndex().collectEntries { bizParams, index ->
            bizParams.keySet().collectEntries { key ->
                [
                    "prpCitemKindsTemp[${index}].${key}": bizParams[key]
                ]
            }
        }

        def (biStartDateText, biEndDateText) = getCommercialInsurancePeriodTexts(context) //商业险起保日期
        def (ciStartDateText, ciEndDateText) = getCompulsoryInsurancePeriodTexts(context) //交强险起保日期
        Auto auto = context.auto
        def selectedCarModel = context.selectedCarModel
        def baseInfo = context.baseInfo
        def transferFlag = context?.additionalParameters?.supplementInfo?.transferDate // 是否是过户车
        def transferDate = transferFlag ? _DATE_FORMAT3.format(context.additionalParameters.supplementInfo?.transferDate) : null  // 过户日期

        [
            'isBICI'                           : getBICIFlag(context),
            'ciStartDate'                      : ciStartDateText,
            'ciStartHour'                      : '0',
            'ciStartMinute'                    : '0',
            'ciEndDate'                        : ciEndDateText,
            'ciEndHour'                        : '24',
            'ciEndMinute'                      : '0',
            'biStartDate'                      : biStartDateText,
            'qualificationName'                : baseInfo?.prpCmainagentName,
            'qualificationNo'                  : context.qualificationNo,
            'queryArea'                        : context.cityName,
            'prpCcarShipTax.taxType'           : '1',  //交税形式(1：正常缴税)
            'prpCcarShipTax.calculateMode'     : 'C1',
            'prpCcarShipTax.carKindCode'       : 'A01',
            'prpCcarShipTax.model'             : 'B11',
            'prpCcarShipTax.taxPayerIdentNo'   : auto.identity,
            'prpCcarShipTax.taxPayerIdentNoLSJ': auto.identity,
            'prpCcarShipTax.taxPayerNumber'    : auto.identity,
            'prpCcarShipTax.taxPayerNumberLSJ' : auto.identity,
            'prpCcarShipTax.taxPayerCode'      : auto.identity,
            'prpCcarShipTax.taxPayerNature'    : '3',
            'prpCcarShipTax.taxRegistryNumber' : auto.identity,
            'prpCcarShipTax.taxPayerName'      : auto.owner,
            'prpCcarShipTax.taxAbateType'      : 1,
            'prpCcarShipTax.id.itemNo'         : 1,
            'prpCcarShipTax.taxComCode'        : '',
            'prpCitemCar.carId'                : '',
            'prpCitemCar.carKindCode'          : 'A01', //车辆种类
            'prpCitemCar.clauseType'           : 'F42', //机动车综合条款（家庭自用汽车产品）
            'prpCitemCar.newCarFlag'           : '0',   // 新旧车标志
            'prpCitemCar.noNlocalFlag'         : '0',   //是否为外地车？1或0
            'prpCmainCar.agreeDriverFlag'      : '0',
            'prpCmainCar.newDeviceFlag'        : '0',
            'prpCitemCar.useNatureCode'        : '211', //家庭自用汽车
            'prpCitemCar.licenseType'          : '02',
            'prpCitemCar.engineNo'             : auto.engineNo,
            'prpCitemCar.licenseNo'            : auto.licensePlateNo,
            'prpCitemCar.frameNo'              : auto.vinNo,
            'prpCitemCar.enrollDate'           : getEnrollDateText(context),
            'prpCitemCar.useYears'             : getYearsUntil(getEnrollDate(context)) ? getYearsUntil(getEnrollDate(context)) : 1, //这里可能有雷 (确实有雷，错误 #13603遇到一辆一年内的车，我们算出来的使用年限是0，折扣较低，官网传的是1，折扣较高，导致报价不一致)
//            'prpCitemCar.fuelType'             : 'A', //'A'汽油
            'prpCitemCar.modelDemandNo'        : selectedCarModel.id?.pmQueryNo,
            'prpCitemCar.seatCount'            : context.vehiclePMCheckResult?.seatCount ?: getCarSeat(context), //南京以交管查车座位数为准
            'prpCitemCar.purchasePrice'        : selectedCarModel.priceT,
            'prpCitemCar.brandName'            : selectedCarModel.vehicleName,
            'prpCitemCar.modelCode'            : selectedCarModel.vehicleId,
            'prpCitemCar.actualValue'          : context.carActualValue,
            //以下几个与代理有关
            'prpCmain.coinsFlag'               : '00', //'00':非联非共
            'prpCmain.startDate'               : biStartDateText,
            'prpCmain.startHour'               : '0',
            'prpCmain.startMinute'             : '0',

            'prpCmain.endDate'                 : biEndDateText,
            'prpCmain.endHour'                 : '24',
            'prpCmain.endMinute'               : '0',
            'prpCmain.operatorCode'            : baseInfo?.operatorCode,
            'prpCmainCommon.queryArea'         : context.provinceCode,
            'prpCinsureds[0].insuredType'      : '1', //1：个人，2：团体
            'prpCinsureds[0].identifyType'     : '01',
            'prpCinsureds[0].insuredName'      : auto.owner,
            'prpCmainCI.endDate'               : ciEndDateText,
            'prpCmainCI.endHour'               : '24',
            'prpCmainCI.endMinute'             : '0',

            'prpCmainCI.startDate'             : ciStartDateText,
            'prpCmainCI.startHour'             : '0',
            'prpCmainCI.startMinute'           : '0',
            'prpCitemCar.monopolyFlag'         : '1',
            'carModelPlatFlag'                 : '0',
            'monopolyConfigsCount'             : '0',
            'prpCitemCar.id.itemNo'            : '1',
            //GuangdongSysFlag : 0,

            'prpCitemCar.exhaustScale'         : context.vehiclePMCheckResult?.displacement ?: selectedCarModel.vehicleExhaust, // 排量wholeWeight
            'prpCitemCar.carLotEquQuality'     : context.vehiclePMCheckResult?.wholeWeight ?: selectedCarModel.vehicleQuality * 1000, // 质量
            'prpCcarShipTax.carLotEquQuality'  : context.vehiclePMCheckResult?.wholeWeight ?: selectedCarModel.vehicleQuality * 1000,

            'prpCitemKindCI.familyNo'          : isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage) ? '1' : '',
            'prpCitemCar.transferVehicleFlag'  : transferFlag ? '1' : '0',
            'prpCitemCar.transferDate'         : transferDate,
            'coinsFlagBak'                     : '00', // 单独报交强险提示 承保调用定价链接失败 详见issues13567

        ] + bizParamsForMap + partialParameter.call()
    }

    static final _QUERY_AND_QUOTE_RPG_PARAMS_410100 = {
        [
            // baseInfo
            'is4SFlag'                    : '',
            'comCode'                     : '41015300',
            'agentCode'                   : '110041100036',
            'bIInsureFlag'                : 1,
            'cIInsureFlag'                : 1,
            'bizType'                     : 'PROPOSAL',
            'useYear'                     : 9,
            'riskCode'                    : 'DAA',
            'operatorCode'                : '',
            'queryCarModelInfo'           : '',
            'prpCmainagentName'           : '北京天道保险经纪有限责任公司',
            'prpBatchVehicle.carId'       : '',
            'prpCitemCar.runMiles'        : '',
            'prpCmain.agentCode'          : '110041100036',
            'prpCmain.businessNature'     : '4',
            'prpCmain.comCode'            : '41015305',
            'prpCmain.makeCom'            : '41015303',
            'prpCmain.handler1Code'       : '84132478',
            'prpCmain.handlerCode'        : '84132478',
            'prpCmain.operateDate'        : today(),
            'prpCmain.projectCode'        : '',
            'prpCmainCommon.clauseIssue'  : '2',
            'prpCinsureds[0].id.serialNo' : '1',
            'prpCinsureds[0].insuredFlag' : '11100000000000000000000000000A',
            'prpCitemKindCI.amount'       : '122000', // TODO : 确认该值能否从前面的步骤中拿到
            'prpCitemKindCI.calculateFlag': 'Y',
            'prpCitemKindCI.clauseCode'   : '050001',
            'prpCitemKindCI.disCount'     : '1',
            'prpCitemKindCI.dutyFlag'     : '2',
            'prpCitemKindCI.kindName'     : '机动车交通事故强制责任保险',
            'prpCitemKindCI.flag'         : '',
            'prpCitemKindCI.id.itemKindNo': '',
            'prpCitemKindCI.kindCode'     : '050100',
            'ciInsureDemandCheckCIVo.flag': 'DEMAND',
            'ciInsureDemandCheckVo.flag'  : 'DEMAND',
            'Today'                       : today(),
            'prpCmain.proposalNo'         : '',
            'idCardCheckInfo[0].flag'     : '',

            //agentInfo
            'prpCmain.operatorCode'       : 'A410100744',
            'agentType'                   : '41102B',
            'quotationRisk'               : 'PUB',
            'userCode'                    : 'A410100744',
            'userType'                    : '02',
            'prpCmain.argueSolution'      : '1',
            'prpCremarks_[0].operatorCode': 'A410100744',
            'operatorName'                : '杨军辉',
            'makeComDes'                  : '郑州市分公司中介业务部中介业务二部',
            'handlerCodeDes'              : '张瑞霞',
            'handler1CodeDes'             : '张瑞霞',
            'comCodeDes'                  : '郑州市分公司中介业务部中介业务一部',
            'businessNatureTranslation'   : '经纪业务'
        ]

    }

    /**
     * 成都
     */
    static final _QUERY_AND_QUOTE_RPG_PARAMS_510100 = {
        [
            // baseInfo
            'is4SFlag'                    : '',
            'comCode'                     : '51010405',
            'agentCode'                   : '440021100120',
            'bIInsureFlag'                : 1,
            'cIInsureFlag'                : 1,
            'bizType'                     : 'PROPOSAL',
            'useYear'                     : 9,
            'riskCode'                    : 'DAA',
            'operatorCode'                : '',
            'queryCarModelInfo'           : '',
            'prpCmainagentName'           : '车车保险销售服务有限公司',
            'prpBatchVehicle.carId'       : '',
            'prpCitemCar.runMiles'        : '',
            'prpCmain.agentCode'          : '440021100120',
            'prpCmain.businessNature'     : '2',
            'prpCmain.comCode'            : '51010405',
            'prpCmain.makeCom'            : '51010405',
            'prpCmain.handler1Code'       : '51620671',
            'prpCmain.handlerCode'        : '51620671',
            'prpCmain.operateDate'        : today(),
            'prpCmain.projectCode'        : '',
            'prpCmainCommon.clauseIssue'  : '2',
            'prpCinsureds[0].id.serialNo' : '1',
            'prpCinsureds[0].insuredFlag' : '11100000000000000000000000000A',
            'prpCitemKindCI.amount'       : '0',
            'prpCitemKindCI.calculateFlag': 'Y',
            'prpCitemKindCI.clauseCode'   : '050001',
            'prpCitemKindCI.disCount'     : '1',
            'prpCitemKindCI.dutyFlag'     : '1', //TODO 确认是否可变参数
            'prpCitemKindCI.kindName'     : '机动车交通事故强制责任保险',
            'prpCitemKindCI.flag'         : '',
            'prpCitemKindCI.id.itemKindNo': '',
            'prpCitemKindCI.kindCode'     : '050100',
            'ciInsureDemandCheckCIVo.flag': 'DEMAND',
            'ciInsureDemandCheckVo.flag'  : 'DEMAND',
            'Today'                       : today(),
            'prpCmain.proposalNo'         : '',
            'idCardCheckInfo[0].flag'     : '',

            //agentInfo
            'prpCmain.operatorCode'       : 'A510103876',
            'agentType'                   : '2111VN',
            'quotationRisk'               : 'DAA',
            'userCode'                    : 'A510103876',
            'userType'                    : '02',
            'prpCmain.argueSolution'      : '1',
            'prpCremarks_[0].operatorCode': '',  //其他城市有数据
            'operatorName'                : '陈弓',
            'makeComDes'                  : '成都市锦江支公司团体客户业务一部',
            'handlerCodeDes'              : '张勇',
            'handler1CodeDes'             : '张勇',
            'comCodeDes'                  : '成都市锦江支公司团体客户业务一部',
            'businessNatureTranslation'   : '专业代理业务'
        ]

    }


    static final _QUERY_AND_QUOTE_RPG_PARAMS_320100 = {
        [

            'prpCmainagentName'              : '江苏恒生保险销售有限公司洪泽县分公司',
            'prpCmain.agentCode'             : '320021101475',
            agentCode                        : '320021101475',
            comCodeDes                       : '南京市城北支公司综合部',
            businessNatureTranslation        : '经纪业务',
            handlerCodeDes                   : '伟妮',
            handler1CodeDes                  : '伟妮',
            operatorName                     : '范芳',
            makeComDes                       : '南京市城北支公司',
            'prpCremarks_[0].operatorCode'   : '3201062096',
            userCode                         : '3201062096',
            comCode                          : '32010600',
            'prpCmain.comCode'               : '32010602',
            'prpCmain.makeCom'               : '32010600',
            'prpCmain.handler1Code'          : '12137614',
            'prpCmain.handlerCode'           : '12137614',
            agentType                        : '2110ZA',
            quotationRisk                    : 'DAA',

            // baseInfo
            'is4SFlag'                       : '',
            'bIInsureFlag'                   : 1,
            'cIInsureFlag'                   : 1,
            'bizType'                        : 'PROPOSAL',
            'useYear'                        : 9,
            'riskCode'                       : 'DAA',
            'operatorCode'                   : '',
            'queryCarModelInfo'              : '',
            'prpBatchVehicle.carId'          : '',
            'prpCitemCar.runMiles'           : '',
            'prpCitemCar.carProofNo'         : '',
            'prpCmain.businessNature'        : '4',
            'prpCmain.operateDate'           : today(),
            'prpCmain.projectCode'           : '',
            'prpCmainCommon.clauseIssue'     : '2',
            'prpCinsureds[0].id.serialNo'    : '1',
            'prpCinsureds[0].insuredFlag'    : '11100000000000000000000000000A',
            'prpCitemKindCI.amount'          : '122000', // TODO : 确认该值能否从前面的步骤中拿到
            'prpCitemKindCI.calculateFlag'   : '',
            'prpCitemKindCI.clauseCode'      : '050001',
            'prpCitemKindCI.disCount'        : '1',
            'prpCitemKindCI.dutyFlag'        : '2',
            'prpCitemKindCI.kindName'        : '机动车交通事故强制责任保险',
            'prpCitemKindCI.flag'            : '',
            'prpCitemKindCI.id.itemKindNo'   : '',
            'prpCitemKindCI.kindCode'        : '050100',
            'ciInsureDemandCheckCIVo.flag'   : 'DEMAND',
            'ciInsureDemandCheckVo.flag'     : 'DEMAND',
            'Today'                          : today(),
            'prpCmain.proposalNo'            : '',
            'idCardCheckInfo[0].flag'        : '',

            //agentInfo
            'userType'                       : '02',
            'prpCmain.argueSolution'         : '1',

        ]
    }

    static final _QUERY_AND_QUOTE_RPG_110000 = _QUERY_AND_QUOTE_RPG_BASE.curry _KIND_ITEM_CONVERTERS_CONFIG
    static final _QUERY_AND_QUOTE_RPG_320100 = _QUERY_AND_QUOTE_RPG_BASE_DEFAULT.curry _KIND_ITEM_CONVERTERS_CONFIG, _QUERY_AND_QUOTE_RPG_PARAMS_320100
    static final _QUERY_AND_QUOTE_RPG_410100 = _QUERY_AND_QUOTE_RPG_BASE_DEFAULT.curry _KIND_ITEM_CONVERTERS_CONFIG, _QUERY_AND_QUOTE_RPG_PARAMS_410100

    static final _QUERY_AND_QUOTE_RPG_510100 = _QUERY_AND_QUOTE_RPG_BASE_DEFAULT.curry _KIND_ITEM_CONVERTERS_CONFIG, _QUERY_AND_QUOTE_RPG_PARAMS_510100

    static final _INSERT_RPG_BASE = { context ->
        Auto auto = context.auto
        def baseInfo = context.baseInfo
        def selectedCarModel = context.selectedCarModel
        def ciInsureDemand = context.ciVOList?.ciInsureDemand
        def ciCarShipTax = context.ciVOList?.ciCarShipTax
        def (stTaxStartDate, stTaxEndDate) = getTaxDate(null) //车船税起止时间
        def (biStartDateText, biEndDateText) = getCommercialInsurancePeriodTexts(context) //商业险起保日期
        def (ciStartDateText, ciEndDateText) = getCompulsoryInsurancePeriodTexts(context) //交强险起保日期
        def address = getAddress context.order?.deliveryAddress
        def prpCitemKindsTemp = [:]
        def prpCprofitDetailsSizeTemp = [:]
        def prpCprofitFactorsTemp = [:]
        def prpCfixationTemp = [:]
        def prpCcommissionsTemp = [:]
        //费用拆分
        def prpDdismantleDetails = [:]
        if (context.biVoList) {
            //将list转换成map型
            prpCitemKindsTemp = context.biVoList?.prpCitemKinds?.withIndex()?.collectEntries { bizParams, index ->
                def kindItemConverter = _KIND_ITEM_CONVERTER_MAPPINGS[bizParams.kindCode]
                (bizParams.subMap([
                    'kindCode',
                    'amount',
                    'benchMarkPremium',
                    'premium',
                    'modeCode',
                    'rate',
                    'unitAmount',
                    'quantity',
                    'pureRiskPremium',
                    'netPremium',
                    'taxPremium',
                    'allNetPremium',
                ]) + [
                    chooseFlag   : 'on',
                    calculateFlag: kindItemConverter.calculateFlag,
                    clauseCode   : kindItemConverter.clauseCode,
                    flag         : kindItemConverter.flag,
                    kindName     : kindItemConverter.name,
                ]).collectEntries { key, value ->
                    [
                        "prpCitemKindsTemp[${index}].${key}": value
                    ]
                }
            }

            def prpCitemKinds = context.biVoList?.prpCitemKinds // 获取折扣信息

            prpCitemKinds.prpCprofits.prpCprofitDetails.flatten().withIndex().collect { prpCprofit, tempIndex ->
                prpCprofitDetailsSizeTemp << [
                    "prpCprofitDetailsTemp[$tempIndex].chooseFlag"   : 'on',
                    "prpCprofitDetailsTemp[$tempIndex].profitName"   : prpCprofit?.profitName,
                    "prpCprofitDetailsTemp[$tempIndex].condition"    : prpCprofit?.condition,
                    "prpCprofitDetailsTemp[$tempIndex].profitRate"   : prpCprofit?.profitRate,
                    "prpCprofitDetailsTemp[$tempIndex].profitRateMin": prpCprofit?.profitRateMin,
                    "prpCprofitDetailsTemp[$tempIndex].profitRateMax": prpCprofit?.profitRateMax,
                    "prpCprofitDetailsTemp[$tempIndex].id.proposalNo": prpCprofit?.id?.proposalNo,
                    "prpCprofitDetailsTemp[$tempIndex].id.itemKindNo": prpCprofit?.id?.itemKindNo,
                    "prpCprofitDetailsTemp[$tempIndex].id.profitCode": prpCprofit?.id?.profitCode,
                    "prpCprofitDetailsTemp[$tempIndex].id.serialNo"  : prpCprofit?.id?.serialNo,
                    "prpCprofitDetailsTemp[$tempIndex].id.profitType": prpCprofit?.id?.profitType,
                    "prpCprofitDetailsTemp[$tempIndex].kindCode"     : prpCprofit?.kindCode,
                    "prpCprofitDetailsTemp[$tempIndex].conditionCode": prpCprofit?.conditionCode,
                    "prpCprofitDetailsTemp[$tempIndex].flag"         : prpCprofit?.flag,
                ]
            }

            prpCprofitFactorsTemp = context.biVoList?.prpCprofitFactors?.withIndex()?.collectEntries { bizParams, index ->
                bizParams['id'].subMap([
                    'profitCode',
                    'conditionCode'
                ]).collectEntries { key, value ->
                    [
                        "prpCprofitFactorsTemp[${index}].id.${key}" : value,
                        "prpCprofitFactorsTemp[${index}].chooseFlag": 'on',
                        "prpCprofitFactorsTemp[${index}].rate"      : bizParams.rate,
                        "prpCprofitFactorsTemp[${index}].profitName": bizParams.profitName,
                        "prpCprofitFactorsTemp[${index}].condition" : bizParams.condition,
                        "prpCprofitFactorsTemp[${index}].lowerRate" : bizParams.lowerRate,
                        "prpCprofitFactorsTemp[${index}].upperRate" : bizParams.upperRate,
                        "prpCprofitFactorsTemp[${index}].flag"      : bizParams.flag,
                    ]
                }
            }

            prpCfixationTemp = context.biVoList?.prpCfixations?.withIndex()?.collectEntries { prpCfixation, index ->
                prpCfixation['id'].subMap([
                    'riskCode'
                ]).collectEntries { key, value ->
                    [
                        "prpCfixationTemp.id.${key}": value,
                    ]
                } + prpCfixation.subMap([
                    'riskCode',
                    'discount',
                    'profits',
                    'cost',
                    'taxorAppend',
                    'payMentR',
                    'basePayMentR',
                    'poundAge',
                    'basePremium',
                    'riskPremium',
                    'riskSumPremium',
                    'signPremium',
                    'isQuotation',
                    'riskClass',
                    'operationInfo',
                    'realDisCount',
                    'realProfits',
                    'realPayMentR',
                    'remark',
                    'responseCode',
                    'errorMessage',
                    'profitClass',
                    'costRate',
                    'unstandDiscount',
                    'targetPayMentr',
                    'targetPoundage',
                    'targetProfitsClass',
                    'pricingModel',
                    'uuid',
                    'basePayRateRCar',
                    'score',
                    'paymentUpper',
                    'marketCostRateUpper',
                    'marketCostRateLower',
                    'carMarketCostRate',
                    'marketCostRate',
                    'discountBI',
                    'poundageBI',
                    'discountCI',
                    'poundageCI',
                    'discountNew',
                    'poundageNew',
                    'premiumNew',
                    'saleFeeRateMax',
                    'marketFeeRate'
                ]).collectEntries { key, value ->
                    [
                        "prpCfixationTemp.${key}": value
                    ]
                }
            }

        }

        [context.prpDpayForPolicies.find {
            it.riskCode == 'DAA'
        }, context.prpDpayForPolicies.find {
            it.riskCode == 'DZA'
        }].withIndex().each { item, index ->
            if (item) {
                prpCcommissionsTemp << [
                    "prpCcommissionsTemp[${index}].costType"     : item?.costType,
                    "prpCcommissionsTemp[${index}].riskCode"     : item?.riskCode,
                    "prpCcommissionsTemp[${index}].adjustFlag"   : item?.adjustFlag,
                    "prpCcommissionsTemp[${index}].auditRate"    : "",
                    "prpCcommissionsTemp[${index}].sumPremium"   : 'DAA' == item?.riskCode ? context.newQuoteRecord.premium : 'DZA' == item?.riskCode ? context.ciVOList?.ciInsureDemand?.premium : '',
                    "prpCcommissionsTemp[${index}].costRate"     : item?.costRate,
                    "prpCcommissionsTemp[${index}].costRateUpper": item?.costRateUpper,
                    "prpCcommissionsTemp[${index}].coinsRate"    : "100",
                    "prpCcommissionsTemp[${index}].costFee"      : ((('DAA' == item?.riskCode ? context.biVoList?.prpCitemKinds?.netPremium?.sum() : 'DZA' == item?.riskCode ? context.ciVOList?.prpCitemKinds?.netPremium?.sum() : 0) as double) * (item?.costRate ?: 0 as double)) / 100,
                    "prpCcommissionsTemp[${index}].agreementNo"  : item?.id?.agreementNo,
                    "prpCcommissionsTemp[${index}].configCode"   : item?.id?.configCode,
                    "prpCcommissionsTemp[${index}].commQueryNo"  : "",
                ]
            } else {
                directive = Closure.DONE
            }
        }

        [context.prpDdismantleDetails.find {
            it.flag == 'DAA'
        }, context.prpDdismantleDetails.find {
            it.flag == 'DZA'
        }].withIndex().each { item, index ->
            if (item) {
                prpDdismantleDetails << [
                    "prpDdismantleDetails[${index}].id.agreementNo": item?.id?.agreementNo,
                    "prpDdismantleDetails[${index}].flag"          : item?.flag,
                    "prpDdismantleDetails[${index}].id.configCode" : item?.id?.configCode,
                    "prpDdismantleDetails[${index}].id.assignType" : item?.id?.assignType,
                    "prpDdismantleDetails[${index}].id.roleCode"   : item?.id?.roleCode,
                    "prpDdismantleDetails[${index}].roleName"      : item?.roleName,
                    "prpDdismantleDetails[${index}].costRate"      : item?.costRate,
                    "prpDdismantleDetails[${index}].roleFlag"      : item?.roleFlag,
                    "prpDdismantleDetails[${index}].businessNature": item?.businessNature,
                    "prpDdismantleDetails[${index}].roleCode_uni"  : item?.roleCode_uni
                ]
            } else {
                directive = Closure.DONE
            }
        }

        [
            'prpCmainCommon.netsales'        : '1', // 保单类型：1:电子保单， 0：纸质监制保单
            'prpCmain.argueSolution'         : '1', // 合同争议方式：1：诉讼，2：仲裁。仲裁无法报价
            'isNetProp'                      : '1',  // 投保形式(1：电子投保)
            'editType'                       : context.renewable ? 'RENEWAL' : 'NEW',
            'isBICI'                         : getBICIFlag(context),
            'agentType'                      : baseInfo.agentType ?: '41101Y',
            'prpCitemCar.carKindCode'        : 'A01',
            'prpCmain.sumPremium1'           : context.newQuoteRecord.premium + (ciInsureDemand?.premium ?: 0), // 总保费
            'prpCitemCar.licenseNo'          : auto.licensePlateNo,
            'prpCitemCar.useNatureCode'      : '211', // 使用性质：家庭自用汽车
            'prpCitemCar.clauseType'         : 'F42', // 机动车综合条款：家庭自用汽车产品
            'prpCitemCar.runAreaCode'        : '11',  // 行驶区域：中华人民共和国境内(不含港澳台)
            'prpCitemCar.modelCode'          : selectedCarModel.vehicleId,
            'prpCitemCar.brandName'          : selectedCarModel.vehicleName,
            'prpCitemCar.seatCount'          : getCarSeat(context),
            'prpCinsureds[0].insuredType'    : '1', //1：个人，2：团体
            'prpCinsureds[0].identifyType'   : '01',
            'prpCinsureds[0].insuredName'    : auto.owner,
            'prpCinsureds[0].identifyNumber' : auto.identity,
            'prpCinsureds[0].email'          : context.email,
            'prpCinsuredsview[0].mobile'     : context.extendedAttributes?.verificationMobile ?: context.order?.applicant?.mobile ?: randomMobile,
            'prpCinsureds[0].mobile'         : context.extendedAttributes?.verificationMobile ?: context.order?.applicant?.mobile ?: randomMobile,
            'prpCinsureds[0].insuredAddress' : address ?: context.cityName,
            'prpCinsureds[0].countryCode'    : 'CHN',
            'prpCitemCar.actualValue'        : context.carActualValue,
            'prpCitemCar.enrollDate'         : getEnrollDateText(context),
            // TODO 初登日期与官网抓取的不一致会导致使用年限不一致，北京不需要输入初登日期，需要输入初登日期的其他地区此处可能会出现问题
            'prpCitemCar.useYears'           : getYearsUntil(getEnrollDate(context)) ? getYearsUntil(getEnrollDate(context)) : 1, //这里可能有雷 (确实有雷，错误 #13603遇到一辆一年内的车，我们算出来的使用年限是0，折扣较低，官网传的是1，折扣较高，导致报价不一致)
            'prpCmain.startDate'             : biStartDateText,
            'prpCmain.endDate'               : biEndDateText,
            'prpCmainCI.startDate'           : ciStartDateText,
            'prpCmainCI.endDate'             : ciEndDateText,
            'prpCitemCar.licenseType'        : '02', // 号牌种类
            'prpCmainCar.agreeDriverFlag'    : '0',
            'prpCitemKind.shortRate'         : '100',
            'prpCitemKind.shortRateFlag'     : '2', // 计费方式：按日
            'prpCmain.sumPremium'            : context.newQuoteRecord.premium,
            'BIdemandNo'                     : context.biVoList?.ciInsureDemandDAA?.demandNo,
            'prpCitemCarExt.lastDamagedCI'   : context.biVoList?.prpCitemCarExt?.lastDamagedCI,
            'prpCitemCarExt_CI.rateRloatFlag': '01',
            'ciInsureDemand.demandNo'        : ciInsureDemand?.demandNo,
            'prpCplanTemps[0].payNo'         : '1',
            'prpCplanTemps[0].payReason'     : 'R29',
            'prpCplanTemps[0].planFee'       : ciInsureDemand?.premium,
            'prpCplanTemps[0].isBICI'        : 'CI',
            'prpCplanTemps[0].planDate'      : ciStartDateText,
            'prpCplanTemps[1].payNo'         : '1',
            'prpCplanTemps[1].payReason'     : 'RM9', // 本期加往期车船税
            'prpCplanTemps[1].currency'      : 'CNY',
            'prpCplanTemps[1].planFee'       : (ciCarShipTax?.thisPayTax ?: 0) + (ciCarShipTax?.prePayTax ?: 0),
            'prpCplanTemps[1].isBICI'        : 'CShip',
            'prpCplanTemps[1].planDate'      : ciStartDateText,
            'prpCplanTemps[2].payNo'         : '1',
            'prpCplanTemps[2].payReason'     : 'RM8', // 车船税滞纳金
            'prpCplanTemps[2].currency'      : 'CNY',
            'prpCplanTemps[2].planFee'       : ciCarShipTax?.delayPayTax,
            'prpCplanTemps[2].isBICI'        : 'CShip',
            'prpCplanTemps[2].planDate'      : ciStartDateText,
            'prpCplanTemps[3].payNo'         : '1',
            'prpCplanTemps[3].payReason'     : 'R21',
            'prpCplanTemps[3].planFee'       : context.newQuoteRecord.premium,
            'prpCplanTemps[3].isBICI'        : 'BI',
            'prpCplanTemps[3].planDate'      : biStartDateText,
            'prpCmainCar.carCheckStatus'     : '1', //验车情况
            'prpCmainCar.carChecker'         : '西城查询',//验车人
            'prpCmainCar.carCheckTime'       : today(),

            'prpCitemCar.carLotEquQuality'   : selectedCarModel.vehicleQuality * 1000, // 质量
            'prpCitemCar.exhaustScale'       : selectedCarModel.vehicleExhaust, // 排量
            'prpCitemCar.carProofNo'         : '',
            'prpCitemCar.carInsuredRelation' : '1', // 被保险人关系代码
            'prpCitemCar.id.itemNo'          : '1',
            'prpCitemCar.monopolyFlag'       : '0', //是否推荐送修 1 是 0 否
            'prpCitemCar.fuelType'           : 'A', //'A'汽油
            'prpCitemCar.modelDemandNo'      : selectedCarModel.id?.pmQueryNo,
            'prpCitemCar.purchasePrice'      : selectedCarModel.priceT,
            'prpCitemCar.vinNo'              : auto.vinNo,
            'prpCitemCar.engineNo'           : auto.engineNo,
            'prpCitemCar.frameNo'            : auto.vinNo,
            'prpCitemCar.carId'              : '',
            'prpCitemCar.newCarFlag'         : '0',   // 新旧车标志
            'prpCitemCar.noNlocalFlag'       : '0',   //是否为外地车？1或0
            'prpCitemCar.licenseColorCode'   : '01',// 号牌颜色
            'LicenseColorCodeDes'            : '蓝',
            'prpCmainCommon.queryArea'       : '110000',
            'useYear'                        : 9,
            'prpCmain.riskCode'              : 'DAA',
            'carShipTaxPlatFormFlag'         : '1',
            'is4SFlag'                       : 'Y',
            'monopolyConfigsCount'           : '1',

            'idCardCheckInfo[0].flag'        : '',
            'prpCcarShipTax.taxType'         : '1',  //交税形式(1：正常缴税)
            'prpCcarShipTax.calculateMode'   : 'C1',
            'prpCcarShipTax.carKindCode'     : 'A01',
            'prpCcarShipTax.model'           : 'B11',
            'prpCcarShipTax.thisPayTax'      : ciCarShipTax?.thisPayTax,
            'prpCcarShipTax.prePayTax'       : ciCarShipTax?.prePayTax,
            'prpCcarShipTax.delayPayTax'     : ciCarShipTax?.delayPayTax,
            'prpCcarShipTax.taxComCode'      : '',
            'prpCcarShipTax.taxUnit'         : '辆/年',
            'prpCcarShipTax.payStartDate'    : stTaxStartDate,
            'prpCcarShipTax.payEndDate'      : stTaxEndDate,
            'prpCcarShipTax.carLotEquQuality': context.vehiclePMCheckResult?.wholeWeight ?: selectedCarModel.vehicleQuality * 1,
            // TODO 报价响应的sumPayTax字段是0，不可取0
            'prpCcarShipTax.sumPayTax'       : ciCarShipTax?.thisPayTax,
            'prpCmainCar.newDeviceFlag'      : '0',
            'prpCmain.coinsFlag'             : '00', //'00':非联非共
            'prpCmain.startHour'             : '0',
            'prpCmain.startMinute'           : '0',

            'prpCmain.endHour'               : '24',
            'prpCmain.endMinute'             : '0',
            'prpCmainCI.endHour'             : '24',
            'prpCmainCI.endMinute'           : '0',
            'prpCmainCI.startHour'           : '0',
            'prpCmainCI.startMinute'         : '0',
            'carModelPlatFlag'               : '0',
            'bIInsureFlag'                   : 1,
            'cIInsureFlag'                   : 1,
            'bizType'                        : 'PROPOSAL',
            'prpBatchVehicle.carId'          : '',
            'ciInsureDemandCheckCIVo.flag'   : 'DEMAND',
            'ciInsureDemandCheckVo.flag'     : 'DEMAND',
            'prpCinsureds[0].insuredNature'  : '3',
            'isNetFlag'                      : '1',

            'prpCmainCommonCI.netsales'      : '1',
            'prpCmainCommonCI.marketFeeRate' : context.ciVOList?.prpCfixations?.marketFeeRate,
            'prpCmainCommonCI.saleFeeRateMax': context.ciVOList?.prpCfixations?.saleFeeRateMax,
            'prpCinsureds[0].insuredCode'    : '1100100005551042',
            'prpCinsureds[0].auditStatus'    : '2',
            'prpCinsureds[0].auditStatusDes' : '审批通过',
            //救助基金比率
            'prpCmainCar.rescueFundRate'     : context.ciVOList?.ciInsureDemand?.rescueFundRate,
            //救助基金金额
            'prpCmainCar.resureFundFee'      : context.ciVOList?.ciInsureDemand?.resureFundFee,
            'prpCitemKindCI.dutyFlag'        : '2',
            'prpCitemKindCI.kindName'        : '机动车交通事故强制责任保险',
            'prpCitemKindCI.id.itemKindNo'   : '',
            'prpCitemKindCI.kindCode'        : '050100',
            'prpCitemKindCI.amount'          : '122000',
            'prpCitemKindCI.benchMarkPremium': ciInsureDemand?.basePremium,
            'prpCitemKindCI.familyNo'        : isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage) ? baseInfo.'prpCitemKindCI.familyNo' : null,
            'prpCmainCI.sumPremium'          : ciInsureDemand?.premium,
            'prpCitemKindCI.premium'         : ciInsureDemand?.premium,
            'prpCitemKindCI.netPremium'      : context.ciVOList?.prpCitemKinds?.netPremium,
            'prpCitemKindCI.taxPremium'      : context.ciVOList?.prpCitemKinds?.taxPremium,
            'prpCitemKindCI.taxRate'         : context.ciVOList?.prpCitemKinds?.taxRate,
            'prpCitemKindCI.allTaxFee'       : context.ciVOList?.prpCitemKinds?.allTaxFee,
            'prpCmain.dmFlag'                : '7'
        ] + prpCitemKindsTemp + prpCprofitDetailsSizeTemp + prpCprofitFactorsTemp + prpCfixationTemp + prpCcommissionsTemp + prpDdismantleDetails + [
            'qualificationNo': context.qualificationNo
        ] + context.angentInfo + baseInfo.subMap([
            'prpCmainCommon.clauseIssue',
            'riskCode',
            'prpCmain.proposalNo',
            'prpCmain.comCode',
            'prpCmain.handler1Code',
            'prpCmain.businessNature',
            'agentCode',
            'prpCmain.operateDate',
            'Today',
            'prpCmain.makeCom',
            'prpCmain.startHour',
            'prpCmain.endHour',
            'prpCmainCI.startHour',
            'prpCmainCI.endHour',
            'prpCitemCar.id.itemNo',
            'idCardCheckInfo[0].flag',
            'prpCitemKindCI.id.itemKindNo',
            'prpCitemKindCI.kindCode',
            'prpCitemKindCI.flag',
            'prpCitemKindCI.calculateFlag',
            'prpCitemKindCI.clauseCode',
            'prpCitemKindCI.disCount',
            'prpCcarShipTax.id.itemNo',
            'prpCmain.handlerCode',
            'prpCmain.projectCode',
            'prpCinsureds[0].id.serialNo',
            'prpCinsureds[0].insuredFlag',
            'comCode',
            'operatorCode',
            'operatorName',
            'prpCmain.agentCode',
            'prpCmain.operatorCode',
            'prpCmainagentName',
            'prpCmainagentName',
            'queryCarModelInfo',
            'quotationRisk',
            'userCode',
            'userType',
        ])
    }

    static final _INSERT_RPG = { context ->
        Auto auto = context.auto
        def baseInfo = context.baseInfo
        def selectedCarModel = context.selectedCarModel
        def ciInsureDemand = context.ciVOList?.ciInsureDemand
        def ciCarShipTax = context.ciVOList?.ciCarShipTax
        def ciInsureTax = context.ciVOList?.ciInsureTax  // 车船税再ciCarShipTax没取到，从这个对象取
        def (biStartDateText, biEndDateText) = getCommercialInsurancePeriodTexts(context) //商业险起保日期
        def (ciStartDateText, ciEndDateText) = getCompulsoryInsurancePeriodTexts(context) //交强险起保日期
        def (stTaxStartDate, stTaxEndDate) = getTaxDate(null) //车船税起止时间  12月31日报下一年的结果
        def prpCitemKindsTemp = [:]
        def prpCprofitDetailsSizeTemp = [:]
        def prpCprofitFactorsTemp = [:] //优惠
        def prpCfixationTemp = [:]
        def transferFlag = context?.additionalParameters?.supplementInfo?.transferDate // 是否是过户车
        def transferDate = transferFlag ? _DATE_FORMAT3.format(context.additionalParameters.supplementInfo?.transferDate) : null  // 过户日期

        if (context.biVoList) {
            //将list转换成map型
            prpCitemKindsTemp = context.biVoList?.prpCitemKinds?.withIndex()?.collectEntries { bizParams, index ->
                def kindItemConverter = _KIND_ITEM_CONVERTER_MAPPINGS[bizParams.kindCode]
                (bizParams.subMap([
                    'kindCode',
                    'amount',
                    'benchMarkPremium',
                    'premium',
                    'modeCode',
                    'rate',
                    'unitAmount',
                    'quantity',
                    'pureRiskPremium',
                    'netPremium',
                    'taxPremium',
                    'allNetPremium'
                ]) + [
                    chooseFlag   : 'on',
                    calculateFlag: kindItemConverter.calculateFlag,
                    clauseCode   : kindItemConverter.clauseCode,
                    flag         : kindItemConverter.flag,
                    kindName     : kindItemConverter.name
                ]).collectEntries { key, value ->
                    [
                        "prpCitemKindsTemp[${index}].${key}": value
                    ]
                }
            }

            def prpCitemKinds = context.biVoList?.prpCitemKinds // 获取折扣信息

            prpCitemKinds.prpCprofits.prpCprofitDetails.flatten().withIndex().collect { prpCprofit, tempIndex ->
                prpCprofitDetailsSizeTemp << [
                    "prpCprofitDetailsTemp[$tempIndex].chooseFlag"   : 'on',
                    "prpCprofitDetailsTemp[$tempIndex].profitName"   : prpCprofit?.profitName,
                    "prpCprofitDetailsTemp[$tempIndex].condition"    : prpCprofit?.condition,
                    "prpCprofitDetailsTemp[$tempIndex].profitRate"   : prpCprofit?.profitRate,
                    "prpCprofitDetailsTemp[$tempIndex].profitRateMin": prpCprofit?.profitRateMin,
                    "prpCprofitDetailsTemp[$tempIndex].profitRateMax": prpCprofit?.profitRateMax,
                    "prpCprofitDetailsTemp[$tempIndex].id.proposalNo": prpCprofit?.id?.proposalNo,
                    "prpCprofitDetailsTemp[$tempIndex].id.itemKindNo": prpCprofit?.id?.itemKindNo,
                    "prpCprofitDetailsTemp[$tempIndex].id.profitCode": prpCprofit?.id?.profitCode,
                    "prpCprofitDetailsTemp[$tempIndex].id.serialNo"  : prpCprofit?.id?.serialNo,
                    "prpCprofitDetailsTemp[$tempIndex].id.profitType": prpCprofit?.id?.profitType,
                    "prpCprofitDetailsTemp[$tempIndex].kindCode"     : prpCprofit?.kindCode,
                    "prpCprofitDetailsTemp[$tempIndex].conditionCode": prpCprofit?.conditionCode,
                    "prpCprofitDetailsTemp[$tempIndex].flag"         : prpCprofit?.flag,
                ]
            }

            prpCprofitFactorsTemp = context.biVoList?.prpCprofitFactors?.withIndex()?.collectEntries { bizParams, index ->
                bizParams['id'].subMap([
                    'profitCode',
                    'conditionCode'
                ]).keySet().collectEntries { key ->
                    [
                        "prpCprofitFactorsTemp[${index}].id.${key}": bizParams.id[key],
                    ]
                } + [
                    "prpCprofitFactorsTemp[${index}].chooseFlag": 'on',
                    "prpCprofitFactorsTemp[${index}].rate"      : bizParams.rate,
                    "prpCprofitFactorsTemp[${index}].profitName": bizParams.profitName,
                    "prpCprofitFactorsTemp[${index}].condition" : bizParams.condition,
                    "prpCprofitFactorsTemp[${index}].lowerRate" : bizParams.lowerRate,
                    "prpCprofitFactorsTemp[${index}].upperRate" : bizParams.upperRate,
                    "prpCprofitFactorsTemp[${index}].flag"      : bizParams.flag,
                ]
            }

            prpCfixationTemp = context.biVoList?.prpCfixations?.withIndex()?.collectEntries { prpCfixation, index ->
                prpCfixation['id'].subMap([
                    'riskCode'
                ]).collectEntries { key, value ->
                    [
                        "prpCfixationTemp.id.${key}": value,
                    ]
                } + prpCfixation.subMap([
                    'riskCode',
                    'discount',
                    'profits',
                    'cost',
                    'taxorAppend',
                    'payMentR',
                    'basePayMentR',
                    'poundAge',
                    'basePremium',
                    'riskPremium',
                    'riskSumPremium',
                    'signPremium',
                    'isQuotation',
                    'riskClass',
                    'operationInfo',
                    'realDisCount',
                    'realProfits',
                    'realPayMentR',
                    'remark',
                    'responseCode',
                    'errorMessage',
                    'profitClass',
                    'costRate',
                    'unstandDiscount',
                    'targetPayMentr',
                    'targetPoundage',
                    'targetProfitsClass',
                    'pricingModel',
                    'uuid',
                    'basePayRateRCar',
                    'score',
                    'paymentUpper',
                    'marketCostRateUpper',
                    'marketCostRateLower',
                    'carMarketCostRate',
                    'marketCostRate',
                    'discountBI',
                    'poundageBI',
                    'discountCI',
                    'poundageCI',
                    'discountNew',
                    'poundageNew',
                    'premiumNew',
                    'saleFeeRateMax',
                    'marketFeeRate'
                ]).collectEntries { key, value ->
                    [
                        "prpCfixationTemp.${key}": value
                    ]
                }
            }
        }
        [
            'isNetFlag'                     : '1',
            'prpCmain.riskCode'             : 'DAA',
            'isBICI'                        : getBICIFlag(context),
            'ciStartDate'                   : ciStartDateText,
            'ciStartHour'                   : '0',
            'ciStartMinute'                 : '0',
            'ciEndDate'                     : ciEndDateText,
            'ciEndHour'                     : '24',
            'ciEndMinute'                   : '0',
            'biStartDate'                   : biStartDateText,
            'qualificationName'             : baseInfo?.prpCmainagentName,
            'qualificationNo'               : context.qualificationNo,
            'queryArea'                     : context.cityName,
            'prpCcarShipTax.model'          : 'A01',
            'prpCcarShipTax.taxPayerName'   : auto.owner,
            'prpCcarShipTax.taxType'        : '1',  //交税形式(1：正常缴税)
            'prpCitemCar.carId'             : '',
            'prpCitemCar.carKindCode'       : 'A01', //车辆种类
            'prpCitemCar.clauseType'        : 'F42', //机动车综合条款（家庭自用汽车产品）
            'prpCitemCar.newCarFlag'        : '0',   // 新旧车标志
            'prpCitemCar.noNlocalFlag'      : '0',   //是否为外地车？1或0
            'prpCmainCar.agreeDriverFlag'   : '0',
            'prpCmainCar.newDeviceFlag'     : '0',
            'prpCitemCar.vinNo'             : auto.vinNo,
            'prpCitemCar.useNatureCode'     : '211', //家庭自用汽车
            'prpCitemCar.licenseType'       : '02',
            'prpCitemCar.engineNo'          : auto.engineNo,
            'prpCitemCar.licenseNo'         : auto.licensePlateNo,
            'prpCitemCar.frameNo'           : auto.vinNo,
            'prpCitemCar.enrollDate'        : getEnrollDateText(context),
            'prpCitemCar.useYears'          : getYearsUntil(getEnrollDate(context)) ? getYearsUntil(getEnrollDate(context)) : 1, //这里可能有雷 (确实有雷，错误 #13603遇到一辆一年内的车，我们算出来的使用年限是0，折扣较低，官网传的是1，折扣较高，导致报价不一致)
            'prpCitemCar.fuelType'          : 'A', //'A'汽油
            'prpCitemCar.modelDemandNo'     : selectedCarModel.id?.pmQueryNo,
            'prpCitemCar.seatCount'         : getCarSeat(context),
            'prpCitemCar.purchasePrice'     : selectedCarModel.priceT,
            'prpCitemCar.brandName'         : selectedCarModel.vehicleName,
            'prpCitemCar.modelCode'         : selectedCarModel.vehicleId,
            'prpCitemCar.actualValue'       : context.carActualValue,
            //以下几个与代理有关
            'prpCmain.coinsFlag'            : '00', //'00':非联非共
            'prpCmain.startDate'            : biStartDateText,
            'prpCmain.startHour'            : '0',
            'prpCmain.startMinute'          : '0',

            'prpCmain.endDate'              : biEndDateText,
            'prpCmain.endHour'              : '24',
            'prpCmain.endMinute'            : '0',
            'prpCmain.operatorCode'         : baseInfo?.operatorCode ?: 'A410100744',
            'prpCmainCommon.queryArea'      : context.provinceCode,
            'prpCinsureds[0].insuredType'   : '1', //1：个人，2：团体
            'prpCinsureds[0].identifyType'  : '01',
            'prpCinsureds[0].insuredName'   : auto.owner,
            'prpCmainCI.endDate'            : ciEndDateText,
            'prpCmainCI.endHour'            : '24',
            'prpCmainCI.endMinute'          : '0',

            'prpCmainCI.startDate'          : ciStartDateText,
            'prpCmainCI.startHour'          : '0',
            'prpCmainCI.startMinute'        : '0',
            'prpCitemKindCI.amount'         : '122000', // TODO : 确认该值能否从前面的步骤中拿到
            'prpCitemCar.monopolyFlag'      : '0', //是否推荐送修 1 是 0 否
            'carModelPlatFlag'              : '0',
            'monopolyConfigsCount'          : '0',
            'prpCitemCar.id.itemNo'         : '1',
            //GuangdongSysFlag : 0,

            // baseInfo
            'is4SFlag'                      : '',
            'comCode'                       : '41015300',
            'agentCode'                     : '110041100036',
            'bIInsureFlag'                  : 1,
            'cIInsureFlag'                  : 1,
            'bizType'                       : 'PROPOSAL',
            'useYear'                       : 9,
            'riskCode'                      : 'DAA',
            'operatorCode'                  : '',
            'queryCarModelInfo'             : '',
            'prpCmainagentName'             : '北京天道保险经纪有限责任公司',
            'prpBatchVehicle.carId'         : '',
            'prpCcarShipTax.id.itemNo'      : 1,
            'prpCcarShipTax.taxComCode'     : '',
            'prpCitemCar.runMiles'          : '',
            'prpCitemCar.carProofNo'        : '',
            'prpCitemCar.carInsuredRelation': '1', // 被保险人关系代码
            'prpCmain.agentCode'            : '110041100036',
            'prpCmain.businessNature'       : '4',
            'prpCmain.comCode'              : '41015305',
            'prpCmain.makeCom'              : '41015303',
            'prpCmain.handler1Code'         : '84132478',
            'prpCmain.handlerCode'          : '84132478',
            'prpCmain.operateDate'          : today(),
            'prpCmainCar.carCheckStatus'    : '1', //验车情况
            'prpCmainCar.carChecker'        : 'A410100744',//验车人
            'prpCmainCar.carCheckTime'      : today(),
            'prpCmain.projectCode'          : '',
            'prpCmainCommon.clauseIssue'    : '2',
            'prpCinsureds[0].id.serialNo'   : '1',
            'prpCinsureds[0].insuredFlag'   : '11100000000000000000000000000A',
            'prpCitemKindCI.calculateFlag'  : 'Y',
            'prpCitemKindCI.clauseCode'     : '050001',
            'prpCitemKindCI.disCount'       : '1',
            'prpCitemKindCI.dutyFlag'       : '',
            'prpCitemKindCI.flag'           : '',
            'prpCitemKindCI.id.itemKindNo'  : '',
            'prpCitemKindCI.kindCode'       : '050100',
            'ciInsureDemandCheckCIVo.flag'  : 'DEMAND',
            'ciInsureDemandCheckVo.flag'    : 'DEMAND',
            'Today'                         : today(),
            'prpCmain.proposalNo'           : '',
            'idCardCheckInfo[0].flag'       : '',

            //agentInfo
            'quotationRisk'                 : 'PUB',
            'userCode'                      : 'A410100744',
            'userType'                      : '02',
            'prpCmain.argueSolution'        : '1',
            'prpCremarks_[0].operatorCode'  : 'A410100744',
            'operatorName'                  : '杨军辉',
            'makeComDes'                    : '郑州市分公司中介业务部中介业务二部',
            'handlerCodeDes'                : '张瑞霞',
            'handler1CodeDes'               : '张瑞霞',
            'comCodeDes'                    : '郑州市分公司中介业务部中介业务一部',
            'businessNatureTranslation'     : '经纪业务'
        ] + [
            'prpCmainCommon.netsales'        : '1', // 保单类型：1:电子保单， 0：纸质监制保单
            'prpCmain.argueSolution'         : '1', // 合同争议方式：1：诉讼，2：仲裁。仲裁无法报价
            'editType'                       : context.renewable ? 'RENEWAL' : 'NEW',
            'isBICI'                         : getBICIFlag(context),
            'agentType'                      : baseInfo?.agentType ?: '41102B',
            //车辆信息
            'prpCitemCar.exhaustScale'       : selectedCarModel.vehicleExhaust, // 排量
            'prpCitemCar.carKindCode'        : 'A01',
            'prpCmain.sumPremium1'           : context.newQuoteRecord.premium + (ciInsureDemand?.premium ?: 0), // 总保费
            'prpCitemCar.licenseNo'          : auto.licensePlateNo,
            'prpCitemCar.useNatureCode'      : '211', // 使用性质：家庭自用汽车
            'prpCitemCar.clauseType'         : 'F42', // 机动车综合条款：家庭自用汽车产品
            'prpCitemCar.runAreaCode'        : '11',  // 行驶区域：中华人民共和国境内(不含港澳台)
            'prpCitemCar.modelCode'          : selectedCarModel.vehicleId,
            'prpCitemCar.brandName'          : selectedCarModel.vehicleName,
            'prpCitemCar.seatCount'          : getCarSeat(context),
            'prpCitemCar.carLotEquQuality'   : selectedCarModel.vehicleQuality * 1000, // 质量
            'prpCitemKindCI.familyNo'        : isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage) ? '1' : '',
            'prpCinsureds[0].insuredType'    : '1', //1：个人，2：团体
            'prpCinsureds[0].identifyType'   : '01',
            'prpCinsureds[0].insuredName'    : auto.owner,
            'prpCinsureds[0].identifyNumber' : auto.identity,
            'prpCinsureds[0].insuredNature'  : '3',
            'prpCinsureds[0].versionNo'      : '2',
            'prpCinsureds[0].countryCode'    : 'CHN',
            'prpCitemCar.actualValue'        : context.carActualValue,
            'prpCitemCar.enrollDate'         : getEnrollDateText(context),
            // TODO 初登日期与官网抓取的不一致会导致使用年限不一致，北京不需要输入初登日期，需要输入初登日期的其他地区此处可能会出现问题
            'prpCitemCar.useYears'           : getYearsUntil(getEnrollDate(context)) ? getYearsUntil(getEnrollDate(context)) : 1, //这里可能有雷 (确实有雷，错误 #13603遇到一辆一年内的车，我们算出来的使用年限是0，折扣较低，官网传的是1，折扣较高，导致报价不一致)
            'prpCmain.startDate'             : biStartDateText,
            'prpCmain.endDate'               : biEndDateText,
            'prpCmainCI.startDate'           : ciStartDateText,
            'prpCmainCI.endDate'             : ciEndDateText,
            'prpCitemCar.licenseType'        : '02', // 号牌种类
            'prpCmainCar.agreeDriverFlag'    : '0',
            'prpCitemKind.shortRate'         : '100',
            'prpCitemKind.shortRateFlag'     : '2', // 计费方式：按日
            'prpCitemKindCI.benchMarkPremium': ciInsureDemand?.basePremium,
            'prpCmainCI.sumPremium'          : ciInsureDemand?.premium,
            'prpCitemKindCI.premium'         : ciInsureDemand?.premium,
            'prpCmain.sumPremium'            : context.newQuoteRecord.premium,
            'prpCcarShipTax.thisPayTax'      : ciInsureTax?.sumTax ?: ciCarShipTax?.thisPayTax, // todo 此处取值可能不对, ciCarShipTax这个对象没有值，官网参数传了这个值 ciCarShipTax?.thisPayTax,
            'prpCcarShipTax.prePayTax'       : ciCarShipTax?.prePayTax,
            'prpCcarShipTax.delayPayTax'     : ciCarShipTax?.delayPayTax,
            'prpCcarShipTax.sumPayTax'       : ciInsureTax?.sumTax ?: ciCarShipTax?.sumPayTax, // todo  ciCarShipTax?.sumPayTax,
            'prpCcarShipTax.taxUnitAmount'   : ciInsureTax?.ciInsureAnnualTaxes?.size() > 0 ? ciInsureTax?.ciInsureAnnualTaxes?.first()?.unitRate : 0, //todo  取第一个没有依据，报价结果返回了一个list 只有一个元素
            'prpCcarShipTax.taxUnit'         : '辆/年',
            'prpCcarShipTax.payStartDate'    : stTaxStartDate,
            'prpCcarShipTax.payEndDate'      : stTaxEndDate,
            'prpCcarShipTax.carLotEquQuality': context.vehiclePMCheckResult?.wholeWeight ?: selectedCarModel.vehicleQuality * 1000,
            'BIdemandNo'                     : context.biVoList?.ciInsureDemandDAA?.demandNo,
            'prpCitemCarExt.lastDamagedCI'   : context.biVoList?.prpCitemCarExt?.lastDamagedCI,
            'prpCitemCarExt_CI.rateRloatFlag': '01',
            'ciInsureDemand.demandNo'        : ciInsureDemand?.demandNo,
            'prpCplanTemps[0].payNo'         : '1',
            'prpCplanTemps[0].payReason'     : 'R29',
            'prpCplanTemps[0].planFee'       : ciInsureDemand?.premium,
            'prpCplanTemps[0].isBICI'        : 'CI',
            'prpCplanTemps[0].planDate'      : ciStartDateText,
            'prpCplanTemps[1].payNo'         : '1',
            'prpCplanTemps[1].payReason'     : 'RM9', // 本期加往期车船税
            'prpCplanTemps[1].currency'      : 'CNY',
            'prpCplanTemps[1].planFee'       : ciInsureTax?.sumTax ?: 0, // todo 当前只测试了没有滞纳金的车辆 有滞纳金的情况需要确定此金额是否正确 (ciCarShipTax?.thisPayTax ?: 0) + (ciCarShipTax?.prePayTax ?: 0),
            'prpCplanTemps[1].isBICI'        : 'CShip',
            'prpCplanTemps[1].planDate'      : ciStartDateText,
            'prpCplanTemps[2].payNo'         : '1',
            'prpCplanTemps[2].payReason'     : 'RM8', // 车船税滞纳金
            'prpCplanTemps[2].currency'      : 'CNY',
            'prpCplanTemps[2].planFee'       : ciCarShipTax?.delayPayTax,
            'prpCplanTemps[2].isBICI'        : 'CShip',
            'prpCplanTemps[2].planDate'      : ciStartDateText,
            'prpCplanTemps[3].payNo'         : '1',
            'prpCplanTemps[3].payReason'     : 'R21',
            'prpCplanTemps[3].planFee'       : context.newQuoteRecord.premium,
            'prpCplanTemps[3].isBICI'        : 'BI',
            'prpCplanTemps[3].planDate'      : biStartDateText,

            //需要验车的险种必须带这2参数，否则会出现与官网核保意见不一致的问题
            'prpCitemCarExt.noDamYearsBI'    : context.biVoList?.prpCitemCarExt?.noDamYearsBI,
            'prpCitemCarExt_CI.noDamYearsCI' : context.biVoList?.prpCitemCarExt?.noDamYearsCI,

            'prpCitemCar.transferVehicleFlag': transferFlag ? '1' : '0', // 过户车标志
            'prpCitemCar.transferDate'       : transferDate,

            'prpCitemCar.licenseColorCode'   : '01',// 号牌颜色
            'LicenseColorCodeDes'            : '蓝',

        ] + prpCitemKindsTemp + prpCprofitDetailsSizeTemp + prpCprofitFactorsTemp + [
            'qualificationNo': context.qualificationNo
        ] + prpCfixationTemp
    }

    static final _INSERT_RPG_320100 = { context ->
        Auto auto = context.auto
        def baseInfo = context.baseInfo
        def selectedCarModel = context.selectedCarModel
        def ciInsureDemand = context.ciVOList?.ciInsureDemand
        def ciInsureTax = context.ciVOList?.ciInsureTax
        def ciCarShipTax = context.ciVOList?.ciCarShipTax
        def (biStartDateText, biEndDateText) = getCommercialInsurancePeriodTexts(context) //商业险起保日期
        def (ciStartDateText, ciEndDateText) = getCompulsoryInsurancePeriodTexts(context) //交强险起保日期
        def (stTaxStartDate, stTaxEndDate) = getTaxDate(null) //车船税起止时间
        def prpCitemKindsTemp = [:]
        def prpCprofitDetailsSizeTemp = [:]  // 折扣 prpCprofitDetailsTemp
        def prpCprofitFactorsTemp = [:] //优惠
        def prpCfixationTemp = [:] // 南京商业险
        def transferFlag = context?.additionalParameters?.supplementInfo?.transferDate // 是否是过户车
        def transferDate = transferFlag ? _DATE_FORMAT3.format(context.additionalParameters.supplementInfo?.transferDate) : null  // 过户日期


        if (context.biVoList) {
            //将list转换成map型
            prpCitemKindsTemp = context.biVoList?.prpCitemKinds?.withIndex()?.collectEntries { bizParams, index ->
                def kindItemConverter = _KIND_ITEM_CONVERTER_MAPPINGS[bizParams.kindCode]
                (bizParams.subMap([
                    'kindCode',
                    'amount',
                    'benchMarkPremium',
                    'premium',
                    'modeCode',
                    'rate',
                    'unitAmount',
                    'quantity',
                    'pureRiskPremium',
                    'netPremium',
                    'taxPremium',
                    'allNetPremium'
                ]) + [
                    chooseFlag   : 'on',
                    calculateFlag: kindItemConverter.calculateFlag,
                    clauseCode   : kindItemConverter.clauseCode,
                    flag         : kindItemConverter.flag,
                    kindName     : kindItemConverter.name
                ]).collectEntries { key, value ->
                    [
                        "prpCitemKindsTemp[${index}].${key}": value
                    ]
                }
            }


            def prpCitemKinds = context.biVoList?.prpCitemKinds // 获取折扣信息

            prpCitemKinds.prpCprofits.prpCprofitDetails.flatten().withIndex().collect { prpCprofit, tempIndex ->
                prpCprofitDetailsSizeTemp << [
                    "prpCprofitDetailsTemp[$tempIndex].chooseFlag"   : 'on',
                    "prpCprofitDetailsTemp[$tempIndex].profitName"   : prpCprofit?.profitName,
                    "prpCprofitDetailsTemp[$tempIndex].condition"    : prpCprofit?.condition,
                    "prpCprofitDetailsTemp[$tempIndex].profitRate"   : prpCprofit?.profitRate,
                    "prpCprofitDetailsTemp[$tempIndex].profitRateMin": prpCprofit?.profitRateMin,
                    "prpCprofitDetailsTemp[$tempIndex].profitRateMax": prpCprofit?.profitRateMax,
                    "prpCprofitDetailsTemp[$tempIndex].id.proposalNo": prpCprofit?.id?.proposalNo,
                    "prpCprofitDetailsTemp[$tempIndex].id.itemKindNo": prpCprofit?.id?.itemKindNo,
                    "prpCprofitDetailsTemp[$tempIndex].id.profitCode": prpCprofit?.id?.profitCode,
                    "prpCprofitDetailsTemp[$tempIndex].id.serialNo"  : prpCprofit?.id?.serialNo,
                    "prpCprofitDetailsTemp[$tempIndex].id.profitType": prpCprofit?.id?.profitType,
                    "prpCprofitDetailsTemp[$tempIndex].kindCode"     : prpCprofit?.kindCode,
                    "prpCprofitDetailsTemp[$tempIndex].conditionCode": prpCprofit?.conditionCode,
                    "prpCprofitDetailsTemp[$tempIndex].flag"         : prpCprofit?.flag,
                ]
            }

            prpCprofitFactorsTemp = context.biVoList?.prpCprofitFactors?.withIndex()?.collectEntries { bizParams, index ->
                bizParams['id'].subMap([
                    'profitCode',
                    'conditionCode'
                ]).keySet().collectEntries { key ->
                    [
                        "prpCprofitFactorsTemp[${index}].id.${key}": bizParams.id[key],
                    ]
                } + [
                    "prpCprofitFactorsTemp[${index}].chooseFlag": 'on',
                    "prpCprofitFactorsTemp[${index}].rate"      : bizParams.rate,
                    "prpCprofitFactorsTemp[${index}].profitName": bizParams.profitName,
                    "prpCprofitFactorsTemp[${index}].condition" : bizParams.condition,
                    "prpCprofitFactorsTemp[${index}].lowerRate" : bizParams.lowerRate,
                    "prpCprofitFactorsTemp[${index}].upperRate" : bizParams.upperRate,
                    "prpCprofitFactorsTemp[${index}].flag"      : bizParams.flag,
                ]
            }

            prpCfixationTemp = context.biVoList?.prpCfixations?.withIndex()?.collectEntries { prpCfixation, index ->
                prpCfixation['id'].subMap([
                    'riskCode'

                ]).collectEntries { key, value ->
                    [
                        "prpCfixationTemp.id.${key}": value,
                    ]
                } +
                    prpCfixation.subMap([
                        'riskCode',
                        'discount',
                        'profits',
                        'cost',
                        'taxorAppend',
                        'payMentR',
                        'basePayMentR',
                        'poundAge',
                        'basePremium',
                        'riskPremium',
                        'riskSumPremium',
                        'signPremium',
                        'isQuotation',
                        'riskClass',
                        'operationInfo',
                        'realDisCount',
                        'realProfits',
                        'realPayMentR',
                        'remark',
                        'responseCode',
                        'errorMessage',
                        'profitClass',
                        'costRate',
                        'unstandDiscount',
                        'targetPayMentr',
                        'targetPoundage',
                        'targetProfitsClass',
                        'pricingModel',
                        'uuid',
                        'basePayRateRCar',
                        'score',
                        'paymentUpper',
                        'marketCostRateUpper',
                        'marketCostRateLower',
                        'carMarketCostRate',
                        'marketCostRate',
                        'discountBI',
                        'poundageBI',
                        'discountCI',
                        'poundageCI',
                        'discountNew',
                        'poundageNew',
                        'premiumNew',
                        'saleFeeRateMax',
                        'marketFeeRate'

                    ]).collectEntries { key, value ->
                        [
                            "prpCfixationTemp.${key}": value
                        ]
                    }

            }
        }
        [
            'isNetFlag'                     : '1',
            'prpCmain.riskCode'             : 'DAA',
            'isBICI'                        : getBICIFlag(context),
            'ciStartDate'                   : ciStartDateText,
            'ciStartHour'                   : '0',
            'ciStartMinute'                 : '0',
            'ciEndDate'                     : ciEndDateText,
            'ciEndHour'                     : '24',
            'ciEndMinute'                   : '0',
            'biStartDate'                   : biStartDateText,
            'qualificationName'             : baseInfo?.prpCmainagentName,
            'qualificationNo'               : context.qualificationNo,
            'queryArea'                     : context.cityName,
            'prpCcarShipTax.model'          : 'B11',
            'prpCcarShipTax.taxPayerName'   : auto.owner,
            'prpCcarShipTax.taxType'        : '1',  //交税形式(1：正常缴税)
            'prpCitemCar.carId'             : '',
            'prpCitemCar.carKindCode'       : 'A01', //车辆种类
            'prpCitemCar.clauseType'        : 'F42', //机动车综合条款（家庭自用汽车产品）
            'prpCitemCar.newCarFlag'        : '0',   // 新旧车标志
            'prpCitemCar.noNlocalFlag'      : '0',   //是否为外地车？1或0
            'prpCmainCar.agreeDriverFlag'   : '0',
            'prpCmainCar.newDeviceFlag'     : '0',
            'prpCitemCar.vinNo'             : auto.vinNo,
            'prpCitemCar.useNatureCode'     : '211', //家庭自用汽车
            'prpCitemCar.licenseType'       : '02',
            'prpCitemCar.engineNo'          : auto.engineNo,
            'prpCitemCar.licenseNo'         : auto.licensePlateNo,
            'prpCitemCar.frameNo'           : auto.vinNo,
            'prpCitemCar.enrollDate'        : getEnrollDateText(context),
            'prpCitemCar.useYears'          : getYearsUntil(getEnrollDate(context)) ? getYearsUntil(getEnrollDate(context)) : 1, //这里可能有雷 (确实有雷，错误 #13603遇到一辆一年内的车，我们算出来的使用年限是0，折扣较低，官网传的是1，折扣较高，导致报价不一致)
            'prpCitemCar.fuelType'          : 'A', //'A'汽油
            'prpCitemCar.modelDemandNo'     : selectedCarModel.id?.pmQueryNo,
            'prpCitemCar.seatCount'         : context.vehiclePMCheckResult?.seatCount ?: getCarSeat(context), //南京以交管查车座位数为准
            'prpCitemCar.purchasePrice'     : selectedCarModel.priceT,
            'prpCitemCar.brandName'         : selectedCarModel.vehicleName,
            'prpCitemCar.modelCode'         : selectedCarModel.vehicleId,
            'prpCitemCar.actualValue'       : context.carActualValue,
            //以下几个与代理有关
            'prpCmain.coinsFlag'            : '00', //'00':非联非共
            'prpCmain.startDate'            : biStartDateText,
            'prpCmain.startHour'            : '0',
            'prpCmain.startMinute'          : '0',

            'prpCmain.endDate'              : biEndDateText,
            'prpCmain.endHour'              : '24',
            'prpCmain.endMinute'            : '0',
            'prpCmain.operatorCode'         : '3201062096',
            'prpCmainCommon.queryArea'      : context.provinceCode,
            'prpCinsureds[0].insuredType'   : '1', //1：个人，2：团体
            'prpCinsureds[0].identifyType'  : '01',
            'prpCinsureds[0].insuredName'   : auto.owner,
            'prpCmainCI.endDate'            : ciEndDateText,
            'prpCmainCI.endHour'            : '24',
            'prpCmainCI.endMinute'          : '0',

            'prpCmainCI.startDate'          : ciStartDateText,
            'prpCmainCI.startHour'          : '0',
            'prpCmainCI.startMinute'        : '0',
            'prpCitemKindCI.amount'         : '122000', // TODO : 确认该值能否从前面的步骤中拿到
            'prpCitemCar.monopolyFlag'      : '0', //是否推荐送修 1 是 0 否
            'carModelPlatFlag'              : '0',
            'monopolyConfigsCount'          : '0',
            'prpCitemCar.id.itemNo'         : '1',
            //GuangdongSysFlag : 0,

            // baseInfo
            'is4SFlag'                      : '',
            'comCode'                       : '32010602',
            'agentCode'                     : '320021101475',
            'bIInsureFlag'                  : 1,
            'cIInsureFlag'                  : 1,
            'bizType'                       : 'PROPOSAL',
            'useYear'                       : 9,
            'riskCode'                      : 'DAA',
            'operatorCode'                  : '',
            'queryCarModelInfo'             : '',
            'prpCmainagentName'             : '江苏恒生保险销售有限公司洪泽县分公司',
            'prpBatchVehicle.carId'         : '',
            'prpCcarShipTax.id.itemNo'      : 1,
            'prpCcarShipTax.taxComCode'     : '',
            'prpCitemCar.runMiles'          : '',
            'prpCitemCar.carProofNo'        : '',
            'prpCitemCar.carInsuredRelation': '1',
            'prpCmain.agentCode'            : '320021101475',
            'prpCmain.businessNature'       : '4',
            'prpCmain.comCode'              : '32010602',
            'prpCmain.makeCom'              : '32010600',
            'prpCmain.handler1Code'         : '12137614',
            'prpCmain.handlerCode'          : '12137614',
            'prpCmain.operateDate'          : today(),
            'prpCmainCar.carCheckStatus'    : '1', //验车情况
            'prpCmainCar.carChecker'        : '3201062097',//验车人
            'carCheckerTranslate'           : '伟妮',
            'prpCmainCar.carCheckTime'      : today(),
            'prpCmain.projectCode'          : '',
            'prpCmainCommon.clauseIssue'    : '2',
            'prpCinsureds[0].id.serialNo'   : '1',
            'prpCinsureds[0].insuredFlag'   : '11100000000000000000000000000A',
            'prpCitemKindCI.calculateFlag'  : 'Y',
            'prpCitemKindCI.clauseCode'     : '050001',
            'prpCitemKindCI.disCount'       : '1',
            'prpCitemKindCI.dutyFlag'       : '2',
            'prpCitemKindCI.flag'           : '',
            'prpCitemKindCI.id.itemKindNo'  : '',
            'prpCitemKindCI.kindCode'       : '050100',
            'prpCitemKindCI.kindName'       : '机动车交通事故强制责任保险',
            'ciInsureDemandCheckCIVo.flag'  : 'DEMAND',
            'ciInsureDemandCheckVo.flag'    : 'DEMAND',
            'Today'                         : today(),
            'prpCmain.proposalNo'           : '',
            'idCardCheckInfo[0].flag'       : '',

            //agentInfo
            'quotationRisk'                 : 'DAA',
            'userCode'                      : '3201062096',
            'userType'                      : '02',
            'prpCmain.argueSolution'        : '1',
            'prpCremarks_[0].operatorCode'  : '3201062096',
            'operatorName'                  : '范芳',
            'makeComDes'                    : '南京市城北支公司',
            'handlerCodeDes'                : '伟妮',
            'handler1CodeDes'               : '伟妮',
            'comCodeDes'                    : '南京市城北支公司综合部',
            'businessNatureTranslation'     : '经纪业务'
        ] + [
            'prpCmainCommon.netsales'        : '1', // 保单类型：1:电子保单， 0：纸质监制保单
            'prpCmain.argueSolution'         : '1', // 合同争议方式：1：诉讼，2：仲裁。仲裁无法报价
            'editType'                       : context.renewable ? 'RENEWAL' : 'NEW',

            'agentType'                      : '2110ZA',
            //车辆信息
            'prpCitemCar.carKindCode'        : 'A01',
            'prpCmain.sumPremium1'           : context.newQuoteRecord.premium + (ciInsureDemand?.premium ?: 0), // 总保费
            'prpCitemCar.licenseNo'          : auto.licensePlateNo,
            'prpCitemCar.useNatureCode'      : '211', // 使用性质：家庭自用汽车
            'prpCitemCar.clauseType'         : 'F42', // 机动车综合条款：家庭自用汽车产品
            'prpCitemCar.runAreaCode'        : '11',  // 行驶区域：中华人民共和国境内(不含港澳台)
            'prpCitemCar.modelCode'          : selectedCarModel.vehicleId,
            'prpCitemCar.brandName'          : selectedCarModel.vehicleName,
            'prpCitemCar.seatCount'          : context.vehiclePMCheckResult?.seatCount ?: getCarSeat(context), //南京以交管查车座位数为准,
            'prpCitemCar.exhaustScale'       : context.vehiclePMCheckResult?.displacement ?: selectedCarModel.vehicleExhaust, // 排量wholeWeight
            'prpCitemCar.carLotEquQuality'   : context.vehiclePMCheckResult?.wholeWeight ?: selectedCarModel.vehicleQuality * 1000, // 质量
            'prpCitemKindCI.familyNo'        : isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage) ? '1' : '',
            'prpCinsureds[0].id.serialNo'    : '1',
            'prpCinsureds[0].insuredFlag'    : '11100000000000000000000000000A',
            'prpCinsureds[0].insuredType'    : '1', //1：个人，2：团体
            'prpCinsureds[0].identifyType'   : '01',
            'prpCinsureds[0].insuredName'    : auto.owner,
            'prpCinsureds[0].identifyNumber' : auto.identity,
            'prpCinsureds[0].insuredNature'  : '3',
            'prpCinsureds[0].versionNo'      : '2',
            'prpCinsureds[0].countryCode'    : 'CHN',
            'prpCitemCar.actualValue'        : context.carActualValue,
            'prpCitemCar.enrollDate'         : getEnrollDateText(context),
            // TODO 初登日期与官网抓取的不一致会导致使用年限不一致，北京不需要输入初登日期，需要输入初登日期的其他地区此处可能会出现问题
            'prpCitemCar.useYears'           : getYearsUntil(getEnrollDate(context)) ? getYearsUntil(getEnrollDate(context)) : 1, //这里可能有雷 (确实有雷，错误 #13603遇到一辆一年内的车，我们算出来的使用年限是0，折扣较低，官网传的是1，折扣较高，导致报价不一致)
            'prpCmain.startDate'             : biStartDateText,
            'prpCmain.endDate'               : biEndDateText,
            'prpCmainCI.startDate'           : ciStartDateText,
            'prpCmainCI.endDate'             : ciEndDateText,
            'prpCitemCar.licenseType'        : '02', // 号牌种类
            'prpCmainCar.agreeDriverFlag'    : '0',
            'prpCitemKind.shortRate'         : '100',
            'prpCitemKind.shortRateFlag'     : '2', // 计费方式：按日
            'prpCitemKindCI.benchMarkPremium': ciInsureDemand?.basePremium,
            'prpCmainCI.sumPremium'          : ciInsureDemand?.premium,
            'prpCitemKindCI.premium'         : ciInsureDemand?.premium,
            'prpCmain.sumPremium'            : context.newQuoteRecord.premium,
            'prpCcarShipTax.thisPayTax'      : ciInsureTax?.sumTax ?: ciCarShipTax?.thisPayTax, // todo 此处取值可能不对, ciCarShipTax这个对象没有值，官网参数传了这个值 ciCarShipTax?.thisPayTax,
            'prpCcarShipTax.prePayTax'       : ciCarShipTax?.prePayTax,
            'prpCcarShipTax.delayPayTax'     : ciCarShipTax?.delayPayTax,
            'prpCcarShipTax.sumPayTax'       : ciInsureTax?.sumTax ?: ciCarShipTax?.sumPayTax, // todo  ciCarShipTax?.sumPayTax,
            'prpCcarShipTax.taxUnitAmount'   : ciInsureTax?.ciInsureAnnualTaxes?.size() > 0 ? ciInsureTax?.ciInsureAnnualTaxes?.first()?.unitRate : 0, //todo  取第一个没有依据，报价结果返回了一个list 只有一个元素
            'prpCcarShipTax.taxUnit'         : '辆/年',
            'prpCcarShipTax.payStartDate'    : stTaxStartDate,
            'prpCcarShipTax.payEndDate'      : stTaxEndDate,
            'prpCcarShipTax.carLotEquQuality': context.vehiclePMCheckResult?.wholeWeight ?: selectedCarModel.vehicleQuality * 1000,
            'BIdemandNo'                     : context.biVoList?.ciInsureDemandDAA?.demandNo,
            'prpCitemCarExt.lastDamagedCI'   : context.biVoList?.prpCitemCarExt?.lastDamagedCI,
            'prpCitemCarExt_CI.rateRloatFlag': '01',
            'ciInsureDemand.demandNo'        : ciInsureDemand?.demandNo,
            'prpCplanTemps[0].payNo'         : '1',
            'prpCplanTemps[0].payReason'     : 'R29',
            'prpCplanTemps[0].planFee'       : ciInsureDemand?.premium,
            'prpCplanTemps[0].isBICI'        : 'CI',
            'prpCplanTemps[0].planDate'      : ciStartDateText,
            'prpCplanTemps[1].payNo'         : '1',
            'prpCplanTemps[1].payReason'     : 'RM9', // 本期加往期车船税
            'prpCplanTemps[1].currency'      : 'CNY',
            'prpCplanTemps[1].planFee'       : ciInsureTax?.sumTax ?: 0, // todo 当前只测试了没有滞纳金的车辆 有滞纳金的情况需要确定此金额是否正确 (ciCarShipTax?.thisPayTax ?: 0) + (ciCarShipTax?.prePayTax ?: 0),
            'prpCplanTemps[1].isBICI'        : 'CShip',
            'prpCplanTemps[1].planDate'      : ciStartDateText,
            'prpCplanTemps[2].payNo'         : '1',
            'prpCplanTemps[2].payReason'     : 'RM8', // 车船税滞纳金
            'prpCplanTemps[2].currency'      : 'CNY',
            'prpCplanTemps[2].planFee'       : ciCarShipTax?.delayPayTax,
            'prpCplanTemps[2].isBICI'        : 'CShip',
            'prpCplanTemps[2].planDate'      : ciStartDateText,
            'prpCplanTemps[3].payNo'         : '1',
            'prpCplanTemps[3].payReason'     : 'R21',
            'prpCplanTemps[3].planFee'       : context.newQuoteRecord.premium,
            'prpCplanTemps[3].isBICI'        : 'BI',
            'prpCplanTemps[3].planDate'      : biStartDateText,
            'prpCitemCar.licenseColorCode'   : '01',// 号牌颜色
            'LicenseColorCodeDes'            : '蓝',
            'prpCitemCar.transferVehicleFlag': transferFlag ? '1' : '0', // 过户车标志
            'prpCitemCar.transferDate'       : transferDate
        ] + prpCitemKindsTemp + prpCprofitDetailsSizeTemp + prpCprofitFactorsTemp + [
            'qualificationNo'               : context.qualificationNo,
            'prpCengageTemps[0].id.serialNo': '1',
            'prpCengageTemps[0].clauseCode' : '993055',
            'prpCengageTemps[0].clauseName' : '条款告知特约',
            'clauses[0]'                    : '保险人已向投保人详细介绍并提供了投保险种所使用的条款',
            'prpCengageTemps[0].engageFlag' : '0',
            'prpCengageTemps[0].clauses'    : '保险人已向投保人详细介绍并提供了投保险种所使用的条款',
            'prpCengageTemps[1].id.serialNo': '2',
            'prpCengageTemps[1].clauseCode' : '9902751',
            'prpCengageTemps[1].clauseName' : '江苏防范电信诈骗提示',
            'clauses[1]'                    : '保险公司不会让您在ATM机上进行任何操作，凡是要求到ATM机上进行所谓收款操作的，均系诈骗行为，必要时及时报警！',
            'prpCengageTemps[1].engageFlag' : '0',
            'prpCengageTemps[1].clauses'    : '保险公司不会让您在ATM机上进行任何操作，凡是要求到ATM机上进行所谓收款操作的，均系诈骗行为，必要时及时报警！',
            'prpCengageTemps[2].id.serialNo': '3',
            'prpCengageTemps[2].clauseCode' : '9913381',
            'prpCengageTemps[2].clauseName' : '3200-保险咨询投诉电话特别约定',
            'clauses[2]'                    : '人保财险投诉监督电话95518；江苏保险纠纷投诉处理中心电话4008012378。',
            'prpCengageTemps[2].engageFlag' : '0',
            'prpCengageTemps[2].clauses'    : '人保财险投诉监督电话95518；江苏保险纠纷投诉处理中心电话4008012378。',


        ] + prpCfixationTemp
    }

    static final _QUERY_PAY_FOR_RPG = { context ->
        Auto auto = context.auto
        def baseInfo = context.baseInfo
        def selectedCarModel = context.selectedCarModel
        def ciInsureDemand = context.ciVOList?.ciInsureDemand
        def ciCarShipTax = context.ciVOList?.ciCarShipTax
        def prpCitemKindCI = context.ciVOList?.prpCitemKinds?.first()
        def (biStartDateText, biEndDateText) = getCommercialInsurancePeriodTexts(context) //商业险起保日期
        def (ciStartDateText, ciEndDateText) = getCompulsoryInsurancePeriodTexts(context) //交强险起保日期
        def prpCitemKindsTemp = [:]
        def prpCprofitDetailsSizeTemp = [:]
        def prpCprofitFactorsTemp = [:]
        def prpCfixationTemp = [:]
        def prpCsaless = [:] //销售费用，涉及手续费比率
        if (context.biVoList) {
            //将list转换成map型
            prpCitemKindsTemp = context.biVoList?.prpCitemKinds?.withIndex()?.collectEntries { bizParams, index ->
                def kindItemConverter = _KIND_ITEM_CONVERTER_MAPPINGS[bizParams.kindCode]
                (bizParams.subMap([
                    'kindCode',
                    'amount',
                    'benchMarkPremium',
                    'premium',
                    'modeCode',
                    'rate',
                    'unitAmount',
                    'quantity',
                    'pureRiskPremium',
                    'netPremium',
                    'taxPremium',
                    'allNetPremium',
                ]) + [
                    chooseFlag   : 'on',
                    calculateFlag: kindItemConverter.calculateFlag,
                    clauseCode   : kindItemConverter.clauseCode,
                    flag         : kindItemConverter.flag,
                    kindName     : kindItemConverter.name,
                ]).collectEntries { key, value ->
                    [
                        "prpCitemKindsTemp[${index}].${key}": value
                    ]
                }
            }

            def prpCitemKinds = context.biVoList?.prpCitemKinds // 获取折扣信息

            prpCitemKinds.prpCprofits.prpCprofitDetails.flatten().withIndex().collect { prpCprofit, tempIndex ->
                prpCprofitDetailsSizeTemp << [
                    "prpCprofitDetailsTemp[$tempIndex].chooseFlag"   : 'on',
                    "prpCprofitDetailsTemp[$tempIndex].profitName"   : prpCprofit?.profitName,
                    "prpCprofitDetailsTemp[$tempIndex].condition"    : prpCprofit?.condition,
                    "prpCprofitDetailsTemp[$tempIndex].profitRate"   : prpCprofit?.profitRate,
                    "prpCprofitDetailsTemp[$tempIndex].profitRateMin": prpCprofit?.profitRateMin,
                    "prpCprofitDetailsTemp[$tempIndex].profitRateMax": prpCprofit?.profitRateMax,
                    "prpCprofitDetailsTemp[$tempIndex].id.proposalNo": prpCprofit?.id?.proposalNo,
                    "prpCprofitDetailsTemp[$tempIndex].id.itemKindNo": prpCprofit?.id?.itemKindNo,
                    "prpCprofitDetailsTemp[$tempIndex].id.profitCode": prpCprofit?.id?.profitCode,
                    "prpCprofitDetailsTemp[$tempIndex].id.serialNo"  : prpCprofit?.id?.serialNo,
                    "prpCprofitDetailsTemp[$tempIndex].id.profitType": prpCprofit?.id?.profitType,
                    "prpCprofitDetailsTemp[$tempIndex].kindCode"     : prpCprofit?.kindCode,
                    "prpCprofitDetailsTemp[$tempIndex].conditionCode": prpCprofit?.conditionCode,
                    "prpCprofitDetailsTemp[$tempIndex].flag"         : prpCprofit?.flag,
                ]
            }

            prpCprofitFactorsTemp = context.biVoList?.prpCprofitFactors?.withIndex()?.collectEntries { bizParams, index ->
                bizParams['id'].subMap([
                    'profitCode',
                    'conditionCode'
                ]).collectEntries { key, value ->
                    [
                        "prpCprofitFactorsTemp[${index}].id.${key}" : value,
                        "prpCprofitFactorsTemp[${index}].chooseFlag": 'on',
                        "prpCprofitFactorsTemp[${index}].rate"      : bizParams.rate,
                        "prpCprofitFactorsTemp[${index}].profitName": bizParams.profitName,
                        "prpCprofitFactorsTemp[${index}].condition" : bizParams.condition,
                        "prpCprofitFactorsTemp[${index}].lowerRate" : bizParams.lowerRate,
                        "prpCprofitFactorsTemp[${index}].upperRate" : bizParams.upperRate,
                        "prpCprofitFactorsTemp[${index}].flag"      : bizParams.flag,
                    ]
                }
            }

            prpCfixationTemp = context.biVoList?.prpCfixations?.withIndex()?.collectEntries { prpCfixation, index ->
                prpCfixation['id'].subMap([
                    'riskCode'
                ]).collectEntries { key, value ->
                    [
                        "prpCfixationTemp.id.${key}": value,
                    ]
                } + prpCfixation.subMap([
                    'riskCode',
                    'discount',
                    'profits',
                    'cost',
                    'taxorAppend',
                    'payMentR',
                    'basePayMentR',
                    'poundAge',
                    'basePremium',
                    'riskPremium',
                    'riskSumPremium',
                    'signPremium',
                    'isQuotation',
                    'riskClass',
                    'operationInfo',
                    'realDisCount',
                    'realProfits',
                    'realPayMentR',
                    'remark',
                    'responseCode',
                    'errorMessage',
                    'profitClass',
                    'costRate',
                    'unstandDiscount',
                    'targetPayMentr',
                    'targetPoundage',
                    'targetProfitsClass',
                    'pricingModel',
                    'uuid',
                    'basePayRateRCar',
                    'score',
                    'paymentUpper',
                    'marketCostRateUpper',
                    'marketCostRateLower',
                    'carMarketCostRate',
                    'marketCostRate',
                    'discountBI',
                    'poundageBI',
                    'discountCI',
                    'poundageCI',
                    'discountNew',
                    'poundageNew',
                    'premiumNew',
                    'saleFeeRateMax',
                    'marketFeeRate'
                ]).collectEntries { key, value ->
                    [
                        "prpCfixationTemp.${key}": value
                    ]
                }
            }
        }

        if (context.prpCsaless) {
            context.prpCsaless.withIndex()?.each { item, index ->
                prpCsaless << [
                    "prpCsaless[${index}].salesDetailName"   : item.salesDetailName,
                    "prpCsaless[${index}].riskCode"          : item.riskCode,
                    "prpCsaless[${index}].splitRate"         : item.splitRate,
                    "prpCsaless[${index}].oriSplitNumber"    : item.oriSplitNumber,
                    "prpCsaless[${index}].splitFee"          : item.splitFee,
                    "prpCsaless[${index}].agreementNo"       : item.agreementNo,
                    "prpCsaless[${index}].id.salesCode"      : item.id?.salesCode,
                    "prpCsaless[${index}].salesName"         : item.salesName,
                    "prpCsaless[${index}].id.proposalNo"     : item.id?.proposalNo,
                    "prpCsaless[${index}].id.salesDetailCode": item.id?.salesDetailCode,
                    "prpCsaless[${index}].totalRate"         : item.totalRate,
                    "prpCsaless[${index}].splitWay"          : item.splitWay,
                    "prpCsaless[${index}].totalRateMax"      : item.totalRateMax,
                    "prpCsaless[${index}].flag"              : item.flag,
                    "prpCsaless[${index}].remark"            : item.remark,
                ]
            }
        }

        [
            'prpCmainCommon.netsales'        : '1', // 保单类型：1:电子保单， 0：纸质监制保单
            'prpCmain.argueSolution'         : '1', // 合同争议方式：1：诉讼，2：仲裁。仲裁无法报价
            'isNetProp'                      : '1',  // 投保形式(1：电子投保)
            'editType'                       : context.renewable ? 'RENEWAL' : 'NEW',
            'isBICI'                         : getBICIFlag(context),
            'agentType'                      : baseInfo.agentType ?: '41101Y',
            'prpCitemCar.carKindCode'        : 'A01',
            'prpCmain.sumPremium1'           : context.newQuoteRecord.premium + (ciInsureDemand?.premium ?: 0), // 总保费
            'prpCitemCar.licenseNo'          : auto.licensePlateNo,
            'prpCitemCar.useNatureCode'      : '211', // 使用性质：家庭自用汽车
            'prpCitemCar.clauseType'         : 'F42', // 机动车综合条款：家庭自用汽车产品
            'prpCitemCar.runAreaCode'        : '11',  // 行驶区域：中华人民共和国境内(不含港澳台)
            'prpCitemCar.modelCode'          : selectedCarModel.vehicleId,
            'prpCitemCar.brandName'          : selectedCarModel.vehicleName,
            'prpCitemCar.seatCount'          : getCarSeat(context),
            'prpCitemKindCI.familyNo'        : isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage) ? baseInfo.'prpCitemKindCI.familyNo' : null,
            'prpCinsureds[0].insuredType'    : '1', //1：个人，2：团体
            'prpCinsureds[0].identifyType'   : '01',
            'prpCinsureds[0].insuredName'    : auto.owner,
            'prpCinsureds[0].identifyNumber' : auto.identity,
            'prpCinsureds[0].email'          : randomEmail,
            'prpCinsuredsview[0].mobile'     : randomMobile,
            'prpCinsureds[0].mobile'         : randomMobile,
            'prpCinsureds[0].insuredAddress' : context.cityName,
            'prpCinsureds[0].versionNo'      : '2',
            'prpCinsureds[0].countryCode'    : 'CHN',
            'prpCitemCar.actualValue'        : context.carActualValue,
            'prpCitemCar.enrollDate'         : getEnrollDateText(context),
            // TODO 初登日期与官网抓取的不一致会导致使用年限不一致，北京不需要输入初登日期，需要输入初登日期的其他地区此处可能会出现问题
            'prpCitemCar.useYears'           : getYearsUntil(getEnrollDate(context)) ? getYearsUntil(getEnrollDate(context)) : 1, //这里可能有雷 (确实有雷，错误 #13603遇到一辆一年内的车，我们算出来的使用年限是0，折扣较低，官网传的是1，折扣较高，导致报价不一致)
            'prpCmain.startDate'             : biStartDateText,
            'prpCmain.endDate'               : biEndDateText,
            'prpCmainCI.startDate'           : ciStartDateText,
            'prpCmainCI.endDate'             : ciEndDateText,
            'prpCitemCar.licenseType'        : '02', // 号牌种类
            'prpCmainCar.agreeDriverFlag'    : '0',
            'prpCitemKind.shortRate'         : '100',
            'prpCitemKind.shortRateFlag'     : '2', // 计费方式：按日
            'prpCitemKindCI.benchMarkPremium': ciInsureDemand?.basePremium,
            'prpCmainCI.sumPremium'          : ciInsureDemand?.premium,
            'prpCitemKindCI.premium'         : ciInsureDemand?.premium,
            'prpCmain.sumPremium'            : context.newQuoteRecord.premium,
            'prpCcarShipTax.thisPayTax'      : ciCarShipTax?.thisPayTax,
            'prpCcarShipTax.prePayTax'       : ciCarShipTax?.prePayTax,
            'prpCcarShipTax.delayPayTax'     : ciCarShipTax?.delayPayTax,
            'BIdemandNo'                     : context.biVoList?.ciInsureDemandDAA?.demandNo,
            'prpCitemCarExt.lastDamagedCI'   : context.biVoList?.prpCitemCarExt?.lastDamagedCI,
            'prpCitemCarExt_CI.rateRloatFlag': '01',
            'ciInsureDemand.demandNo'        : ciInsureDemand?.demandNo,

            'prpCitemCar.carLotEquQuality'   : selectedCarModel.vehicleQuality * 1000, // 质量
            'prpCitemCar.exhaustScale'       : selectedCarModel.vehicleExhaust, // 排量
            'prpCitemCar.carProofNo'         : '',
            'prpCitemCar.carInsuredRelation' : '1', // 被保险人关系代码
            'prpCitemCar.id.itemNo'          : '1',
            'prpCitemCar.monopolyFlag'       : '0', //是否推荐送修 1 是 0 否
            'prpCitemCar.fuelType'           : 'A', //'A'汽油
            'prpCitemCar.modelDemandNo'      : selectedCarModel.id?.pmQueryNo,
            'prpCitemCar.purchasePrice'      : selectedCarModel.priceT,
            'prpCitemCar.vinNo'              : auto.vinNo,
            'prpCitemCar.engineNo'           : auto.engineNo,
            'prpCitemCar.frameNo'            : auto.vinNo,
            'prpCitemCar.carId'              : '',
            'prpCitemCar.newCarFlag'         : '0',   // 新旧车标志
            'prpCitemCar.noNlocalFlag'       : '0',   //是否为外地车？1或0
            'prpCitemCar.licenseColorCode'   : '01',// 号牌颜色
            'LicenseColorCodeDes'            : '蓝',
            'prpCmainCommon.queryArea'       : '110000',
            'useYear'                        : 9,
            'prpCmain.riskCode'              : 'DAA',
            'carShipTaxPlatFormFlag'         : '1',

            'prpCplanTemps[0].payNo'         : '1',
            'prpCplanTemps[0].payReason'     : 'R29',
            'prpCplanTemps[0].planFee'       : ciInsureDemand?.premium,
            'prpCplanTemps[0].isBICI'        : 'CI',
            'prpCplanTemps[0].planDate'      : ciStartDateText,
            'prpCplanTemps[1].payNo'         : '1',
            'prpCplanTemps[1].payReason'     : 'RM9', // 本期加往期车船税
            'prpCplanTemps[1].currency'      : 'CNY',
            'prpCplanTemps[1].planFee'       : (ciCarShipTax?.thisPayTax ?: 0) + (ciCarShipTax?.prePayTax ?: 0),
            'prpCplanTemps[1].isBICI'        : 'CShip',
            'prpCplanTemps[1].planDate'      : ciStartDateText,
            'prpCplanTemps[2].payNo'         : '1',
            'prpCplanTemps[2].payReason'     : 'RM8', // 车船税滞纳金
            'prpCplanTemps[2].currency'      : 'CNY',
            'prpCplanTemps[2].planFee'       : ciCarShipTax?.delayPayTax,
            'prpCplanTemps[2].isBICI'        : 'CShip',
            'prpCplanTemps[2].planDate'      : ciStartDateText,
            'prpCplanTemps[3].payNo'         : '1',
            'prpCplanTemps[3].payReason'     : 'R21',
            'prpCplanTemps[3].planFee'       : context.newQuoteRecord.premium,
            'prpCplanTemps[3].isBICI'        : 'BI',
            'prpCplanTemps[3].planDate'      : biStartDateText,

            'prpCitemKindCI.amount'          : '122000',
            'prpCitemKindCI.allTaxFee'       : prpCitemKindCI?.allTaxFee,
            'prpCitemKindCI.kindName'        : '机动车交通事故强制责任保险',
            'prpCitemKindCI.allNetPremium'   : prpCitemKindCI?.allNetPremium,
            'prpCitemKindCI.netPremium'      : prpCitemKindCI?.netPremium,
            'prpCitemKindCI.taxPremium'      : prpCitemKindCI?.taxPremium,
            'prpCitemKindCI.taxRate'         : prpCitemKindCI?.taxRate,
            'prpCitemKindCI.deductible'      : prpCitemKindCI?.deductible,
            'prpCitemKindCI.adjustRate'      : prpCitemKindCI?.adjustRate,
            'prpCitemKindCI.rate'            : prpCitemKindCI?.rate,
            'prpCitemKindCI.riskPremium'     : prpCitemKindCI?.riskPremium,
            'prpCitemKindCI.unitAmount'      : prpCitemKindCI?.unitAmount,
            'prpCitemKindCI.shortRate'       : prpCitemKindCI?.shortRate,
        ] + prpCitemKindsTemp + prpCprofitDetailsSizeTemp + prpCprofitFactorsTemp + prpCfixationTemp + prpCsaless + [
            'qualificationNo': context.qualificationNo
        ] + context.angentInfo + baseInfo.subMap([
            'prpCmainCommon.clauseIssue',
            'riskCode',
            'prpCmain.proposalNo',
            'prpCmain.comCode',
            'prpCmain.handler1Code',
            'prpCmain.businessNature',
            'agentCode',
            'prpCmain.operateDate',
            'Today',
            'prpCmain.makeCom',
            'prpCmain.startHour',
            'prpCmain.endHour',
            'prpCmainCI.startHour',
            'prpCmainCI.endHour',
            'prpCitemCar.id.itemNo',
            'idCardCheckInfo[0].flag',
            'prpCitemKindCI.id.itemKindNo',
            'prpCitemKindCI.kindCode',
            'prpCitemKindCI.flag',
            'prpCitemKindCI.calculateFlag',
            'prpCitemKindCI.clauseCode',
            'prpCitemKindCI.disCount',
            'prpCitemKindCI.dutyFlag',
            'prpCitemKindCI.familyNo',
            'prpCcarShipTax.id.itemNo',
            'prpCmain.handlerCode',
            'prpCmain.projectCode',
            'prpCinsureds[0].id.serialNo',
            'prpCinsureds[0].insuredFlag',
            'comCode',
            'operatorCode',
            'operatorName',
            'prpCmain.agentCode',
            'prpCmain.operatorCode',
            'prpCmainagentName',
            'prpCmainagentName',
            'queryCarModelInfo',
            'quotationRisk',
            'userCode',
            'userType',
        ])
    }

    /**
     * 保存收款信息
     */
    static final _SAVE_JF_RPG_BASE = { payType, context ->
        def workbenchCertiNo = context.processNo  // 报价单号
        [
            workbenchCertiNo         : workbenchCertiNo,
            worekbenchUserComCode    : context.worekbenchUserComCode,
            workbenchUserCode        : context.workbenchUserCode,
            strCertiNoMap            : context.strCertiNoMap,
            issh                     : 0,
            saveFlag                 : 'FALSE',
            icCardCheck              : '0',
            pointButtonCheck         : true,
            isShuangLu               : 'isFalse',
            'prpJfPayExch.currency'  : 'CNY',
            'prpJfPayExch.registDate': today(),
            'prpJfPayExch.payState'  : 1,
            modifyFlag               : 0,
            addPayFeeFlag            : 0,
            serverTime               : today(),
            initFlag                 : '16SOCKET',
            payMsgQrcodeFlag         : 0,
            payMsgQrcodeSwitch       : 0,
            certiNo_uiBEdit          : workbenchCertiNo,
            payTypeNo                : payType,   //'wechat'微信 郑州,   wechatpublic  微信公众号 南京
            pointactivitybutton      : 'off',
            jumpToPage               : 1,
            unionflag_               : '0000000000'
        ]
    }

    /**
     * 北京地区保存收款信息
     */
    static final _SAVE_JF_RPG_110000 = { context ->
        def workbenchCertiNo = context.processNo  // 报价单号
        [
            workbenchCertiNo         : workbenchCertiNo,
            worekbenchUserComCode    : context.worekbenchUserComCode,
            workbenchUserCode        : context.workbenchUserCode,
            strCertiNoMap            : context.strCertiNoMap,
            issh                     : 0,
            saveFlag                 : 'FALSE',
            icCardCheck              : '0',
            pointButtonCheck         : true,
            isShuangLu               : 'isFalse',
            'prpJfPayExch.currency'  : 'CNY',
            'prpJfPayExch.registDate': today(),
            'prpJfPayExch.payState'  : 1,
            modifyFlag               : 0,
            addPayFeeFlag            : 0,
            serverTime               : today(),
            initFlag                 : '16SOCKET',
            payMsgQrcodeFlag         : 0,
            payMsgQrcodeSwitch       : 0,
            certiNo_uiBEdit          : workbenchCertiNo,
            payTypeNo                : context.order?.channel == AGENT_PARSER_ALIPAY_62 ? 'alipay' : 'wechat',
            pointactivitybutton      : 'off',
            jumpToPage               : 1,
            unionflag_               : '0000000000'
        ]
    }


    static final _SAVE_JF_RPG_510100 = { context ->
        def workbenchCertiNo = context.processNo  // 报价单号
        [
            workbenchCertiNo         : workbenchCertiNo,
            worekbenchUserComCode    : context.worekbenchUserComCode,
            workbenchUserCode        : context.workbenchUserCode,
            strCertiNoMap            : context.strCertiNoMap,
            issh                     : 0,
            saveFlag                 : 'FALSE',
            icCardCheck              : '0',
            pointButtonCheck         : true,
            isShuangLu               : 'isFalse',
            'prpJfPayExch.currency'  : 'CNY',
            'prpJfPayExch.registDate': today(),
            'prpJfPayExch.payState'  : 1,
            modifyFlag               : 0,
            addPayFeeFlag            : 0,
            serverTime               : today(),
            initFlag                 : '16SOCKET',
            payMsgQrcodeFlag         : 0,
            payMsgQrcodeSwitch       : 0,
            certiNo_uiBEdit          : workbenchCertiNo,
            payTypeNo                : context.order?.channel == AGENT_PARSER_ALIPAY_62 ? 'alipay' : 'wechat',
            //支付宝只能起保日期15天内的
            pointactivitybutton      : 'off',
            jumpToPage               : 1,
            unionflag_               : '0000000000'
        ]
    }


    //当前郑州只支持微信支付，南京只支持微信公众号支付 2018-08-02
    static final _SAVE_JF_RPG_410100 = _SAVE_JF_RPG_BASE.curry 'wechat'
    static final _SAVE_JF_RPG_320100 = _SAVE_JF_RPG_BASE.curry 'wechatpublic'

    static final _EDIT_PAY_FEE_BY_WECHAT_ADD_RPG_BASE = { context ->
        [
            exchangeNo: context.exchangeNo,
            wechatFlag: 'NEW',
            now       : new Date()
        ]
    }

    static final _EDIT_PAY_FEE_BY_WECHAT_ADD_320100 = { context ->
        [
            exchangeNo: context.exchangeNo,
            wechatFlag: 'NEW',
            payType   : 'PUBLIC',
            now       : new Date()
        ]
    }

    static final _EDIT_PAY_FEE_BY_ALIPAY_OR_WECHAT_ADD_110000 = { context ->
        [
            exchangeNo: context.exchangeNo,
            wechatFlag: 'NEW',
            payType   : context.order?.channel == AGENT_PARSER_ALIPAY_62 ? 'ALIPAY' : 'PUBLIC',
            now       : new Date()
        ]
    }

    static final _EDIT_PAY_FEE_BY_ALIPAY_OR_WECHAT_ADD_510100 = { context ->
        [
            exchangeNo: context.exchangeNo,
            wechatFlag: 'NEW',
            payType   : context.order?.channel == AGENT_PARSER_ALIPAY_62 ? 'ALIPAY' : 'PUBLIC',
            now       : new Date()
        ]
    }

    static final _INSERT_RPG_510100 = { context ->
        Auto auto = context.auto
        def baseInfo = context.baseInfo
        def selectedCarModel = context.selectedCarModel
        def ciInsureDemand = context.ciVOList?.ciInsureDemand
        def ciInsureTax = context.ciVOList?.ciInsureTax
        def ciCarShipTax = context.ciVOList?.ciCarShipTax
        def (biStartDateText, biEndDateText) = getCommercialInsurancePeriodTexts(context) //商业险起保日期
        def (ciStartDateText, ciEndDateText) = getCompulsoryInsurancePeriodTexts(context) //交强险起保日期
        def (stTaxStartDate, stTaxEndDate) = getTaxDate(null) //车船税起止时间
        def prpCitemKindsTemp = [:]
        def prpCprofitDetailsSizeTemp = [:]  // 折扣 prpCprofitDetailsTemp
        def prpCprofitFactorsTemp = [:] //优惠
        def prpCfixationTemp = [:] // 南京商业险
        def transferFlag = context?.additionalParameters?.supplementInfo?.transferDate // 是否是过户车
        def transferDate = transferFlag ? _DATE_FORMAT3.format(context.additionalParameters.supplementInfo?.transferDate) : null  // 过户日期

         if (context.biVoList) {
            //将list转换成map型
            prpCitemKindsTemp = context.biVoList?.prpCitemKinds?.withIndex()?.collectEntries { bizParams, index ->
                def kindItemConverter = _KIND_ITEM_CONVERTER_MAPPINGS[bizParams.kindCode]
                (bizParams.subMap([
                    'kindCode',
                    'amount',
                    'benchMarkPremium',
                    'premium',
                    'modeCode',
                    'rate',
                    'unitAmount',
                    'quantity',
                    'pureRiskPremium',
                    'netPremium',
                    'taxPremium',
                    'allNetPremium'
                ]) + [
                    chooseFlag   : 'on',
                    calculateFlag: kindItemConverter.calculateFlag,
                    clauseCode   : kindItemConverter.clauseCode,
                    flag         : kindItemConverter.flag,
                    kindName     : kindItemConverter.name
                ]).collectEntries { key, value ->
                    [
                        "prpCitemKindsTemp[${index}].${key}" : value
                    ]
                }
            }


            def prpCitemKinds = context.biVoList?.prpCitemKinds // 获取折扣信息

            prpCitemKinds.prpCprofits.prpCprofitDetails.flatten().withIndex().collect { prpCprofit, tempIndex ->
                prpCprofitDetailsSizeTemp << [
                    "prpCprofitDetailsTemp[$tempIndex].chooseFlag"   : 'on',
                    "prpCprofitDetailsTemp[$tempIndex].profitName"   : prpCprofit?.profitName,
                    "prpCprofitDetailsTemp[$tempIndex].condition"    : prpCprofit?.condition,
                    "prpCprofitDetailsTemp[$tempIndex].profitRate"   : prpCprofit?.profitRate,
                    "prpCprofitDetailsTemp[$tempIndex].profitRateMin": prpCprofit?.profitRateMin,
                    "prpCprofitDetailsTemp[$tempIndex].profitRateMax": prpCprofit?.profitRateMax,
                    "prpCprofitDetailsTemp[$tempIndex].id.proposalNo": prpCprofit?.id?.proposalNo,
                    "prpCprofitDetailsTemp[$tempIndex].id.itemKindNo": prpCprofit?.id?.itemKindNo,
                    "prpCprofitDetailsTemp[$tempIndex].id.profitCode": prpCprofit?.id?.profitCode,
                    "prpCprofitDetailsTemp[$tempIndex].id.serialNo"  : prpCprofit?.id?.serialNo,
                    "prpCprofitDetailsTemp[$tempIndex].id.profitType": prpCprofit?.id?.profitType,
                    "prpCprofitDetailsTemp[$tempIndex].kindCode"     : prpCprofit?.kindCode,
                    "prpCprofitDetailsTemp[$tempIndex].conditionCode": prpCprofit?.conditionCode,
                    "prpCprofitDetailsTemp[$tempIndex].flag"         : prpCprofit?.flag,
                ]
            }

            prpCprofitFactorsTemp = context.biVoList?.prpCprofitFactors?.withIndex()?.collectEntries { bizParams, index ->
                bizParams['id'].subMap([
                    'profitCode',
                    'conditionCode'
                ]).keySet().collectEntries { key ->
                    [
                        "prpCprofitFactorsTemp[${index}].id.${key}": bizParams.id[key],
                    ]
                } + [
                        "prpCprofitFactorsTemp[${index}].chooseFlag": 'on',
                        "prpCprofitFactorsTemp[${index}].rate"      : bizParams.rate,
                        "prpCprofitFactorsTemp[${index}].profitName": bizParams.profitName,
                        "prpCprofitFactorsTemp[${index}].condition" : bizParams.condition,
                        "prpCprofitFactorsTemp[${index}].lowerRate" : bizParams.lowerRate,
                        "prpCprofitFactorsTemp[${index}].upperRate" : bizParams.upperRate,
                        "prpCprofitFactorsTemp[${index}].flag"      : bizParams.flag,
                    ]
            }

            prpCfixationTemp = context.biVoList?.prpCfixations?.withIndex()?.collectEntries { prpCfixation, index ->
                prpCfixation['id'].subMap([
                    'riskCode'

                ]).collectEntries { key, value ->
                    [
                        "prpCfixationTemp.id.${key}": value,
                    ]
                } +
                    prpCfixation.subMap([
                        'riskCode',
                        'discount',
                        'profits',
                        'cost',
                        'taxorAppend',
                        'payMentR',
                        'basePayMentR',
                        'poundAge',
                        'basePremium',
                        'riskPremium',
                        'riskSumPremium',
                        'signPremium',
                        'isQuotation',
                        'riskClass',
                        'operationInfo',
                        'realDisCount',
                        'realProfits',
                        'realPayMentR',
                        'remark',
                        'responseCode',
                        'errorMessage',
                        'profitClass',
                        'costRate',
                        'unstandDiscount',
                        'targetPayMentr',
                        'targetPoundage',
                        'targetProfitsClass',
                        'pricingModel',
                        'uuid',
                        'basePayRateRCar',
                        'score',
                        'paymentUpper',
                        'marketCostRateUpper',
                        'marketCostRateLower',
                        'carMarketCostRate',
                        'marketCostRate',
                        'discountBI',
                        'poundageBI',
                        'discountCI',
                        'poundageCI',
                        'discountNew',
                        'poundageNew',
                        'premiumNew',
                        'saleFeeRateMax',
                        'marketFeeRate'

                    ]).collectEntries { key, value ->
                        [
                            "prpCfixationTemp.${key}": value
                        ]
                    }

            }
        }
        [
            'isNetFlag'                     : '1',
            'prpCmain.riskCode'             : 'DAA',
            'isBICI'                        : getBICIFlag(context),
            'ciStartDate'                   : ciStartDateText,
            'ciStartHour'                   : '0',
            'ciStartMinute'                 : '0',
            'ciEndDate'                     : ciEndDateText,
            'ciEndHour'                     : '24',
            'ciEndMinute'                   : '0',
            'biStartDate'                   : biStartDateText,
            'qualificationName'             : baseInfo?.prpCmainagentName,
            'qualificationNo'               : context.qualificationNo,
            'queryArea'                     : context.cityName,
            'prpCcarShipTax.model'          : 'B11',
            'prpCcarShipTax.taxPayerName'   : auto.owner,
            'prpCcarShipTax.taxType'        : '1',  //交税形式(1：正常缴税)
            'prpCitemCar.carId'             : '',
            'prpCitemCar.carKindCode'       : 'A01', //车辆种类
            'prpCitemCar.clauseType'        : 'F42', //机动车综合条款（家庭自用汽车产品）
            'prpCitemCar.newCarFlag'        : '0',   // 新旧车标志
            'prpCitemCar.noNlocalFlag'      : '0',   //是否为外地车？1或0
            'prpCmainCar.agreeDriverFlag'   : '0',
            'prpCmainCar.newDeviceFlag'     : '0',
            'prpCitemCar.vinNo'             : auto.vinNo,
            'prpCitemCar.useNatureCode'     : '211', //家庭自用汽车
            'prpCitemCar.licenseType'       : '02',   // 证件类型？
            'prpCitemCar.engineNo'          : auto.engineNo,
            'prpCitemCar.licenseNo'         : auto.licensePlateNo,
            'prpCitemCar.frameNo'           : auto.vinNo,
            'prpCitemCar.enrollDate'        : getEnrollDateText(context),
            'prpCitemCar.useYears'          : getYearsUntil(getEnrollDate(context)) ? getYearsUntil(getEnrollDate(context)) : 1, //这里可能有雷 (确实有雷，错误 #13603遇到一辆一年内的车，我们算出来的使用年限是0，折扣较低，官网传的是1，折扣较高，导致报价不一致)
            'prpCitemCar.fuelType'          : 'A', //'A'汽油  -------
            'prpCitemCar.modelDemandNo'     : selectedCarModel.id?.pmQueryNo,
            'prpCitemCar.seatCount'         : context.vehiclePMCheckResult?.seatCount ?: getCarSeat(context), //南京以交管查车座位数为准
            'prpCitemCar.purchasePrice'     : selectedCarModel.priceT,
            'prpCitemCar.brandName'         : selectedCarModel.vehicleName,
            'prpCitemCar.modelCode'         : selectedCarModel.vehicleId,
            'prpCitemCar.actualValue'       : context.carActualValue,
            //以下几个与代理有关
            'prpCmain.coinsFlag'            : '00', //'00':非联非共
            'prpCmain.startDate'            : biStartDateText,
            'prpCmain.startHour'            : '0',
            'prpCmain.startMinute'          : '0',

            'prpCmain.endDate'              : biEndDateText,
            'prpCmain.endHour'              : '24',
            'prpCmain.endMinute'            : '0',
            'prpCmain.operatorCode'         : context.workbenchUserCode,
            'prpCmainCommon.queryArea'      : context.provinceCode,

            'prpCmainCI.endDate'            : ciEndDateText,
            'prpCmainCI.endHour'            : '24',
            'prpCmainCI.endMinute'          : '0',

            'prpCmainCI.startDate'          : ciStartDateText,
            'prpCmainCI.startHour'          : '0',
            'prpCmainCI.startMinute'        : '0',
            'prpCitemKindCI.amount'         : '122000', // TODO : 确认该值能否从前面的步骤中拿到
            'prpCitemCar.monopolyFlag'      : '0', //是否推荐送修 1 是 0 否
            'carModelPlatFlag'              : '0',
            'monopolyConfigsCount'          : '0',
            'prpCitemCar.id.itemNo'         : '1',

            // baseInfo
            'is4SFlag'                      : '',
            'comCode'                       : context.worekbenchUserComCode,
            'agentCode'                     : '440021100120',
            'bIInsureFlag'                  : 1,
            'cIInsureFlag'                  : 1,
            'bizType'                       : 'PROPOSAL',
            'useYear'                       : 9,
            'riskCode'                      : 'DAA',
            'operatorCode'                  : '',
            'queryCarModelInfo'             : '',
            'prpCmainagentName'             : '成都市锦江支公司团体客户业务一部',
            'prpBatchVehicle.carId'         : '',
            'prpCcarShipTax.id.itemNo'      : 1,
            'prpCcarShipTax.taxComCode'     : '',
            'prpCitemCar.runMiles'          : '',
            'prpCitemCar.carProofNo'        : '',
            'prpCitemCar.carInsuredRelation': '1',  //guanxi
            'prpCmain.agentCode'            : '440021100120',
            'prpCmain.businessNature'       : '2',   //??????????
            'prpCmain.comCode'              : context.worekbenchUserComCode,
            'prpCmain.makeCom'              : context.worekbenchUserComCode,
            'prpCmain.handler1Code'         : '51620671',
            'prpCmain.handlerCode'          : '51620671',
            'prpCmain.operateDate'          : today(),
            'prpCmainCar.carCheckStatus'    : '1', //验车情况
            'prpCmainCar.carChecker'        : '3151132300',//验车人
            'carCheckerTranslate'           : '肖丹',  // 这个是测试数据 。 需要产品提供具体验车人
            'prpCmainCar.carCheckTime'      : today(),
            'prpCmain.projectCode'          : '',
            'prpCmainCommon.clauseIssue'    : '2',

            'prpCitemKindCI.calculateFlag'  : 'Y',
            'prpCitemKindCI.clauseCode'     : '050001',
            'prpCitemKindCI.disCount'       : '1',
            'prpCitemKindCI.dutyFlag'       : '',   //=======
            'prpCitemKindCI.flag'           : '',
            'prpCitemKindCI.id.itemKindNo'  : '',
            'prpCitemKindCI.kindCode'       : '050100',
            'prpCitemKindCI.kindName'       : '机动车交通事故强制责任保险',
            'ciInsureDemandCheckCIVo.flag'  : 'DEMAND',
            'ciInsureDemandCheckVo.flag'    : 'DEMAND',
            'Today'                         : today(),
            'prpCmain.proposalNo'           : '',
            'idCardCheckInfo[0].flag'       : '',

            //agentInfo
            'quotationRisk'                 : 'DAA',
            'userCode'                      : context.workbenchUserCode,
            'userType'                      : '02',
            'prpCmain.argueSolution'        : '1',
            'prpCremarks_[0].operatorCode'  : '',

            'operatorName'                  : '张勇',
            'makeComDes'                    : '成都市锦江支公司团体客户业务一部',
            'handlerCodeDes'                : '张勇',
            'handler1CodeDes'               : '张勇',
            'comCodeDes'                    : '成都市锦江支公司团体客户业务一部',
            'businessNatureTranslation'     : '专业代理业务'
        ] + [
            'prpCmainCommon.netsales'         : '1', // 保单类型：1:电子保单， 0：纸质监制保单
            'prpCmain.argueSolution'          : '1', // 合同争议方式：1：诉讼，2：仲裁。仲裁无法报价
            'editType'                        : context.renewable ? 'RENEWAL' : 'NEW',

            'agentType'                       : '2111VN',
            //车辆信息
            'prpCitemCar.carKindCode'         : 'A01',
            'prpCmain.sumPremium1'            : context.newQuoteRecord.premium + (ciInsureDemand?.premium ?: 0), // 总保费
            'prpCitemCar.licenseNo'           : auto.licensePlateNo,
            'prpCitemCar.useNatureCode'       : '211', // 使用性质：家庭自用汽车
            'prpCitemCar.clauseType'          : 'F42', // 机动车综合条款：家庭自用汽车产品
            'prpCitemCar.runAreaCode'         : '11',  // 行驶区域：中华人民共和国境内(不含港澳台)
            'prpCitemCar.modelCode'           : selectedCarModel.vehicleId,
            'prpCitemCar.brandName'           : selectedCarModel.vehicleName,
            'prpCitemCar.seatCount'           : getCarSeat(context), //南京以交管查车座位数为准,
            'prpCitemCar.exhaustScale'        : context.vehiclePMCheckResult?.displacement ?: selectedCarModel.vehicleExhaust, // 排量wholeWeight
            'prpCitemCar.carLotEquQuality'    : context.vehiclePMCheckResult?.wholeWeight ?: selectedCarModel.vehicleQuality * 1000, // 质量

            /////////被保人/////////////
            'prpCinsureds[0].insuredAddress'  : '成都',
            'prpCinsureds[0].versionNo'       : '2',
            'prpCinsureds[0].soldierRelations': '',
            'prpCinsureds[0].identifyNumber'  : auto.identity,
            'prpCinsureds[0].insuredName'     : auto.owner,
            'prpCinsureds[0].id.serialNo'     : '1',
            'prpCinsureds[0].insuredFlag'     : '11100000000000000000000000000A',
            'prpCitemKindCI.familyNo'         : isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage) ? '1' : '',
            'prpCinsureds[0].insuredType'     : '1', //1：个人，2：团体
            'prpCinsureds[0].identifyType'    : '01',
            'prpCinsureds[0].insuredNature'   : '3',
            'prpCinsureds[0].countryCode'     : 'CHN',
            'mobile[0]'                       : '13261682200',
            'prpCinsureds[0].mobile'          : 'YG|oAhC7D7gBUunmsy0XNCnapJE/pXk2BIQYo5akGVovvA=',
            /////////被保人/////////////


            'prpCitemCar.actualValue'         : context.carActualValue,
            'prpCitemCar.enrollDate'          : getEnrollDateText(context),
            // TODO 初登日期与官网抓取的不一致会导致使用年限不一致，北京不需要输入初登日期，需要输入初登日期的其他地区此处可能会出现问题
            'prpCitemCar.useYears'            : getYearsUntil(getEnrollDate(context)) ? getYearsUntil(getEnrollDate(context)) : 1, //这里可能有雷 (确实有雷，错误 #13603遇到一辆一年内的车，我们算出来的使用年限是0，折扣较低，官网传的是1，折扣较高，导致报价不一致)
            'prpCmain.startDate'              : biStartDateText,
            'prpCmain.endDate'                : biEndDateText,
            'prpCmainCI.startDate'            : ciStartDateText,
            'prpCmainCI.endDate'              : ciEndDateText,
            'prpCitemCar.licenseType'         : '02', // 号牌种类
            'prpCmainCar.agreeDriverFlag'     : '0',
            'prpCitemKind.shortRate'          : '100',
            'prpCitemKind.shortRateFlag'      : '2', // 计费方式：按日
            'prpCitemKindCI.benchMarkPremium' : ciInsureDemand?.basePremium,
            'prpCmainCI.sumPremium'           : ciInsureDemand?.premium,
            'prpCitemKindCI.premium'          : ciInsureDemand?.premium,
            'prpCmain.sumPremium'             : context.newQuoteRecord.premium,
            'prpCcarShipTax.thisPayTax'       : ciInsureTax?.sumTax ?: ciCarShipTax?.thisPayTax,
            'prpCcarShipTax.prePayTax'        : ciCarShipTax?.prePayTax,
            'prpCcarShipTax.delayPayTax'      : ciCarShipTax?.delayPayTax,
            'prpCcarShipTax.sumPayTax'        : ciInsureTax?.sumTax ?: ciCarShipTax?.sumPayTax, // todo  ciCarShipTax?.sumPayTax,
            'prpCcarShipTax.taxUnitAmount'    : ciInsureTax?.ciInsureAnnualTaxes?.size() > 0 ? ciInsureTax?.ciInsureAnnualTaxes?.first()?.unitRate : 0, //todo  取第一个没有依据，报价结果返回了一个list 只有一个元素
            'prpCcarShipTax.taxUnit'          : '辆/年',
            'prpCcarShipTax.payStartDate'     : stTaxStartDate,
            'prpCcarShipTax.payEndDate'       : stTaxEndDate,
            'prpCcarShipTax.carLotEquQuality' : context.vehiclePMCheckResult?.wholeWeight ?: selectedCarModel.vehicleQuality * 1000,
            'BIdemandNo'                      : context.biVoList?.ciInsureDemandDAA?.demandNo,
            'prpCitemCarExt.lastDamagedCI'    : context.biVoList?.prpCitemCarExt?.lastDamagedCI,
            'prpCitemCarExt_CI.rateRloatFlag' : '01',
            'ciInsureDemand.demandNo'         : ciInsureDemand?.demandNo,
            'prpCplanTemps[0].payNo'          : '1',
            'prpCplanTemps[0].payReason'      : 'R29',
            'prpCplanTemps[0].planFee'        : ciInsureDemand?.premium,
            'prpCplanTemps[0].isBICI'         : 'CI',
            'prpCplanTemps[0].planDate'       : ciStartDateText,
            'prpCplanTemps[1].payNo'          : '1',
            'prpCplanTemps[1].payReason'      : 'RM9', // 本期加往期车船税
            'prpCplanTemps[1].currency'       : 'CNY',
            'prpCplanTemps[1].planFee'        : ciInsureTax?.sumTax ?: 0, // todo 当前只测试了没有滞纳金的车辆 有滞纳金的情况需要确定此金额是否正确 (ciCarShipTax?.thisPayTax ?: 0) + (ciCarShipTax?.prePayTax ?: 0),
            'prpCplanTemps[1].isBICI'         : 'CShip',
            'prpCplanTemps[1].planDate'       : ciStartDateText,
            'prpCplanTemps[2].payNo'          : '1',
            'prpCplanTemps[2].payReason'      : 'RM8', // 车船税滞纳金
            'prpCplanTemps[2].currency'       : 'CNY',
            'prpCplanTemps[2].planFee'        : ciCarShipTax?.delayPayTax,
            'prpCplanTemps[2].isBICI'         : 'CShip',
            'prpCplanTemps[2].planDate'       : ciStartDateText,
            'prpCplanTemps[3].payNo'          : '1',
            'prpCplanTemps[3].payReason'      : 'R21',
            'prpCplanTemps[3].planFee'        : context.newQuoteRecord.premium,
            'prpCplanTemps[3].isBICI'         : 'BI',
            'prpCplanTemps[3].planDate'       : biStartDateText,
            'prpCitemCar.licenseColorCode'    : '01',// 号牌颜色
            'LicenseColorCodeDes'             : '蓝',
            'prpCitemCar.transferDate'        : transferDate
        ] + prpCitemKindsTemp + prpCprofitDetailsSizeTemp + prpCprofitFactorsTemp + [
            'qualificationNo'                : context.qualificationNo,
            //需要验车的险种必须带这2参数，否则会出现与官网核保意见不一致的问题
            'prpCitemCarExt.noDamYearsBI'    : context.biVoList?.prpCitemCarExt?.noDamYearsBI,
            'prpCitemCarExt_CI.noDamYearsCI' : context.biVoList?.prpCitemCarExt?.noDamYearsCI,

            'prpCitemCar.transferVehicleFlag': transferFlag ? '1' : '0', // 过户车标志
            'prpCitemCar.transferDate'       : transferDate
        ] + prpCfixationTemp
    }

}
