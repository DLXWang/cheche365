package com.cheche365.cheche.externalapi.api.huanong

import com.cheche365.cheche.externalapi.model.huanong.HuanongPaymentResponse
import org.springframework.stereotype.Service

/**
 * Created by wen on 2018/8/7.
 */
@Service
class HuanongPaymentAPI extends HuanongAPI{

    static final String PAYMENT_CHANNEL_WECHARTS = '5'   //微信扫码支付

    @Override
    String transCode() {
        'APPLYFORPAY'
    }

}
