package com.cheche365.cheche.developer.controller;

import com.cheche365.cheche.web.counter.annotation.NonProduction;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.service.order.ModifyOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @Author shanxf
 * @Date 2018/4/24  11:09
 */

@RestController
@RequestMapping(value = "/internal/developer/payment")
public class ModifyPaymentResource {

    @Autowired
    private ModifyOrderService modifyOrderService;

    @PostMapping(value = "/payment/amount" )
    @NonProduction
    public HttpEntity<RestResponseEnvelope> modifyPayAmount(@RequestParam String orderNo){

        return new ResponseEntity<>(new RestResponseEnvelope(modifyOrderService.modifyPayAmount(orderNo)), HttpStatus.OK);
    }
}
