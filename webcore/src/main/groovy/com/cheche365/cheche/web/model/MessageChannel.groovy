package com.cheche365.cheche.web.model

import groovy.util.logging.Slf4j

import java.util.concurrent.Executor

import static com.cheche365.cheche.web.app.listener.IntegrationBuilderListener.MessageChannelFactory.getMessageChannel
import static com.cheche365.cheche.web.model.MessageChannel.ChannelType.DIRECT
import static com.cheche365.cheche.web.model.MessageChannel.ChannelType.EXECUTOR
import static com.cheche365.cheche.web.model.MessageChannel.ChannelType.EXECUTOR_CHANNEL
import static com.cheche365.cheche.web.model.MessageChannel.ChannelType.POLLABLE_CHANNEL
import static com.cheche365.cheche.web.model.MessageChannel.ChannelType.REDIS_TOPIC
import static java.lang.Integer.MAX_VALUE
import static org.springframework.integration.support.MessageBuilder.withPayload

/**
 * Created by liheng on 2018/5/18 0018.
 */
@Slf4j
class MessageChannel<T> {

    private String channelName
    private String topicName
    private ChannelType channelType
    private Integer capacity
    private Executor executor

    MessageChannel(String channelName, ChannelType channelType = DIRECT, Integer capacity = MAX_VALUE) {
        this.channelName = channelName
        this.channelType = channelType
        this.capacity = capacity
    }

    MessageChannel(String channelName, String topicName, ChannelType channelType = REDIS_TOPIC) {
        this.channelName = channelName
        this.topicName = topicName
        this.channelType = channelType
    }

    MessageChannel(String channelName, Executor executor, ChannelType channelType = EXECUTOR) {
        this.channelName = channelName
        this.channelType = channelType
        this.executor = executor
    }

    String getChannelName() {
        return channelName
    }

    ChannelType getChannelType() {
        return channelType
    }

    Integer getCapacity() {
        return capacity
    }

    String getTopicName() {
        return topicName
    }

    Executor getExecutor() {
        return executor
    }

    boolean needClone() {
        this.channelType in POLLABLE_CHANNEL + EXECUTOR_CHANNEL
    }

    boolean send(Message<T> message) {
        log.debug 'id：{} payloadClassType：{} 发送至 {} {}', message.id, message.payloadClassType, this.channelName, message
        getMessageChannel(this)?.send withPayload(this.needClone() ? message.clone() : message).build()
    }

    boolean send(T message) {
        log.debug '消息发送至 {} {}', this.channelName, message
        getMessageChannel(this)?.send withPayload(this.needClone() ? message.clone() : message).build()
    }

    enum ChannelType {

        DIRECT, QUEUE, PUBLISH_SUBSCRIBE, REDIS_TOPIC, PRIORITY, EXECUTOR

        public static final POLLABLE_CHANNEL = [QUEUE, PRIORITY]
        public static final EXECUTOR_CHANNEL = [EXECUTOR, PUBLISH_SUBSCRIBE]
    }
}
