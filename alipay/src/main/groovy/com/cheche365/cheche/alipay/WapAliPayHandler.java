package com.cheche365.cheche.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.cheche365.cheche.alipay.constants.AlipayServiceEnvConstants;
import com.cheche365.cheche.alipay.dto.PayRequestDto;
import com.cheche365.cheche.alipay.util.AliPayConstant;
import com.cheche365.cheche.alipay.util.AlipayCore;
import com.cheche365.cheche.core.util.CacheUtil;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class WapAliPayHandler implements AliPayHandler {

    @Autowired
    protected AlipayCore alipayCore;

    @Override
    public String buildPayRequest(PayRequestDto payRequestDto) {


        AlipayClient alipayClient = new DefaultAlipayClient(AliPayConstant.WAP_ALIPAY_GATEWAY_URL, AlipayServiceEnvConstants.APP_ID, AlipayServiceEnvConstants.PRIVATE_KEY, "json", AliPayConstant.INPUT_CHARSET, AlipayServiceEnvConstants.ALIPAY_PUBLIC_KEY, AlipayServiceEnvConstants.SIGN_TYPE); //获得初始化的AlipayClient
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
        alipayRequest.putOtherTextParam("app_id", AlipayServiceEnvConstants.APP_ID);
        alipayRequest.putOtherTextParam("charset", AliPayConstant.INPUT_CHARSET);
        alipayRequest.putOtherTextParam("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        alipayRequest.setReturnUrl(AliPayConstant.getDirectPayReturnUrl());
        alipayRequest.setNotifyUrl(AliPayConstant.getWapPayNotifyUrl());//在公共参数中设置回跳和通知地址

        Map<String, String> contentMap = new HashMap<>();
        contentMap.put("body", payRequestDto.getBody());
        contentMap.put("subject", payRequestDto.getOutTradeNo());
        contentMap.put("out_trade_no", payRequestDto.getOutTradeNo());
        contentMap.put("total_amount", payRequestDto.getTotalFee());
        contentMap.put("product_code", "QUICK_WAP_PAY");
        contentMap.put("goods_type", AliPayConstant.PAYMENT_TYPE);
        contentMap.put("timeout_express", AliPayConstant.PAY_TIME);
        contentMap.put("seller_id", AliPayConstant.WAP_PARTNER);

        alipayRequest.setBizContent(CacheUtil.doJacksonSerialize(contentMap));//填充业务参数
        String form = null; //调用SDK生成表单
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            alipayCore.logResult(payRequestDto.getOutTradeNo(), e.toString());
        }

        return form;
    }

    @Override
    public boolean verify(Map<String, String> params, String outTradeNo) {
        try {
            return AlipaySignature.rsaCheckV1(params, AlipayServiceEnvConstants.ALIPAY_PUBLIC_KEY, AliPayConstant.INPUT_CHARSET, AlipayServiceEnvConstants.SIGN_TYPE);
        } catch (AlipayApiException e) {
            String sWord = "outTradeNo=" + outTradeNo + "\n params=" + params + "\n response的参数：" + alipayCore.createLinkString(params);
            alipayCore.logResult(outTradeNo, sWord);
            return false;
        }
    }

}
