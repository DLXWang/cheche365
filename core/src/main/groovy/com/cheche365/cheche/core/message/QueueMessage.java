package com.cheche365.cheche.core.message;

/**
 * Created by zhengwei on 3/18/16.
 */
public abstract class QueueMessage<K, V> {

    String queueName;  //队列名字
    String queueSet;   //队列集合，用于控制只有一个listener能获取到消息，redis不支持原生的message queue

    K key;
    V message;

    public QueueMessage() {
        this.setQueueName(this.getQueueName());
        this.setQueueSet(this.getQueueSet());
    }

    public abstract String getQueueName();

    public QueueMessage setQueueName(String queueName) {
        this.queueName = queueName;
        return this;
    }

    public abstract String getQueueSet();

    public QueueMessage setQueueSet(String queueSet) {
        this.queueSet = queueSet;
        return this;
    }

    public K getKey() {
        return key;
    }

    public QueueMessage setKey(K key) {
        this.key = key;
        return this;
    }

    public V getMessage() {
        return message;
    }

    public QueueMessage setMessage(V message) {
        this.message = message;
        return this;
    }


}
