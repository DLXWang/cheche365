package com.cheche365.cheche.wechat

import com.cheche365.cheche.core.WechatConstant
import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.marketing.service.MarketingService
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import javax.ws.rs.core.UriBuilder

import static com.cheche365.cheche.core.WechatConstant.APP_ID
import static com.cheche365.cheche.core.WechatConstant.APP_SECRET
import static com.cheche365.cheche.core.WechatConstant.FANHUA_APP_ID
import static com.cheche365.cheche.core.WechatConstant.FANHUA_APP_SECRET
import static com.cheche365.cheche.core.constants.WebConstants.getRootPath
import static com.cheche365.cheche.core.model.Channel.Enum.WE_CHAT_3
import static com.cheche365.cheche.core.model.PaymentChannel.Enum.PING_PLUS_WX_23

/**
 * Created by liqiang on 3/30/15.
 * OAuth异常response示例：
 *{"errcode":40029,"errmsg":"invalid code, hints: [ req_id: GoeoDa0217ns62 ]"}*/
@Component
@Slf4j
class PublicAccountManager extends OAuthManager {

    static String getAppId(Map stateParams) {
        if ("pay" == stateParams.type) {
            return (PING_PLUS_WX_23.id as String == stateParams.paymentChannelId) ? FANHUA_APP_ID : APP_ID
        }
        WechatConstant.getAppId(stateParams.channel)
    }

    static String getAppSecret(Map stateParams) {
        if ("pay" == stateParams.type) {
            return (PING_PLUS_WX_23.id as String == stateParams.paymentChannelId) ? FANHUA_APP_SECRET : APP_SECRET
        }
        WechatConstant.getAppSecret(stateParams.channel)
    }

    static Channel getChannel(Map stateParams) {
        stateParams.get("channel")
    }

    static void initParam(Map stateParams) {
        stateParams.put("channel", Channel.toChannel(stateParams.channelId as Long))
    }

    static String getRedirectUrl(Map stateParams) {
        log.info("stateParams:{}",stateParams)
        Boolean marketingOauth = (WE_CHAT_3 == stateParams.channel) && ('marketing' == stateParams.type)
        if (marketingOauth) {
            MarketingService marketingService = ApplicationContextHolder.getApplicationContext().getBean('service' + stateParams.code)
            return marketingService.getOauthRedirectUrl(stateParams)
        }

        Channel channel = getChannel(stateParams)
        String rootPath = stateParams.path ? stateParams.path : getRootPath(channel)
        UriBuilder builder = UriBuilder.fromPath(WebConstants.getSecurityDomainURL() + rootPath)
        if (channel.isPartnerChannel()) {
            builder.queryParam("src", channel.apiPartner.code)
        }
        if (stateParams.fragment) {
            builder.fragment(stateParams.fragment)
        }
        log.info("redirect Url :{}",builder.build().toString())
        return "redirect:" + builder.build().toString()
    }

    @Override
    String getOAuthPath() {
        return "/sns/oauth2/access_token"
    }

}
