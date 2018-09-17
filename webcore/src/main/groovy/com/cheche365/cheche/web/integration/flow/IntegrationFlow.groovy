package com.cheche365.cheche.web.integration.flow

import com.cheche365.cheche.common.flow.IFlow

/**
 * Integration消息流程
 * Created by liheng on 2018/6/15 0015.
 */
class IntegrationFlow implements IFlow {

    private Tuple2 stepsWithRouter

    @Override
    def run(context) {
        def (step, router) = stepsWithRouter
        if (prefabFSRV.get()) {
            step.prefabFSRV.set(prefabFSRV.get())
            prefabFSRV.remove()
        }
        router(*[context, step.run(context)].flatten())
    }

    def buildFlow(context) {
        run(context).with { flag, code, payload, msg ->
            payload.flowBuilder.get()
        }
    }
}
