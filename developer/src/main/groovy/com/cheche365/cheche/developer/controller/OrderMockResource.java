package com.cheche365.cheche.developer.controller;

import com.cheche365.cheche.developer.service.OrderInfoService;
import com.cheche365.cheche.developer.service.SyncOrderService;
import com.cheche365.cheche.web.counter.annotation.NonProduction;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author shanxf
 * @Date 2018/4/23  14:39
 */
@RestController
@RequestMapping(value = "/internal/developer/order")
public class OrderMockResource {
    private Logger logger = LoggerFactory.getLogger(OrderMockResource.class);

    @Autowired
    private SyncOrderService syncOrderService;

    @Autowired
    private OrderInfoService orderInfoService;

    @RequestMapping(value = "/sync/history/{orderNo}",method = RequestMethod.GET)
    @NonProduction
    public HttpEntity<RestResponseEnvelope> orderHistory(@PathVariable String orderNo){

        return new ResponseEntity(new RestResponseEnvelope(syncOrderService.findSyncHistory(orderNo)), HttpStatus.OK);
    }

    @RequestMapping(value = "/{orderNo}/info",method = RequestMethod.GET)
    @NonProduction
    public HttpEntity<RestResponseEnvelope> orderInfo(@PathVariable String orderNo) {

        return new ResponseEntity<>(new RestResponseEnvelope(orderInfoService.assembleOrderInfo(orderNo)), HttpStatus.OK);
    }

    @PostMapping(value = "/{orderNo}/{status}")
    @NonProduction
    public HttpEntity<RestResponseEnvelope> syncOrder(@PathVariable String orderNo,
                                                      @PathVariable Long status) {
        logger.info("模拟修改订单状态，orderNo:{}, status:{}", orderNo, status);
        return new ResponseEntity(new RestResponseEnvelope<>(syncOrderService.syncOrder(orderNo,status)), HttpStatus.OK);

    }
}
