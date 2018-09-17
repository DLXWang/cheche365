package com.cheche365.cheche.pingan.util

import com.cheche365.cheche.core.model.InsurancePackage
import groovy.json.JsonSlurper

import static com.cheche365.cheche.common.flow.Constants.get_STATUS_CODE_OK
import static com.cheche365.cheche.core.model.GlassType.Enum.DOMESTIC_1
import static com.cheche365.cheche.core.model.GlassType.Enum.IMPORT_2
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.Constants._DRIVER_AMOUNT
import static com.cheche365.cheche.parser.Constants._GLASS_TYPE
import static com.cheche365.cheche.parser.Constants._PASSENGER_AMOUNT
import static com.cheche365.cheche.parser.Constants._SCRATCH_AMOUNT
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_AMOUNT
import static com.cheche365.cheche.parser.Constants._DRIVER_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants._PASSENGER_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants._SCRATCH_AMOUNT_LIST
import static com.cheche365.cheche.parser.Constants._STATUS_CODE_SHOW_INSURANCE_CHANGE_ADVICE
import static com.cheche365.cheche.parser.Constants._THIRD_PARTY_AMOUNT_LIST
import static com.cheche365.cheche.parser.util.BusinessUtils.adjustInsureAmount
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCommercial
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCommercialTimeCause
import static com.cheche365.cheche.parser.util.BusinessUtils.getCommercialInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.setCommercialInsurancePeriodTexts
import static com.cheche365.cheche.pingan.flow.Constants._STATUS_CODE_PINGAN_M_QUOTE_RESULT_INSPECTION_ERROR
import static com.cheche365.cheche.pingan.flow.step.m.Handlers._CODE_INSURANCE_CHINESE_NAME_MAPPINGS_DEFAULT
import static com.cheche365.flow.core.Constants._STATUS_CODE_KNOWN_REASON_ERROR
import static com.cheche365.flow.business.flow.util.BusinessUtils.getVehicleOption
import static groovy.json.JsonParserType.LAX
import static groovy.json.StringEscapeUtils.escapeJava
import static java.time.LocalDate.now as today



/**
 * 业务对象相关的工具
 */
class BusinessUtils {

    //<editor-fold defaultstate="collapsed" desc="处理套餐建议">

    private static final _E_RULES_MAPPINGS = [
        L120 : _E_RULE_ERROR_CODE_L120,
        L121 : _E_RULE_ERROR_CODE_L121,
        default: _E_RULE_ERROR_CODE_DEFAULT
    ]

    private static final _D_RULES_MAPPINGS = [
        default: _D_RULE_ERROR_CODE_DEFAULT
    ]

    private static final _E_RULE_ERROR_CODE_L120 = { rule, context ->
        def m = rule.msg =~ /.*>((\d{4})-(\d{2})-(\d{2}))<.*/
        def commercialStartDate = m.matches() ? _DATE_FORMAT3.parse(m[0][1]) : today()
        setCommercialInsurancePeriodTexts(context, commercialStartDate)
        disableCommercialTimeCause context, 0
        log.info '商业险投保失败，原因：{}', rule.msg
        //不需要再次报价，不需要中断流程，打印消息,流程中断stateCode
        [false, false, rule.msg, _STATUS_CODE_OK]
    }

    private static final _E_RULE_ERROR_CODE_L121 = { rule, context ->
        def m = rule.msg =~ /.*>((\d{4})-(\d{2})-(\d{2}))<.*/
        if (m.matches()) {
            setCommercialInsurancePeriodTexts(context, _DATE_FORMAT3.parse(m[0][1]))
            disableCommercialTimeCause context, 90
        }
        [false, false, rule.msg, _STATUS_CODE_OK]
    }

    private static final _E_RULE_ERROR_CODE_DEFAULT = { rule, context ->
        log.info '商业险投保失败，原因：{}', rule.msg
        //不需要再次报价，不需要中断流程，打印消息,流程中断stateCode
        [false, false, rule.msg, _STATUS_CODE_OK]
    }

    private static final _D_RULE_ERROR_CODE_DEFAULT = { rule, context ->
        log.info '{}', rule.msg
        //不需要再次报价，中断流程，打印消息,流程中断stateCode
        [false, true, [message: '需要客服人员协助报价'], _STATUS_CODE_KNOWN_REASON_ERROR]
    }

    //修改Double类型的值
    private static final _CHANGE_PACKAGE_ITEM_MODIFY_AMOUNT_TYPE1 = { propName, mapping, rule, context ->
        def originalValue = context.accurateInsurancePackage[propName] as double
        def values = rule.limitItems.collect { option ->
            option as double
        } << originalValue
        values.sort()
        def propValue = adjustInsureAmount(originalValue, values)
        [(propName) : propValue]
    }

