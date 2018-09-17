package com.cheche365.cheche.operationcenter.test.sms;

import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.QuoteRecord;
import com.cheche365.cheche.core.model.ScheduleCondition;
import com.cheche365.cheche.core.service.sms.SmsCodeConstant;
import org.junit.Test;

/**
 * Created by xu.yelong on 2016-06-22.
 * 人工报价提交订单-普通线下支付 || 支付宝好车主提交订单
 */
public class CustomerQuoteOrderNormalOfflineTest extends BaseTest {
    @Test
    public void test() {
        PurchaseOrder purchaseOrder = purchaseOrderService.findById(1L);
        QuoteRecord quoteRecord = quoteRecordService.getById(purchaseOrder.getObjId());
        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.CUSTOMER_QUOTE_ORDER_NORMAL_OFFLINE.getId().toString());
        paramMap.put(SmsCodeConstant.ORDER_ORDER_NO, purchaseOrder.getOrderNo());
        //paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.CUSTOMER_QUOTE_ORDER_ALIPAY_PERFECT_DRIVER.getId().toString());
        paramMap.put(SmsCodeConstant.AMOUNT, purchaseOrder.getPayableAmount().toString());
        paramMap.put(SmsCodeConstant.INSURANCE_COMPANY_NAME, quoteRecord.getInsuranceCompany().getName());
        process();
    }
}
