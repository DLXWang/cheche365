package com.cheche365.cheche.unionpay

import com.cheche365.cheche.core.app.config.CoreConfig
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.service.UnifiedRefundHandler
import com.cheche365.cheche.unionpay.app.config.UnionPayConfig
import com.cheche365.cheche.unionpay.payment.IUnionPayHandler
import com.cheche365.cheche.unionpay.payment.UnionPaySignature
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

@WebAppConfiguration
@ContextConfiguration( classes = [ CoreConfig, UnionPayConfig ] )
class UnionPayRefundFT extends Specification {


    @Autowired
    PaymentRepository paymentRepository

    @Autowired
    UnifiedRefundHandler refundTradeHandler

    /**
     * 银联退款测试
     * @return
     */
    def "union pay refund test"() {
        given: "订单保单数据"
        //目标值
        def payment = paymentRepository.findOne(Long.valueOf(System.getProperty('test.payment.id')))

        when: "转换数据格式"

        refundTradeHandler.refund(payment)

        then: "校验格式"
        //判断目标值和实际值
        true

    }


    public Map<String, String> createReqMap(String price) {
        Map<String, String> dataMap = new HashMap<>();
        // 版本号
        dataMap.put("version", IUnionPayHandler.UNION_PAY_VERSION);
        // 字符集编码 默认"UTF-8"
        dataMap.put("encoding", IUnionPayHandler.UNION_PAY_ENCODING);
        // 签名方法 01 RSA
        dataMap.put("signMethod", IUnionPayHandler.UNION_PAY_SIGN_METHOD);
        // 交易类型 04-退货
        dataMap.put("txnType", IUnionPayHandler.UNION_PAY_TXN_TYPE_04);
        // 交易子类型 01:自助消费 02:订购 03:分期付款 00默认
        dataMap.put("txnSubType", IUnionPayHandler.UNION_PAY_TXN_SUBTYPE_00);
        // 业务类型
        dataMap.put("bizType", IUnionPayHandler.UNION_PAY_BIZ_TYPE_000201);
        // 渠道类型，07-PC，08-手机
        dataMap.put("channelType", "08");
        // 后台通知地址
        dataMap.put("backUrl", UnionPayConstant.UNION_PAY_CALLBACK_BACK_URL);
        // 接入类型，商户接入填0 0- 商户 ， 1： 收单， 2：平台商户
        dataMap.put("accessType", IUnionPayHandler.UNION_PAY_ACCESS_TYPE);
        // 商户号码，请改成自己的商户号
        dataMap.put("merId", "898111475380111");
        // 订单号
        dataMap.put("orderId", "T20161010000003TK001");
        // 原交易流水号
        dataMap.put("origQryId", "201610101650416121778");
        // 订单发送时间，取系统时间
        dataMap.put("txnTime", IUnionPayHandler.dateFormat.format(new Date()));
        // 交易金额，单位分
        dataMap.put("txnAmt", price);
        // 交易币种
        dataMap.put("currencyCode", IUnionPayHandler.UNION_PAY_CURRENCY_CODE);
        //请求方保留域
        dataMap.put("reqReserved", "WAP");
        return UnionPaySignature.signData(dataMap);
    }

}