    private static final _CHANGE_PACKAGE_ITEM_MODIFY_GLASS = { propName, mapping, rule, context ->
        def originalOption = context.accurateInsurancePackage[propName]
        originalOption == DOMESTIC_1 ? [(propName): IMPORT_2] : [(propName): DOMESTIC_1]
    }

    private static final _CHANGE_PACKAGE_ITEM_ADD_ERROR_CODE = { mapping, rule, context ->
        def changePackage = _B_ERROR_CODE_MAPPING[rule.ruleCode] ?: _B_ERROR_CODE_MAPPING.default
        changePackage.call(mapping, rule, context)
    }

    //M站double类型险种的取值选项
    private static final _CODE_TO_OPTIONS_MAPPING_DEFAULT = [
        '02': _THIRD_PARTY_AMOUNT_LIST,
        '04': _DRIVER_AMOUNT_LIST,
        '05': _PASSENGER_AMOUNT_LIST,
        '17': _SCRATCH_AMOUNT_LIST
    ]

    //布尔值类型的添加闭包
    private static final _CHANGE_PACKAGE_ITEM_ADD_TYPE1 = { mapping, rule, context ->
        def propNameENGs = mapping[rule.insuranceCode].propNameENG
        if (propNameENGs instanceof List) {
            propNameENGs.collect { propName ->
                [(propName): 1]
            }
        } else {
            [(propNameENGs): _CODE_TO_OPTIONS_MAPPING_DEFAULT[insuranceCode]?.first() ?: 1]
        }
    }

    private static final _CHANGE_PACKAGE_ITEM_ADD_GLASS = { mapping, rule, context ->
        // do nothing here
    }

    private static final _CHANGE_PACKAGE_ITEM_CANCEL_DEFAULT = { mapping, rule, context ->
        if (rule.msg.contains('取消') || rule.msg.contains('不投保')) {
            def propNameENGs = mapping[rule.insuranceCode].propNameENG
            if (propNameENGs instanceof List) {
                propNameENGs.collectEntries { propName ->
                    [(propName): 0]
                }
            } else {
                [(propNameENGs): 0]
            }
        } else {
            [:]
        }
    }

    private static final _A_RULE_MAPPINGS = [
        'default'  : _CHANGE_PACKAGE_ITEM_CANCEL_DEFAULT
    ]

    private static final _B_RULE_MAPPINGS = [
        '08'       : _CHANGE_PACKAGE_ITEM_ADD_GLASS,
        'errorCode': _CHANGE_PACKAGE_ITEM_ADD_ERROR_CODE,
        'default'  : _CHANGE_PACKAGE_ITEM_ADD_TYPE1
    ]

    private static final _C_RULE_MAPPINGS = [
        '02': _CHANGE_PACKAGE_ITEM_MODIFY_AMOUNT_TYPE1.curry(_THIRD_PARTY_AMOUNT),
        '04': _CHANGE_PACKAGE_ITEM_MODIFY_AMOUNT_TYPE1.curry(_DRIVER_AMOUNT),
        '05': _CHANGE_PACKAGE_ITEM_MODIFY_AMOUNT_TYPE1.curry(_PASSENGER_AMOUNT),
        '08': _CHANGE_PACKAGE_ITEM_MODIFY_GLASS.curry(_GLASS_TYPE),
        '17': _CHANGE_PACKAGE_ITEM_MODIFY_AMOUNT_TYPE1.curry(_SCRATCH_AMOUNT),
    ]

    private static final _TYPE_RULE_MAPPINGS = [
        'A': _A_RULE_MAPPINGS,
        'B': _B_RULE_MAPPINGS,
        'C': _C_RULE_MAPPINGS
    ]

    private static final getErrorMsg(msg) {
        def effectiveMsg
        effectiveMsg = msg.with {
            if (it.matches('.*(增加|减少).*')) {
                [message: '请修改险种', changeAdvice: msg, stateCode: _STATUS_CODE_SHOW_INSURANCE_CHANGE_ADVICE]
            } else if (it.matches('.*(客服|电话联系).*')) {
                [message: '请联系客服', changeAdvice: null, stateCode: _STATUS_CODE_PINGAN_M_QUOTE_RESULT_INSPECTION_ERROR]
            }
        }
        effectiveMsg ?: [message: msg, stateCode: _STATUS_CODE_PINGAN_M_QUOTE_RESULT_INSPECTION_ERROR]
    }

    //如果insuranceCode为空,那么根据insuranceCode跟险种中文名称的映射关系确定firstChangeAdvice中insuranceCode的值
    private static final _REGULATE_INSURANCE_CODE = { mapping, firstChangeAdvice, context ->
        firstChangeAdvice.each { adjustRuleMap ->
            adjustRuleMap.insuranceCode = adjustRuleMap.insuranceCode ?: 'errorCode'
        }
    }

