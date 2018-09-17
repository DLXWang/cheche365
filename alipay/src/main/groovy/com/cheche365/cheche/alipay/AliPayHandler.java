package com.cheche365.cheche.alipay;

import com.cheche365.cheche.alipay.dto.PayRequestDto;

import java.util.Map;

/**
 * Created by chenxiaozhe on 15-8-18.
 */
public interface AliPayHandler {


    /**
     * 生成请求AliPay的请求串
     *
     * @param payRequestDto
     * @return
     */
    String buildPayRequest(PayRequestDto payRequestDto);

    /**
     * 验证异步回调时支付宝推送过来的信息
     *
     * @param params
     * @param outTradeNo
     * @return
     */
    boolean verify(Map<String, String> params, String outTradeNo);


}
