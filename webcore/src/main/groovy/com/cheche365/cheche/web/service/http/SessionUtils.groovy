package com.cheche365.cheche.web.service.http

import com.cheche365.cheche.core.model.BusinessActivity
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.InternalUser
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.WechatUserInfo
import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.web.model.UserCallbackInfo
import groovy.util.logging.Slf4j
import net.sf.json.util.JSONUtils

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

import static com.cheche365.cheche.core.constants.WebConstants.*
import static com.cheche365.cheche.core.util.CacheUtil.doJacksonDeserialize

/**
 * Created by zhengwei on 09/12/2017.
 */
import static com.cheche365.cheche.core.util.CacheUtil.doJacksonSerialize
import static com.cheche365.cheche.web.util.UserDeviceUtil.SESSION_KEY_DEVICE_TYPE
import static com.cheche365.cheche.web.util.UserDeviceUtil.SESSION_KEY_USER_AGENT_HEADER

@Slf4j
class SessionUtils {

    public static final List<String> USER_RELATED = [
        (SESSION_KEY_USER),
        (SESSION_KEY_IMPERSONATION_USER)
    ].asImmutable()

    static final KEY_TO_CLASS = [
        (SESSION_KEY_USER)              : User,
        (SESSION_KEY_IMPERSONATION_USER): User,
        (SESSION_KEY_USER_CALLBACK)     : UserCallbackInfo,
        (SESSION_KEY_INTERNAL_USER)     : InternalUser,
        (SESSION_KEY_WECHAT_USER_INFO)  : WechatUserInfo,
        (SESSION_KEY_CPS_CHANNEL)       : BusinessActivity,
        (SESSION_KEY_CHANNEL_AGENT)     : ChannelAgent
    ].asImmutable()


    static final ORDER = [
        (SESSION_KEY_LOG_TRACE),
        (SESSION_KEY_CLIENT_TYPE),
        (SESSION_KEY_USER),
        (SESSION_KEY_USER_CALLBACK),
        (SESSION_KEY_INTERNAL_USER),
        (SESSION_KEY_IMPERSONATION_USER),
        (SESSION_KEY_WECHAT_USER_INFO),
        (SESSION_KEY_WECHAT_OPEN_ID),
        (SESSION_KEY_PARTNER_STATE),
        (SESSION_KEY_USER_AGENT_HEADER),
        (SESSION_KEY_DEVICE_TYPE)

    ]
    static final DESC = [
        (SESSION_KEY_USER)              : [
            desc: '用户'
        ],
        (SESSION_KEY_USER_CALLBACK)     : [
            desc: '回调用户'
        ],
        (SESSION_KEY_CLIENT_TYPE)       : [
            desc: '渠道'
        ],
        (SESSION_KEY_WECHAT_USER_INFO)  : [
            desc: '微信用户信息'
        ],
        (SESSION_KEY_WECHAT_OPEN_ID)    : [
            desc: '微信OpenID, 用户跨公众号微信支付'
        ],
        (SESSION_KEY_INTERNAL_USER)     : [
            desc: '车车客服用户'
        ],
        (SESSION_KEY_IMPERSONATION_USER): [
            desc: '车车客服操作的C端用户'
        ],
        (SESSION_KEY_PARTNER_STATE)     : [
            desc: 'partner状态参数，用户后续同步订单'
        ],
        (SESSION_KEY_LOG_TRACE)         : [
            desc: '报价流程'
        ],
        (SESSION_KEY_USER_AGENT_HEADER) : [
            desc: '客户端User-Agent'
        ],
        (SESSION_KEY_DEVICE_TYPE)       : [
            desc: '客户端设备类型'
        ]
    ].asImmutable()

    static final SESSION_KEY_LOG_TRACE = "log_trace"

    static get(HttpSession session, String key) {
        session.getAttribute(key)?.with {
            KEY_TO_CLASS.containsKey(key) ? doJacksonDeserialize(it.toString(), KEY_TO_CLASS.get(key)) : it
        }
    }

    static get(HttpSession session, List<String> keys) {
        keys.findResult { get(session, it) }
    }

    static toTree(Map sessionObj) {
        def noPojoObj = doJacksonDeserialize(doJacksonSerialize(sessionObj), Map.class)  //把所有的pojo对象转换为map或list结构
        convert(noPojoObj)
            .sort {
            a, b -> findIndex(a.text) <=> findIndex(b.text)
        }
        .collect {
            if (desc(it.text)) {
                it.text = desc(it.text)
            }
            it
        }
    }

    static convert(current) {

        if (current instanceof Map) {
            current.collect {
                [
                    text : it.key,
                    nodes: convert(it.value).flatten()
                ]
            }
        } else if (current instanceof List) {
            current.collect { entry ->
                convert(entry)
            }
        } else {
            if (current instanceof String && JSONUtils.mayBeJSON(current as String)) {
                [
                    [text: "<pre>$current</pre>".toString()]
                ]
            } else {
                [
                    [text: current]
                ]
            }

        }
    }

    static findIndex(String key) {
        ORDER.indexOf(key) < 0 ? Integer.MAX_VALUE : ORDER.indexOf(key)
    }

    static desc(String key) {
        DESC.get(key)?.desc
    }

    static void clearSession(Channel beforeSwitchChannel, Channel afterSwitchChannel, HttpServletRequest request) {
        if (beforeSwitchChannel != null && beforeSwitchChannel != afterSwitchChannel) {
            logger.info(" before channel id :{},after channel id:{},clear session attrs", beforeSwitchChannel.id, afterSwitchChannel.id)
            removeSessionAttr([SESSION_KEY_USER,SESSION_KEY_CHANNEL_AGENT],request)
        }
    }

    static void removeSessionAttr(List<String> attrs, HttpServletRequest request){
        attrs.each {
            request.session.removeAttribute it
        }
    }
}
