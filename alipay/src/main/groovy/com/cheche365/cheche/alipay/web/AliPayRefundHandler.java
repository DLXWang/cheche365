package com.cheche365.cheche.alipay.web;

import com.cheche365.cheche.alipay.util.AliPayConstant;
import com.cheche365.cheche.alipay.util.AlipayCore;
import com.cheche365.cheche.alipay.util.refund.AliHttpPostUtil;
import com.cheche365.cheche.core.model.Payment;
import com.cheche365.cheche.core.model.PaymentChannel;
import com.cheche365.cheche.core.model.PaymentStatus;
import com.cheche365.cheche.core.repository.PaymentRepository;
import com.cheche365.cheche.core.service.PaymentSerialNumberGenerator;
import com.cheche365.cheche.core.service.UnifiedRefundHandler;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by Administrator on 2016/10/17 0017.
 */
@Component
public class AliPayRefundHandler extends UnifiedRefundHandler {

    private Logger logger = LoggerFactory.getLogger(AliPayRefundHandler.class);

    @Autowired
    private  AlipayCore alipayCore;

    @Autowired
    private PaymentSerialNumberGenerator paymentSerialNumberGenerator;

    @Autowired
    private PaymentRepository paymentRepository;

    public boolean support(Payment payment){
        return payment.getChannel().equals(PaymentChannel.Enum.ALIPAY_1);
    }


    @Transactional
    public Map<Long,Boolean> refund(List<Payment> payments) {
        return super.refund(payments);
    }

    @Transactional
    public boolean refund(Payment payment){
        try {
            paymentSerialNumberGenerator.next(payment);
            String result = AliHttpPostUtil.doPost(buildRefundRequest(payment), AliPayConstant.ALIPAY_GATEWAY_URL);
            logger.info("支付宝退款,交易号:{},响应报文:{}", payment.getOutTradeNo(), result);
            return "T".equals(aliRefundCheck(result));
        } catch (Exception e) {
            logger.info("支付宝退款,交易号:{},异常信息:{}", payment.getOutTradeNo(), ExceptionUtils.getStackTrace(e));
        }
        payment.setStatus(PaymentStatus.Enum.CANCEL_4);
        paymentRepository.save(payment);
        return false;

    }

    public Map<Long,Boolean> callPlatform(String orderNo,Map<Long,Map> sendMap){
        Map<Long,Boolean> map = new HashMap<Long,Boolean>();
        Set<Long> set = sendMap.keySet();
        for(Long id:set){
            Boolean  bol = false;
            try {
                if(null!=sendMap.get(id)) {
                    String result = AliHttpPostUtil.doPost(sendMap.get(id), AliPayConstant.ALIPAY_GATEWAY_URL);
                    bol = "T".equals(aliRefundCheck(result));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }catch ( Exception e) {
                e.printStackTrace();
            }
            map.put(id, bol);
        }
        return map;
    }

    public  Map<String, String> createMap(Payment payment){
        return buildRefundRequest(payment);
    }

    public String name() {
        return "支付宝支付";
    }

    private String aliRefundCheck(String result){
        String resultType = null;
        try {
            Document doc = DocumentHelper.parseText(result);
            Element root = doc.getRootElement();
            Element is_success = root.element("is_success");
            resultType = is_success.getTextTrim();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return resultType;
    }


    public Map<String,String> buildRefundRequest(Payment payment) {
        return alipayCore.buildRequestPara(getRequestRefundMap(payment), AliPayConstant.SIGN_RSA);
    }

    private Map<String, String> getRequestRefundMap(Payment payment) {
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", AliPayConstant.WAPREFUND_SERVICE);
        sParaTemp.put("partner", AliPayConstant.PARTNER);
        sParaTemp.put("_input_charset", AliPayConstant.INPUT_CHARSET);
        sParaTemp.put("notify_url", AliPayConstant.getWapPayNotifyUrl());
        sParaTemp.put("batch_no", payment.getOutTradeNo());
        sParaTemp.put("refund_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        sParaTemp.put("batch_num", "1");
        sParaTemp.put("detail_data", payment.getUpstreamId().getThirdpartyPaymentNo()+"^"+payment.getAmount()+"^退款");
        return sParaTemp;
    }
}
