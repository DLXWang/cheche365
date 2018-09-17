package com.cheche365.cheche.rest.util

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Channel
import org.springframework.data.redis.core.StringRedisTemplate

import javax.servlet.http.HttpSession

import static com.cheche365.cheche.core.constants.WebConstants.NOT_CHECK_ALLOW_QUOTE
import static com.cheche365.cheche.core.constants.WebConstants.NOT_CHECK_CHANNEL

/**
 * Created by taichangwei on 2017/7/28.
 */
class CheckUtil {

    static checkQuoteable(Channel channel, def session){
        if(!quoteable(channel,session)){
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, '尊敬的用户，您的报价请求已为您提交至保险公司，请等候专人与您联系，谢谢！')
        }
    }

    static Boolean quoteable(Channel channel, def session){
        StringRedisTemplate redisTemplate = ApplicationContextHolder.getApplicationContext().getBean(StringRedisTemplate.class)

        def tag
        if (session instanceof HttpSession){
             tag = session.getAttribute(WebConstants.SESSION_KEY_ALLOW_QUOTE_TAG)
        }else{
            tag = session.get(WebConstants.SESSION_KEY_ALLOW_QUOTE_TAG)
        }

        if (Boolean.valueOf(redisTemplate.opsForValue().get(NOT_CHECK_CHANNEL + channel.apiPartner?.code)) ||
            Boolean.valueOf(redisTemplate.opsForValue().get(NOT_CHECK_ALLOW_QUOTE))) {
            true
        } else {
            //tag为boolean无论是true还是false都不等于null，不能简写成tag?:~的形式
            tag != null ? tag : !Channel.unAllowQuote().contains(channel)
        }
    }

}
