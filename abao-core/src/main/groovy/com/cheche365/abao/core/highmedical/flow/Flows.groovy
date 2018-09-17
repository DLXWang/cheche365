package com.cheche365.abao.core.highmedical.flow

import com.cheche365.abao.core.highmedical.flow.step.QuotePrice
import com.cheche365.cheche.common.flow.FlowBuilder


/**
 * 阿宝高端医疗流程
 */
class Flows {

    static final _STEP_NAME_CLAZZ_MAPPINGS = [
        报价: QuotePrice
    ]

    private static get_FLOW_BUILDER() {
        new FlowBuilder(nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS)
    }

    static final _FLOW_CATEGORY_QUOTING_FLOW = _FLOW_BUILDER {
        报价
    }

}
