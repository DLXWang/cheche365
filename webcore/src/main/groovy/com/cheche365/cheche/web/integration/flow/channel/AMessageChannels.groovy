package com.cheche365.cheche.web.integration.flow.channel

import org.springframework.messaging.MessageChannel

/**
 * 消息渠道基类
 * 用于扩展org.springframework.integration.dsl.channel.MessageChannels里没有没有的渠道，如topicChannel
 * Created by liheng on 2018/6/22 0022.
 */
abstract class AMessageChannels {

    protected String topicName

    AMessageChannels(String topicName) {
        this.topicName = topicName
    }

    abstract MessageChannel doGet()

    static AMessageChannels redisTopicChannel(String topicName) {
        new RedisTopicChannel(topicName)
    }
}
