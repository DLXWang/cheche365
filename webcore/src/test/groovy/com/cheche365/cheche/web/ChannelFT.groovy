package com.cheche365.cheche.web

import com.cheche365.cheche.core.model.Channel

/**
 * Created by zhengwei on 6/2/16.
 */
class ChannelFT extends WebFT {

    def "channel method test"(){

        given: "测试数据"

        def ids = ["23", "25"]

        when: "对比"
        println("${Channel.getDataSourceChannel(ids as String[])}")
        print("${Channel.isInGroup(3, Channel.Enum.WE_CHAT_3)}")
        print("${Channel.isInGroup(8, Channel.Enum.WE_CHAT_3)}")

        then: "检查结果"
        true

    }
}
