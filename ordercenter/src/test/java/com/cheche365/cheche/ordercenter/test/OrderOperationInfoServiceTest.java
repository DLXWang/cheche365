package com.cheche365.cheche.ordercenter.test;

import com.cheche365.cheche.core.app.config.CoreConfig;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.service.OrderOperationInfoService;
import com.cheche365.cheche.ordercenter.app.config.OrderCenterConfig;
import com.cheche365.cheche.ordercenter.service.order.OrderReverseGeneratorService;
import com.cheche365.cheche.ordercenter.web.model.order.OrderInsuranceViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by sunhuazhong on 2015/11/24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = { OrderCenterConfig.class, CoreConfig.class }
)
@EnableAutoConfiguration
@EnableWebMvc
@WebAppConfiguration
@EnableSpringDataWebSupport
@Rollback
public class OrderOperationInfoServiceTest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private OrderOperationInfoService orderOperationInfoService;

    @Autowired
    private OrderReverseGeneratorService orderReverseGeneratorService;

    @Test
    public void testSaveOrderCenterInfo() {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(2023l);
        orderOperationInfoService.saveOrderCenterInfo(purchaseOrder);
    }


    @Test
    public void testFindModelByOrderNo(){
        OrderInsuranceViewModel model = orderReverseGeneratorService.findModelByOrderNo("OrderInsuranceViewModel");
    }
}
