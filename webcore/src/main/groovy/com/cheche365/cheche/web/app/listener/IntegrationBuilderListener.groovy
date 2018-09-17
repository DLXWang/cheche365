package com.cheche365.cheche.web.app.listener

import com.cheche365.cheche.web.integration.flow.TIntegrationConstants
import com.cheche365.cheche.web.integration.flow.TIntegrationFlows
import com.cheche365.cheche.web.model.MessageChannel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.integration.dsl.context.IntegrationFlowContext
import org.springframework.messaging.MessageChannel as SpringMessageChannel
import org.springframework.messaging.support.ChannelInterceptor

import static com.cheche365.cheche.core.context.ApplicationContextHolder.getApplicationContext
import static com.cheche365.cheche.core.tools.SystemTool.newInstancesByClass
import static com.cheche365.cheche.web.integration.flow.channel.AMessageChannels.redisTopicChannel
import static com.cheche365.cheche.web.model.MessageChannel.ChannelType.DIRECT
import static com.cheche365.cheche.web.model.MessageChannel.ChannelType.EXECUTOR
import static com.cheche365.cheche.web.model.MessageChannel.ChannelType.PRIORITY
import static com.cheche365.cheche.web.model.MessageChannel.ChannelType.PUBLISH_SUBSCRIBE
import static com.cheche365.cheche.web.model.MessageChannel.ChannelType.QUEUE
import static com.cheche365.cheche.web.model.MessageChannel.ChannelType.REDIS_TOPIC
import static org.springframework.integration.dsl.channel.MessageChannels.direct
import static org.springframework.integration.dsl.channel.MessageChannels.executor
import static org.springframework.integration.dsl.channel.MessageChannels.priority
import static org.springframework.integration.dsl.channel.MessageChannels.publishSubscribe
import static org.springframework.integration.dsl.channel.MessageChannels.queue

/**
 * Created by liheng on 2018/5/18 0018.
 */
class IntegrationBuilderListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    IntegrationFlowContext context

    @Autowired(required = false)
    List<ChannelInterceptor> channelInterceptors

    @Override
    void onApplicationEvent(ContextRefreshedEvent event) {
        def integrationBuilders = applicationContext ? newInstancesByClass(TIntegrationFlows) : null
        def constants = applicationContext ? newInstancesByClass(TIntegrationConstants) : null
        if (constants && integrationBuilders && !MessageChannelFactory.messageChannels) { // 防止多次注入
            MessageChannelFactory.messageChannels = constants.messageChannels.flatten().inject([:]) { messageChannels, messageChannel ->
                messageChannels << registerChannel(messageChannel)
            }
            integrationBuilders._FLOWS.flatten().each {
                context.registration(it.buildFlow([:])).register()
            }
        }
    }

    private registerChannel(MessageChannel messageChannel) {
        def messageChannelTemplateMappings = [
            (DIRECT)           : direct(),
            (QUEUE)            : queue(messageChannel.capacity),
            (PUBLISH_SUBSCRIBE): messageChannel.executor ? publishSubscribe(messageChannel.executor) : publishSubscribe(),
            (REDIS_TOPIC)      : redisTopicChannel(messageChannel.topicName),
            (PRIORITY)         : priority().with { it.setCapacity(messageChannel.capacity); it },
            (EXECUTOR)         : executor(messageChannel.executor)
        ]
        ((DefaultListableBeanFactory) applicationContext.autowireCapableBeanFactory).registerSingleton messageChannel.channelName, channelInterceptors.inject(messageChannelTemplateMappings[messageChannel.channelType].get()) { channel, interceptor ->
            channel.addInterceptor interceptor
            channel
        }
        [(messageChannel): applicationContext.getBean(messageChannel.channelName)]
    }

    class MessageChannelFactory {

        private static Map<MessageChannel, SpringMessageChannel> messageChannels

        static SpringMessageChannel getMessageChannel(MessageChannel messageChannel) {
            messageChannels?.get messageChannel
        }
    }
}
