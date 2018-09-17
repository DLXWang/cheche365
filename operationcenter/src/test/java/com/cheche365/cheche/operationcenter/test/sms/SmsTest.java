package com.cheche365.cheche.operationcenter.test.sms;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by xu.yelong on 2016-06-13.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({VerifyCodeTest.class,PaymentRemindTest.class})
@EnableAutoConfiguration
@EnableWebMvc
@WebAppConfiguration
@EnableSpringDataWebSupport
@TransactionConfiguration

public class SmsTest {

}
