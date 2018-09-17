package com.cheche365.cheche.cpicuk.flow

import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.ContactUtils.getRandomMobile
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1
import static com.cheche365.cheche.core.model.GlassType.Enum.IMPORT_2
import static com.cheche365.cheche.cpicuk.flow.Constants.OWNER_PROP_MAPPING
import static com.cheche365.cheche.cpicuk.flow.Constants._CERTI_TYPE_MAPPING
import static com.cheche365.cheche.cpicuk.flow.Constants._USECHARACTER_DEETAIL_MAPPINGS
import static com.cheche365.cheche.cpicuk.flow.Constants._USECHARACTER_MAPPINGS
import static com.cheche365.cheche.cpicuk.flow.Constants._USER_RELATIONSHIP_MAPPINGS
import static com.cheche365.cheche.cpicuk.util.BusinessUtils.calculateActualValue
import static com.cheche365.cheche.cpicuk.util.BusinessUtils.changeBaxTypeByCity
import static com.cheche365.cheche.cpicuk.util.BusinessUtils.getAddress
import static com.cheche365.cheche.cpicuk.util.BusinessUtils.getCpicTime
import static com.cheche365.cheche.cpicuk.util.BusinessUtils.getFuelType
import static com.cheche365.cheche.cpicuk.util.BusinessUtils.getTaxDate
import static com.cheche365.cheche.cpicuk.util.BusinessUtils.selectVehicleType
import static com.cheche365.cheche.parser.Constants._DAMAGE
import static com.cheche365.cheche.parser.Constants._DAMAGE_IOP
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT3
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
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_COMMERCIAL_CAPTCHA_IMAGE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_COMPULSORY_CAPTCHA_IMAGE_TEMPLATE_QUOTING
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
import static com.cheche365.cheche.parser.util.BusinessUtils.isCommercialQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV



/**
 * 请求生成器（RPG）和响应处理器（RH）
 */
@Slf4j
class Handlers {

    static getAllBaseKindItems(context, convertersConfig) {
        convertersConfig.collectEntries { outerKindCode, _1, _2, extConfig, _4, _5 ->
            [
                (outerKindCode): [
                    amountList: extConfig,
                    amount    : context.theftAmount ?: context.platformVo?.purchasePrice as double
                ]
            ]
        }
    }

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

    /**
     * 获取报价后，将返回的报价转换成内部的结果（外转内）
     * 返回值有四项：保额，报价，iop的报价，其他的信息(比如玻璃险的类型，获取乘客险的座位数)
     */
    static final _O2I_PREMIUM_CONVERTER = { context, innerKindCode, kindItem, amountName, premiumName, isIop,
                                            iopPremiumName, extConfig ->
        def other
        if (_GLASS == innerKindCode) {
            other = ('0' == kindItem?.glassType) ? DOMESTIC_1 : ('1' == kindItem?.glassType) ? IMPORT_2 : null
        }
        if (_PASSENGER_AMOUNT == innerKindCode) {
            other = kindItem?.quantity ?: 0  // 座位数在上一步已-1
        }

        [
            isIop ? null : kindItem?.amount,
            isIop ? null : kindItem?.premium,
            isIop ? kindItem?.nonDeductible : null,
            other
        ]
    }

