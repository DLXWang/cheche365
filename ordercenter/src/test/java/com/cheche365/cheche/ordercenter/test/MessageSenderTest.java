package com.cheche365.cheche.ordercenter.test;

import com.cheche365.cheche.core.message.RedisPublisher;
import com.cheche365.cheche.ordercenter.app.config.OrderCenterConfig;
import com.cheche365.cheche.ordercenter.constants.OrderCenterConstants;
import com.cheche365.cheche.ordercenter.exception.OrderCenterException;
import com.cheche365.cheche.ordercenter.service.message.IMessageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息发送测试
 * Created by sunhuazhong on 2015/4/27.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {OrderCenterConfig.class}
)
@EnableAutoConfiguration
@EnableWebMvc
@WebAppConfiguration
@EnableSpringDataWebSupport
@TransactionConfiguration
public class MessageSenderTest {

    private Logger logger = LoggerFactory.getLogger(MessageSenderTest.class);

    @Autowired
    private IMessageService messageService;

//    @Autowired
//    private PaymentRepository paymentRepository;


    @Autowired
    private RedisPublisher redisPublisher;

    @Test
    public void sendEmail() throws OrderCenterException {
        // 未付款新订单
        String status = OrderCenterConstants.OPERATE_STATUS_EMAIL_NEW_ORDER_WITHOUT_PAYMENT;

        Map paramMap = new HashMap<>();
        paramMap.put("orderStatus", "未付款");
        paramMap.put("orderNo", "T20150428000001");
        paramMap.put("orderAddress", "T20150428000001");

        messageService.sendMessage(OrderCenterConstants.SEND_TYPE_EMAIL, status, paramMap, "cheche-insurances@cheche365.com", "sunhz@cheche365.com");//sunhz@cheche365.com
    }

    @Test
    public void sendSms() throws OrderCenterException {
        // 派单外勤
        String status = OrderCenterConstants.OPERATE_STATUS_SMS_PAYMENT;

        Map paramMap = new HashMap<>();
        paramMap.put("orderNo", "T20150428000001");
        paramMap.put("premium", "1234.00");
        paramMap.put("dispatchTime", "2015-05-05");
        paramMap.put("name", "张三哥");
        paramMap.put("mobile", "13061367820");
        paramMap.put("address", "北京市朝阳区北苑路甲13号");

        messageService.sendMessage(OrderCenterConstants.SEND_TYPE_SMS, status, paramMap, "15010066753", "15010066753");
    }

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void genPaymentString() throws Exception {
      //  Payment payment = paymentRepository.findOne(11812L);

    }
}
