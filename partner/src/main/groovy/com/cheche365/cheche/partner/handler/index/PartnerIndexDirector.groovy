package com.cheche365.cheche.partner.handler.index

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.ApiPartner
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.Marketing
import com.cheche365.cheche.core.model.agent.ChannelAgent
import com.cheche365.cheche.partner.service.index.PartnerServiceFactory
import com.cheche365.cheche.web.service.http.SessionUtils
import com.cheche365.cheche.web.util.ClientTypeUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.servlet.http.HttpServletRequest
import javax.ws.rs.core.UriBuilder

import static com.cheche365.cheche.core.constants.WebConstants.DEFAULT_REDIS_TIME_OUT
import static com.cheche365.cheche.core.constants.WebConstants.SESSION_KEY_PARTNER_STATE
import static com.cheche365.cheche.core.constants.WebConstants.getRootPath
import static com.cheche365.cheche.core.model.Channel.findByApiPartner
import static com.cheche365.cheche.web.service.system.SystemURL.PAYMENT_URL_COMPANY_KEY
import static com.cheche365.cheche.web.service.system.SystemURL.root

/**
 * 车车&第三方合作,通用处理、首页链接跳转
 */
@Component
class PartnerIndexDirector {

    protected final Logger logger = LoggerFactory.getLogger(getClass())

    @Autowired
    private PartnerServiceFactory partnerServiceFactory

    ApiPartner apiPartner() {
        return null
    }

    /**
     * 返回要重定向到M站首页/base页url
     */
    String toIndexPageUrl(ApiPartner partner, HttpServletRequest request) {
        toTargetUrl(partner, request, true, null)
    }

    String toMarketingIndexPageUrl(ApiPartner partner, Marketing marketing, HttpServletRequest request) {
        toTargetUrl(partner, request, true, null, marketing).with { path ->
            path.split(/[&#]/).with { it.first() + (path.contains('#') ? '#' + it.last() : '') }
        }
    }
    /**
     * 第三方链接跳转重定向到特定url模板方法
     */
    String toTargetUrl(ApiPartner partner, HttpServletRequest request, Boolean queryStrPart, String fragment, Marketing marketing = null) {

        if (findByApiPartner(partner)?.disable()) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "第三方合作渠道[" + partner.getCode() + "]已下线")
        }

        clearSession(request)
        initChannel(request,partner)

        request.session.setMaxInactiveInterval(DEFAULT_REDIS_TIME_OUT)

        def partnerService = partnerServiceFactory.getPartnerService(partner)
        new PartnerIndexParams(partner, request.queryString).generateUrl partner, queryStrPart, fragment, rootPart(partner, marketing), partnerService, request.session
    }

    private void initChannel(HttpServletRequest request, ApiPartner partner) {
        Channel beforeSwitchChannel = ClientTypeUtil.getCachedChannel(request)
        Channel afterSwitchChannel = findByApiPartner(partner)
        ChannelAgent channelAgent = SessionUtils.get(request.getSession(), WebConstants.SESSION_KEY_CHANNEL_AGENT)
        if (channelAgent) {
            SessionUtils.clearSession(beforeSwitchChannel, afterSwitchChannel, request)
        }
        ClientTypeUtil.cacheChannel(request, afterSwitchChannel)
    }

    private void clearSession(HttpServletRequest request) {
        request.session.removeAttribute SESSION_KEY_PARTNER_STATE
        request.session.removeAttribute PAYMENT_URL_COMPANY_KEY
    }

    /**
     * 处理root path部分(url问号后面的name value pair)
     */
    protected UriBuilder rootPart(ApiPartner partner, Marketing marketing) {
        String rootPath = marketing ? getMarketingRootPart(marketing) : getRootPath(findByApiPartner(partner))
        root().path(rootPath).queryParam("src", partner.getCode())
    }

    private String getMarketingRootPart(Marketing marketing) {
        "/marketing/m/" + marketing.code + "/index.html"
    }

}
