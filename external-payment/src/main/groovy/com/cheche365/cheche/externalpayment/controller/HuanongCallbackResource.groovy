package com.cheche365.cheche.externalpayment.controller

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.externalpayment.handler.huanong.HuanongCallbackHandler
import com.cheche365.cheche.externalpayment.model.HuanongCallbackBody
import com.cheche365.cheche.web.response.RestResponseEnvelope
import org.apache.commons.lang.exception.ExceptionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import static com.cheche365.cheche.externalpayment.model.HuanongCallbackBody.CALLBACK_TYPES

/**
 * Created by wen on 2018/8/7.
 */
@Controller
@RequestMapping("/api/callback/huanong")
class HuanongCallbackResource {

    private Logger logger = LoggerFactory.getLogger(HuanongCallbackResource.class);

    @Autowired
    List<HuanongCallbackHandler> huanongCallbackHandlers

    @RequestMapping(value = "", method = RequestMethod.POST)
    HttpEntity<RestResponseEnvelope<Object>> callback(@RequestBody Map reqParams){

        logger.debug("华农异步通知参数: ${reqParams}")

        HuanongCallbackBody body = validate(reqParams)
        HuanongCallbackHandler handler = huanongCallbackHandlers.find {it.support(body)}

        try {
            handler.handle(body)
        }catch (BusinessException e){
            logger.error("华农异步通知处理异常 \n ${ExceptionUtils.getStackTrace(e)}")
        }

        return new ResponseEntity<>(new RestResponseEnvelope(body.buildResponse()), HttpStatus.OK);
    }

   def validate(Map reqParams){
      if(!reqParams){
         throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID,'华农回调参数为空')
      }

       HuanongCallbackBody body = new HuanongCallbackBody(reqParams)
       if(!(body.transCode() in CALLBACK_TYPES)){
           throw new BusinessException(BusinessException.Code.ILLEGAL_STATE, "华农回调未匹配到处理的服务,transCode:${body.transCode()}")
       }

       body
   }


}
