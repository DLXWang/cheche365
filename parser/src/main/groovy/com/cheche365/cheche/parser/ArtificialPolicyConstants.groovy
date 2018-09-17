package com.cheche365.cheche.parser

import com.cheche365.cheche.core.util.ValidationUtil
import com.cheche365.cheche.parser.service.InsuranceRules
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.core.exception.BusinessException.Code.BAD_QUOTE_PARAMETER
import static com.cheche365.cheche.core.exception.BusinessException.Code.QUOTE_NEED_SUPPLY_INFO
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.ALLTRUST_95000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.ANSWERN_65000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.AXATP_55000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.CCIC_240000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.CHINALIFE_40000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.CIC_45000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.CPIC_25000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.FUNDINS_60000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.MINANINS_85000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.PICC_10000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.PINGAN_20000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.SINOSAFE_205000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.SINOSIG_15000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.TAIPING_30000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.ZHONGAN_50000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.ZKING_165000
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.YDTH_220000
import static com.cheche365.cheche.core.model.QuoteSource.Enum.API_4
import static com.cheche365.cheche.core.model.QuoteSource.Enum.PLANTFORM_BX_6
import static com.cheche365.cheche.core.model.QuoteSource.Enum.REFERENCED_7
import static com.cheche365.cheche.core.model.QuoteSource.Enum.RULEENGINE2_8
import static com.cheche365.cheche.core.model.QuoteSource.Enum.WEBPARSER_2
import static com.cheche365.cheche.core.model.QuoteSource.getQuoteSource
import static com.cheche365.cheche.core.util.InsuranceDateUtil.getEffectiveDate
import static com.cheche365.cheche.parser.Constants._DAMAGE
import static com.cheche365.cheche.parser.Constants._DAMAGE_IOP
import static com.cheche365.cheche.parser.Constants._DRIVER_AMOUNT
import static com.cheche365.cheche.parser.Constants._DRIVER_IOP
import static com.cheche365.cheche.parser.Constants._ENGINE
import static com.cheche365.cheche.parser.Constants._ENGINE_IOP
import static com.cheche365.cheche.parser.Constants._GLASS
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
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted
import static java.math.BigDecimal.ROUND_HALF_UP

/**
 * Artificial Quote Policy常量
 */
@Slf4j
class ArtificialPolicyConstants {

    private static final _RULE_CPIC_PREMIUM_FLOAT = 3
    private static final _RULE_PINGAN_PREMIUM_FLOAT = 11
    private static final _RULE_CHINALIFE_PREMIUM_FLOAT = 8
    private static final _RULE_CIC_PREMIUM_FLOAT = 7
    private static final _RULE_SINOSIG_PREMIUM_FLOAT = 5

    private static final _COMMERCIAL_PREMIUM_NAME = [
        'thirdPartyPremium',
        'damagePremium',
        'theftPremium',
        'enginePremium',
        'driverPremium',
        'passengerPremium',
        'spontaneousLossPremium',
        'glassPremium',
        'scratchPremium',
        'unableFindThirdPartyPremium',
    ]

    private static final _INSURANCE_PACKAGE_KIND_NAME = [
        [_THIRD_PARTY_AMOUNT, 'thirdPartyAmount', 'thirdPartyPremium', _THIRD_PARTY_IOP, 15.0],
        [_DAMAGE, 'damageAmount', 'damagePremium', _DAMAGE_IOP, 15.0],
        [_THEFT, 'theftAmount', 'theftPremium', _THEFT_IOP, 20.0],
        [_ENGINE, null, 'enginePremium', _ENGINE_IOP, 20.0],
        [_GLASS, null, 'glassPremium', null, null],
        [_DRIVER_AMOUNT, 'driverAmount', 'driverPremium', _DRIVER_IOP, 15.0],
        [_PASSENGER_AMOUNT, 'passengerAmount', 'passengerPremium', _PASSENGER_IOP, 15.0],
        [_SPONTANEOUS_LOSS, 'spontaneousLossAmount', 'spontaneousLossPremium', _SPONTANEOUS_LOSS_IOP, 15.0],
        [_SCRATCH_AMOUNT, 'scratchAmount', 'scratchPremium', _SCRATCH_IOP, 15.0],
        [_UNABLE_FIND_THIRDPARTY, null, 'unableFindThirdPartyPremium', null, null],
    ]


