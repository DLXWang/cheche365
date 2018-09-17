package com.cheche365.cheche.partner.web.interceptor

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.ApiPartnerProperties
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.repository.ApiPartnerPropertiesRepository
import com.cheche365.cheche.core.util.RuntimeUtil
import com.cheche365.cheche.signature.APISignature
import com.cheche365.cheche.signature.Parameters
import com.cheche365.cheche.signature.Secrets
import com.cheche365.cheche.signature.api.ServletPreSignRequest
import com.cheche365.cheche.signature.spi.PreSignRequest
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static com.cheche365.cheche.core.model.ApiPartnerProperties.Key.SYNC_APP_ID
import static com.cheche365.cheche.core.model.ApiPartnerProperties.Key.SYNC_APP_SECRET

class SignVerifyInterceptor extends HandlerInterceptorAdapter {

    private Logger logger = LoggerFactory.getLogger(SignVerifyInterceptor.class)

    @Autowired
    ApiPartnerPropertiesRepository apiPartnerPropertiesRepository

    private Set<String> extendUrls = null


    @Override
    boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (isExtendUrl(request)) {
            return super.preHandle(request, response, handler)
        }

        if (!RuntimeUtil.isProductionEnv() && request.getQueryString() != null && request.getQueryString().contains("sign=off")) {
            return super.preHandle(request, response, handler)
        }

        PreSignRequest signRequest = getRequest(request)
        Parameters parameters = new Parameters().readRequest(signRequest)
        if (null == parameters || StringUtils.isBlank(parameters.getAppId())) {
            logger.error '签名header不存在'
        } else {
            ApiPartnerProperties apiPartnerProperties = getPartnerThird(parameters)
            if (null == apiPartnerProperties) {
                throw new BusinessException(BusinessException.Code.UNAUTHORIZED_ACCESS, "合作方不存在, " + parameters.getAppId())
            }
            Secrets secrets = new Secrets().appSecret(ApiPartnerProperties.findByPartnerAndKey(apiPartnerProperties.partner, SYNC_APP_SECRET)?.value)
            boolean valid = false
            try {
                valid = APISignature.verify(signRequest, parameters, secrets)
            } catch (Exception e) {
                logger.info("签名失败 ：{},app_id :{},app_secret:{}", e.getMessage(), parameters.appId(),
                    ApiPartnerProperties.findByPartnerAndKey(apiPartnerProperties.partner, SYNC_APP_SECRET)?.value)
            }
            if (valid) {
                return super.preHandle(request, response, handler)
            } else {
                throw new BusinessException(BusinessException.Code.UNAUTHORIZED_ACCESS, "签名校验失败")
            }
        }
        response.sendRedirect '/m/error.html'
        false
    }

    PreSignRequest getRequest(HttpServletRequest request) {
        new ServletPreSignRequest(request)
    }

    private boolean isExtendUrl(HttpServletRequest request) {
        if (null == extendUrls) {
            return false
        }

        Iterator<String> iterator = extendUrls.iterator()
        while (iterator.hasNext()) {
            String extendUrl = iterator.next()
            if (request.getRequestURI().toString().matches(extendUrl)) {
                return true
            }
        }

        Boolean partnerIndexAllowed = Channel.allPartners().findAll { !it.disable() }.any {
            request.getRequestURI() == "/partner/${it.apiPartner.code}/index" || request.getRequestURI().startsWith("/partner/${it.apiPartner.code}/marketing/")
        }
        if (partnerIndexAllowed) {
            return true
        }

        return false
    }

    private ApiPartnerProperties getPartnerThird(Parameters parameters) {
        return apiPartnerPropertiesRepository.findByKeyAndValue(SYNC_APP_ID, parameters.getAppId())
    }

    @Override
    void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView)
    }

    @Override
    void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex)
    }

    Set<String> getExtendUrls() {
        return extendUrls
    }

    void setExtendUrls(Set<String> extendUrls) {
        this.extendUrls = extendUrls
    }
}
