package com.cheche365.cheche.rest.interceptor

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.model.BusinessActivity
import com.cheche365.cheche.core.repository.BusinessActivityRepository
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.manage.common.repository.ActivityMonitorUrlRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.core.UriBuilder

import static java.util.UUID.randomUUID

/**
 * Created by mahong on 2015/8/18.
 */
class CPSChannelInterceptor extends HandlerInterceptorAdapter {
    private Logger log = LoggerFactory.getLogger(CPSChannelInterceptor.class);

    @Autowired
    private BusinessActivityRepository activityRepository;
    @Autowired
    private ActivityMonitorUrlRepository activityMonitorUrlRepository;

    @Override
    boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        def codeAndUrl = findBACode(request)
        BusinessActivity ba = findBA(codeAndUrl?.code)
        if(ba && ba.checkActivityDate()){
            request.getSession(true).setAttribute(WebConstants.SESSION_KEY_ALLOW_QUOTE_TAG, quoteAllow(ba))
            request.getSession(true).setAttribute(WebConstants.SESSION_KEY_CPS_CHANNEL, CacheUtil.doJacksonSerialize(ba))

            response.sendRedirect(codeAndUrl.redirectUrl)
        } else {
            log.debug("不支持此CPS通道，请确认链接是否正确: {}", request.getRequestURI());
            response.sendRedirect(WebConstants.getDomainURL() + WebConstants.M_ROOT_PATH);
        }
        return false
    }

    def findBACode(HttpServletRequest request){
        def path = request.requestURI
        def result
        switch (path) {
            case ~/\/cps.*/:
                result = path.split('/').last().with {
                    def baCode = request.getParameter('cps')?:it
                    [
                            code       : baCode,
                            redirectUrl: findBA(baCode).getOriginalUrl().with { originalUrl ->
                                def uri = originalUrl.trim().toURI()
                                UriBuilder.fromUri(originalUrl).with {
                                    uri.getQuery()?.contains("cps=") ?: queryParam("cps", baCode)
                                    queryParam('noCache', randomUUID())
                                    build().toString()
                                }
                            }
                    ]
                }
                break
            case ~/\/m\/channel\/.*/:
                result =
                    path.split('/').last().with {
                        [
                            code: it,
                            redirectUrl: '/m/index.html?cps=' + it
                        ]
                    }
                break
            case ~/\/web\/channel\/.*/:
                result =
                    path.split('/').last().with {
                        [
                            code: it,
                            redirectUrl: '/website/index.html'
                        ]
                    }
                break
            case ~/\/marketing\/m.*/:
                result = [
                    code: path.split('/').last() - 'index_' - '.action',
                    redirectUrl: path.split('/')[0..-2].join('/') + '/index.html'
                ]

        }
        return result
    }

    def findBA(String code){
        if(code){
            activityRepository.findFirstByCode(code);
        } else {
            log.debug('cps拦截器非预期参数，code is null')
        }
    }

    private boolean quoteAllow(BusinessActivity ba){
        activityMonitorUrlRepository.findFirstByBusinessActivity(ba)?.quote
    }

}
