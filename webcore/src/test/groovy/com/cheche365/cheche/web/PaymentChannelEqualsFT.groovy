package com.cheche365.cheche.web

import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.core.model.PurchaseOrder

/**
 * Created by zhengwei on 6/14/16.
 */
class PaymentChannelEqualsFT extends WebFT {

    def "compare equals"(){

        given: "payment from db and payment from enum"

        PurchaseOrder order = nextOrder()

        when: "prepare data"
        def payments = payment(order)
        payments.each { chDb ->
            print(chDb.channel.channel)
            print("from db ${chDb.channel.hashCode()}")
            def fromEnum = PaymentChannel.Enum.ALL.find { ch -> ch.id == chDb.channel.id}
            print("from enum ${fromEnum.hashCode()}")

            print("${chDb.channel.equals(fromEnum)}  ${chDb.channel.is(fromEnum)}")
        }

        then: "compare result"
        true



    }
}
