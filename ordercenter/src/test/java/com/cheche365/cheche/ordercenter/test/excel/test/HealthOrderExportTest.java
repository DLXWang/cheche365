package com.cheche365.cheche.ordercenter.test.excel.test;

import com.cheche365.cheche.core.app.config.CoreConfig;
import com.cheche365.cheche.core.model.abao.InsurancePolicy;
import com.cheche365.cheche.ordercenter.app.config.OrderCenterConfig;
import com.cheche365.cheche.ordercenter.service.healthOrder.HealthOrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import java.util.List;

/**
 * Created by xu.yelong on 2016/12/28.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = { OrderCenterConfig.class, CoreConfig.class }
)
@EnableAutoConfiguration
@EnableWebMvc
@WebAppConfiguration
@EnableSpringDataWebSupport
@TransactionConfiguration
public class HealthOrderExportTest {
    @Autowired
    private HealthOrderService healthOrderService;

    @Test
    public void exportNews(){
        List<InsurancePolicy> insurancePolicyList=healthOrderService.findByExportNotExists();

    }
}
