package com.cheche365.cheche.aibao.flow

import static com.cheche365.cheche.aibao.flow.Flows._QUOTE_FLOW
import static com.cheche365.cheche.aibao.flow.Flows._INSURE_FLOW
import static com.cheche365.cheche.aibao.flow.Flows._INSURE_FLOW_110000



/**
 * 爱保流程映射
 */
class FlowMappings {

    static final _FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS = [
        default: _QUOTE_FLOW
    ]

    static final _FLOW_CATEGORY_QUERYVECHILE_FLOW_MAPPINGS = [
//            default:_QUERYVECHILE_FLOW
    ]

    static final _FLOW_AIBAO_INSURING_FLOW_MAPPINGS = [
        110000L: _INSURE_FLOW_110000,
        default: _INSURE_FLOW
    ]

}