    private static final _DO_NOTHING = { quoteRecord, additionalParameters -> }

    /**
     * 详见a7afcfc的log message
     */
    private static final _DO_STH = { quoteRecord, additionalParameters ->
        additionalParameters.quoteTimes += 1 //验证bug的代码，稳定后删除
        if (additionalParameters.supplementInfo.selectedAutoModel?.companyId == quoteRecord.insuranceCompany.id) {
            additionalParameters.supplementInfo.autoModel = null
        }
    }

    /**
     * 真实服务成功后，采用精确报价，将quoteSource改为WEBPARSER_2
     */
    private static final _POST_SUCCESSFUL_RULE_REFERRED_BASE = { qrType, quoteRecord, additionalParameters ->
        quoteRecord.type = qrType
    }

    /**
     * 除人保是精准报价外，其他保险公司都基于人保模糊报价，太平洋+3元、平安+11元、国寿+8元、中华联合+7元、阳光+5元，
     */
    private static
    final _SUCCESSFUL_RULE_OTHER_COMPANY_BASE = { quoteSource, offsetFloat, quoteRecord, additionalParameters ->
        _COMMERCIAL_PREMIUM_NAME.any { premiumPropName ->
            if (quoteRecord[premiumPropName]) {
                quoteRecord[premiumPropName] = ((quoteRecord[premiumPropName] + offsetFloat) as BigDecimal).setScale(2, ROUND_HALF_UP)
            }
        }
        quoteRecord.type = quoteSource
        quoteRecord.calculatePremium()
    }

    /**
     * 如人保报不出价格则车损15万去报价，每增加一个险种，或增加险种金额，报价+200元。
     * 点击报价后直接跳转至填写订单页面，不展示险种。不可支付，只可下单
     */
    public static final _POST_QUOTE_RECORD_FAILED_RULE = { offsetFloat, quoteRecord, additionalParameters, ex ->
        def insurancePackage = quoteRecord.insurancePackage
        def auto = quoteRecord.auto

        if (isCompulsoryOrAutoTaxQuoted(insurancePackage)) {
            quoteRecord['compulsoryPremium'] = InsuranceRules.complusoryPremium(auto)
            quoteRecord['autoTax'] = InsuranceRules.autoTaxPremium(auto)
        }

        _INSURANCE_PACKAGE_KIND_NAME.each { kindName, amountPropName, qrPremiumPropName, iopPropName, iopRate ->
            def kindValue = insurancePackage[kindName]
            if (kindValue) {
                def premiumValue = 0
                def newCarPrice = auto?.autoType?.newPrice ?: 150000

                if (amountPropName in ['damageAmount', 'theftAmount', 'spontaneousLossAmount']) {
                    quoteRecord[amountPropName] = newCarPrice
                }

                if (amountPropName in ['thirdPartyAmount', 'driverAmount', 'passengerAmount', 'scratchAmount']) {
                    quoteRecord[amountPropName] = kindValue
                }

                if (_THIRD_PARTY_AMOUNT == kindName) {
                    premiumValue = InsuranceRules.thirdPartyPremium(auto, kindValue)
                }
                if (_DAMAGE == kindName) {
                    premiumValue = InsuranceRules.damagePremium(auto, newCarPrice)
                }
                if (_THEFT == kindName) {
                    premiumValue = InsuranceRules.theftPremium(auto, newCarPrice)
                }
                if (_ENGINE == kindName) {
                    def damagePremium = quoteRecord['damagePremium'] ?: 0.0
                    premiumValue = InsuranceRules.enginePremium(damagePremium)
                }
                if (_GLASS == kindName) {
                    premiumValue = InsuranceRules.glassPremium(auto, insurancePackage.glassType, newCarPrice)
                }
                if (_DRIVER_AMOUNT == kindName) {
                    premiumValue = InsuranceRules.driverPremium(auto, kindValue)
                }
                if (_PASSENGER_AMOUNT == kindName) {
                    premiumValue = InsuranceRules.passengerPremium(auto, kindValue)
                }
                if (_SPONTANEOUS_LOSS == kindName) {
                    premiumValue = InsuranceRules.spontaneousLossPremium(auto, newCarPrice)
                }
                if (_SCRATCH_AMOUNT == kindName) {
                    premiumValue = InsuranceRules.scratchPremium(auto, newCarPrice, kindValue)
                }
                if (_UNABLE_FIND_THIRDPARTY == kindName) {
                    def damagePremium = quoteRecord['damagePremium'] ?: 0.0
                    def thirdPartyPremium = quoteRecord['thirdPartyPremium'] ?: 0.0
                    premiumValue = InsuranceRules.unableFindThirdPartyPremium(damagePremium, thirdPartyPremium)
                }
                quoteRecord[qrPremiumPropName] = (premiumValue as BigDecimal).setScale(2, ROUND_HALF_UP)

                if (iopPropName && insurancePackage[iopPropName]) {
                    def premiumIopValue = InsuranceRules.commercialIopPremium(premiumValue, iopRate)
                    quoteRecord[iopPropName] = (premiumIopValue as BigDecimal).setScale(2, ROUND_HALF_UP)
                }

                def supplementInfo = additionalParameters.supplementInfo

                quoteRecord.effectiveDate = quoteRecord.effectiveDate ?: getEffectiveDate(supplementInfo.commercialStartDate)
                quoteRecord.compulsoryEffectiveDate = quoteRecord.compulsoryEffectiveDate ?: getEffectiveDate(supplementInfo.compulsoryStartDate)
            }
        }

        _COMMERCIAL_PREMIUM_NAME.any { premiumPropName ->
            if (quoteRecord[premiumPropName]) {
                quoteRecord[premiumPropName] += offsetFloat
            }
        }

        //需求 #10739 商业险在现有计算引擎的基础上统一八折
        _INSURANCE_PACKAGE_KIND_NAME.each { kindName, amountPropName, qrPremiumPropName, iopPropName, iopRate ->
            if(quoteRecord[qrPremiumPropName]){
                quoteRecord[qrPremiumPropName] *= 0.8
            }
            if (iopPropName && insurancePackage[iopPropName]) {
                quoteRecord[iopPropName] *= 0.8
            }
        }

        quoteRecord.type = RULEENGINE2_8
        quoteRecord.calculatePremium()
        quoteRecord.iopTotal = quoteRecord.sumIopItems()

    }

