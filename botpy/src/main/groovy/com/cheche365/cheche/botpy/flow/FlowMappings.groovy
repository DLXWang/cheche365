package com.cheche365.cheche.botpy.flow

import static com.cheche365.cheche.botpy.flow.Flows._INSURE_FLOW_110000
import static com.cheche365.cheche.botpy.flow.Flows._INSURE_FLOW
import static com.cheche365.cheche.botpy.flow.Flows._QUERY_VEHICLE_FLOW
import static com.cheche365.cheche.botpy.flow.Flows._QUOTE_FLOW
import static com.cheche365.cheche.botpy.flow.Flows._QUOTE_FLOW_PICC



/**
 * 金斗云流程映射
 */
class FlowMappings {

    static final _FLOW_CATEGORY_QUOTE_FLOW_MAPPINGS = [
        default : _QUOTE_FLOW
    ]

    static final _FLOW_CATEGORY_INSURE_FLOW_MAPPINGS = [
        110000L : _INSURE_FLOW_110000,
        default : _INSURE_FLOW
    ]

    static final _FLOW_CATEGORY_QUERY_VEHICLE_FLOW_MAPPINGS = [
        default : _QUERY_VEHICLE_FLOW
    ]

}
