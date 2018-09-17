package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.repository.PaymentChannelRepository
import com.cheche365.cheche.core.util.RuntimeUtil
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Transient

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
class PaymentChannel extends AutoLoadEnum {
    private static final long serialVersionUID = 1L

    private boolean customerPay;
    private externalUrl;
    private Long parentId;
    private String logoUrl

    @Column(columnDefinition = "tinyint(1)")
    boolean isCustomerPay() {
        return customerPay;
    }

    void setCustomerPay(boolean customerPay) {
        this.customerPay = customerPay;
    }

    @Transient
    def getExternalUrl() {
        return externalUrl
    }

    void setExternalUrl(externalUrl) {
        this.externalUrl = externalUrl
    }

    @Transient
    String getChannel() { //为了保证与前端兼容
        return this.description
    }

    @Column(columnDefinition = "bigint(20)")
    Long getParentId() {
        return parentId
    }

    void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Column(columnDefinition = "VARCHAR(100)")
    String getLogoUrl() {
        return logoUrl
    }

    void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl
    }

    @Transient
    String getFullDescription() {
        return PaymentChannel.Enum.getFullDescription(this)
    }

    static class Enum {

        public static PaymentChannel ALIPAY_1;
        public static PaymentChannel QUICKMONEY_2;
        public static PaymentChannel UNIONPAY_3;
        public static PaymentChannel WECHAT_4;
        public static PaymentChannel COUPONS_8;
        public static PaymentChannel AGENTREBATE_9;
        public static PaymentChannel BAOXIAN_PAY_16;
        public static PaymentChannel SOO_PAY_17
        public static PaymentChannel OFFLINE_PAY_19
        public static PaymentChannel ZA_PAY_20
        public static PaymentChannel PING_PLUS_PAY_21
        public static PaymentChannel PING_PLUS_ALIPAY_22
        public static PaymentChannel PING_PLUS_WX_23
        public static PaymentChannel PING_PLUS_UPACP_24
        public static PaymentChannel PING_PLUS_BFB_25
        public static PaymentChannel SINOSAFE_PAY_49
        public static PaymentChannel ALIPAY_OFFLINE_PAY_50
        public static PaymentChannel ACCOUNT_OFFLINE_PAY_51
        public static PaymentChannel BOTPY_52
        public static PaymentChannel BOTPY_ALIPAY_53
        public static PaymentChannel BOTPY_WEIXIN_54
        public static PaymentChannel TK_55
        public static PaymentChannel TK_ALIPAY_56
        public static PaymentChannel TK_WECHAT_57
        public static PaymentChannel HUANONG_59
        public static PaymentChannel HUANONG_WECHATS_60
        public static PaymentChannel AGENT_PARSER_61
        public static PaymentChannel AGENT_PARSER_ALIPAY_62
        public static PaymentChannel AGENT_PARSER_WECHAT_63
        public static List<PaymentChannel> ONLINE_CHANNELS;
        public static List<PaymentChannel> ALL;

        public static List<PaymentChannel> WECHAT_SUPPORT_CHANNELS;
        public static List<PaymentChannel> DEFAULT_SUPPORT_CHANNELS;
        public static List<PaymentChannel> ALIPAY_SUPPORT_CHANNELS;
        public static List<PaymentChannel> NON_AUTO_CHANNELS;
        public static List<PaymentChannel> PING_PLUS_DEFAULT_SUPPORT_CHANNELS;
        public static List<PaymentChannel> PING_PLUS_WECHAT_SUPPORT_CHANNELS;
        public static List<PaymentChannel> PING_PLUS_ALIPAY_SUPPORT_CHANNELS;
        public static List<PaymentChannel> PING_PLUS_WECHAT_LITE_SUPPORT_CHANNELS
        public static List<PaymentChannel> PING_PLUS_BDINSUR_SUPPORT_CHANNELS
        public static List<PaymentChannel> PING_PLUS_JD_SUPPORT_CHANNELS
        public static List<PaymentChannel> BOTPY_SUPPORT_CHANNELS
        public static List<PaymentChannel> TK_SUPPORT_CHANNELS
        public static List<PaymentChannel> AGENT_PARSER_SUPPORT_CHANNELS
        public static List<PaymentChannel> HUANONG_SUPPORT_CHANNELS
        public static HashMap DEFAULT_NAME = new HashMap()


        static {

            ALL = RuntimeUtil.loadEnum(PaymentChannelRepository, PaymentChannel, Enum)

            ONLINE_CHANNELS = [ALIPAY_1, UNIONPAY_3, WECHAT_4, SOO_PAY_17,
                               PING_PLUS_ALIPAY_22, PING_PLUS_WX_23, PING_PLUS_UPACP_24]

            DEFAULT_SUPPORT_CHANNELS = [ALIPAY_1, UNIONPAY_3, WECHAT_4, SOO_PAY_17]
            WECHAT_SUPPORT_CHANNELS = [UNIONPAY_3, WECHAT_4, SOO_PAY_17]
            ALIPAY_SUPPORT_CHANNELS = [ALIPAY_1]
            NON_AUTO_CHANNELS = [ALIPAY_1, UNIONPAY_3, WECHAT_4]

            PING_PLUS_DEFAULT_SUPPORT_CHANNELS = [PING_PLUS_ALIPAY_22, PING_PLUS_WX_23, PING_PLUS_UPACP_24]
            PING_PLUS_WECHAT_SUPPORT_CHANNELS = [PING_PLUS_WX_23, PING_PLUS_UPACP_24]
            PING_PLUS_ALIPAY_SUPPORT_CHANNELS = [PING_PLUS_ALIPAY_22, PING_PLUS_UPACP_24]
            PING_PLUS_WECHAT_LITE_SUPPORT_CHANNELS = [PING_PLUS_WX_23]
            PING_PLUS_BDINSUR_SUPPORT_CHANNELS = [PING_PLUS_ALIPAY_22, PING_PLUS_WX_23, PING_PLUS_UPACP_24, PING_PLUS_BFB_25]
            PING_PLUS_JD_SUPPORT_CHANNELS = [PING_PLUS_WX_23, PING_PLUS_UPACP_24]

            AGENT_PARSER_SUPPORT_CHANNELS = [AGENT_PARSER_ALIPAY_62, AGENT_PARSER_WECHAT_63]
            BOTPY_SUPPORT_CHANNELS = [BOTPY_52,BOTPY_ALIPAY_53,BOTPY_WEIXIN_54]
            TK_SUPPORT_CHANNELS=[TK_ALIPAY_56,TK_WECHAT_57]
            HUANONG_SUPPORT_CHANNELS = [HUANONG_WECHATS_60]

            DEFAULT_NAME.put(PING_PLUS_ALIPAY_22.id, "alipay_wap")
            DEFAULT_NAME.put(PING_PLUS_WX_23.id, "wx_wap")
            DEFAULT_NAME.put(PING_PLUS_UPACP_24.id, "upacp_wap")
            DEFAULT_NAME.put(PING_PLUS_BFB_25.id, "bfb_wap")

        }

        static List<PaymentChannel> getNoNAutoChannels() {
            NON_AUTO_CHANNELS.collect { it }
        }

        static boolean isWeChat(PaymentChannel targetChannel) {
            WECHAT_4 == targetChannel
        }

        static boolean isOnLinePay(PaymentChannel targetChannel) {
            ONLINE_CHANNELS.contains(targetChannel)
        }


        static boolean isCoupon(PaymentChannel targetChannel) {
            COUPONS_8 == targetChannel
        }

        static boolean isNonRebatePayment(PaymentChannel targetChannel) {
            isOnLinePay(targetChannel);
        }

        static boolean isPingPlusPay(PaymentChannel targetChannel) {
            PING_PLUS_PAY_21.id == targetChannel.parentId
        }

        static PaymentChannel toPaymentChannel(Long id) {
            ALL.find { c -> c.id == id };
        }

        static PaymentChannel toPaymentChannel(String name) {
            ALL.find { c -> c.name.equalsIgnoreCase(name) };
        }

        static PaymentChannel format(PaymentChannel targetChannel) {
            toPaymentChannel(targetChannel?.id)
        }

        static String getFullDescription(PaymentChannel targetChannel) {
            if (targetChannel?.parentId && targetChannel.parentId != targetChannel.id) {
                return getFullDescription(toPaymentChannel(targetChannel.parentId)) + targetChannel.getDescription()
            }
            return targetChannel.getDescription()
        }
    }

}
