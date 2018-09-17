package com.cheche365.cheche.operationcenter.test.sms;

import com.cheche365.cheche.core.app.config.CoreConfig;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.core.service.QuoteRecordService;
import com.cheche365.cheche.core.service.sms.ConditionTriggerHandler;
import com.cheche365.cheche.core.service.sms.SmsCodeConstant;
import com.cheche365.cheche.operationcenter.app.config.OperationCenterConfig;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xu.yelong on 2016-06-13.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {CoreConfig.class, OperationCenterConfig.class}
)
@EnableWebMvc
@WebAppConfiguration
@EnableSpringDataWebSupport
public class BaseTest {
    String mobile = "17600806470";
    Map paramMap = new HashMap<>();

    @Autowired
    private ConditionTriggerHandler conditionTriggerHandler;

    @Autowired
    protected PurchaseOrderService purchaseOrderService;

    @Autowired
    protected QuoteRecordService quoteRecordService;

    protected void process() {
        paramMap.put(SmsCodeConstant.MOBILE, mobile);
        conditionTriggerHandler.process(paramMap);
    }

}