    /*
     * 内部的保额转换成外部的请求(内转外)
     * 必传字段insuranceCode
     * amount为非必传字段。result返回值非0的情况,需要按照险种设置金额
     */
    static final _I2O_PREMIUM_CONVERTER = { context, outerKindCode, kindItem, result ->
        if (result) {
            def vo = [
                insuranceCode: outerKindCode,
            ]
            if (outerKindCode in ['DAMAGELOSSCOVERAGE', 'THIRDPARTYLIABILITYCOVERAGE', 'THEFTCOVERAGE', 'SELFIGNITECOVERAGE', 'CARBODYPAINTCOVERAGE', 'INCARDRIVERLIABILITYCOVERAGE', 'INCARPASSENGERLIABILITYCOVERAGE']) {
                vo.amount = (result as int) as String
            }

            if ('INCARPASSENGERLIABILITYCOVERAGE' == outerKindCode) { // 乘客
                vo.factorVos = [
                    [
                        factorKey  : 'seat',
                        factorValue: (context.ecarvo.seatCount as int) - 1,
                    ]
                ]
            }
            if ('GLASSBROKENCOVERAGE' == outerKindCode) { // 玻璃
                vo.amount = 0
                vo.factorVos = [
                    [
                        factorKey  : 'producingArea',
                        factorValue: context.accurateInsurancePackage.glassTypeId == 1 ? '0' : '1',   // 国产0，进口1
                    ]
                ]
            }

            vo
        }

    }

