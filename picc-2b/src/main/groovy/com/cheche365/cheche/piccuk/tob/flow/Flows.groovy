package com.cheche365.cheche.piccuk.tob.flow

import com.cheche365.cheche.common.flow.FlowBuilder
import com.cheche365.cheche.piccuk.tob.flow.step.ContinueToPageLogin
import com.cheche365.cheche.piccuk.tob.flow.step.PageLogin
import com.cheche365.cheche.piccuk.tob.flow.step.Quote
import com.cheche365.cheche.piccuk.tob.flow.step.RelationPersons
import com.cheche365.cheche.piccuk.tob.flow.step.ToInsurance
import com.cheche365.cheche.piccuk.tob.flow.step.ToQuote
import com.cheche365.cheche.piccuk.tob.flow.step.VehicleInfo

class Flows {

    static final _STEP_NAME_CLAZZ_MAPPINGS = [
        // page
        继续页面   : ContinueToPageLogin,
        登录页面   : PageLogin,
        门户页面   : ToInsurance,
        报价页面   : ToQuote,
        填写车辆信息 : VehicleInfo,
        填写关系人信息: RelationPersons,
        保险责任   : Quote,
    ]

    private static get_FLOW_BUILDER() {
        new FlowBuilder(
            nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS
        )
    }

    static final _PAGE_QUOTING_FLOW = _FLOW_BUILDER {
        继续页面 >> 登录页面 >> 门户页面 >> 报价页面 >> 填写车辆信息 >> loop { 填写关系人信息 } >> 保险责任
    }

}