    /**
     * 以人保财险报价为基准，若人保报价失败或两公司都失败，以安心保险报价为基准；
     * 以下保险公司模糊报价规则为
     * 阳光保险：+0.2‰
     * 平安保险：+0.3‰
     * 太平洋保险：+0.15‰
     * 中国太平：+0.25‰
     * 国寿财险：+0.3‰
     * 安盛天平：+0.45‰
     * 富德：+0.55‰
     * 人保：+0.5‰
     * 众安：+0.6%
     * 华安：+0.35%
     */
    private static final _OFFSET_FLOAT_RATE_RULE_MAPPINGS = [
        (SINOSIG_15000)  : 0.2,
        (PINGAN_20000)   : 0.3,
        (CPIC_25000)     : 0.15,
        (TAIPING_30000)  : 0.25,
        (CHINALIFE_40000): 0.3,
        (AXATP_55000)    : 0.45,
        (FUNDINS_60000)  : 0.55,
        (CIC_45000)      : 0.4,
        (PICC_10000)     : 0.5,
        (ZHONGAN_50000)  : 0.6,
        (SINOSAFE_205000): 0.35,
        (CCIC_240000)    : 0.85,
        (MINANINS_85000) : 0.75,
        (ALLTRUST_95000) : 0.8,
        (ZKING_165000)   : 0.7,
        (YDTH_220000)    : 0.85,
        default          : 0
    ]

    public static final Map REFERENCED_SERVICE_CONFIG_MAPPINGS = [
        answernService: [ANSWERN_65000, API_4], // insuranceCompany, quoteSource
        piccService   : [PICC_10000, WEBPARSER_2]
    ]

    // 当报价公司传入是一家报价基准公司，不走并发报价
    public static final _GET_QUOTING_SERVICE = { referred ->
        referred.companyServiceMappings.keySet().size() == 1 ?
            [referred.companyServiceMappings.keySet()[0], referred.companyServiceMappings.values()[0]] :
            referred
    }

