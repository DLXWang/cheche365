package com.cheche365.cheche.wechat.web.controller;

import com.cheche365.cheche.wechat.message.PaymentResponse;
import com.cheche365.cheche.wechat.payment.OrderPaymentManager;
import com.cheche365.cheche.wechat.util.XStreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by liqiang on 4/7/15.
 */

@Controller
public class PaymentController {

    @Autowired
    private OrderPaymentManager orderPaymentManager;

    private Logger logger = LoggerFactory.getLogger(PaymentController.class);


    @RequestMapping("/web/wechat/payment/callback")
    @ResponseBody
    public String callback(@RequestBody String requestBody){
        if (logger.isDebugEnabled()){
            logger.debug("received payment result from wechat: " + requestBody);
        }

        Map<String, Object> response = XStreamUtil.parseToMap(requestBody);
        orderPaymentManager.processOrderQueryResponse(response);
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setReturn_code("SUCCESS");
        paymentResponse.setReturn_msg("OK");
        return paymentResponse.toXmlString();
    }

}
