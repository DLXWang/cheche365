package com.cheche365.cheche.rest.web.controller

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.core.service.callback.CallBackType
import com.cheche365.cheche.core.service.callback.ICallBackService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Controller
@RequestMapping("/api/callback")
class CallBackResource {

    @Autowired
    private List<ICallBackService> payCallBackServices;

    @RequestMapping(value = "/{paymentChannel}/{callbackType}", method = [RequestMethod.POST , RequestMethod.GET])
    public void doCallback(HttpServletRequest request, HttpServletResponse response, @PathVariable String paymentChannel, @PathVariable String callbackType) {

        PaymentChannel pc = PaymentChannel.Enum.toPaymentChannel(paymentChannel)
        CallBackType callBackType = CallBackType.getCallBackType(callbackType)
        if(pc == null){
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED,"不支持的paymentChannel:"+paymentChannel)
        }
        if(callBackType == null){
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED,"不支持的callBackType:"+callBackType)
        }
        payCallBackServices.find {it.support(pc)}.callBack(request,callBackType).with {
            response.getWriter().write(it)
        }
    }
}