    private static final _FLOATING_RATE_RULE_BASE = { referencedServiceId, serviceId, quoteRecord, additionalParameters ->
        if (referencedServiceId == serviceId) {
            if (!quoteRecord.type) { // quoteSource为空的时候 为真实报价
                quoteRecord.type = REFERENCED_SERVICE_CONFIG_MAPPINGS[serviceId][1]
            }
        } else {
            def floatingRate = _OFFSET_FLOAT_RATE_RULE_MAPPINGS[quoteRecord.insuranceCompany] ?: _OFFSET_FLOAT_RATE_RULE_MAPPINGS.default
            def offsetFloat = (floatingRate * 0.001 * quoteRecord.getTotalPremium() as BigDecimal).setScale(2, ROUND_HALF_UP)
            _SUCCESSFUL_RULE_OTHER_COMPANY_BASE.call(quoteRecord.type == RULEENGINE2_8 ? RULEENGINE2_8 : REFERENCED_7, offsetFloat, quoteRecord, [:])
        }

    }

    private static final _FLOATING_RATE_RULE_BASE_1 = { referencedCompany, company, quoteRecord, additionalParameters ->
        def quoteSourceMap = additionalParameters.quoteSourceMap
        if (referencedCompany == company) {
            if (!quoteRecord.type) { // quoteSource为空的时候 为真实报价
                quoteRecord.type = quoteSourceMap.get(company)
            }
        } else if (REFERENCED_7 == quoteSourceMap.get(company)) {
            def floatingRate = _OFFSET_FLOAT_RATE_RULE_MAPPINGS[quoteRecord.insuranceCompany] ?: _OFFSET_FLOAT_RATE_RULE_MAPPINGS.default
            def offsetFloat = (floatingRate * 0.001 * quoteRecord.getTotalPremium() as BigDecimal).setScale(2, ROUND_HALF_UP)
            _SUCCESSFUL_RULE_OTHER_COMPANY_BASE.call(quoteRecord.type == RULEENGINE2_8 ? RULEENGINE2_8 : REFERENCED_7, offsetFloat, quoteRecord, [:])
        }
    }

    /**
     * 第一次报价返回： 参考报价返回结果处理组合3*3
     * picc：QR, answern：QR -> 返回两个QR，无二次报价
     * picc：QR, answern：补充 -> 返回一个人保QR，一个安心补充，第二次返回一个安心报价请求
     * picc：QR, answern：非预期 -> 返回一个QR，一个安心模糊QR，无二次报价
     * picc：补充或非预期，answern，QR -> 返回一个安心的报价QR，无二次报价
     * picc：补充，answern，补充 -> 返回两个补充信息， 第二次返回所有的报价请求  ***
     * picc：补充，answern，非预期 -> 返回一个人保的补充信息，一个安心的模糊QR， 第二次返回除安心之外的所有报价请求， serviceId指定为人保
     * picc：非预期，answern，非预期 -> 一个模糊QR，无二次报价
     * picc：非预期，answern，补充 -> 返回一个安心的补充信息，第二次返回所有的报价请求，serviceId指定为安心
     * 第二次报价返回：4种
     * picc：QR -> 返回除安心之外的所有参考报价QR结果
     * picc：补充或非预期 -> 模糊报价， 返回除安心之外的所有模糊QR结果
     * answern：QR -> 返回安心报价结果
     * answern：补充或非预期 -> 返回安心模糊报价结果
     */
    public static final _QUOTE_SUCCESS_CODE             = 0
    public static final _QUOTE_EXCEPTION_CODE           = -1
    public static final _TWO_TIMES_THROW_SUPPLY_CODE    = -2
    public static final _MISSING_REFERRED_RESULT_CODE   = -3
    public static final _NEED_SUPPLY_AND_BAD_PARAMETER  = [QUOTE_NEED_SUPPLY_INFO, BAD_QUOTE_PARAMETER].codeValue
    public static final _EXCEPT_QUOTE_FAILED            = _NEED_SUPPLY_AND_BAD_PARAMETER + _QUOTE_SUCCESS_CODE

    private static final _CHECK_QUOTE_CODE_BASE = { quoteSource, codes, reversed = false, results, quoteSourceMap ->
        results.any { company, result ->
            def inCodes = result.code in codes
            company.referenceBase() && (reversed ? !inCodes : inCodes) && quoteSource == getQuoteSource(company, quoteSourceMap)
        }
    }