    /**
     * 外部保险类型内部保险类型转换
     */
    static final _KIND_ITEM_CONVERTERS_CONFIG = [
        ['GLASSBROKENCOVERAGE', _GLASS, _GLASS_CONVERTER_FROM_AMOUNT, null, _O2I_PREMIUM_CONVERTER, null], //玻璃单独破碎险
        ['DAMAGELOSSCOVERAGE', _DAMAGE, _AMOUNT_CONVERTER_FROM_JSON, null, _O2I_PREMIUM_CONVERTER, null], //机动车损失保险
        ['SELFIGNITECOVERAGE', _SPONTANEOUS_LOSS, _AMOUNT_CONVERTER_FROM_JSON, null, _O2I_PREMIUM_CONVERTER, null], //自燃损失险
        ['THIRDPARTYLIABILITYCOVERAGE', _THIRD_PARTY_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, _THIRD_PARTY_AMOUNT_LIST, _O2I_PREMIUM_CONVERTER, null], //机动车第三者责任保险
        ['CARBODYPAINTCOVERAGE', _SCRATCH_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, _SCRATCH_AMOUNT_LIST, _O2I_PREMIUM_CONVERTER, null], //车身划痕损失险
        ['THEFTCOVERAGE', _THEFT, _AMOUNT_CONVERTER_FROM_JSON, null, _O2I_PREMIUM_CONVERTER, null], //机动车盗抢保险
        ['PADDLEDAMAGECOVERAGE', _ENGINE, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //涉水发动机损坏险
        ['INCARDRIVERLIABILITYCOVERAGE', _DRIVER_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, _DRIVER_AMOUNT_LIST, _O2I_PREMIUM_CONVERTER, null], //机动车车上人员责任保险（司机）
        ['INCARPASSENGERLIABILITYCOVERAGE', _PASSENGER_AMOUNT, _AMOUNT_CONVERTER_FROM_AMOUNT_LIST, _PASSENGER_AMOUNT_LIST, _O2I_PREMIUM_CONVERTER, null], //机动车车上人员责任保险（乘客）
        ['THEFTCOVERAGEEXEMPTDEDUCTIBLESPECIALCLAUSE', _THEFT_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（盗抢险）
        ['PADDLEDAMAGEEXEMPTDEDUCTIBLESPECIALCLAUSE', _ENGINE_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（涉水发动机损坏险）
        ['DAMAGELOSSEXEMPTDEDUCTIBLESPECIALCLAUSE', _DAMAGE_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（车损险）
        ['THIRDPARTYLIABILITYEXEMPTDEDUCTIBLESPECIALCLAUSE', _THIRD_PARTY_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（三者险）
        ['CARBODYPAINTEXEMPTDEDUCTIBLESPECIALCLAUSE', _SCRATCH_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（车身划痕险）
        ['SELFIGNITEEXEMPTDEDUCTIBLESPECIALCLAUSE', _SPONTANEOUS_LOSS_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（自燃险）
        ['INCARDRIVERLIABILITYEXEMPTDEDUCTIBLESPECIALCLAUSE', _DRIVER_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（车上人员司机）
        ['INCARPASSENGERLIABILITYEXEMPTDEDUCTIBLESPECIALCLAUSE', _PASSENGER_IOP, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //不计免赔特约条款（车上人员乘客）
        ['DAMAGELOSSCANNOTFINDTHIRDSPECIALCOVERAGE', _UNABLE_FIND_THIRDPARTY, _AMOUNT_CONVERTER_BOOLEAN, null, _O2I_PREMIUM_CONVERTER, null], //车损险无法找到第三方
    ]

    static final _CALCULATE_BASE_110000 = { config, context ->
        context.kindItemConvertersConfig = config
        def auto = context.auto
        def platformVo = context.ecarvo.platformVo // 车辆信息
        // 商业险时间 太平洋官网起始时间
        def (stStartDate, stEndDate) = getCommercialInsurancePeriodTexts(context, _DATETIME_FORMAT3, false)  //商业险时间
        def (compulsoryStartDate, compulsoryEndDate) = getCompulsoryInsurancePeriodTexts(context, _DATETIME_FORMAT3, false)  //交强险时间

        context.theftAmount = calculateActualValue(context.ecarvo.usage, context.ecarvo.vehiclePurpose, context.ecarvo.purchasePrice, context.auto.enrollDate, stStartDate)

        def allBaseKindItems = getAllBaseKindItems context, config
        def quoteInsuranceVos = getQuoteKindItemParams context, allBaseKindItems, config, _I2O_PREMIUM_CONVERTER
        // 时间格式转换下  2018-05-20 00:00, 2019-5-20 00:00
        if (isCommercialQuoted(context.accurateInsurancePackage)) {
            stStartDate = getCpicTime(stStartDate)
            stEndDate = getCpicTime(stEndDate)
        }

        if (isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage)) {
            compulsoryStartDate = getCpicTime(compulsoryStartDate)
            compulsoryEndDate = getCpicTime(compulsoryEndDate)
        }


        [
            redata: [
                quotationNo         : context.quotationNo,  // 报价单号
                accident            : false,  //意外险 默认不投
                isHolidaysDouble    : 0,  // 三责险附加法定节假日限额翻倍险 默认不投
                ifAllroundClause    : 0,  //
                platformVo          : [   // 车辆信息
                                          benchmarkRiskPremium: context.vehicleInfo.pureRiskPremium,
                                          brand               : platformVo.brand,
                                          brandCode           : platformVo.brandCode,
                                          carName             : platformVo.carName,
                                          configType          : platformVo.configType,
                                          id                  : platformVo.id,
                                          modelCode           : platformVo.modelCode,
                                          pureRiskPremium     : context.vehicleInfo.pureRiskPremium,
                                          series              : context.vehicleInfo.series
                ],
                compulsory          : isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage), // 交强险 true, false
                compulsoryInsuransVo: [
                    stStartDate           : compulsoryStartDate,
                    stEndDate             : compulsoryEndDate,
                    taxType               : '3',  // 纳税3 免税2 完税1 拒缴0 已申报未入库4 不在征收范围5
                    taxpayerName          : context.ecarvo.ownerName,
                    taxpayerType          : _CERTI_TYPE_MAPPING[auto.identityType?.id ?: 1L], // 证件类型 1身份证
                    taxpayerNo            : context.ecarvo.certNo,
                    taxVehicleType        : 'K11',//compulsoryInsuransVo.taxVehicleType,  //税务车辆类型
                    stVehicleLicensingDate: _DATE_FORMAT3.format(context.auto.enrollDate ?: new Date()),
                ],
                commercial          : isCommercialQuoted(context.accurateInsurancePackage),// 商业险
                commercialInsuransVo: [
                    stStartDate: stStartDate,  // 商业险开始时间
                    stEndDate  : stEndDate,       //       商业险结束时间
                ],
                quoteInsuranceVos   : quoteInsuranceVos
            ]
        ]
    }


    static final _CALCULATE_BASE_DEFAULT = { config, context ->
        context.kindItemConvertersConfig = config
        def auto = context.auto
        def platformVo = context.ecarvo.platformVo // 车辆信息
        // 商业险时间 太平洋官网起始时间
        def (stStartDate, stEndDate) = getCommercialInsurancePeriodTexts(context, _DATETIME_FORMAT3, false)  //商业险时间
        def (compulsoryStartDate, compulsoryEndDate) = getCompulsoryInsurancePeriodTexts(context, _DATETIME_FORMAT3, false)  //交强险时间

        def (stTaxStartDate, stTaxEndDate) = getTaxDate(null) //车船税起止时间

        context.theftAmount = calculateActualValue(context.ecarvo.usage, context.ecarvo.vehiclePurpose, context.ecarvo.purchasePrice, context.auto.enrollDate, stStartDate)

        def allBaseKindItems = getAllBaseKindItems context, config
        def quoteInsuranceVos = getQuoteKindItemParams context, allBaseKindItems, config, _I2O_PREMIUM_CONVERTER
        // 时间格式转换下  2018-05-20 00:00, 2019-5-20 00:00
        if (isCommercialQuoted(context.accurateInsurancePackage)) {
            stStartDate = getCpicTime(stStartDate)
            stEndDate = getCpicTime(stEndDate)
        }

        if (isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage)) {
            compulsoryStartDate = getCpicTime(compulsoryStartDate)
            compulsoryEndDate = getCpicTime(compulsoryEndDate)
        }

        //构造 checkcode
        def checkInfoVo = context.additionalParameters.supplementInfo?.compulsoryCaptchaImage ? [
            questionAnswer   : context.additionalParameters.supplementInfo?.compulsoryCaptchaImage,
            isTrafficQuestion: 'Y',
            answerFlag       : '2',
            checkCode        : '',

        ] : context.additionalParameters.supplementInfo?.commercialCaptchaImage ? [
            questionAnswer   : context.additionalParameters.supplementInfo?.commercialCaptchaImage,
            isTrafficQuestion: 'N',
            answerFlag       : '2',
            checkCode        : '',

        ] : [:]
        if (context.getCheckCodeAgain) {
            checkInfoVo = [:]
        }
        [
            redata: [
                quotationNo         : context.quotationNo,  // 报价单号
                accident            : false,  //意外险 默认不投
                isHolidaysDouble    : 0,  // 三责险附加法定节假日限额翻倍险 默认不投
                ifAllroundClause    : 0,  //
                compulsory          : isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage), // 交强险 true, false
                compulsoryInsuransVo: [
                    stStartDate           : compulsoryStartDate,
                    stEndDate             : compulsoryEndDate,
                    taxType               : changeBaxTypeByCity(context.area.id),  // 纳税3 免税2 完税1 拒缴0 已申报未入库4 不在征收范围5
                    taxpayerName          : context.ecarvo.ownerName,
                    taxpayerType          : _CERTI_TYPE_MAPPING[auto.identityType?.id ?: 1L],  // 证件类型 1身份证
                    taxpayerNo            : context.ecarvo.certNo,
                    taxVehicleType        : 'K11',//compulsoryInsuransVo.taxVehicleType,  //税务车辆类型
                    stVehicleLicensingDate: _DATE_FORMAT3.format(context.auto.enrollDate ?: new Date()),
                    stTaxStartDate        : stTaxStartDate,
                    stTaxEndDate          : stTaxEndDate,
                ],
                commercial          : isCommercialQuoted(context.accurateInsurancePackage),// 商业险
                commercialInsuransVo: [
                    stStartDate: stStartDate,  // 商业险开始时间
                    stEndDate  : stEndDate,       //       商业险结束时间
                ],
                quoteInsuranceVos   : quoteInsuranceVos,
                checkInfoVo         : checkInfoVo
            ]
        ]
    }

    // 报价
    static final _CALCULATE_RPG_DEFAULT = _CALCULATE_BASE_DEFAULT.curry _KIND_ITEM_CONVERTERS_CONFIG
    static final _CALCULATE_RPG_110000 = _CALCULATE_BASE_110000.curry _KIND_ITEM_CONVERTERS_CONFIG


    static final _QUERY_PAYMENT_RH_110000 = { result, context ->
        log.debug '查询支付状态 北京流程 结果处理'
        def payments = result?.result
        if (payments) {
            def isAvailable = payments.every {
                it.payStatus && it.payStatus == '初始'
            }
            if (isAvailable) {
                log.debug '符合支付初始状态'
                //将payment数据保存到上下文，给支付步骤使用
                context.payments = result.result
                getContinueFSRV null
            } else {
                log.error '支付状态不是初始，无法进行支付'
                getKnownReasonErrorFSRV '支付状态不合要求'
            }
            //查不到结果表示，未审核通过
        } else {
            log.error '报价单号为：{}的保单核保未通过', context.quotationNo
            getKnownReasonErrorFSRV '核保失败的保单'
        }
    }


    static final _CALCULATE_RH_NEED_VERIFY = { result, context ->
        def m = result.result?.checkInfoVo?.customMsg
        //state  处理状态  0 ：需要验证码   1：报价成功  2： 报价错误   msg：携带信息
        def res = [
            state: '',
            msg  : ''
        ]
        if (result && 'success' == result?.message?.code) {
            if (result?.result?.checkInfoVo?.checkCode && result?.result?.checkInfoVo?.answerFlag in ['1', '2']) {
                log.debug '转保车需要校验验证码'
                def imageType = result.result.checkInfoVo.isTrafficQuestion == 'Y' ? _SUPPLEMENT_INFO_COMPULSORY_CAPTCHA_IMAGE_TEMPLATE_QUOTING : _SUPPLEMENT_INFO_COMMERCIAL_CAPTCHA_IMAGE_TEMPLATE_QUOTING
                context.needSupplementInfos = []
                context.needSupplementInfos << mergeMaps(imageType, [meta: [imageData: result.result.checkInfoVo.checkCode]])
                res.msg = "推送验证码给前端"
                //save
                context?.checkCode = result?.result?.checkInfoVo?.checkCode
                res.state = '0'
            } else if (m && m.contains('与当前所选车型不一致') && !context.additionalParameters.referToOtherAutoModel) {
                def ms = m.split('平台对应车型代码有：')
                ms.size() > 1 ? res.msg = ms[1].split('、') : ''
                res.state = '4'
            } else {
                res.state = '1'
                res.msg = '报价成功'
            }
        } else if (result.message?.message?.contains('录入的校验码有误')) {
            // log.info '录入的校验码有误'
            res.state = '3'
            res.msg = '录入的校验码有误'
            context.getCheckCodeAgain = true
        } else {
            //验证码填错或者其他200以外错误
            res.state = '2'
            res.msg = result?.message?.message ?: '未知原因错误'
        }
        res
    }


    static final _QUERY_PAYMENT_RH_DEFAULT = { result, context ->
        log.debug '查询支付状态 default流程 结果处理'
        def payments = result?.result
        if (payments) {
            def isAvailable = payments.every {
                it.payStatus && it.payStatus == '初始'
            }
            if (isAvailable) {
                log.debug '符合支付初始状态'
                //将payment数据保存到上下文，给支付步骤使用
                context.payments = result.result
                getContinueFSRV '符合支付初始状态'
            } else {
                log.error '支付状态不是初始，无法进行支付'
                getKnownReasonErrorFSRV '支付状态不合要求'
            }
            //查不到结果表示，未审核通过
        } else {
            log.error '报价单号为：{} 未查询到支付状态,需要查询支付结果', context.quotationNo
            getContinueFSRV '继续查询支付结果'
        }
    }

    /**
     * @param identity 车主证件号码
     * @param owner 车主
     * @param applicantIdNo 投保人证件号码
     * @param insuredIdNo 被保人证件号码
     * @param applicantName 投保人姓名
     * @param insuredName 被保人姓名
     * @param ownerProp 个人/企业
     * @return 返回结果 第一个投保人的关系值（1：所有 2：管理  3：其他）  第二个：被保人的关系值（1：所有 2：管理  3：其他）
     */
    static final Object dealHolderInsuredRelationship(identity, owner, applicantIdNo, insuredIdNo, applicantName, insuredName, ownerProp) {
        [
            _USER_RELATIONSHIP_MAPPINGS[ownerProp as Long][(owner == applicantName && identity == applicantIdNo) as String],
            _USER_RELATIONSHIP_MAPPINGS[ownerProp as Long][(owner == insuredName && identity == insuredIdNo) as String]
        ]
    }


    static final _SAVE_CLAUSE_INFO_RPG_BASE = { isEApplication, context ->

        // 投保人手机号
        def userMobile = context.additionalParameters?.supplementInfo?.verificationMobile ?: context.insurance?.applicantMobile ?: context.compulsoryInsurance?.applicantMobile

        //投保人地址
        def address = getAddress context.order?.deliveryAddress
        def auto = context.auto
        def email = context?.insurance.applicantEmail ?: context.compulsoryInsurance?.applicantEmail
        //---------处理关系-------------
        def ownerProp = context.order.auto?.identityType?.parent?.id ?: '1'

        def order = context.order
        def insurance = context.insurance
        def autoId = auto.identity                      // 车主身份证
        def compulsoryInsurance = context.compulsoryInsurance
        //证件类型
        def applicantIdType = (insurance ?: compulsoryInsurance ?: order)?.applicantIdentityType?.id ?: auto.identityType?.id
        def insuredIdType = (insurance ?: compulsoryInsurance ?: order)?.insuredIdentityType?.id ?: auto.identityType?.id
        //获取投保人，被保人的关系值
        def relationship = dealHolderInsuredRelationship auto.identity, auto.owner,
            (insurance ?: compulsoryInsurance ?: order).applicantIdNo ?: autoId,
            (insurance ?: compulsoryInsurance ?: order).insuredIdNo ?: autoId,
            (insurance ?: compulsoryInsurance ?: order).applicantName ?: auto.owner,
            (insurance ?: compulsoryInsurance ?: order).insuredName ?: auto.owner,
            ownerProp


        log.debug 'relationship:--------------------:,{}', relationship
        //----------处理关系---------------
        [
            meta  : [:],
            redata: [
                holderVo      : [
                    name                 : (insurance ?: compulsoryInsurance ?: order)?.applicantName ?: auto.owner, // 投保人姓名,
                    relationship         : relationship[0] ?: '9', //1车辆所有人 2车辆管理人 3保险人允许的合法驾驶员 9 其他
                    certificateType      : _CERTI_TYPE_MAPPING[applicantIdType] ?: '1',
                    stCertificateValidity: '9999-12-31',
                    certificateCode      : (insurance ?: compulsoryInsurance ?: order)?.applicantIdNo ?: auto.identity,
                    otherInfo            : auto.owner,
                    telephone            : userMobile,
                    address              : address,
                    email                : email
                ],
                insuredVo     : [//被保险人
                                 name                 : (insurance ?: compulsoryInsurance ?: order)?.insuredName ?: auto.owner,      // 被保险人姓名,
                                 relationship         : relationship[1] ?: '9', //1车辆所有人 2车辆管理人 3保险人允许的合法驾驶员 9 其他
                                 certificateType      : _CERTI_TYPE_MAPPING[insuredIdType] ?: '1',
                                 certificateCode      : (insurance ?: compulsoryInsurance ?: order)?.insuredIdNo ?: auto.identity, // 被保险人身份证
                                 stCertificateValidity: '9999-12-31',
                                 telephone            : context.insuredTelephone,
                                 issueInvoice         : '0'
                ],
                claimerVo     : [
                    sameAsHolder         : '1',
                    stCertificateValidity: '9999-12-31'
                ],
                quotationNo   : context.quotationNo,
                sendEpolicy   : '1',
                createEpolicy : '1',
                createInvoice : '1',
                invoiceType   : '0',
                lawsuits      : '1',
                isEApplication: isEApplication ?: 0 //  杭州 0 （不需要确认电子保单）  北京 1 （需要确认电子保单） 此处先注释，不影响两种流程 2018-06-11
            ]
        ]
    }

    static final _QUICK_SAVE_RPG_DEFAULT = { context ->
        def auto = context.auto
        def usage = _USECHARACTER_MAPPINGS[context.auto.useCharacter?.id] ?: 101L // // 车辆使用性质
        def selectedCarModel = context.selectedCarModel
        def vehicleInfo = context.vehicleInfo
        def actualValue = calculateActualValue('101', '01',
            selectedCarModel.purchaseValue, context.auto.enrollDate ?: new Date(), _DATE_FORMAT3.format(new Date()))
        //协商价值
        // 投保人手机号  浙江杭州必传
        def userMobile = context.insurance?.applicantMobile ?: context.compulsoryInsurance?.applicantMobile
        def transferFlag = context?.additionalParameters?.supplementInfo?.transferDate // 是否是过户车
        def changeRegisterDate = transferFlag ? _DATE_FORMAT3.format(context.additionalParameters.supplementInfo?.transferDate) : null
        def result = [
            meta  : [:],
            redata: [
                plateNo              : auto.licensePlateNo,
                carVIN               : auto.vinNo,
                engineNo             : auto.engineNo,
                plateColor           : '1',  // 号牌底色 1 蓝色
                certType             : _CERTI_TYPE_MAPPING[auto.identityType?.id ?: 1L], //证件类型
                stRegisterDate       : _DATE_FORMAT3.format(context.auto.enrollDate ?: new Date()),
                ownerName            : auto.owner,
                ownerProp            : OWNER_PROP_MAPPING[auto.identityType?.parent?.id] ?: '1', //车主性质 1个人 2 机关 3 企业 这个选择没所谓  太平洋个人/公户都能过
                certNo               : auto.identity,
                purchasePrice        : selectedCarModel.purchaseValue,
                quotationNo          : context.mainQuotationNo ? context.mainQuotationNo : '',
                modelType            : selectedCarModel.name,
                factoryType          : selectedCarModel.name,
                tpyRiskflagCode      : selectedCarModel.tpyRiskflagCode, //太平洋风险标识
                usage                : usage,  // 使用性质
                usageSubdivs         : _USECHARACTER_DEETAIL_MAPPINGS[usage],     // 使用性质细分（公户车需要）
                stCertificateValidity: '9999-12-31', // 客户营业执照有效期
                vehiclePurpose       : '01', // 车辆用途
                loan                 : '0', // 多年车贷 1 是 0 否
                vehicleType          : selectVehicleType(selectedCarModel.seatCount),
                negotiatedValue      : actualValue,
                actualValue          : actualValue,
                specialVehicleIden   : transferFlag ? '1' : '',
                stChangeRegisterDate : changeRegisterDate,
                platformVo           :
                    [
                        brand               : selectedCarModel.name,
                        modelCode           : selectedCarModel.hyVehicleCode,
                        series              : vehicleInfo.series,
                        brandCode           : vehicleInfo.brandCode,
                        carName             : vehicleInfo.carName,
                        configType          : vehicleInfo.configType,
                        benchmarkRiskPremium: vehicleInfo.pureRiskPremium,
                        pureRiskPremium     : vehicleInfo.pureRiskPremium,
                        pureRiskPremiumFlag : vehicleInfo.pureRiskPremiumFlag,

                    ],
                fuelType             : getFuelType(context) ?: 'A',
                plateType            : '02', //与座位数无关  固定值
                holderTelphone       : userMobile   // 投保人手机号 默认流程必传， 北京流程不需要
            ] + selectedCarModel.subMap([
                'moldCharacterCode', 'seatCount', 'oriEngineCapacity',
                'jyFuelType', 'engineCapacity',
                'shortcutCode', 'vehiclePowerJY', 'purchasePrice'
                , 'emptyWeight', 'power', 'tpyRiskflagName', 'producingArea'
            ])
        ]
        if (context.carVo) {
            result.redata = result.redata << context.carVo
        }

        if (context.sellerVo?.certNo[0]) {
            result.redata.sellerVos = context.sellerVo  //销售人员信息
            result.redata.salesPerson = context.sellerVo.name[0] //销售人员姓名
        }
        result

    }


    static final _SAVE_CLAUSE_INFO_RPG_110000 = _SAVE_CLAUSE_INFO_RPG_BASE.curry(1)
    static final _SAVE_CLAUSE_INFO_RPG_DEFAULT = _SAVE_CLAUSE_INFO_RPG_BASE.curry(0)

    static final _SUBMIT_INSURE_INFO_RPG_BASE = { isNeedMacAddress, isNeedCaptcha, context ->
        def checkInfoVo = context.additionalParameters.supplementInfo?.compulsoryCaptchaImage ? [
            questionAnswer   : context.additionalParameters.supplementInfo?.compulsoryCaptchaImage,
            isTrafficQuestion: 'Y',
            answerFlag       : '2',
            checkCode        : '',

        ] : context.additionalParameters.supplementInfo?.commercialCaptchaImage ? [
            questionAnswer   : context.additionalParameters.supplementInfo?.commercialCaptchaImage,
            isTrafficQuestion: 'N',
            answerFlag       : '2',
            checkCode        : '',

        ] : [:]
        if (context.getInsureCheckCode) {
            checkInfoVo = [:]
        }
        [
            meta  : [:],
            redata: [
                quotationNo: context.quotationNo,
            ] + (isNeedMacAddress ? [macAddress: context.macAddress] : [:]) + (isNeedCaptcha ? [checkInfoVo: checkInfoVo] : [:]),
        ]

    }

    static final _SUBMIT_INSURE_INFO_RPG_440100L = _SUBMIT_INSURE_INFO_RPG_BASE.curry(true, true)
    static final _SUBMIT_INSURE_INFO_RPG_410100L = _SUBMIT_INSURE_INFO_RPG_BASE.curry(false, true)
    static final _SUBMIT_INSURE_INFO_RPG_DEFAULT = _SUBMIT_INSURE_INFO_RPG_BASE.curry(false, false)

    static final _PAY_RPG_320100L = { context ->
        [
            payments: context.payments.collect { //补充payments的请求参数
                it + [paymentType: 'weixin',     //"1":支票；"weixin":微信；"2":划卡（支付宝、微信都行）；"chinapay":银联电子支付
                      //todo 注意：官网页面现在多了银行选择，参数值是否写死得看此银行是太平洋的收款行还是客户的付款行。
                      cooperant  : '',
                ]
            }
        ]
    }
    static final _PAY_RPG_DEFAULT = { context ->
        [
            payments: context.payments.collect { //补充payments的请求参数
                it + [paymentType: '2',     //"1":支票；"weixin":微信；"2":划卡（支付宝、微信都行）；"chinapay":银联电子支付
                      //todo 注意：官网页面现在多了银行选择，参数值是否写死得看此银行是太平洋的收款行还是客户的付款行。
                      cooperant  : '01',
                      isPartyPay : '0']
            }
        ]
    }


}
