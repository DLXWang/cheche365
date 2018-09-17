package com.cheche365.cheche.ordercenter.web.controller.order;

import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.PurchaseOrderAmend;
import com.cheche365.cheche.core.model.ResultModel;
import com.cheche365.cheche.core.repository.PurchaseOrderAmendRepository;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * Created by wangfei on 2015/4/30.
 */
@RestController
@RequestMapping("/orderCenter/order/refund/")
public class OrderRufundTempController {

    private Logger logger = LoggerFactory.getLogger(OrderRufundTempController.class);

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private PurchaseOrderAmendRepository purchaseOrderAmendRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping(value = "{orderNo}/{status}", method = RequestMethod.GET)
    public ResultModel processRefund(@PathVariable String orderNo, @PathVariable String status) {

        PurchaseOrder purchaseOrder = purchaseOrderService.findFirstByOrderNo(orderNo.toString());
        if (purchaseOrder == null) {
            return new ResultModel(false, "没有查询到该订单!");
        }

        PurchaseOrderAmend purchaseOrderAmend = purchaseOrderAmendRepository.findLatestAmendByPurchaseOrder(purchaseOrder);
        if (purchaseOrderAmend == null) {
            return new ResultModel(false, "没有查询到该订单的退款记录!");
        }

        if (status == null || !status.equals("1")) {
            status = "0";
        }

        //{ "id": "999999999", "flag":"false"}
//        redisTemplate.convertAndSend(MultiPaymentMessage.QUEUE_NAME, "{ \"id\": \"" + purchaseOrderAmend.getId() + "\", \"flag\":\"" + status + "\"}");

        return new ResultModel(true, "消息发送成功!");

    }


}
