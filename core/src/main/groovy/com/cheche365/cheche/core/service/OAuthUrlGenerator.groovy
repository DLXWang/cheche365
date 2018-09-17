package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.exception.BusinessException
import org.apache.http.client.utils.URIBuilder
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.WechatConstant.APP_ID
import static com.cheche365.cheche.core.WechatConstant.FANHUA_APP_ID
import static com.cheche365.cheche.core.model.PaymentChannel.Enum.PING_PLUS_WX_23

/**
 * Created by zhengwei on 11/18/15.<p>
 * 生成微信OAuth URL，用户点击URL会先跳到微信服务器授权车车微信公众号访问用户信息。完成OAuth流程后页面会跳到redirect_uri参数指定的车车服务上。<br>
 * 通过state参数，回调时可以实现一些定制化需求，比如得到用户点击OAuth链接时的上下文参数（手机号等）。由于state参数在回调时候通过URL query string的形式<br>
 * 传回来，所以state参数的值需要URL编码，在使用时候需要先解码。<p>
 */
@Service
class OAuthUrlGenerator {

    private static final String BASE = "https://open.weixin.qq.com/connect/oauth2/authorize"

    String toOAuthUrl(String callback, Map stateParameters) {
        return this.toOAuthUrl(callback, "snsapi_base", stateParameters)
    }

    String toOAuthUrl(String callback, String scope, Map stateParameters) {
        try {
            URIBuilder builder = new URIBuilder(BASE)
            return builder
                .addParameter("appid", (PING_PLUS_WX_23.id == stateParameters.paymentChannelId) ? FANHUA_APP_ID : APP_ID)
                .addParameter("redirect_uri", WebConstants.getDomainURL() + callback)
                .addParameter("response_type", "code")
                .addParameter("scope", scope)
                .addParameter("state", toState(stateParameters)).toString()

        } catch (URISyntaxException e) {
            throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "生成微信OAuth URL失败")
        }
    }

    private String toState(Map stateParameters) {
        StringBuilder builder = new StringBuilder()
        stateParameters.entrySet().each {
            builder.append(it.key).append('=').append(it.value).append('&')
        }
        builder.toString().endsWith("&") ? builder.toString().substring(0, builder.toString().length() - 1) : builder.toString()
    }
}
