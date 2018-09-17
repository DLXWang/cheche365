package com.cheche365.cheche.scheduletask.task

import com.cheche365.cheche.externalapi.api.agentParser.AgentParserStatusAPI
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Slf4j
class AgentParserStatusTask extends BaseTask{
    @Autowired
    AgentParserStatusAPI agentParserStatusAPI

    @Override
    protected void doProcess() throws Exception {
        log.info("开始小鳄鱼定时任务，批量查询订单支付状态")
        def result = agentParserStatusAPI.call([:])
        log.info("定时任务查询小鳄鱼支付状态结果：{}", result)

    }
}
