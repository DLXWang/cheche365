package com.cheche365.cheche.marketing.service.activity

import com.cheche365.cheche.common.util.StringUtil
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Marketing
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.service.OAuthUrlGenerator
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.marketing.service.MarketingService
import com.cheche365.cheche.core.model.WechatUserInfo
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.apache.commons.lang.time.DateFormatUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

import javax.servlet.http.HttpSession

/**
 * Created by wenling on 2017/10/10.
 * 摩拜月卡活动-易桥
 */
@Service
@Slf4j
class Service201710004 extends MarketingService {

    private Logger logger = LoggerFactory.getLogger(Service201710004.class);

    @Autowired
    private OAuthUrlGenerator oAuthUrlGenerator;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired(required = false)
    private HttpSession session;

    @Override
    Map<String, Object> isAttend(String code, User user, Map<String, String> params) {
        Marketing marketing = this.getMarketingByCode(code);

        if (marketing == null) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "营销活动不存在");
        }
        if (new Date().before(marketing.getBeginDate())) {
            throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "活动尚未开始");
        }
        if (new Date().after(marketing.getEndDate())) {
            throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "活动已结束");
        }

        Object wechatUserInfo = session.getAttribute(SESSION_KEY_WECHAT_USER_INFO)
        if (null == wechatUserInfo) {
            throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "参加活动异常，微信用户信息为空");
        }

        WechatUserInfo currentUser = CacheUtil.doJacksonDeserialize(wechatUserInfo, WechatUserInfo)
        [
            shareLink: getMarketingCodeURL(code) + "?owner=" + currentUser.unionid,
            friends  : saveFriends(params.get("owner"), currentUser, marketing)
        ]
    }

    def saveFriends(String shareUser, WechatUserInfo currentUser, Marketing marketing) {
        if (StringUtil.isNull(shareUser)) {
            logger.info("分享人为空")
            return;
        }
        def key = "weixin:share:shareUser:" + shareUser
        if (shareUser != currentUser.unionid) {
            List friends = find(key)
            if (friends.any { it.owner == currentUser.unionid }) {
                logger.info("当前用户{}已分享过此活动", currentUser.unionid)
            } else {
                redisTemplate.opsForList().leftPush(key, JsonOutput.toJson(
                    [
                        nickname  : currentUser.nickname,
                        headimgurl: currentUser.headimgurl,
                        createTime: DateFormatUtils.format(new Date(), "yyyy-MM-dd"),
                        owner     : currentUser.unionid
                    ]
                ));
            }
            if (friends.size() == 0) {
                redisTemplate.expireAt(key, marketing.getEndDate())
            }
        } else {
            logger.info("分享人:{}与当前用户:{}是同一人，不做好友分享的缓存操作", shareUser, currentUser.unionid)
        }
        find(key)
    }


    def find(String owner) {
        redisTemplate.opsForList().range(owner, 0, redisTemplate.opsForList().size(owner) - 1).collect {
            new JsonSlurper().parseText(it)
        }
    }

    @Override
    protected String activityName() {
        return "摩拜月卡活动";
    }

    @Override
    protected String getOauthRedirectUrl(Map stateParams) {
        logger.debug("oauth owner 原始值 " + stateParams.get("owner"));
        String owner = (null == stateParams.get("owner") || StringUtils.isEmpty(stateParams.get("owner").toString())) ? stateParams.wechatUserInfo?.unionid : (String) stateParams.get("owner")
        logger.debug("oauth owner 最终值 " + owner)
        return "redirect:" + getMarketingCodeURL(stateParams.get("code")) + "?owner=" + owner
    }
}
