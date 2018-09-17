package com.cheche365.cheche.web

import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.util.BeanUtil

/**
 * Created by zhengwei on 6/1/16.
 */
class BeanCompareFT extends WebFT {

    def "bean compare test"(){

        given: "测试数据"

        OrderStatus statusOne = OrderStatus.Enum.PENDING_PAYMENT_1
        OrderStatus statusAnother = order('T20160527000003').status

        when: "对比"
        println("${statusOne.status} ${statusAnother.status} ${statusOne==statusAnother}")
        println(BeanUtil.compare(statusOne, statusAnother))

        then: "检查结果"
        true

    }
}