    private static final _CHECK_QUOTE_SOURCE_BASE = { results, quoteSourceMap ->
        (results.collect { company, result -> getQuoteSource(company, quoteSourceMap) }.flatten() as Set).size() <= 3
    }

    private static final _CHECK_STATUS_ONLY_BASE = { codes, exceptedCount, reversed = false, results ->
        results.count {
            def inCodes = it.value.code in codes
            reversed ? !inCodes : inCodes
        } == exceptedCount
    }

    private static final _CHECK_COMPANY_AND_STATUS = { refer, results, reversed /*是否反转“在Codes中”的真值*/->
        def (company, codes) = refer
        results.any { k, v ->
            def inCodes = v.code in codes
            k.code == company && (reversed ? !inCodes : inCodes)
        }
    }

    private static final _CHECK_COMPANY_AND_STATUS_BASE = { refer1, refer1Reversed, refer2, refer2Reversed, results ->
        _CHECK_COMPANY_AND_STATUS.call(refer1, results, refer1Reversed) &&
            _CHECK_COMPANY_AND_STATUS.call(refer2, results, refer2Reversed)
    }

    private static final _CHECK_COMPANY_ONLY_BASE = { companies, results ->
        results.any { k, v ->
            k.code in companies
        }
    }


    // 1: 返回两个QR，无二次报价
    // 3: 返回一个QR,一个安心模糊QR， 无二次报价
    // 返回一个人保QR,一个安心补充, 第二次返回一个安心报价请求
    // 6: 返回一个人保的补充信息，一个安心的模糊QR
    // 返回两个补充信息， 第二次返回所有的报价请求
    private static final _HANDLE_REFERENCED_RESULTS_BASE_1 = { referencedServiceIds, defaultServiceId, insuranceCompanyMaps, services, results ->
        services.collectEntries { serviceId, service ->
            def insuranceCompany = insuranceCompanyMaps[serviceId]
            def referencedServiceId = referencedServiceIds.find { referencedServiceId -> serviceId == referencedServiceId } ?: defaultServiceId
            def result = results.find { key, value ->
                key.code == (referencedServiceId - 'Service').toUpperCase()
            }.value.clone()
            result.metaInfo = mergeMaps result.metaInfo, [referencedInsuranceCompany: insuranceCompanyMaps[referencedServiceId]]
            if (result.code == 0) {
                def qr = result.data.quoteRecord.clone()
                def addParams = result.data.additionalParameters.clone()
                qr.insuranceCompany = insuranceCompany
                // 调整人保和安心参考自己的时候将quoteSource改为真实报价
                _FLOATING_RATE_RULE_BASE.call(referencedServiceId, serviceId, qr, addParams)
                result.data = [quoteRecord: qr, additionalParameters: addParams]
            }
            [(insuranceCompany): result]
        }
    }

    // 返回一个安心的报价QR，无二次报价
    // 两个模糊QR，无二次报价
    private static final _HANDLE_REFERENCED_RESULTS_BASE_2 = { referencedServiceId, insuranceCompanyMaps, services, results ->
        def refer = results.find { key, value ->
            key.code == (referencedServiceId - 'Service').toUpperCase()
        }.value

        services.collectEntries { serviceId, service ->
            def insuranceCompany = insuranceCompanyMaps[serviceId]
            def result = refer.clone()
            result.metaInfo = mergeMaps result.metaInfo, [referencedInsuranceCompany: insuranceCompanyMaps[referencedServiceId]]

            if (result.code == 0) {
                def qr = result.data.quoteRecord.clone()
                def addParams = result.data.additionalParameters.clone()
                qr.insuranceCompany = insuranceCompany
                // 调整人保和安心参考自己的时候将quoteSource改为真实报价
                _FLOATING_RATE_RULE_BASE.call(referencedServiceId, serviceId, qr, addParams)
                result.data = [quoteRecord: qr, additionalParameters: addParams]
            }
            [(insuranceCompany): result]
        }
    }

