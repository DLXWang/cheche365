package com.cheche365.cheche.ordercenter.test;

import com.cheche365.cheche.ordercenter.app.config.OrderCenterConfig;
import com.cheche365.cheche.ordercenter.service.order.OrderManageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by sunhuazhong on 2015/5/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = { OrderCenterConfig.class }
)
@EnableAutoConfiguration
@EnableWebMvc
@WebAppConfiguration
@EnableSpringDataWebSupport
@TransactionConfiguration
public class IssueCodeTest {

    Logger logger = LoggerFactory.getLogger(IssueCodeTest.class);

    @Autowired
    private OrderManageService orderManageService;


}
