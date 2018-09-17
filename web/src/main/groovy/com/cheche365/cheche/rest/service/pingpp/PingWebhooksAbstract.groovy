package com.cheche365.cheche.rest.service.pingpp

import com.pingplusplus.model.Charge
import com.pingplusplus.model.Event
import com.pingplusplus.model.Order

/**
 * @Author shanxf
 * @Date 2018/1/3  15:57
 */
abstract class PingWebhooksAbstract {
    /**
     * 回掉事件类型
     * @param eventString
     * @return
     */
    abstract boolean webhooksType(String eventString)

    /**
     * 具体回掉事件业务逻辑
     * @param event
     */
    abstract void handle(Map event)

}