    /**
     * 泛华报价不参考不模糊，直接返回结果
     * 其他按照PARSER、API原有参考规则
     */
    private static final _HANDLE_REFERENCED_RESULTS_BASE_3 = { referredQuoteSource, codes, quoteSourceMap, quoteCompanies, results ->
        def referencedCompany = (referredQuoteSource) ?
            results.find { company, result ->
                (!codes || codes.contains(result.code)) &&
                    company.referenceBase() &&
                    referredQuoteSource == getQuoteSource(company, quoteSourceMap)
            }?.key : null

        quoteCompanies.collectEntries { insuranceCompany ->
            def referred = (REFERENCED_7 == quoteSourceMap.get(insuranceCompany)) ? referencedCompany : insuranceCompany
            def result = results.find { key, value -> (key == referred) }.value.clone()
            result.metaInfo = mergeMaps result.metaInfo, [referencedInsuranceCompany: referred]
            if (result.code == 0) {
                def qr = result.data.quoteRecord.clone()
                def addParams = result.data.additionalParameters.clone()
                qr.insuranceCompany = insuranceCompany
                // 调整人保和安心参考自己的时候将quoteSource改为真实报价
                _FLOATING_RATE_RULE_BASE_1.call(referred, insuranceCompany, qr, addParams)
                result.data = [quoteRecord: qr, additionalParameters: addParams]
            }
            [(insuranceCompany): result]
        }
    }

    private static final _HANDLE_DEBUG_INFO = { msg, results, quoteSourceMap ->
        results.collectEntries { insuranceCompany, result ->
            [
                (insuranceCompany):
                    mergeMaps(
                        result,
                        [metaInfo: [debugInfo: msg]],
                        !(result.code in _EXCEPT_QUOTE_FAILED) && ValidationUtil.ableRuleQuote(result.data?.quoteRecord?.channel, getQuoteSource(insuranceCompany, quoteSourceMap)) ?
                            [code: _QUOTE_SUCCESS_CODE, metaInfo: [realCode: result.code]] : [:]
                    )
            ]
        }
    }

    public static final Map<Object, List<Closure>> _THIRD_PARTY_HANDLER_RULE_MAPPINGS_1 = [
        // parser = success
        (_CHECK_QUOTE_CODE_BASE.curry(WEBPARSER_2, [_QUOTE_SUCCESS_CODE]))         : [
            _HANDLE_DEBUG_INFO.curry('PARSER报价成功，API走自己或模糊报价,其他公司参考PARSER'),
            _HANDLE_REFERENCED_RESULTS_BASE_3.curry(WEBPARSER_2, [_QUOTE_SUCCESS_CODE])
        ],
        // parser != success, api = success
        (_CHECK_QUOTE_CODE_BASE.curry(API_4, [_QUOTE_SUCCESS_CODE]))               : [
            _HANDLE_DEBUG_INFO.curry('PARSER报价不成功，API报价成功，所有公司均参考API'),
            _HANDLE_REFERENCED_RESULTS_BASE_3.curry(API_4, [_QUOTE_SUCCESS_CODE])
        ],
        // parser = 补充或有价值提示信息, api != success
        (_CHECK_QUOTE_CODE_BASE.curry(WEBPARSER_2, _NEED_SUPPLY_AND_BAD_PARAMETER)): [
            _HANDLE_DEBUG_INFO.curry('PARSER补充或有价值提示信息，API报价不成功，API参考自己，其他公司参考PARSER推补充或有价值提示信息'),
            _HANDLE_REFERENCED_RESULTS_BASE_3.curry(WEBPARSER_2, _NEED_SUPPLY_AND_BAD_PARAMETER)
        ],
        // parser = 非预期 , api = 补充或有价值提示信息
        (_CHECK_QUOTE_CODE_BASE.curry(API_4, _NEED_SUPPLY_AND_BAD_PARAMETER)): [
            _HANDLE_DEBUG_INFO.curry('PARSER非预期，API补充或有价值提示信息，API参考自己，其他公司参考API推补充或有价值提示信息'),
            _HANDLE_REFERENCED_RESULTS_BASE_3.curry(API_4, _NEED_SUPPLY_AND_BAD_PARAMETER)
        ],
        // (parser = 非预期 && api = 非预期) || parser = 非预期
        (_CHECK_QUOTE_CODE_BASE.curry(WEBPARSER_2, _EXCEPT_QUOTE_FAILED, true)): [
            _HANDLE_DEBUG_INFO.curry('PARSER非预期，所有的公司参考PARSER'),
            _HANDLE_REFERENCED_RESULTS_BASE_3.curry(WEBPARSER_2, null)
        ],
        // api = 非预期
        (_CHECK_QUOTE_CODE_BASE.curry(API_4, _EXCEPT_QUOTE_FAILED, true)): [
            _HANDLE_DEBUG_INFO.curry('API非预期， 所有的公司参考API'),
            _HANDLE_REFERENCED_RESULTS_BASE_3.curry(API_4, null)
        ],
        // 泛华
        (_CHECK_QUOTE_SOURCE_BASE)                                                 : [
            _HANDLE_DEBUG_INFO.curry('不互相参考，返回实际状态'),
            _HANDLE_REFERENCED_RESULTS_BASE_3.curry(null, null)
        ]
    ]

