package com.cheche365.cheche.rest.jsonfilter;

import com.cheche365.cheche.web.response.RestResponseEnvelope;

/**
 * Created by zhengwei on 7/13/15.
 */
public class ResponseHandlerV1 implements ResponseHandler {

    @Override
    public Object getBodyData(Object rawObj) {

        return rawObj instanceof RestResponseEnvelope ? ((RestResponseEnvelope)rawObj).getEntity() : rawObj;
    }
}
