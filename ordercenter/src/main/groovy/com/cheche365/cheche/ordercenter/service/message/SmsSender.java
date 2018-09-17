package com.cheche365.cheche.ordercenter.service.message;

import com.cheche365.cheche.core.service.ISmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 短信发送器实现类
 * Created by sunhuazhong on 2015/4/27.
 */
@Service("smsSender")
@Transactional
public class SmsSender implements ISender {

    private Logger logger = LoggerFactory.getLogger(SmsSender.class);

    @Autowired
    private ISmsService smsService;

    @Override
    public void sender(String status, Map<String, String> params, String... to) {
        // 针对各个手机号发送短信
        for (String phone : to) {
            send(params, phone);
        }
    }

    /**
     * 如果超过短信的长度，则分成几条发
     *
     * @param params
     * @param phoneNo
     */
    private void send(Map<String, String> params, String phoneNo) {
        /*String content = "";
        switch (params.get(OrderCenterConstants.SMS_TEMPLATE_KEY_TEMPLATE_NO)) {
            case OrderCenterConstants.PAYMENT_SMS_TEMPLATE_NO:                                      //出单中心：线下发送短信给外勤
                content = params.get(OrderCenterConstants.SMS_TEMPLATE_KEY_TEMPLATE_NO)             //SMS编号
                        + "|" + params.get(OrderCenterConstants.SMS_TEMPLATE_KEY_ORDER_NO)              //订单编号
                        + "|" + params.get(OrderCenterConstants.SMS_TEMPLATE_KEY_PREMIUM)               //总金额
                        + "|" + params.get(OrderCenterConstants.SMS_TEMPLATE_KEY_PREMIUM_NO_AUTO_TAX)   //保费金额（不含车船税）
                        + "|" + params.get(OrderCenterConstants.SMS_TEMPLATE_KEY_NAME)                  //客户姓名
                        + "|" + params.get(OrderCenterConstants.SMS_TEMPLATE_KEY_MOBILE)                //客户手机
                        + "|" + params.get(OrderCenterConstants.SMS_TEMPLATE_KEY_PAY_TIME)              //客户预约收款时间
                        + "|" + params.get(OrderCenterConstants.SMS_TEMPLATE_KEY_ADDRESS)               //收款地址
                        + "|" + params.get(OrderCenterConstants.SMS_TEMPLATE_KEY_CONFIRM_NO);           //确认单号
                break;
            case OrderCenterConstants.INPUT_QUOTE_SMS_TEMPLATE_NO:                                  //录入报价给用户发送短信
                content = params.get(OrderCenterConstants.SMS_TEMPLATE_KEY_TEMPLATE_NO)             //SMS编号
                        + "|" + params.get(OrderCenterConstants.SMS_TEMPLATE_KEY_THIRD_PARTY_PREMIUM)   //三者险保费
                        + "|" + params.get(OrderCenterConstants.SMS_TEMPLATE_KEY_DRIVER_PREMIUM)        //司机险保费
                        + "|" + params.get(OrderCenterConstants.SMS_TEMPLATE_KEY_PASSENGER_PREMIUM)     //乘客险保费
                        + "|" + params.get(OrderCenterConstants.SMS_TEMPLATE_KEY_PREMIUM)               //总保费
                        + "|" + params.get(OrderCenterConstants.SMS_TEMPLATE_KEY_QUOTE_PAYMENT_URL);    //报价支付URL
                break;
            default:
                break;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("接收人手机号：" + phoneNo + "，短信内容：" + content);
        }

        Map<String, String> additionalParams = new HashMap<>();
        additionalParams.put(OrderCenterConstants.SMS_SEND_DEST, phoneNo);
        additionalParams.put(OrderCenterConstants.SMS_SEND_CONTENT, content);

        List<String> sendData = new ArrayList<>();
        sendData.add(phoneNo);// 目的地
        sendData.add(content);// 内容
        List<List> payload = new ArrayList<>();
        payload.add(sendData);

        List<List> resultList = smsService.sendSmsContents(payload, false);
        if (resultList != null && resultList.size() > 0) {
            List result = resultList.get(0);
            int errorCode = (int) result.get(0);
            if (OrderCenterConstants.SMS_SEND_STATUS_SUCCESS == errorCode) {
                logger.info("发送短信成功.");
            } else {
                logger.error("发送短信失败.");
                throw new OrderCenterException(
                        OrderCenterConstants.EXCEPTION_SEND_SMS,
                        OrderCenterConstants.EXCEPTION_SEND_SMS_MESSAGE
                );
            }
        }*/
    }
}
