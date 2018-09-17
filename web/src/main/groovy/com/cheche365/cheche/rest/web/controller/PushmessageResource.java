package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.service.UserService;
import com.cheche365.cheche.rest.service.pushmessage.IPushService;
import com.cheche365.cheche.rest.service.pushmessage.PushBusinessType;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.counter.annotation.NonProduction;
import com.cheche365.cheche.web.version.VersionedResource;
import com.cheche365.pushmessage.api.model.PushBody;
import com.cheche365.pushmessage.api.service.IPushmessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/pushmessage")
@VersionedResource(from = "1.5")
class PushmessageResource extends ContextResource{

    @Autowired
    private IPushmessageService pushmessageService;

    @Autowired
    private IPushService pushService;
    @Autowired
    private UserService userService;
    @NonProduction
    @RequestMapping(value = "push", method = RequestMethod.POST)
    public HttpEntity push(@RequestBody PushBody pushBody) {

        return getResponseEntity(pushmessageService.push(pushBody));
    }

    @NonProduction
    @RequestMapping(value = "simplePush", method = RequestMethod.POST)
    public HttpEntity simplePush(@RequestParam(value = "mobile") String mobile) {
        User user = userService.getUserByMobile(mobile);
        return getResponseEntity(pushService.simplePush(user, PushBusinessType.ORDER));
    }
}
