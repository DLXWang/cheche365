package com.cheche365.cheche.baoxian.flow.step.v2

import com.cheche365.cheche.common.flow.IStep

import static com.cheche365.cheche.baoxian.flow.Constants._FLOW_ENCRIPT_PARAMS_SENDING_MAPPINGS
import static com.cheche365.cheche.common.util.FlowUtils.getObjectByCityCode

/**
 * Created by wangxin on 2017/12/4.
 */
abstract class ABaoXianCommonStep implements IStep {

    String prefix

    ABaoXianCommonStep(prefix){
        this.prefix = prefix
    }


    def send(context, api, params) {
        def send = getObjectByCityCode context.area, _FLOW_ENCRIPT_PARAMS_SENDING_MAPPINGS
        send context, this.class.name, api, params
    }
}
