package com.cheche365.cheche.operationcenter.test.sms;

import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.QuoteRecord;
import com.cheche365.cheche.core.model.ScheduleCondition;
import com.cheche365.cheche.core.serializer.SerializerUtil;
import com.cheche365.cheche.core.service.sms.SmsCodeConstant;
import org.junit.Test;

/**
 * Created by xu.yelong on 2016-06-21.
 * 人工报价报价详情
 */
public class CustomerQuoteTest extends BaseTest {

    @Test
    public void test() {
        QuoteRecord quoteRecord = quoteRecordService.getById(1L);
        PurchaseOrder purchaseOrder = purchaseOrderService.findByQuoteRecordId(quoteRecord.getId());
        paramMap.put(SmsCodeConstant.ORDER_ORDER_NO, purchaseOrder.getOrderNo());
        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.CUSTOMER_QUOTE_DETAIL_ALIPAY.getId().toString());
        paramMap.put(SmsCodeConstant.CUSTOMER_QUOTE_DETAIL, SerializerUtil.generateQuoteDetail(quoteRecord));
        paramMap.put(SmsCodeConstant.INSURANCE_COMPANY_NAME, quoteRecord.getInsuranceCompany().getName());
        process();
    }
}
