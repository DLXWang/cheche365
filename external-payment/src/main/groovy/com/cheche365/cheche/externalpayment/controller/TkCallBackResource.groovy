package com.cheche365.cheche.externalpayment.controller

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.LogType
import com.cheche365.cheche.core.model.MoApplicationLog
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.externalpayment.model.TkResponseBody
import com.cheche365.cheche.externalpayment.service.TkCallbackService
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

/**
 * Created by wen on 2018/4/10.
 */

@Controller
@RequestMapping("/api/callback/tk")
class TkCallBackResource {

    private Logger logger = LoggerFactory.getLogger(TkCallBackResource.class);

    @Autowired
    TkCallbackService tkCallbackService

    @RequestMapping(value = "", method = RequestMethod.POST)
    HttpEntity<RestResponseEnvelope<Object>> callback(@RequestBody Map body){
        logger.info("泰康出单异步回调参数: \n ${body}")

        TkResponseBody bodyObj = validate(body)
        tkCallbackService.handle(bodyObj)

        return new ResponseEntity<>(new RestResponseEnvelope(bodyObj.buildResponse()), HttpStatus.OK);
    }


    TkResponseBody validate(Map rawRequest){
        if(!rawRequest) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, '泰康回调内容为空')
        }

        return new TkResponseBody(rawRequest)
    }




}

