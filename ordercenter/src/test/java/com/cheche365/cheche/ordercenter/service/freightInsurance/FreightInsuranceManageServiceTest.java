package com.cheche365.cheche.ordercenter.service.freightInsurance;

import com.cheche365.cheche.manage.common.web.model.DataTablePageViewModel;
import com.cheche365.cheche.ordercenter.app.config.OrderCenterConfig;
import com.cheche365.cheche.ordercenter.web.model.freightInsurance.FreightInsuranceOrderRequestModel;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

/**
 * Created by yinJianBin on 2017/9/1.
 */
@ContextConfiguration(classes = {
        OrderCenterConfig.class
})
@RunWith(SpringJUnit4ClassRunner.class)
public class FreightInsuranceManageServiceTest {

    @Autowired
    FreightInsuranceManageService freightInsuranceManageService;


    @org.junit.Test
    public void testListOrder() throws Exception {
        FreightInsuranceOrderRequestModel freightInsuranceOrderRequestModel = new FreightInsuranceOrderRequestModel();
        freightInsuranceOrderRequestModel.setPageSize(10);
        freightInsuranceOrderRequestModel.setCurrentPage(1);

        DataTablePageViewModel<Map<String, Object>> result = freightInsuranceManageService.listOrder(freightInsuranceOrderRequestModel);
        System.out.println(result);
    }
}