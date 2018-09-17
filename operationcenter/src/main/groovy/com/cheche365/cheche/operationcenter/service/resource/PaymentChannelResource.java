package com.cheche365.cheche.operationcenter.service.resource;

import com.cheche365.cheche.core.model.PaymentChannel;
import com.cheche365.cheche.core.repository.PaymentChannelRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.operationcenter.web.model.partner.PaymentChannelViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wangfei on 2015/6/16.
 */
@Component
public class PaymentChannelResource extends BaseService<PaymentChannel, PaymentChannel> {

    @Autowired
    private PaymentChannelRepository paymentChannelRepository;

    public List<PaymentChannel> listAll() {
        return super.getAll(paymentChannelRepository);
    }

    public List<PaymentChannel> listEnable() {
        // 支付宝，微信，银联，线下付款刷卡
        List<PaymentChannel> paymentChannelList = Arrays.asList(
            PaymentChannel.Enum.ALIPAY_1, PaymentChannel.Enum.UNIONPAY_3, PaymentChannel.Enum.WECHAT_4);
        return paymentChannelList;
    }

    public List<PaymentChannelViewModel> createViewData(List<PaymentChannel> paymentChannelList) {
        if (paymentChannelList == null)
            return null;

        List<PaymentChannelViewModel> viewDataList = new ArrayList<>();
        paymentChannelList.forEach(paymentChannel -> {
            PaymentChannelViewModel viewData = new PaymentChannelViewModel();
            viewData.setId(paymentChannel.getId());
            viewData.setChannel(paymentChannel.getName());
            viewDataList.add(viewData);
        });

        return viewDataList;
    }
}
