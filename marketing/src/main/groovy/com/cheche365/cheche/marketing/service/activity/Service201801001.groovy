package com.cheche365.cheche.marketing.service.activity

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.Marketing
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.marketing.service.MarketingService
import org.springframework.stereotype.Service

import static com.cheche365.cheche.common.Constants._DATE_FORMAT2
import static com.cheche365.cheche.core.exception.BusinessException.Code.ILLEGAL_STATE
import static com.cheche365.cheche.core.util.CacheUtil.putValueWithExpire
import static java.util.Collections.shuffle
import static java.util.concurrent.TimeUnit.DAYS
import static org.apache.commons.lang.time.DateUtils.addDays

/**
 * 2018年1月抽奖活动
 * Created by liheng on 2017/12/27 027.
 */
@Service
class Service201801001 extends MarketingService {

    private static final int AWARD_COUNT = 3
    private static final AWARD_NAME_ACCIDENT_INSURANCE = 2
    private static final AWARD_NAME_GAS_CARD = 4
    private static final AWARD_NAME_PHONE_BILL = 5

    List attends(code) {
        marketingSuccessRepository.findByMarketingIdAndCreateTime(
            [getMarketingByCode(code).id], new Date().clearTime(), addDays(new Date(), 1).clearTime()
        ).findAll { it.detail }.sort { it.createTime }.collect {
            [
                date   : SDF.format(it.createTime),
                content: "恭喜${it.mobile.with { it.substring(0, 3) + '****' + it.substring(7) }}抽中${4 == it.detail ? '100元加油卡' : 5 == it.detail ? '5元话费' : '100万出行意外险'}".toString()
            ]
        }
    }

    @Override
    def attend(Marketing marketing, User user, Channel channel, Map<String, Object> payload) {
        String mobile = loginUnRequired(marketing) ? payload.mobile : user.mobile
        def ms = toMS(marketing.getAmount() as Double, marketing, mobile, channel)
        ms.licensePlateNo = payload.licensePlateNo

        if (checkIsAttend(marketing, mobile)) {
            def awardType = [
                (AWARD_NAME_ACCIDENT_INSURANCE): [
                    id       : 2,
                    name     : '出行意外险',
                    awardText: '您已抽中最高100万出行意外险，请完善信息后领取奖励。'
                ],
                (AWARD_NAME_GAS_CARD)          : [
                    id         : 4,
                    name       : '100元加油卡',
                    awardText  : '您已抽中100元加油卡，预计7个工作日内发放奖励，请耐心等待',
                    maxCount   : 2,
                    probability: 0.05
                ],
                (AWARD_NAME_PHONE_BILL)        : [
                    id         : 5,
                    name       : '5元话费',
                    awardText  : '您已抽中5元话费，预计7个工作日内充值到账，请耐心等待',
                    maxCount   : 70,
                    probability: 0.25
                ]
            ]
            def randomList = awardType.findAll { key, value ->
                value.maxCount ? getAwardedCount(value.id) < value.maxCount : true
            }.with {
                it[AWARD_NAME_ACCIDENT_INSURANCE].probability = 1 - ((it.values().probability - null).sum() ?: 0)
                it
            }.collect { key, value ->
                [key] * (value.probability * 100)
            }.flatten()
            shuffle randomList
            def award = randomList[new Random().nextInt(randomList.size())]

            ms.detail = award
            marketingSuccessRepository.save ms

            if (AWARD_NAME_ACCIDENT_INSURANCE != award) {
                addAwardedCount award
            }

            sendSimpleMessage marketing, channel, mobile

            def userAwardCount = getSweepStakeCount(marketing, mobile)
            awardType[award].subMap(['id', 'name', 'awardText']) << [
                count  : userAwardCount,                    // 剩余抽奖次数
                current: AWARD_COUNT - userAwardCount       // 当前第几次抽奖
            ]
        } else {
            this.doAfterAttend marketingSuccessRepository.save(ms), user, payload
        }
    }

    @Override
    Map<String, Object> isAttend(String code, User user, Map<String, String> params) {
        super.isAttend(code, user, params) << [awardCount: getSweepStakeCount(getMarketingByCode(code), params.mobile ?: user.mobile)]
    }

    @Override
    void preCheck(Marketing marketing, String mobile, Channel channel) {
        super.preCheck marketing, mobile, channel

        if (getSweepStakeCount(marketing, mobile) <= 0) {
            throw new BusinessException(ILLEGAL_STATE, "该手机号抽奖次数已用完!")
        }
    }

    @Override
    def needCheckIsAttend() {
        false
    }

    /**
     * 剩余抽奖次数，用户参加活动会有一条marketingSuccess记录，每抽中一次奖品也会添一条marketingSuccess记录，detail记录奖品id
     * @param marketing
     * @param mobile
     * @return
     */
    private getSweepStakeCount(Marketing marketing, String mobile) {
        def marketingSuccesses = marketingSuccessRepository.findByMobileAndMarketingId mobile, marketing.id
        AWARD_COUNT - marketingSuccesses.count { it.detail }
    }

    /**
     * 获取奖品抽中次数
     * @param awardId
     * @return
     */
    private getAwardedCount(awardId) {
        if (!redisTemplate.hasKey(getAwardedCountKey(awardId))) {
            putAwardedCount awardId, 0
        }
        redisTemplate.opsForValue().get getAwardedCountKey(awardId)
    }

    private addAwardedCount(awardId) {
        putAwardedCount awardId, getAwardedCount(awardId) + 1
    }

    private putAwardedCount(awardId, value) {
        putValueWithExpire redisTemplate, getAwardedCountKey(awardId), value, 1, DAYS
    }

    private static getAwardedCountKey(awardId) {
        new StringBuilder('marketing_awarded_count_').append(awardId).append('_').append(_DATE_FORMAT2.format(new Date())).toString()
    }
}
