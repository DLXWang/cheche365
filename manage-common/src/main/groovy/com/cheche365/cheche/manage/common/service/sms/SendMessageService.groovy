package com.cheche365.cheche.manage.common.service.sms

import com.cheche365.cheche.core.constants.SmsConstants
import com.cheche365.cheche.core.service.IBindingService
import com.cheche365.cheche.core.service.sms.SmsInfo
import com.cheche365.cheche.core.util.RuntimeUtil
import com.cheche365.cheche.sms.client.service.SmsService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.constants.SmsConstants._SMS_PRODUCT_KEY
import static com.cheche365.cheche.core.constants.SmsConstants._SMS_VENDOR_KEY

/**
 * Created by sunhuazhong on 2015/10/27.
 */
@Service
@Slf4j
public class SendMessageService {

    @Autowired
    private SmsService smsService;

    @Autowired
    private IBindingService bindingService;

    public int sendMessage(SmsInfo smsInfo) {
        def smsList = [[smsInfo.mobile, smsInfo.content]]

        Map paramMap = [
                (_SMS_VENDOR_KEY) : smsInfo.getSmsChannel(),
                (_SMS_PRODUCT_KEY): SmsConstants._SMS_PRODUCT_CHECHE
        ]
        List<List> resultList = smsService.sendSmsContents(smsList, paramMap, false);
        List result = resultList.get(0);
        return (int) result.get(0);
    }

    public int sendBatchMessage(SmsInfo smsInfo) {
        def mobileList = smsInfo.getMobile().split(',')
        def smsList = [[]]
        mobileList.each { mobile ->
            smsList << [mobile, smsInfo.getContent()]
        }
        Map paramMap = [
                (_SMS_VENDOR_KEY) : smsInfo.getSmsChannel(),
                (_SMS_PRODUCT_KEY): SmsConstants._SMS_PRODUCT_CHECHE
        ]
        List<List> resultList = smsService.sendSmsContents(smsList, paramMap, false);
        List result = resultList.get(0);
        return (int) result.get(0);
    }

    public int sendValidateCodeMessage(SmsInfo smsInfo) {
        Map additionalParameters = new HashMap<>();
        additionalParameters.put(IBindingService.DEST, smsInfo.mobile);
        additionalParameters.put(IBindingService.CONTENT, smsInfo.content);
        additionalParameters.put(IBindingService.DRYRUN, RuntimeUtil.isDevEnv());
        additionalParameters.put(_SMS_VENDOR_KEY, smsInfo.getSmsChannel())
        additionalParameters.put(_SMS_PRODUCT_KEY, SmsConstants._SMS_PRODUCT_CHECHE)
        String verifyCode = smsInfo.getVerifyCode()
        return bindingService.bind(smsInfo.getMobile(), verifyCode, additionalParameters);
    }

    public String getSmsResultDetail(Integer result) {
        Map<Integer, String> smsResultExplainMappings = (Map<Integer, String>) SmsConstants._SMS_RESULT_EXPLAIN_MAPPINGS;
        return smsResultExplainMappings.get(result);
    }

}
