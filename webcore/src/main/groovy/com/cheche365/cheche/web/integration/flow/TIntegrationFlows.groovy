package com.cheche365.cheche.web.integration.flow

/**
 * 消息流程基类
 * Created by liheng on 2018/5/20 0020.
 */
trait TIntegrationFlows {

    /**
     * 消息端点
     * @return
     */
    abstract Map<Object, Class> get_STEP_NAME_CLAZZ_MAPPINGS()

    IntegrationFlowBuilder getFlowBuilder() {
        new IntegrationFlowBuilder(nameClazzMappings: _STEP_NAME_CLAZZ_MAPPINGS)
    }

    abstract List get_FLOWS()
}
