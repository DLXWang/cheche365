package com.cheche365.cheche.rest.interceptor

import com.cheche365.cheche.core.repository.ApiPartnerPropertiesRepository
import com.cheche365.cheche.core.service.UserService
import com.cheche365.cheche.signature.Parameters
import com.cheche365.cheche.signature.Secrets
import com.cheche365.cheche.signature.api.ServletPreSignRequest
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static com.cheche365.cheche.core.constants.WebConstants.schema
import static com.cheche365.cheche.core.exception.BusinessException.Code.UNAUTHORIZED_ACCESS
import static com.cheche365.cheche.core.model.ApiPartnerProperties.Key.SYNC_APP_ID
import static com.cheche365.cheche.core.model.ApiPartnerProperties.Key.SYNC_APP_SECRET
import static com.cheche365.cheche.core.model.ApiPartnerProperties.findByPartnerAndKey
import static com.cheche365.cheche.core.model.Channel.findByApiPartner
import static com.cheche365.cheche.core.util.CacheUtil.cacheUser
import static com.cheche365.cheche.core.util.RuntimeUtil.isProductionEnv
import static com.cheche365.cheche.signature.APISignature.verify
import static com.cheche365.cheche.web.util.ClientTypeUtil.cacheChannel
import static org.apache.commons.lang3.StringUtils.isBlank
import static org.springframework.web.bind.annotation.RequestMethod.GET
import static org.springframework.web.bind.annotation.RequestMethod.POST
import static org.springframework.web.bind.annotation.RequestMethod.PUT

/**
 * Created by liheng on 2018/8/22 0022.
 */
@Slf4j
class APISignVerifyInterceptor extends HandlerInterceptorAdapter {

    private static final String _API_AREAS_GROUP = 'areasGroup'
    private static final String _API_COMPANIES = 'companies'
    private static final String _API_AUTO_LICENSE = 'autoLicense'
    private static final String _API_PACKAGES = 'packages'
    private static final String _API_DEFAULT_QUOTE = 'defaultQuote'
    private static final String _API_SAVE_QUOTE = 'saveQuote'
    private static final String _API_PLACE_ORDER = 'placeOrder'
    private static final String _API_IMAGES = 'images'

    private static final _PARTNER_API_PATH_CONFIG = [
        [_API_AREAS_GROUP, '/areas/group', GET],
        [_API_COMPANIES, '/companies', GET],
        [_API_AUTO_LICENSE, '/autos/license', GET],
        [_API_PACKAGES, '/packages/', GET],
        [_API_DEFAULT_QUOTE, '/quotes/default', POST],
        [_API_SAVE_QUOTE, '/quotes', POST],
        [_API_PLACE_ORDER, "/quotes/${isProductionEnv() ? 'I' : 'T'}?\\d+/order", PUT],
        [_API_IMAGES, '/images', GET],
        [_API_IMAGES, '/images', POST]
    ]

    private static final _THROW_EXCEPTION = { String msg, request, response ->
        log.error msg
        request.getRequestDispatcher("/api/public/error?code=$UNAUTHORIZED_ACCESS.codeValue&msg=$msg").forward request, response
    }
    private static final _VERIFY_PARAMETERS = { context ->
        context.parameters = new Parameters().readRequest context.signRequest
        isBlank context.parameters?.getAppId()
    }
    private static final _VERIFY_PARTNER_API_CHANNEL = { context ->
        def channel = findByApiPartner context.propertiesRepository.findByKeyAndValue(SYNC_APP_ID, context.parameters.getAppId())?.partner
        if (channel) {
            context.channel = channel
            cacheChannel context.request, channel
            cacheUser context.request.session, context.userService.findOrCreateUser('18512345678', channel, null) //TODO
            context.request.session.setMaxInactiveInterval 10 * 60
        }
        !channel?.partnerAPIChannel
    }
    private static final _VERIFY_REQUEST_URI = { context ->
        def requestURI = (context.request.requestURI =~ /\/api\/public\/(v[\d.]+)(\/.*)/).with {
            it.matches() ? it[0][2] : null
        }
        !requestURI || !_PARTNER_API_PATH_CONFIG.any() { _0, regex, method ->
            (requestURI =~ /$regex/).matches() && method.toString() == context.request.method
        }
    }
    private static final _VERIFY_PARTNER_API_PERMISSION = { context -> //TODO 检查接口权限 API版本限制
        false
    }
    private static final _VERIFY_API_SIGN = { context ->
        def appSecret = findByPartnerAndKey(context.channel.apiPartner, SYNC_APP_SECRET)?.value
        try {
            return !verify(context.signRequest, context.parameters, new Secrets().appSecret(appSecret))
        } catch (Exception e) {
            log.error '签名失败 ：{}，app_id ：{}，app_secret：{}', e.message, context.parameters.getAppId(), appSecret
        }
        true
    }
    private static final _VERIFY_HANDLE_MAPPINGS = [
        (_VERIFY_PARAMETERS)            : _THROW_EXCEPTION.curry('签名header不存在！'),
        (_VERIFY_PARTNER_API_CHANNEL)   : _THROW_EXCEPTION.curry('合作方不是API渠道！'),
        (_VERIFY_REQUEST_URI)           : _THROW_EXCEPTION.curry('API接口不存在！'),
        (_VERIFY_PARTNER_API_PERMISSION): _THROW_EXCEPTION.curry('合作方未开通该API接口！'),
        (_VERIFY_API_SIGN)              : _THROW_EXCEPTION.curry('签名校验失败！'),
        ({ context -> true })           : { request, response ->
            request.getRequestDispatcher(request.requestURI - '/api/public').forward request, response
        }
    ]

    @Autowired
    private ApiPartnerPropertiesRepository apiPartnerPropertiesRepository

    @Autowired
    private UserService userService

    @Override
    boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info '开始验签，请求路径：{},请求头：{}',request.getRequestURI(),request.getHeader('Authorization')
        def context = [
            request             : request,
            signRequest         : new APIPreSignRequest(request),
            propertiesRepository: apiPartnerPropertiesRepository,
            userService         : userService
        ]
        _VERIFY_HANDLE_MAPPINGS.find { verify, handle ->
            verify context
        }.value request, response
        false
    }

    class APIPreSignRequest extends ServletPreSignRequest {

        APIPreSignRequest(HttpServletRequest request) {
            super(request)
        }

        @Override
        URL getRequestURL() {
            new URL(getSchema() + super.getRequestURL().with { it.toString() - it.protocol })
        }
    }
}
