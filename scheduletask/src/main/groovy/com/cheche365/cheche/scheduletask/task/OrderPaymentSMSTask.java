package com.cheche365.cheche.scheduletask.task;
import com.cheche365.cheche.manage.common.service.OrderUnpaidSmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by cxy on 2018-03-1.
 */
@Service
public class OrderPaymentSMSTask extends BaseTask {
    @Autowired
    private OrderUnpaidSmsService service;

    @Override
    protected void doProcess() throws Exception {
        service.sendUnpayOrderSMS();
    }
}