    public static final Map<Object, List<Closure>> _THIRD_PARTY_HANDLER_RULE_MAPPINGS = [
        // picc:QR, answern: QR
        (_CHECK_STATUS_ONLY_BASE.curry([_QUOTE_SUCCESS_CODE], 2))                                                                            : [
            _HANDLE_DEBUG_INFO.curry('人保和安心都报价成功，安心走自己其他公司参考人保'),
            _HANDLE_REFERENCED_RESULTS_BASE_1.curry(['answernService', 'piccService'], 'piccService')
        ],
        // picc:QR, answern: 补充或有价值提示信息
        (_CHECK_COMPANY_AND_STATUS_BASE.curry(['PICC', [_QUOTE_SUCCESS_CODE]], false, ['ANSWERN', _NEED_SUPPLY_AND_BAD_PARAMETER], false)): [
            _HANDLE_DEBUG_INFO.curry('人保报价成功，安心补充或有价值提示信息，除安心外抛出补充信息异常外，其他参考人保'),
            _HANDLE_REFERENCED_RESULTS_BASE_1.curry(['answernService', 'piccService'], 'piccService')
        ],
        // picc:QR, answern: 非预期
        (_CHECK_COMPANY_AND_STATUS_BASE.curry(['PICC', [_QUOTE_SUCCESS_CODE]], false, ['ANSWERN', _EXCEPT_QUOTE_FAILED], true))           : [
            _HANDLE_DEBUG_INFO.curry('人保成功，安心非预期，除安心走模糊报价外，其他参考人保'),
            _HANDLE_REFERENCED_RESULTS_BASE_1.curry(['answernService', 'piccService'], 'piccService')
        ],
        // picc:补充或有价值提示信息或非预期, answern: QR
        (_CHECK_COMPANY_AND_STATUS_BASE.curry(['PICC', [_QUOTE_SUCCESS_CODE]], true, ['ANSWERN', [_QUOTE_SUCCESS_CODE]], false))          : [
            _HANDLE_DEBUG_INFO.curry('人保补充或有价值提示信息或非预期，安心报价成功，所有公司均参考安心'),
            _HANDLE_REFERENCED_RESULTS_BASE_2.curry('answernService')
        ],
        // picc:补充或有价值提示信息, answern: 补充或有价值提示信息
        (_CHECK_STATUS_ONLY_BASE.curry(_NEED_SUPPLY_AND_BAD_PARAMETER, 2))                                                                   : [
            _HANDLE_DEBUG_INFO.curry('人保和安心都抛出补充或有价值提示信息，安心参考自己，其他公司参考人保推补充或有价值提示信息'),
            _HANDLE_REFERENCED_RESULTS_BASE_1.curry(['answernService', 'piccService'], 'piccService')
        ],
        // picc:补充或有价值提示信息, answern: 非预期
        (_CHECK_COMPANY_AND_STATUS_BASE.curry(['PICC', _NEED_SUPPLY_AND_BAD_PARAMETER], false, ['ANSWERN', _EXCEPT_QUOTE_FAILED], true))  : [
            _HANDLE_DEBUG_INFO.curry('人保补充或有价值提示信息， 安心非预期异常，安心走模糊报价，其他同人保一样抛出补充信息异常'),
            _HANDLE_REFERENCED_RESULTS_BASE_1.curry(['answernService', 'piccService'], 'piccService')
        ],
        // picc:非预期, answern: 非预期
        (_CHECK_STATUS_ONLY_BASE.curry(_EXCEPT_QUOTE_FAILED, 2, true))                                                                       : [
            _HANDLE_DEBUG_INFO.curry('人保和安心都非预期异常，所有返回模糊报价'),
            _HANDLE_REFERENCED_RESULTS_BASE_2.curry('answernService')
        ],
        // picc:非预期, answern: 补充或有价值提示信息
        (_CHECK_COMPANY_AND_STATUS_BASE.curry(['PICC', _EXCEPT_QUOTE_FAILED], true, ['ANSWERN', _NEED_SUPPLY_AND_BAD_PARAMETER], false))  : [
            _HANDLE_DEBUG_INFO.curry('人保非预期，安心补充或有价值提示信息， 所有的公司参考安心返回补充信息异常'),
            _HANDLE_REFERENCED_RESULTS_BASE_2.curry('answernService')
        ],
        // 只存在一个真实报价公司的情况
        (_CHECK_COMPANY_ONLY_BASE.curry(['PICC']))                                                                                      : [
            _HANDLE_DEBUG_INFO.curry('参考公司仅人保，返回人保的状态'),
            _HANDLE_REFERENCED_RESULTS_BASE_2.curry('piccService')
        ],
        (_CHECK_COMPANY_ONLY_BASE.curry(['ANSWERN']))                                                                                   : [
            _HANDLE_DEBUG_INFO.curry('参考公司仅安心，返回安心的状态'),
            _HANDLE_REFERENCED_RESULTS_BASE_2.curry('answernService')
        ]
    ]

