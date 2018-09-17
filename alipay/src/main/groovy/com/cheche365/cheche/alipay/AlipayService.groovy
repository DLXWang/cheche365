package com.cheche365.cheche.alipay

import com.cheche365.cheche.alipay.util.AlipayCore;
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.core.service.spi.IPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service;

import static com.cheche365.cheche.core.model.Channel.Enum.*;

/**
 * Created by chenxiaozhe on 15-8-18.
 */
@Service
class AlipayService implements IPayService {

    @Autowired
    private WapAliPayHandler wapAliPayHandler;

    @Autowired
    private MobileAliPayHandler mobileAliPayHandler;

    @Autowired
    AlipayCore coreService

    AliPayHandler findByChannel(Channel channel) {
        Channel.selfApp().contains(channel) ? mobileAliPayHandler : wapAliPayHandler
    }

    @Override
    def prePay(Map<String, Object> params) {
        return findByChannel(params.channel).buildPayRequest(coreService.getPayRequestDto(params))
    }

    @Override
    def refund(Map<String, Object> params) {
        return null
    }

    @Override
    def syncCallback(Map<String, Object> params) {
        return null
    }

    @Override
    def asyncCallback(Map<String, Object> params) {
        return null
    }

    @Override
    boolean support(PaymentChannel pc) {
        return PaymentChannel.Enum.ALIPAY_1 == pc
    }
}
