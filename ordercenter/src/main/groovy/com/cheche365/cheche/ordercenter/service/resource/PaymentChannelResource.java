package com.cheche365.cheche.ordercenter.service.resource;

import com.cheche365.cheche.core.model.PaymentChannel;
import com.cheche365.cheche.core.repository.PaymentChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by wangfei on 2015/9/7.
 */
@Component
public class PaymentChannelResource {

    @Autowired
    private PaymentChannelRepository paymentChannelRepository;

    public List<PaymentChannel> listCustomerPayChannels() {
        return paymentChannelRepository.findByCustomerPay(true);
    }

    public List<PaymentChannel> listOnLinePayChannels() {
        return PaymentChannel.Enum.ONLINE_CHANNELS;
    }
}
