package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;


@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/iosmodule")
class AppModuleResource extends ContextResource {

    @RequestMapping(value = "/pushmessage/info", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> myMessage() {
        return getResponseEntity(new ArrayList<>());
    }
}
