package com.cheche365.cheche.developer.interceptor

import com.cheche365.cheche.partner.web.interceptor.SignVerifyInterceptor
import com.cheche365.cheche.signature.spi.PreSignRequest

import javax.servlet.http.HttpServletRequest

/**
 * @Author shanxf
 * @Date 2018/4/26  17:26
 */
class DeveloperSignInterceptor  extends SignVerifyInterceptor{

    @Override
    PreSignRequest getRequest(HttpServletRequest request) {
        new DeveloperPreSignRequest(request)
    }
}
