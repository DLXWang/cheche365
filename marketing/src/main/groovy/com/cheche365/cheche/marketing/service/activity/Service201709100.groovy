package com.cheche365.cheche.marketing.service.activity

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.Marketing
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.service.AutoService
import com.cheche365.cheche.core.util.AutoUtils
import com.cheche365.cheche.core.util.ValidationUtil
import com.cheche365.cheche.marketing.service.MarketingService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by shanxf on 2017/9/14.
 * m记录手机号
 */
@Service
@Slf4j
class Service201709100 extends MarketingService {

    @Autowired
    private AutoService autoService

    @Override
    void preCheck(Marketing marketing, String mobile, Channel clientType) {
        if(!ValidationUtil.validMobile(mobile)){
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "手机号格式校验失败");
        }
        if(session){
            session.setAttribute(SESSION_MOBILE,mobile)
        }else{
          log.info("session is null ,mobile:{}",mobile)
        }
    }

    @Override
    Object attend(Marketing marketing, User user, Channel channel, Map<String, Object> payload) {
        if (AutoUtils.containStarChars(payload.owner)){
            Auto auto = new Auto()
            auto.owner = payload.owner
            autoService.decryptAuto(auto,null,session.id)
            payload.owner =auto.owner
        }
        return super.attend(marketing, user, channel, payload)
    }
}
