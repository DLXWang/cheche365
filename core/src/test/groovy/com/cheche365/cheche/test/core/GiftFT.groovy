package com.cheche365.cheche.test.core

import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.AutoType
import com.cheche365.cheche.core.model.Gift
import com.cheche365.cheche.core.util.CacheUtil
import spock.lang.Specification

import static com.cheche365.cheche.core.model.GiftStatus.Enum.CREATED_1
import static com.cheche365.cheche.core.model.GiftStatus.Enum.USED_3
import static com.cheche365.cheche.core.model.GiftType.Enum.CASH_37
import static com.cheche365.cheche.core.model.GiftType.Enum.COUPON_3
import static com.cheche365.cheche.core.model.GiftTypeUseType.Enum.GIVENAFTERORDER_3
import static com.cheche365.cheche.core.model.GiftTypeUseType.Enum.REDUCE_1

/**
 * Created by zhengwei on 5/31/17.
 */
class GiftFT extends Specification {

    def "gift排序测试"(){

        given:
        COUPON_3.useType = REDUCE_1
        CASH_37.useType = GIVENAFTERORDER_3

        def data = [
                new Gift(giftDisplay: '加油卡', id: 5l, status: CREATED_1, giftType: CASH_37),
                new Gift(giftAmount: 200d, id: 1l, status: CREATED_1, giftType: COUPON_3),
                new Gift(giftAmount: 500d, id: 6l, status: USED_3, giftType: COUPON_3),
                new Gift(giftAmount: 300d, id: 2l, status: CREATED_1, giftType: COUPON_3),
                new Gift(giftDisplay: '150', id: 3l, status: CREATED_1, giftType: CASH_37),
                new Gift(giftAmount: 300d, id: 4l, status: USED_3, giftType: COUPON_3),
                new Gift(giftDisplay: '加油卡', id: 7l, status: USED_3, giftType: CASH_37)

        ]

        when:
        def result = data.groupBy {it.status}
            .values()
            .each { sameStatus ->
                sameStatus.sort {a, b -> b.comparableAmount() - a.comparableAmount()}
            }
            .sum() as List


        then:
        result.size() == 7
        result.id == [2l, 1l, 3l, 5l, 6l, 4l, 7l]

    }

    def 'de' () {
        when:
        def data = '{"kilometerPerYear":0,"autoType":{"id":5948},"licensePlateNo":"京Ka0812","engineNo":"7635948","owner":"新妖怪","vinNo":"LDCC13L30B1611267","enrollDate":"2013-03-22","area":{"id":110000},"licenseType":null,"licenseColorCode":null,"identity":"110107195701060910","identityType":{"id":1},"insuredIdNo":null,"disable":false,"billRelated":false,"createTime":null,"updateTime":null}'
        def auto = CacheUtil.doJacksonDeserialize(data, Auto)

        def autotype = CacheUtil.doJacksonDeserialize('{"id":5948}', AutoType)

        def at = new AutoType(id: 1)
        def atString = CacheUtil.doJacksonSerialize(at)
        def at1 = CacheUtil.doJacksonDeserialize(atString, AutoType)

        then:
        auto && autotype
    }
}
