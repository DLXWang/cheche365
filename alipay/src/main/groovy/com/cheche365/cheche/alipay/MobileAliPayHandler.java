package com.cheche365.cheche.alipay;

import com.cheche365.cheche.alipay.dto.PayRequestDto;
import com.cheche365.cheche.alipay.util.AlipayCore;
import com.cheche365.cheche.alipay.util.AlipayNotify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class MobileAliPayHandler implements AliPayHandler {

    @Autowired
    protected AlipayCore alipayCore;

    @Autowired
    protected AlipayNotify alipayNotify;

    @Override
    public String buildPayRequest(PayRequestDto payRequestDto) {
        return alipayCore.buildMobilePayRequest(payRequestDto);
    }

    @Override
    public boolean verify(Map<String, String> params, String outTradeNo) {
        return alipayNotify.verify(params, outTradeNo);
    }

}
