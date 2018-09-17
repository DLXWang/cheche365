package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.service.RebateRateMongoService;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by shanxf on 2017/8/28.
 */
@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/rebate/rate")
public class RebateRateResource extends ContextResource {

    @Autowired
    private RebateRateMongoService rebateRateMongoService;

    @RequestMapping(value ="list",method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<Map>> rebateRateList(){

        Map map =rebateRateMongoService.getRebateRate();
        return new ResponseEntity<>(new RestResponseEnvelope<>(map), HttpStatus.OK);
    }

}
