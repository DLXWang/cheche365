package com.cheche365.cheche.baoxian.flow

import static com.cheche365.cheche.baoxian.flow.Flows._INSURE_FLOW
import static com.cheche365.cheche.baoxian.flow.Flows._PAYING_FLOW_INSURE_FIRST
import static com.cheche365.cheche.baoxian.flow.Flows._DEDUCTING_FLOW
import static com.cheche365.cheche.baoxian.flow.Flows._INSURE_FIRST_FLOW
import static com.cheche365.cheche.baoxian.flow.Flows._PAYING_FLOW
import static com.cheche365.cheche.baoxian.flow.Flows._QUOTING_FLOW
import static com.cheche365.cheche.baoxian.flow.Flows._QUOTING_FLOW_INSURE_FIRST
import static com.cheche365.cheche.baoxian.flow.Flows._REFUNDING_FLOW


/**
 * 流程步骤所需的常量
 */
class FlowMappings {

    static final _FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS = [
        default : _QUOTING_FLOW_INSURE_FIRST
    ]

    static final _FLOW_CATEGORY_INSURING_FLOW_MAPPINGS = [
        default : _INSURE_FIRST_FLOW
    ]

    static final _FLOW_CATEGORY_REFUNDING_FLOW_MAPPINGS = [
        default : _REFUNDING_FLOW
    ]

    static final _FLOW_CATEGORY_DEDUCTING_FLOW_MAPPINGS = [
        default : _DEDUCTING_FLOW
    ]

    static final _FLOW_CATEGORY_PAYING_FLOW_MAPPINGS = [
        default : _PAYING_FLOW_INSURE_FIRST
    ]
}
