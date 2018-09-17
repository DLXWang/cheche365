package com.cheche365.cheche.developer.util

import groovy.json.JsonSlurper
import org.slf4j.Logger
import org.slf4j.LoggerFactory



/**
 * Created by liushijie on 2018/7/19.
 */
class LogMessageUtils {

    private static Logger logger = LoggerFactory.getLogger(LogMessageUtils.class)
    static Object formatMessage(Object message) {

        def messageList = message.split("\n")

        messageList.with {
            it[0..-1]
        }.flatten().with {
            try {
                it[-1] = new JsonSlurper().parseText(it[-1])
            }catch (Exception e){
                logger.error("用户信息同步历史记录转换异常")
            }
            it
        }
    }

}
