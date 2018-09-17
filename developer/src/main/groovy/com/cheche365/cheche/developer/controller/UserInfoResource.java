package com.cheche365.cheche.developer.controller;

import com.cheche365.cheche.developer.service.UserInfoService;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/developer/userInfoResource")
public class UserInfoResource {

    @Autowired
    UserInfoService userInfoService;


    @RequestMapping(value = "/userInfoSyncHistory/{mobile}",method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> userInfoSyncHistory(@PathVariable String mobile){
        return new ResponseEntity<>(new RestResponseEnvelope(userInfoService.findSyncHistory(mobile)), HttpStatus.OK);
    }

}
