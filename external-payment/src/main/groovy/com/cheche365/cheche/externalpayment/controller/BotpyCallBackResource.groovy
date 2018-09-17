package com.cheche365.cheche.externalpayment.controller

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.LogType
import com.cheche365.cheche.core.model.MoApplicationLog
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository
import com.cheche365.cheche.core.service.OrderRelatedService
import com.cheche365.cheche.externalpayment.handler.botpy.callback.BotpyCallbackHandler
import com.cheche365.cheche.externalpayment.model.BotpyCallBackBody
import com.cheche365.cheche.web.response.RestResponseEnvelope
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import static com.cheche365.cheche.externalpayment.model.BotpyCallBackBody.BOTPY_CALLBACK_TYPES

/**
 * Created by wenling on 2018/3/5.
 */

@Controller
@RequestMapping("/api/callback/botpy")
class BotpyCallBackResource {

    private Logger logger = LoggerFactory.getLogger(BotpyCallBackResource.class);

    @Autowired
    private List<BotpyCallbackHandler> handlers

    @Autowired
    private OrderRelatedService orService

    @Autowired
    StringRedisTemplate stringRedisTemplate


    @RequestMapping(value = "", method = RequestMethod.POST)
    HttpEntity<RestResponseEnvelope<Object>> callback(@RequestBody String body){
        logger.info("金斗云异步回调参数: \n ${body}")

        BotpyCallBackBody bodyObj = validate(body)
        doService(bodyObj)

        return new ResponseEntity<>(new RestResponseEnvelope(["payload": ["code": 200]]), HttpStatus.OK);
    }

    def doService(BotpyCallBackBody body) {


        BotpyCallbackHandler handler = handlers.find {it.support(body)}

        if(!handler){
            throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "金斗云回调未匹配到处理服务")
        }

        OrderRelatedService.OrderRelated or = handler.initORByPid(body.proposalId())

        handler.persistLog(body, or)
        handler.handle(body, or)
    }


    static BotpyCallBackBody validate(String rawRequest){
        if(!rawRequest) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, '金斗云回调内容为空')
        }

        BotpyCallBackBody requestObj = new BotpyCallBackBody(rawRequest)
        if(!BOTPY_CALLBACK_TYPES.containsKey(requestObj.type())) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "金斗云回调非预期type: ${requestObj.type()}")
        }

        return requestObj
    }

}

