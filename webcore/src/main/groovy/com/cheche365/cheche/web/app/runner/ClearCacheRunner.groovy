package com.cheche365.cheche.web.app.runner

import com.cheche365.cheche.core.service.agent.AgentRewardCacheHandler
import com.cheche365.cheche.web.service.ClearCacheService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

/**
 * Created by liheng on 2018/5/9 0009.
 */
@Component
@Slf4j
class ClearCacheRunner implements CommandLineRunner {

    @Autowired
    ClearCacheService clearCacheService

    @Autowired
    AgentRewardCacheHandler agentRewardCacheHandler

    @Override
    void run(String... args) throws Exception {

        clearCacheService.clearCache(null)

        agentRewardCacheHandler.initActivityParameter()
    }



}
