package com.cheche365.cheche.unionpay.payment.front;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.core.service.spi.IPayService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.Channel.Enum.*;


@Service
class UnionPayService implements IPayService{

    @Autowired
    private MobileWapHandler mobileWapHandler;
    @Autowired
    private MobileAppHandler mobileAppHandler;

    UnionPayFrontTradeHandler findByChannel(Channel channel) {
        Channel.selfApp().contains(channel) ? mobileAppHandler : mobileWapHandler
    }

    @Override
    def prePay(Map<String, Object> params) {
        findByChannel(params.channel).preTrade(params);
    }

    @Override
    def refund(Map<String, Object> params) {
        return null
    }

    @Override
    def syncCallback(Map<String, Object> params) {
        findByChannel(params.channel).callFront(params);
    }

    @Override
    def asyncCallback(Map<String, Object> params) {
        findByChannel(params.channel).callBack(params);
    }

    @Override
    boolean support(PaymentChannel pc) {
        return PaymentChannel.Enum.UNIONPAY_3 == pc
    }

    void callBack(Channel channel, Map<String, String> respMap) {
        this.findByChannel(channel).callBack(respMap);
    }

    void callFront(Channel channel, Map<String, String> respMap) {
        this.findByChannel(channel).callFront(respMap);
    }
}
