package com.cheche365.cheche.web.integration.flow.step

import com.cheche365.cheche.web.integration.flow.TIntegrationStep
import org.springframework.integration.handler.LoggingHandler

import static org.springframework.integration.handler.LoggingHandler.Level.INFO

/**
 * Created by liheng on 2018/5/15 0015.
 */
class Logger implements TIntegrationStep {

    LoggingHandler.Level level
    String category
    Closure closure

    Logger(LoggingHandler.Level level = INFO, String category = null, Closure closure) {
        this.level = level
        this.category = category
        this.closure = closure
    }

    @Override
    def build(context) {
        context.flowBuilder.log level, category, closure
    }
}
