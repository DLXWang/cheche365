package com.cheche365.cheche.sinosig.flow

import static Flows._INSURING_FLOW_TYPE1
import static com.cheche365.cheche.sinosig.flow.Flows._INSURANCE_BASIC_INFO_FLOW_DEFAULT
import static com.cheche365.cheche.sinosig.flow.Flows._INSURANCE_INFO_FLOW_DEFAULT
import static com.cheche365.cheche.sinosig.flow.Flows._QUOTING_FLOW_COMM_TYPE1

/**
 * Created by suyq on 2015/8/19.
 * 城市流程定义映射关系
 */
class FlowMappings {

    static final _FLOW_CATEGORY_QUOTING_FLOW_MAPPINGS = [
        default : _QUOTING_FLOW_COMM_TYPE1
    ]

    static final _FLOW_CATEGORY_INSURING_FLOW_MAPPINGS = [
        default : _INSURING_FLOW_TYPE1
    ]

    static final _FLOW_CATEGORY_INSURANCE_BASIC_INFO_FLOW_MAPPINGS = [
            default : _INSURANCE_BASIC_INFO_FLOW_DEFAULT
    ]


    static final _FLOW_CATEGORY_INSURANCE_INFO_FLOW_MAPPINGS = [
            default : _INSURANCE_INFO_FLOW_DEFAULT
    ]
}
