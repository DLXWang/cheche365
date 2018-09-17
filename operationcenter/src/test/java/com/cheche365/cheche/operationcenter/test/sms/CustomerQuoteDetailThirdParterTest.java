package com.cheche365.cheche.operationcenter.test.sms;

import com.cheche365.cheche.core.model.QuoteRecord;
import com.cheche365.cheche.core.model.ScheduleCondition;
import com.cheche365.cheche.core.serializer.SerializerUtil;
import com.cheche365.cheche.core.service.sms.ConditionTriggerUtil;
import com.cheche365.cheche.core.service.sms.SmsCodeConstant;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * Created by xu.yelong on 2016-06-22.
 * 人工报价报价详情-第三方合作
 */
public class CustomerQuoteDetailThirdParterTest extends BaseTest {
    @Autowired
    private Environment environment;

    @Test
    public void test() {
        QuoteRecord quoteRecord = quoteRecordService.getById(1L);
        paramMap.put(SmsCodeConstant.TYPE, ScheduleCondition.Enum.CUSTOMER_QUOTE_DETAIL_THIRDPARTNER.getId().toString());
        ConditionTriggerUtil.getThirdPartnerParams(quoteRecord.getChannel().getId(), paramMap);
        paramMap.put(SmsCodeConstant.CUSTOMER_QUOTE_DETAIL, SerializerUtil.generateQuoteDetail(quoteRecord));
        paramMap.put(SmsCodeConstant.INSURANCE_COMPANY_NAME, quoteRecord.getInsuranceCompany().getName());
        process();
    }
}
