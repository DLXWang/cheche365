package com.cheche365.cheche.marketing.service.activity

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.Marketing
import com.cheche365.cheche.core.model.MarketingSuccess
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.marketing.model.AttendResult
import com.cheche365.cheche.marketing.service.MarketingService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

import static com.cheche365.cheche.common.Constants.get_DATE_FORMAT2
import static com.cheche365.cheche.core.exception.BusinessException.Code.ILLEGAL_STATE
import static com.cheche365.cheche.core.model.Channel.toChannel
import static com.cheche365.cheche.core.util.StringRedisUtil.addCount
import static com.cheche365.cheche.core.util.StringRedisUtil.addHash
import static com.cheche365.cheche.core.util.StringRedisUtil.addSet
import static com.cheche365.cheche.core.util.StringRedisUtil.getCount
import static com.cheche365.cheche.core.util.StringRedisUtil.getHash
import static com.cheche365.cheche.core.util.StringRedisUtil.getValue
import static com.cheche365.cheche.core.util.StringRedisUtil.isInSet
import static java.util.Collections.shuffle
import static java.util.UUID.randomUUID
import static org.apache.commons.lang3.SerializationUtils.clone

/**
 * 2月抽奖活动
 * Created by liheng on 2018/1/17 017.
 */
@Service
@Slf4j
class Service201802001 extends MarketingService {

    private static final MARKETING_CACHE_TIMEOUT = 40
    private static final AWARD_NAME_ACCIDENT_INSURANCE = 1L
    private static final AWARD_NAME_PHONE_BILL = 2L
    private static final AWARD_NAME_IQIYI_VIP = 3L
    private static final AWARD_NAME_AGAIN_1 = 4L
    private static final AWARD_NAME_AGAIN_2 = 5L
    private static final AWARD_NAME_AGAIN_3 = 6L

    /**
     * maxCount    每天最大中奖次数
     * probability 中奖概率
     * prAddition  奖品抽完后概率叠加的奖品
     */
    private static final AWARD_TYPE = [
        (AWARD_NAME_ACCIDENT_INSURANCE): [
            name       : '出行意外险',
            probability: 0.5
        ],
        (AWARD_NAME_PHONE_BILL)        : [
            name       : '5元话费',
            maxCount   : 30,
            probability: 0.02,
            prAddition : AWARD_NAME_AGAIN_2
        ],
        (AWARD_NAME_IQIYI_VIP)         : [
            name       : '爱奇艺黄金会员',
            maxCount   : 5,
            probability: 0.01,
            prAddition : AWARD_NAME_AGAIN_3
        ],
        (AWARD_NAME_AGAIN_1)           : [
            name       : '88元现金红包',
            probability: 0.15
        ],
        (AWARD_NAME_AGAIN_2)           : [
            name       : '888元现金红包',
            probability: 0.15
        ],
        (AWARD_NAME_AGAIN_3)           : [
            name       : '财富金条20克',
            probability: 0.17
        ]
    ]

    @Autowired
    private StringRedisTemplate redisTemplate

    /**
     * 中奖及领取记录，奖品不可重复领取
     * @param code
     * @return
     */
    List attends(code) {
        super.isAttend code, this.safeGetCurrentUser(), [:]
        def awardType = clone AWARD_TYPE
        marketingSuccessRepository.findByMobileAndMarketingId(this.safeGetCurrentUser().mobile, getMarketingByCode(code).id).findAll {
            [AWARD_NAME_ACCIDENT_INSURANCE, AWARD_NAME_PHONE_BILL, AWARD_NAME_IQIYI_VIP].contains it.detail
        }.groupBy { it.detail }.collect { key, value ->
            [
                id        : key,
                name      : awardType[key].name,
                isReceived: value.any { 1 == it.status }
            ]
        }
    }

    /**
     * 抽奖/领奖 有awardId为领奖
     * @param marketing
     * @param user
     * @param channel
     * @param payload
     * @return
     */
    @Override
    def attend(Marketing marketing, User user, Channel channel, Map<String, Object> payload) {
        String mobile = loginUnRequired(marketing) ? payload.mobile : user.mobile
        def ms = toMS(marketing.getAmount() as Double, marketing, mobile, channel)
        ms.licensePlateNo = payload.licensePlateNo

        if (!payload.awardId) {
            if (getAwardCount(marketing, mobile) <= 0) {
                throw new BusinessException(ILLEGAL_STATE, "该手机号抽奖次数已用完!")
            }
            def awardType = clone AWARD_TYPE
            def randomList = awardType.findAll { key, value ->
                value.maxCount ? getCount(redisTemplate, getAwardedCountKey(marketing.code, key)) < value.maxCount : true
            }.with {
                awardType.each { key, value ->
                    if (value.prAddition && !it[key]) {
                        it[value.prAddition].probability = it[value.prAddition].probability + value.probability
                    }
                }
                it
            }.collect { key, value ->
                [key] * (value.probability * 100)
            }.flatten()
            shuffle randomList
            def award = randomList[new Random().nextInt(randomList.size())]

            ms.detail = award
            marketingSuccessRepository.save ms

            addCount redisTemplate, getAwardCountKey(marketing.code, mobile), -1

            if (awardType.findAll { it.value.maxCount }.keySet().contains(award)) {
                addCount redisTemplate, getAwardedCountKey(marketing.code, award)
            }

            sendSimpleMessage marketing, channel, mobile

            [
                id      : award,
                name    : awardType[award].name,
                canAward: getAwardCount(getMarketingByCode(marketing.code), user.mobile) > 0
            ]
        } else {
            this.doAfterAttend ms, user, payload
        }
    }