    public static final Map<Object, List<Closure>> _INSURANCE_COMPANY_RULE_PICC_MAPPINGS = [
        (PICC_10000)     : [
            _DO_STH,
            _POST_SUCCESSFUL_RULE_REFERRED_BASE.curry(WEBPARSER_2),
            _POST_QUOTE_RECORD_FAILED_RULE.curry(0.0)
        ],
        (PINGAN_20000)   : [
            _DO_STH,
            _SUCCESSFUL_RULE_OTHER_COMPANY_BASE.curry(REFERENCED_7, _RULE_PINGAN_PREMIUM_FLOAT),
            _POST_QUOTE_RECORD_FAILED_RULE.curry(_RULE_PINGAN_PREMIUM_FLOAT)
        ],
        (CPIC_25000)     : [
            _DO_STH,
            _SUCCESSFUL_RULE_OTHER_COMPANY_BASE.curry(REFERENCED_7, _RULE_CPIC_PREMIUM_FLOAT),
            _POST_QUOTE_RECORD_FAILED_RULE.curry(_RULE_CPIC_PREMIUM_FLOAT)
        ],
        (SINOSIG_15000)  : [
            _DO_STH,
            _SUCCESSFUL_RULE_OTHER_COMPANY_BASE.curry(REFERENCED_7, _RULE_SINOSIG_PREMIUM_FLOAT),
            _POST_QUOTE_RECORD_FAILED_RULE.curry(_RULE_SINOSIG_PREMIUM_FLOAT)
        ],
        (CHINALIFE_40000): [
            _DO_STH,
            _SUCCESSFUL_RULE_OTHER_COMPANY_BASE.curry(REFERENCED_7, _RULE_CHINALIFE_PREMIUM_FLOAT),
            _POST_QUOTE_RECORD_FAILED_RULE.curry(_RULE_CHINALIFE_PREMIUM_FLOAT)
        ],
        (CIC_45000)      : [
            _DO_STH,
            _SUCCESSFUL_RULE_OTHER_COMPANY_BASE.curry(REFERENCED_7, _RULE_CIC_PREMIUM_FLOAT),
            _POST_QUOTE_RECORD_FAILED_RULE.curry(_RULE_CIC_PREMIUM_FLOAT)
        ],
        default          : [
            _DO_NOTHING,
            _SUCCESSFUL_RULE_OTHER_COMPANY_BASE.curry(REFERENCED_7, 0.0),
            _POST_QUOTE_RECORD_FAILED_RULE.curry(0.0)
        ]
    ]

    public static final Map<Object, List<Closure>> _INSURANCE_COMPANY_RULE_BAOXIAN_MAPPINGS = [
        default: [
            _POST_SUCCESSFUL_RULE_REFERRED_BASE.curry(PLANTFORM_BX_6),
            _POST_QUOTE_RECORD_FAILED_RULE.curry(0.0)
        ]
    ]

}