    private static final processInsuranceErrorItem(mapping, insuranceErrorItemList, context) {
        def regulatedAdviceList = insuranceErrorItemList.collect { adjustRuleMap ->
            def typeRules = _TYPE_RULE_MAPPINGS[adjustRuleMap.type]
            def changePackage = typeRules[adjustRuleMap.insuranceCode] ?: typeRules.default
            changePackage.call(mapping, adjustRuleMap, context)
        }
        //获取所有的insuranceItemError
        def insuranceItemErrorList = regulatedAdviceList.findResults { advice ->
            advice.insuranceItemError?.terminateFlow ? advice : null
        }
        if (insuranceItemErrorList.size()) {
            //合并信息
            def message = insuranceItemErrorList.sum {
                it.insuranceItemError.errorMsg.changeAdvice
            }
            regulatedAdviceList.collectEntries().with {
                it.insuranceItemError.errorMsg = getErrorMsg(message)
                it
            }
        } else {
            regulatedAdviceList.collectEntries()
        }
    }

    //将limitItems标准化
    private static final _REGULATE_LIMIT_ITEMS = { firstChangeAdvice, context ->
        firstChangeAdvice.each { rule ->
            //如果limitMax和limitMin都等于-1，则认为limitItems里面有可选的值，不再处理
            if ((rule.limitMax as double) >= 0 && (rule.limitMin as double) >= 0) {
                def insuranceCode = rule.insuranceCode
                //M站的默认值和可选范围
                def options = _CODE_TO_OPTIONS_MAPPING_DEFAULT[insuranceCode]
                //用平安的可选值和车车M站的可选值做交集处理
                def optionInString = context.insuranceItemOptions."bizConfig.amount$insuranceCode"
                def optionInDouble = optionInString.collect {
                    option -> option as double
                }
                def accurateOptions = options.intersect(optionInDouble)
                def (min, max) = ['limitMin', 'limitMax'].collect { k ->
                    //平安M站的三者险返回的数据是万元为单位，这里统一变为元为单位
                    '02' == insuranceCode ? (rule."$k" as double) * 10000 : (rule.limitMax as double)
                }
                rule.limitItems = accurateOptions.findAll { option ->
                    option >= min && option <= max
                }
            }
        }
    }

    private static final _B_RULE_ERROR_CODE_DEFAULT = { mapping, rule, context ->
        log.info '规则修改建议：{}', rule.msg
        //返回的值表明，需要中断流程
        [insuranceItemError: [terminateFlow: true, errorMsg: getErrorMsg(rule.msg)]]
    }

    //抱歉，您上张保单的保险止期与您现在选的保险起期有重复，请填写正确的保险起期。如有疑问可与我们的坐席联系。
    private static final _B_RULE_ERROR_CODE_D135 = { mapping, rule, context ->
        def bsStartDateText = getCommercialInsurancePeriodTexts(context).first as String
        log.info '商业险提前投保，与去年投保时间有重复，投保时间：{}', bsStartDateText
        disableCommercial context
        //不需要中断流程
        [insuranceItemError: [terminateFlow: false, errorMsg: [changeAdvice: "商业险未到投保时间，最早投保时间：${bsStartDateText}", message: rule.msg]]]
    }

    private static final _B_ERROR_CODE_MAPPING = [
        'D135'   : _B_RULE_ERROR_CODE_D135,
        'default': _B_RULE_ERROR_CODE_DEFAULT
    ]

    private static final handleFailRules(context, failRule, rulesMappings) {
        def result
        failRule.'1'.each { rule ->
            def ruleMapping = rulesMappings[rule.ruleCode] ?: rulesMappings.default
            result = ruleMapping(rule, context)
        }
        result
    }

