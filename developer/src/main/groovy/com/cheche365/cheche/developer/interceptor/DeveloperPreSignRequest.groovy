package com.cheche365.cheche.developer.interceptor

import com.cheche365.cheche.signature.api.ServletPreSignRequest

import javax.servlet.http.HttpServletRequest

/**
 * @Author shanxf
 * @Date 2018/4/26  18:12
 */
class DeveloperPreSignRequest extends ServletPreSignRequest {

    private HttpServletRequest request

    DeveloperPreSignRequest(HttpServletRequest request) {
        super(request)
        this.request = request
    }

    @Override
    URL getRequestURL() {
        try {
            String url = request.getRequestURL().toString()
//            return new URL(url.contains("11111") ? url : url.replace('cheche365.com', 'cheche365.com:11111'))
            return new URL(url)
        } catch (MalformedURLException e) {
            throw new IllegalStateException("URL 格式错误，" + request.getRequestURL().toString(), e);
        }
    }

}
