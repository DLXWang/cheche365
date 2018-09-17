package com.cheche365.cheche.bihu.flow

import static Flows._VEHICLE_LICENSE_FLOW
import static com.cheche365.cheche.bihu.flow.Flows._INSURE_FLOW
import static com.cheche365.cheche.bihu.flow.Flows._QUOTE_FLOW


/**
 * 壁虎流程步骤所需的常量
 */
class FlowMappings {

    static final _FLOW_CATEGORY_INSURANCE_INFO_FLOW_MAPPINGS = [
        default : _VEHICLE_LICENSE_FLOW
    ]

    static final _FLOW_CATEGORY_QUOTE_FLOW_MAPPINGS = [
        default : _QUOTE_FLOW
    ]

    static final _FLOW_CATEGORY_INSURE_FLOW_MAPPINGS = [
        default : _INSURE_FLOW
    ]

}
