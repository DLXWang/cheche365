package com.cheche365.cheche.taikang.flow

import static com.cheche365.cheche.taikang.flow.Flows._QUOTE_FLOW
import static com.cheche365.cheche.taikang.flow.Flows._INSURE_FLOW
import static com.cheche365.cheche.taikang.flow.Flows._INSURE_FLOW_110000



/**
 * 泰康流程映射
 */
class FlowMappings {

    static final _FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS = [
        default: _QUOTE_FLOW
    ]

    static final _FLOW_CATEGORY_INSURING_FLOW_MAPPINGS = [
        110000L: _INSURE_FLOW_110000,
        default: _INSURE_FLOW
    ]

}
