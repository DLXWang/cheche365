package com.cheche365.cheche.web.service

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.util.CacheUtil
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.exception.BusinessException.Code.NEED_SUPPLY_CAPTCHA_IMAGES
import static com.cheche365.cheche.core.message.IPFilterMessage.CODE_SMS
import static com.cheche365.cheche.core.message.IPFilterMessage.CODE_MARKETING
import static com.cheche365.cheche.web.service.security.throttle.SmsValidationUtils.clearCache
import static groovyx.net.http.ContentType.JSON

@Service
@Slf4j
class CaptchaImageService {

    @Autowired
    private RedisTemplate redisTemplate

    Map getCaptchaImageMap() {
        String url = WebConstants.getDomainURL() + "/captchaimages"
        new RESTClient(url).get([contentType: JSON], { resp, data -> data })
    }

    static def cacheCaptchaImageStatus(String sessionId, RedisTemplate redisTemplate, Boolean flag) {
        CacheUtil.putValueWithExpire(redisTemplate, captchaImageStatusKey(sessionId), flag)
    }

    static Boolean getCaptchaImageStatus(String sessionId, RedisTemplate redisTemplate) {
        CacheUtil.getValueToObject(redisTemplate, captchaImageStatusKey(sessionId))
    }

    static String captchaImageStatusKey(String sessionId) {
        return "captchaImageStatus:" + sessionId
    }

    static def cacheCaptchaImageCode(String sessionId, RedisTemplate redisTemplate, String imageCode) {
        CacheUtil.putValueWithExpire(redisTemplate, captchaImageCodeKey(sessionId), imageCode)
    }

    static String getCaptchaImageCode(String sessionId, RedisTemplate redisTemplate) {
        CacheUtil.getValueToObject(redisTemplate, captchaImageCodeKey(sessionId))
    }

    static String captchaImageCodeKey(String sessionId) {
        return "captchaImageCode:" + sessionId
    }


    Map getCaptchaImageMap(String sessionId) {
        def captchaImageMap = this.getCaptchaImageMap()

        cacheCaptchaImageCode(sessionId, redisTemplate, captchaImageMap.token)

        [captchaImage: captchaImageMap.img]
    }

    Boolean needSupplyCaptchaImage(String sessionId, String imageCode){
        def needSupplyCaptchaImage = getCaptchaImageStatus(sessionId, redisTemplate)
        if(!needSupplyCaptchaImage){
            return false
        }

        if (needSupplyCaptchaImage && !imageCode) {
            throw new BusinessException(NEED_SUPPLY_CAPTCHA_IMAGES, '')
        }
        return true
    }

    def validate(String sessionId, String imageCode) {
        def imageCodeSession = getCaptchaImageCode(sessionId, redisTemplate)
        if (imageCode.toLowerCase() != imageCodeSession?.toLowerCase()) {
            throw new BusinessException(NEED_SUPPLY_CAPTCHA_IMAGES, "图片验证码输入错误")
        }
        clearCache(CODE_SMS, redisTemplate)
        clearCache(CODE_MARKETING, redisTemplate)
        cacheCaptchaImageStatus(sessionId, redisTemplate, false)
        cacheCaptchaImageCode(sessionId, redisTemplate, null)

        [result: 'success']
    }

}
