package com.cheche365.cheche.web.service.http

import com.cheche365.cheche.core.util.RuntimeUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.servlet.http.HttpSession

/**
 * Created by zhengwei on 30/11/2017.
 */

@Service
@Slf4j
class SessionScopeLogger {

    @Autowired(required = false)
    public HttpSession session

    static final SESSION_KEY_LOG_TRACE ="log_trace"


    void debug(String subPath, String message, Object... arguments) {
        if(!RuntimeUtil.isProductionEnv()){
            def subLogs = findExistedLogs(subPath)

            if(arguments){
                subLogs << [(addPrefix(message)) : arguments as List]
            } else {
                subLogs << addPrefix(message)
            }
        }

    }

    void debugVL(String message, Object... arguments) {
        debug('vl', message, arguments)
    }

    void debugQuote(String message, Object... arguments) {
        debug('quote', message, arguments)
    }

    void debugOrder(String message, Object... arguments) {
        debug('order', message, arguments)
    }

    void debugPayment(String message, Object... arguments) {
        debug('payment', message, arguments)
    }

    void clear(){
        session.removeAttribute(SESSION_KEY_LOG_TRACE)
    }

    static addPrefix(String message){
        "${new Date().format('HH:mm:ss')} $message".toString()
    }

    def findExistedLogs(String subPath) {
        def existedLogs = session.getAttribute(SESSION_KEY_LOG_TRACE) ?: [:]
        def subLogs = existedLogs."$subPath" ?: []
        existedLogs."$subPath" = subLogs
        session.setAttribute(SESSION_KEY_LOG_TRACE, existedLogs)
        return subLogs


    }
}
