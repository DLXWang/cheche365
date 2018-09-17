package com.cheche365.cheche.chinalife.flow.step

import com.cheche365.cheche.common.flow.IStep

import static com.cheche365.cheche.chinalife.util.BusinessUtils._KIND_CODE_CONVERTERS
import static com.cheche365.cheche.chinalife.util.BusinessUtils.changeInsurancePackageOption
import static com.cheche365.cheche.chinalife.util.BusinessUtils.checkMandatoryKindItems
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * 根据国寿财前端逻辑，检查必须投保的险种
 */
class CheckKindItems implements IStep {

    @Override
    run(Object context) {
        def params = context.carVerify
        def newCarLimitKindFlag = params.UInewCarLimitKindFlag
        // TODO : JSON中可能是“B_第三者责任保险;”
        def newCarLimitKindFlagMessage = params.UInewCarLimitKindFlagMessage?.tokenize(';')?.collect { rule ->
            rule.tokenize('_')[0]
        }
        def newCarControlSpecialRiskFlag = params.UInewCarControlSpecialRiskFlag
        def newCarControlSpecialRiskFlagMessage = params.UInewCarControlSpecialRiskFlagMessage?.tokenize(';')?.collectEntries { rule ->
            def (kindCodeText, adviceTexts)  = rule.tokenize(':')
            def kindCode = _KIND_CODE_CONVERTERS.findResult { conf ->
                conf[0] == kindCodeText.tokenize('_')[0] ? conf[1] : null
            }
            def adviceKindCodes = adviceTexts.tokenize(',').collect {
                it.tokenize('_')[0]
            }
            [(kindCode) : adviceKindCodes]
        }

        def mandatoryKindItems = []
        if ('1' == newCarLimitKindFlag && newCarLimitKindFlagMessage) {
            mandatoryKindItems = checkMandatoryKindItems context, newCarLimitKindFlagMessage
        }

        if ('1' == newCarControlSpecialRiskFlag && newCarControlSpecialRiskFlagMessage) {
            mandatoryKindItems += newCarControlSpecialRiskFlagMessage.collectMany { propName, dependencies ->
                if (context.insurancePackage[propName]) { // 判断是否投保了
                    dependencies.collectMany {
                        checkMandatoryKindItems context, it
                    }
                } else {
                    []
                }
            }.unique()
        }

        mandatoryKindItems.each {
            // 修改套餐
            changeInsurancePackageOption context, it
        }

        getContinueFSRV true
    }

}
