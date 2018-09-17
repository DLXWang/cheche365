package com.cheche365.cheche.core.service.agent

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.model.agent.ChannelAgent
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

/**
 * Author:   shanxf
 * Date:     2018/9/3 12:31
 */
@Component
@Slf4j
class AgentRewardCacheHandler {

    public static final String _ACTIVITY_HASH_KEY = 'activity_hash_key'

    public static final String _MAX_REGISTERS = 'max_new_user_count'
    public static final String _REGISTERS = 'current_new_user_count'
    public static final String _MAX_INVITES = 'max_invites_count'
    public static final String _INVITES = 'current_invites_count'
    public static final String _INDIVIDUAL_INVITES = 'max_invites_count_each_user'

    public static final String _REGISTER_REBATE = 'register_rebate_amount'
    public static final String _INVITE_REBATE = 'register_rebate_amount_for_parent'
    public static final String _INVITE_FIRST_ORDER_REBATE = 'first_order_rebate_amount_for_parent'
    public static final String _FIRST_ORDER_REBATE = 'first_order_rebate_amount'
    public static final String _INDIVIDUAL_INVITE_ORDER = 'individual_max_invites_order_count'

    public static final String _BEGIN_TIME = 'begin_time'
    public static final String _END_TIME = 'end_time'

    public static final String _AGENT_COUNT_HASH_KEY = 'agent_count_hash_key'
    public static final String _INVITE_REGISTER_PRE = 'invite_register'
    public static final String _INVITE_CA_REGISTER_PRE = 'invite_first_order_register'

    private static final Map _DEFAULT_VALUE = [
        (_MAX_REGISTERS)            : '3888',
        (_REGISTERS)                : '0',
        (_MAX_INVITES)              : '5888',
        (_INDIVIDUAL_INVITES)       : '3',
        (_INVITES)                  : '0',
        (_REGISTER_REBATE)          : '20',
        (_INVITE_REBATE)            : '10',
        (_INVITE_FIRST_ORDER_REBATE): '15',
        (_FIRST_ORDER_REBATE)       : '30',
        (_INDIVIDUAL_INVITE_ORDER)  : '3',
        (_BEGIN_TIME)               : '2018-09-07 00:00:00',
        (_END_TIME)                 : '2020-01-01 00:00:00'
    ].asImmutable()

    @Autowired
    StringRedisTemplate stringRedisTemplate

    void initActivityParameter() {

        if (!stringRedisTemplate.hasKey(_ACTIVITY_HASH_KEY)) {
            log.info('begin init toA activity parameter')
            stringRedisTemplate.opsForHash().putAll(_ACTIVITY_HASH_KEY, _DEFAULT_VALUE)
            log.info('end init toA activity parameter')
        }
    }

    def final registerLimit = {
        stringRedisTemplate.opsForHash().get(_ACTIVITY_HASH_KEY, _REGISTERS).with {
            !it || Long.valueOf(it as String) < Long.valueOf(getRebateFromCache(_MAX_REGISTERS))
        }
    }

    def final inviteRegisterLimit = { ChannelAgent channelAgent ->
        stringRedisTemplate.opsForHash().get(_AGENT_COUNT_HASH_KEY, _GET_HASH_KEY.call(_INVITE_REGISTER_PRE, channelAgent.id)).with {
            !it || Long.valueOf(it as String) < Long.valueOf(getRebateFromCache(_INDIVIDUAL_INVITES))
        } && stringRedisTemplate.opsForHash().get(_ACTIVITY_HASH_KEY, _INVITES).with {
            !it || Long.valueOf(it as String) < Long.valueOf(getRebateFromCache(_MAX_INVITES))
        }
    }

    def final inviteFirstOrderLimit = { ChannelAgent channelAgent ->
        stringRedisTemplate.opsForHash().get(_AGENT_COUNT_HASH_KEY, _GET_HASH_KEY.call(_INVITE_CA_REGISTER_PRE, channelAgent.id)).with {
            !it || Long.valueOf(it as String) < Long.valueOf(getRebateFromCache(_INDIVIDUAL_INVITE_ORDER))
        }
    }

    def static final _GET_HASH_KEY = { pre, id ->
        pre + '_' + id as String
    }

    def final countClosure = { String key, String hashKey ->
        stringRedisTemplate.opsForHash().increment(key, hashKey, 1L)
    }

    String getRebateFromCache(hashKey) {
        stringRedisTemplate.opsForHash().get(_ACTIVITY_HASH_KEY, hashKey)
    }

    Date getActivityTime(hashKey) {
        DateUtils.getDate(getRebateFromCache(hashKey), DateUtils.DATE_LONGTIME24_PATTERN)
    }

    Boolean activityValid(){
        Date beginTime = getActivityTime(_BEGIN_TIME)
        Date endTime = getActivityTime(_END_TIME)
        DateUtils.compareCurrentDateBetweenStartDateAndEndDate(new Date(),beginTime,endTime)
    }
}
