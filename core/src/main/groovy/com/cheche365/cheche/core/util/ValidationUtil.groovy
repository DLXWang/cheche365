package com.cheche365.cheche.core.util

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.QuoteSource
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.servlet.http.HttpServletRequest

import static com.cheche365.cheche.core.model.QuoteSource.Enum.API_QUOTE_SOURCES

/**
 * Created by zhengwei on 2/26/16.
 */
class ValidationUtil {


    private static Logger logger = LoggerFactory.getLogger(ValidationUtil.class)

    static boolean validMobile(String mobile) {
        mobile && (mobile ==~ /^1[3-9]\d{9}$/)
    }

    static boolean validIdentity(String identity){
        identity && (identity ==~ /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/)
    }

    static boolean containChinese(String str){
        str && (str ==~ /[\S\s]*[\u4e00-\u9fa5][\S\s]*/) && !(str ==~ /[\S\s]*\u951f\u65a4\u62f7[\S\s]*/)
    }



    static boolean ableRuleQuote(Channel channel, QuoteSource quoteSource) {

       if (!RuntimeUtil.isProductionEnv()){

           if(channel?.id == 111111L) {
               return true
           }

           try{

               HttpServletRequest request = Class.forName('org.springframework.web.context.request.RequestContextHolder')?.currentRequestAttributes()?.getRequest()
               if(request?.getSession()?.getAttribute(WebConstants.SESSION_KEY_TURN_OFF_REFER_RULE_ENGINE_QUOTE)){
                    return false
               }
           }catch (Exception e){
               logger.debug(ExceptionUtils.getStackTrace(e))
           }

       }

       return !channel?.isAgentChannel() || (channel?.isAgentChannel() && !API_QUOTE_SOURCES.contains(quoteSource))
    }

}