    /**
     * 检查是否可以抽奖
     * @param code
     * @param user
     * @param params
     * @return
     */
    @Override
    Map<String, Object> isAttend(String code, User user, Map<String, String> params) {
        def channel = toChannel session.getAttribute(SESSION_KEY_CLIENT_TYPE) as Long
        def result = super.isAttend code, user, params
        def mobile = user.mobile
        // 生成用户uuid并缓存至redis
        def uuid = getValue redisTemplate, getUUIDKey(code, mobile), randomUUID().toString(), MARKETING_CACHE_TIMEOUT
        addHash redisTemplate, getCacheUUIDKey(code), [(uuid): mobile], MARKETING_CACHE_TIMEOUT

        // 为分享者助力，自己不能给自己助力，不能重复助力，shareUUID会在MarketingInterceptor存到session
        def shareUUID = session.getAttribute getMarketingUUIDSessionKey(code)
        def shareMobile = shareUUID ? getHash(redisTemplate, getCacheUUIDKey(code), shareUUID) : null
        if (shareMobile && mobile != shareMobile && !isInSet(redisTemplate, getShareMobileSetKey(code, shareMobile), mobile)) {
            addSet redisTemplate, getShareMobileSetKey(code, shareMobile), mobile, MARKETING_CACHE_TIMEOUT
            addCount redisTemplate, getAwardCountKey(code, shareMobile)
        }
        result << [
            shareLink: getMarketingCodeURL(code) + '?toShare=true&uuid=' + uuid + (channel.isPartnerChannel() ? '&channel=' + channel.apiPartner.code : ''),
            canAward : getAwardCount(getMarketingByCode(code), mobile) > 0
        ]
    }

    /**
     * 领奖
     * @param marketingSuccess
     * @param user
     * @param payload
     * @return
     */
    @Override
    def doAfterAttend(MarketingSuccess marketingSuccess, User user, Map<String, Object> payload) {
        marketingSuccessRepository.findByMobileAndMarketingId(user.mobile, marketingSuccess.marketing.id).findAll {
            payload.awardId as Long == it.detail
        }.with {
            if (!it) {
                throw new BusinessException(ILLEGAL_STATE, "未抽中奖品不可领取!")
            } else if (it.any { ms -> 1 == ms.status }) {
                throw new BusinessException(ILLEGAL_STATE, "奖品已经领取成功，不可重复领取!")
            } else {
                marketingSuccessRepository.save it.first().with { ms ->
                    ms.status = 1
                    ms
                }
                new AttendResult(message: '成功领取奖品。')
            }
        }
    }

    @Override
    String getOauthRedirectUrl(Map stateParams) {
        'redirect:' + getMarketingCodeURL(stateParams.code)
    }

    /**
     * 用户剩余抽奖次数
     * @param marketing
     * @param mobile
     * @return
     */
    private getAwardCount(Marketing marketing, String mobile) {
        getCount redisTemplate, getAwardCountKey(marketing.code, mobile), 1, MARKETING_CACHE_TIMEOUT
    }

    /**
     * 奖品抽中次数
     * @param marketingCode
     * @param awardId
     * @return
     */
    private static getAwardedCountKey(marketingCode, awardId) {
        "marketing_${marketingCode}_awarded_count_${awardId}_${_DATE_FORMAT2.format(new Date())}".toString()
    }

    /**
     * 用户剩余抽奖次数
     * @param marketingCode
     * @param mobile
     * @return
     */
    private static getAwardCountKey(marketingCode, mobile) {
        "marketing_${marketingCode}_${mobile}_award_count".toString()
    }

    /**
     * 手机号绑定uuid
     * @param marketingCode
     * @param mobile
     * @return
     */
    private static getUUIDKey(marketingCode, mobile) {
        "marketing_${marketingCode}_${mobile}_uuid".toString()
    }

    /**
     * 助力记录
     * @param marketingCode
     * @param mobile
     * @return
     */
    private static getShareMobileSetKey(marketingCode, mobile) {
        "marketing_${marketingCode}_${mobile}_share_mobile_set".toString()
    }

    /**
     * 缓存[uuid:mobile] 用户通过uuid查找手机号
     * @param marketingCode
     * @return
     */
    private static getCacheUUIDKey(marketingCode) {
        "marketing_${marketingCode}_cache_uuid_mobile".toString()
    }
}