    /**
     * 按照M站返回的修改建议，对accurateInsurancePack进行处理，优先修改D类和E类的建议
     * return:返回值 第一个参数表示是否需要再次报价，第二个参数表示是否需要中断报价流程,第三个参数表示需要打印的信息
     */
    private static final _M_QUOTE_RESULT_INSPECTION_RH_BASE = { mapping, failRules, context ->
        //有failRules，则进行规则的处理，若无，则表明核保通过，不需要再次报价，不需要中断流程，不返回信息
        def errorMsg
        if (!failRules) {
            //不需要再次报价，不需要中断流程，不用打印信息,没有流程中断的stateCode
            return [false, false, null]
        }

        if (failRules.E || failRules.D) {
            def result = failRules.D ? handleFailRules(context, failRules.D, _D_RULES_MAPPINGS)
                            : handleFailRules(context, failRules.E, _E_RULES_MAPPINGS)
            //是否再次报价,是否中断流程的控制，还有错误的信息，由每个建议给出
            //返回信息的案例：不需要再次报价，不需要中断流程，打印消息：[false,false,errorMsg]
            result
        } else {
            //def firstChangeAdvice = failRules.all.'1'
            //不能用上面的方式，因为有时候all里面没有1，但是会有3，（有这样的情况发生过）
            def firstChangeAdvice = failRules.all.entrySet()[0].value.collect {
                it as HashMap
            }
            //规则化修改建议
            [_REGULATE_INSURANCE_CODE.curry(mapping), _REGULATE_LIMIT_ITEMS].collectEntries { regulator ->
                regulator(firstChangeAdvice, context)
            }

            def regulatedAdviceMap = processInsuranceErrorItem(mapping, firstChangeAdvice, context)

            //先对建议当中有insuranceItemError的规则进行处理，如果没有，则进行正常的规则处理,比如在投保的险种比较少（加入只投了三者和车损），failRule的ruleType为B，但是insuranceType被规则化为
            //errorCode,那么在上一步获取的键值对为[insuranceItemError:true],并且也的确不知道要怎么修改套餐，所以不再报价，中断流程，打印信息
            if (regulatedAdviceMap.containsKey('insuranceItemError')) {
                //提取错误信息
                errorMsg = regulatedAdviceMap.insuranceItemError.errorMsg
                def isBreakFlow = regulatedAdviceMap.insuranceItemError.terminateFlow ? true : false
                //不需要再次报价，isBreakFlow为true时需要中断流程，否则不需要中断流程，打印信息,没有流程中断的stateCode
                [false, isBreakFlow, errorMsg]
            } else {
                [false, false, null]
            }
        }
    }

    //适用于费改之后的情况
    static final _M_QUOTE_RESULT_INSPECTION_RH_DEFAULT = _M_QUOTE_RESULT_INSPECTION_RH_BASE.curry(_CODE_INSURANCE_CHINESE_NAME_MAPPINGS_DEFAULT)

    //</editor-fold>


    /**
     * 车型列表option构造闭包
     */
    static final _GET_VEHICLE_OPTION = { context, vehicle ->
        def vehicleOptionInfo = [
            brand          : vehicle.brand_name,
            family         : vehicle.family_name,
            gearbox        : vehicle.gearbox_name,
            exhaustScale   : vehicle.engine_desc,
            model          : vehicle.vehicle_fgw_code,
            productionDate : vehicle.parent_veh_name,
            seats          : vehicle.seat,
            newPrice       : (context.hasTax ?: 0) as int == 1 && !vehicle.supervise ? vehicle.taxprice : vehicle.price,
        ]
        getVehicleOption vehicle.vehicle_id, vehicleOptionInfo
    }


    /**
     * 将json格式的字符串（包含正则表达式）转换成Json对象.
     */
    static final textToJson(String text){
        new JsonSlurper().with {
            type = LAX
            it
        }.parseText text.tokenize('\n').collect { line ->
            !line.contains('regex') ? line :
                line[line.indexOf(':')+1..-1].with { regex ->
                    def regexStr = regex =~ /"(.*)"/
                    "\"regex\" : \"${escapeJava(regexStr[0][1])}\","
                }
        }.join('\n')
    }

    /**
     * 创建平安续保套餐，在续保通道里使用
     */
    static final generateRenewalPackage(result, converts) {
        def amounts = result.bizPremium.amounts
        converts.inject new InsurancePackage(), { insurancePackage, key, value ->
            if (!(key in ['08', '49', '50'])) {
                insurancePackage[(value.propNameENG)] = amounts['amount' + key] as double
            }
            //玻璃险，iop合起来的险，都要特别处理
            if ('08' == key) {
                insurancePackage.glass = amounts['amount08'] as double
                insurancePackage.glassType = insurancePackage.glass ? '1' == amounts['amount08'] ? DOMESTIC_1 : IMPORT_2 : null
            } else if ('49' == key) {
                //司机乘客
                if (value.propNameENG == 'driverIop') {
                    insurancePackage.driverIop = amounts['amount49'] as double && insurancePackage.driverAmount
                } else {
                    insurancePackage.passengerIop = amounts['amount49'] as double && insurancePackage.passengerAmount
                    insurancePackage.driverIop = amounts['amount49'] as double && insurancePackage.driverAmount
                }

            } else if ('50' == key) {
                insurancePackage.scratchIop = amounts['amount50'] as double && insurancePackage.scratchAmount
                insurancePackage.engineIop = amounts['amount50'] as double && insurancePackage.engine
            }
            insurancePackage
        }
    }

}
