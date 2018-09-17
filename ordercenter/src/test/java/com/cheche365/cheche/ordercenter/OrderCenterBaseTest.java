package com.cheche365.cheche.ordercenter;

import com.cheche365.cheche.core.app.config.CoreConfig;
import com.cheche365.cheche.ordercenter.app.config.OrderCenterConfig;
import org.junit.runner.RunWith;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by yinJianBin on 2017/10/11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {CoreConfig.class, OrderCenterConfig.class}
)
@EnableWebMvc
@WebAppConfiguration
@EnableSpringDataWebSupport
public class OrderCenterBaseTest {
}
