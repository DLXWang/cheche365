package com.cheche365.cheche.web.integration.flow

import com.cheche365.cheche.web.model.MessageChannel

import static java.lang.reflect.Modifier.isStatic

/**
 * 配置消息渠道以及消息队列等
 * Integration相关的Constants须实现此特征，便于渠道统一注入上下文
 * Created by liheng on 2018/6/22 0022.
 */
trait TIntegrationConstants {

    List getMessageChannels() {
        this.class.declaredFields.findAll {
            isStatic(it.modifiers) && it.type == MessageChannel
        }.name.collect {
            this[it]
        }
    }
}
