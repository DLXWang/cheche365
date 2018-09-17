package com.cheche365.cheche.baoxian.flow.step

import com.cheche365.cheche.baoxian.flow.step.v2.ABaoXianCommonStep
import groovy.transform.InheritConstructors
import org.springframework.stereotype.Component

import static com.cheche365.cheche.parser.Constants._FLOW_PARTICIPANT_MESSAGE_2

/**
 * Created by wangxin on 2017/2/13.
 */
@Component
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
abstract class ASubmitQuote extends ABaoXianCommonStep {

    @Override
    run(context) {
        context.flowParticipant?.sendMessage _FLOW_PARTICIPANT_MESSAGE_2

        def params = getParams context

        def result = send context,prefix + API, params

        getReturnFSRV(context,result)
    }

    abstract protected getParams(context)

    abstract protected getReturnFSRV(context,result)

    abstract protected getAPI()
}
