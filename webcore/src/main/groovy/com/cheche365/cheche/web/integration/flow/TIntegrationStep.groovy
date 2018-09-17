package com.cheche365.cheche.web.integration.flow

import com.cheche365.cheche.common.flow.IStep

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * Created by liheng on 2018/5/15 0015.
 */
trait TIntegrationStep implements IStep {

    @Override
    run(context) {
        context.flowBuilder = build context
        getContinueFSRV context
    }

    abstract build(context)
}
