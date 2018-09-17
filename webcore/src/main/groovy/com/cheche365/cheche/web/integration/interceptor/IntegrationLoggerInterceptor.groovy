package com.cheche365.cheche.web.integration.interceptor

import groovy.util.logging.Slf4j
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.stereotype.Component

/**
 * Created by liheng on 2018/5/21 0021.
 */
@Slf4j
@Component
class IntegrationLoggerInterceptor implements ChannelInterceptor {

    @Override
    Message<?> preSend(Message<?> message, MessageChannel channel) {
        message
    }

    @Override
    void postSend(Message<?> message, MessageChannel channel, boolean sent) {

    }

    @Override
    void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        log.info '消息：{} 发送{} messageID：{}', message.headers.getId(), sent ? '成功' : '失败', message.payload.id
    }

    @Override
    boolean preReceive(MessageChannel channel) {
        true
    }

    @Override
    Message<?> postReceive(Message<?> message, MessageChannel channel) {
        message
    }

    @Override
    void afterReceiveCompletion(Message<?> message, MessageChannel channel, Exception ex) {
        if (message) {
            log.info '消息：{} 接收成功 messageID：{}', message.headers.getId(), message.payload.id
        }
    }
}
