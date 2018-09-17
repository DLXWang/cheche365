package com.cheche365.cheche.web.integration.flow.step.from

import com.cheche365.cheche.web.integration.flow.TIntegrationStep
import org.springframework.integration.dsl.IntegrationFlowBuilder

/**
 * 消息来源节点基类，流程开始必须是此类节点
 * Created by liheng on 2018/6/14 0014.
 */
abstract class AMessageFrom implements TIntegrationStep {

    private IntegrationFlowBuilder integrationFlowBuilder

    abstract from(context)

    @Override
    def build(context) {
        from context
    }
}
