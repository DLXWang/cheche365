package com.cheche365.cheche.web.service.security.throttle

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.message.IPFilterMessage
import com.cheche365.cheche.core.message.RedisPublisher
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.core.util.RuntimeUtil
import com.cheche365.cheche.web.util.ClientTypeUtil
import com.cheche365.cheche.web.util.UserDeviceUtil
import groovy.util.logging.Slf4j
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.context.request.RequestContextHolder

import javax.servlet.http.HttpServletRequest

import static com.cheche365.cheche.core.constants.WebConstants.NOT_INTERCEPT_SMS
import static com.cheche365.cheche.core.exception.BusinessException.Code.NEED_SUPPLY_CAPTCHA_IMAGES
import static com.cheche365.cheche.core.exception.BusinessException.Code.UNAUTHORIZED_ACCESS
import static com.cheche365.cheche.core.message.IPFilterMessage.KEY_IP_DAY
import static com.cheche365.cheche.core.message.IPFilterMessage.KEY_IP_HOUR
import static com.cheche365.cheche.core.message.IPFilterMessage.SET_FOREIGN_COUNTRY
import static com.cheche365.cheche.core.message.IPFilterMessage.cleanCache
import static com.cheche365.cheche.core.message.IPFilterMessage.dayHashKey
import static com.cheche365.cheche.core.message.IPFilterMessage.hourHashKey
import static com.cheche365.cheche.core.message.IPFilterMessage.ipKey
import static com.cheche365.cheche.core.model.Channel.Enum.WE_CHAT_APP_39
import static com.cheche365.cheche.core.util.IpUtil.getIP
import static com.cheche365.cheche.web.service.CaptchaImageService.cacheCaptchaImageStatus

@Slf4j
class SmsValidationUtils {

    static void validateRequest(String code, HttpServletRequest request, RedisTemplate redisTemplate, RedisPublisher redisPublisher) {
        //添加redis状态，是否拦截短信，主要是本地开发调式使用
        StringRedisTemplate stringRedisTemplate = ApplicationContextHolder.getApplicationContext().getBean(StringRedisTemplate.class)
        if (Boolean.valueOf(stringRedisTemplate.opsForValue().get(NOT_INTERCEPT_SMS)) || RuntimeUtil.isDevEnv()) {
            return
        }

        Channel channel = ClientTypeUtil.getChannel(request)
        String referer = request.getHeader("Referer")
        String ua = UserDeviceUtil.getUserAgent(request)

        if (!(ua?.startsWith(WebConstants.NON_AUTO_USER_AGENT_KEY)) && !Channel.selfApp().contains(channel) && WE_CHAT_APP_39 != channel) {
            log.debug('referer value: {}, smsFlag: {}', referer, CacheUtil.getSmsFlag(request.session))
            if (!CacheUtil.getSmsFlag(request.session) || !referer || !(referer.contains('cheche365.com') || referer.contains('chetimes.com'))) {
                throw new BusinessException(UNAUTHORIZED_ACCESS, 'API请求失败')
            }
        }

        String ip = getIP(request)
        def ipMessage = [code: code, ip: ip]
        Boolean foreignCountry = redisTemplate.opsForSet().isMember(SET_FOREIGN_COUNTRY, ip)
        log.debug('code:{}, ip: {}, foreignCountry: {}', ipMessage.code, ip, foreignCountry)
        redisPublisher.publish(new IPFilterMessage().setKey(ipKey(ipMessage)).setMessage(ipMessage))

        Date now = Calendar.getInstance().getTime()
        if ((redisTemplate.opsForHash().get(KEY_IP_HOUR, hourHashKey(ipMessage, now)) as Long) > 10) {
            cacheCaptchaImageStatus(request.session.id, redisTemplate, true)
            throw new BusinessException(NEED_SUPPLY_CAPTCHA_IMAGES, "")
        }

        if ((redisTemplate.opsForHash().get(KEY_IP_DAY, dayHashKey(ipMessage, now)) as Long) > 50) {
            cacheCaptchaImageStatus(request.session.id, redisTemplate, true)
            throw new BusinessException(NEED_SUPPLY_CAPTCHA_IMAGES, "")
        }

    }

    static void clearCache(String code, RedisTemplate redisTemplate) {
        HttpServletRequest request = RequestContextHolder.currentRequestAttributes().getRequest()
        String ip = getIP(request)
        def ipMessage = [code: code, ip: ip]
        Date now = Calendar.getInstance().getTime()

        cleanCache(redisTemplate, KEY_IP_HOUR, hourHashKey(ipMessage, now))
        cleanCache(redisTemplate, KEY_IP_DAY, dayHashKey(ipMessage, now))
    }

}
