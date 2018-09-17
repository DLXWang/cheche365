/**
 * Created by zhengwei on 3/26/16.
 *
 * 消息相关工具类。通过消息系统可以大大简化项目之间的依赖关系，同时也减少代码的耦合度。目前支持两种消息：
 * 1. 进程内部消息。通过{@link com.google.common.eventbus.EventBus}。当前package下有些用{@link org.springframework.context.ApplicationEventPublisher}发布消息的代码，后续都会迁移到{@link com.google.common.eventbus.EventBus}。
 * 2. 进程间消息（比如web到order center）。使用{@link com.cheche365.cheche.core.message.RedisPublisher}发布消息。
 */
package com.cheche365.cheche.core.message;
