package com.cheche365.cheche.web.service

import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.Payment
import com.cheche365.cheche.core.model.PaymentChannel
import com.cheche365.cheche.core.repository.PaymentChannelRepository
import com.cheche365.cheche.core.repository.UserRepository
import com.cheche365.cheche.core.service.OAuthUrlGenerator
import com.cheche365.cheche.core.service.PurchaseOrderService
import com.cheche365.cheche.web.util.ClientTypeUtil
import org.apache.commons.collections.CollectionUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpServletRequest

import static com.cheche365.cheche.core.model.Channel.Enum.*
import static com.cheche365.cheche.core.model.PaymentChannel.Enum.*

/**
 * Created by mahong on 2015/9/22.
 */
@Service
@Transactional
class PaymentChannelService {

    @Autowired
    private PaymentChannelRepository paymentChannelRepository;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OAuthUrlGenerator oAuthUrlGenerator;

    @Autowired(required = false)
    HttpServletRequest request

    @Autowired
    private  PingPlusCnService pingPlusCnService

    static List<PaymentChannel> getByChannel(Channel channel) {
        List<PaymentChannel> paymentChannels
        if (WE_CHAT_3 == channel) {
            paymentChannels = WECHAT_SUPPORT_CHANNELS.collect { it }
        } else if (ALIPAY_21 == channel) {
            paymentChannels = ALIPAY_SUPPORT_CHANNELS.collect { it }
        } else if (channel.isSelf()) {
            paymentChannels = DEFAULT_SUPPORT_CHANNELS.collect { it }
        } else {
            paymentChannels = [ALIPAY_1, UNIONPAY_3]
        }

        if (channel.isThirdPartnerChannel()) {
            paymentChannels - SOO_PAY_17
        }
        return paymentChannels;
    }

    static List<PaymentChannel> getPingPlusByChannel(Channel channel) {
        List<PaymentChannel> paymentChannels
        if (WE_CHAT_3 == channel) {
            paymentChannels = PING_PLUS_WECHAT_SUPPORT_CHANNELS.collect { it }
        } else if (ALIPAY_21 == channel) {
            paymentChannels = PING_PLUS_ALIPAY_SUPPORT_CHANNELS.collect { it }
        } else if (WE_CHAT_APP_39 == channel) {
            paymentChannels = PING_PLUS_WECHAT_LITE_SUPPORT_CHANNELS.collect { it }
        } else if (channel.isSelf()) {
            paymentChannels = PING_PLUS_DEFAULT_SUPPORT_CHANNELS.collect { it }
        } else if (PARTNER_BDINSUR_215 == channel) {
            paymentChannels = PING_PLUS_BDINSUR_SUPPORT_CHANNELS.collect { it }
        } else if('jd' == channel?.apiPartner?.code){
            paymentChannels = PING_PLUS_JD_SUPPORT_CHANNELS.collect { it }
        } else {
            paymentChannels = [PING_PLUS_ALIPAY_22, PING_PLUS_UPACP_24]
        }

        return paymentChannels
    }


    static List<PaymentChannel> filterPaidChannel(List<PaymentChannel> channels, Payment payment) {
        channels.findAll { payment.channel == it }
    }

    synchronized void addOAuthUrl(List<PaymentChannel> paymentChannels, Payment paidPayment, Channel channel, String orderNo, Boolean newVersion) {
        PaymentChannel targetWechat = newVersion ? PING_PLUS_WX_23 : WECHAT_4
        PaymentChannel wechat = paymentChannels.find { paymentChannel -> paymentChannel == targetWechat }
        if (paidPayment && paidPayment.channel != wechat) {
            return
        }
        if (ClientTypeUtil.inWechat(request)) {
            PaymentChannel wechatPaymentChannel;
            if (wechat) {
                wechatPaymentChannel = wechat
            } else {
                paymentChannels.add(targetWechat)
                wechatPaymentChannel = targetWechat
            }

            def authParameter = [
                "type"     : "pay",
                "orderNo"  : orderNo,
                "channelId": channel.id,
                "paymentChannelId": wechatPaymentChannel.id,
                "oauthonly": "true",
                "fragment" : URLEncoder.encode("pay&" + orderNo + "&" + wechatPaymentChannel.id, 'utf-8')
            ]

            wechatPaymentChannel.setExternalUrl(oAuthUrlGenerator.toOAuthUrl("/wechat/oauth/callback", authParameter))
        } else {
            if (wechat) {
                wechat.setExternalUrl(null)
            }
        }
        removeAliPay(paymentChannels)
    }

    def nonAutoChannels(String orderNo, HttpServletRequest request) {

        List<PaymentChannel> channels = getNoNAutoChannels();
        addOAuthUrl(channels, null, null, orderNo, false);
        if (request.queryString?.contains('src')) {
            channels -= WECHAT_4
        }
        return channels
    }


    void removeAliPay(List<PaymentChannel> paymentChannels) {
        if (CollectionUtils.isNotEmpty(paymentChannels)) {
            if (ClientTypeUtil.inWechat(request)) {
                paymentChannels.removeAll([ALIPAY_1, PING_PLUS_ALIPAY_22])
            }
        }
    }

    void handleName(List<Map> paymentChannels, Channel channel) {
        paymentChannels.each {
            paymentChannel ->
                paymentChannel.put("name", pingPlusCnService.getPpChannelName(Long.valueOf(paymentChannel.id), channel, ClientTypeUtil.inWechat(request)))
        }

    }


}
