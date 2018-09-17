package com.cheche365.cheche.core.service.callback

import com.cheche365.cheche.core.model.PaymentChannel

import javax.servlet.http.HttpServletRequest

interface ICallBackService {

    public String callBack(HttpServletRequest request,CallBackType callBackType)

    public boolean support(PaymentChannel pc)
}
