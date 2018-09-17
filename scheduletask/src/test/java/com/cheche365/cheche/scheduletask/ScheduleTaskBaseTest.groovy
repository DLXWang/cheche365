package com.cheche365.cheche.scheduletask;

import com.cheche365.cheche.core.app.config.CoreConfig;
import com.cheche365.cheche.scheduletask.app.config.ScheduleTaskConfig;
import org.junit.runner.RunWith;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by yinJianBin on 2018/6/1.
 */
@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(
        classes = [CoreConfig, ScheduleTaskConfig]
)
@EnableWebMvc
@WebAppConfiguration
@EnableSpringDataWebSupport
public class ScheduleTaskBaseTest {
}
