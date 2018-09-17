package com.cheche365.cheche.rest.jsonfilter;


import com.cheche365.cheche.rest.processor.VersionFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zhengwei on 7/13/15.
 */
public class ResponseHandlerFactory extends VersionFactory {

    private static final ResponseHandlerV1 v1Handler = new ResponseHandlerV1(); //即使不支持v1版本api也不能把v1Handler完全干掉，否则会有其他第三方api响应问题，比如跟微信交互的api
    private static final ResponseHandlerV1_1 v1_1Handler = new ResponseHandlerV1_1();

    public ResponseHandler getHandler(HttpServletRequest request){
        if (after1_0(request)) {
            return v1_1Handler;
        } else {
            return v1Handler;
        }
    }

}
