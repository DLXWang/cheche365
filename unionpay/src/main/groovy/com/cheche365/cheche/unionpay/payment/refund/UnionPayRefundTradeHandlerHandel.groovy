package com.cheche365.cheche.unionpay.payment.refund

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.PaymentRepository
import com.cheche365.cheche.core.service.PaymentSerialNumberGenerator
import com.cheche365.cheche.core.service.UnifiedRefundHandler
import com.cheche365.cheche.unionpay.UnionPayConstant
import com.cheche365.cheche.unionpay.UnionPayState
import com.cheche365.cheche.unionpay.payment.IUnionPayHandler
import com.cheche365.cheche.unionpay.payment.UnionPayProcessor
import com.cheche365.cheche.unionpay.payment.UnionPaySignature
import com.unionpay.acp.sdk.HttpClient
import com.unionpay.acp.sdk.SDKConfig
import com.unionpay.acp.sdk.SDKUtil
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Created by wangfei on 2016/3/1.
 */
@Component
class UnionPayRefundTradeHandlerHandel extends UnifiedRefundHandler implements IUnionPayHandler {
    private Logger logger = LoggerFactory.getLogger(UnionPayRefundTradeHandlerHandel.class);

    @Autowired
    private UnionPayProcessor unionPayProcessor;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentSerialNumberGenerator paymentSerialNumberGenerator;

    boolean support(Payment payment){
        return payment.getChannel() == PaymentChannel.Enum.UNIONPAY_3
    }

    @Transactional
    Map<Long,Boolean> refund(List<Payment> payments) {
        return super.refund(payments);
    }

    @Transactional
    boolean refund(Payment payment) {
        String orderNo = payment.getPurchaseOrder().getOrderNo();
        logger.debug("订单{}发起银联退款交易", orderNo);
        paymentSerialNumberGenerator.next(payment);
        Map<String, String> reqMap = createReqMap(payment);
        boolean bol = send(orderNo, reqMap);
        if (!bol) {
            payment.setStatus(PaymentStatus.Enum.CANCEL_4);
        }
        paymentRepository.save(payment);
        return bol;
    }

    Map<Long,Boolean> callPlatform(String orderNo,Map<Long,Map> sendMap){
        Map<Long,Boolean> map = new HashMap<Long,Boolean>();
        Set<Long> set = sendMap.keySet();
        for(Long id:set){
            Boolean  bol = false;
            if(null!=sendMap.get(id)){
                bol = send(orderNo, sendMap.get(id));
            }
            map.put(id,bol);
        }
        return map;
    }

    boolean send(String orderNo,Map<String,String> reqMap){
        boolean bol = false;//银联调用接口是否调用成功标识
        logger.debug("订单{}银联退款交易，请求数据：{}", orderNo, reqMap.toString());
        unionPayProcessor.saveUnionPayLog(orderNo, "银联退款交易，请求数据：" + reqMap.toString());

        HttpClient httpClient = new HttpClient(SDKConfig.getConfig().getBackRequestUrl(), UnionPayConstant.UNION_PAY_CONNECTION_TIMEOUT, UnionPayConstant.UNION_PAY_CONNECTION_TIMEOUT);

        try {
            int resState = httpClient.send(reqMap, UNION_PAY_ENCODING);
            if (200 == resState) {
                String result = httpClient.getResult();
                logger.debug("订单{}银联退款交易，同步返回数据：{}", orderNo, result);
                unionPayProcessor.saveUnionPayLog(orderNo,  "银联退款交易，同步返回数据：" + result);

                Map<String, String> resData = SDKUtil.convertResultStringToMap(result);
                if (!SDKUtil.validate(resData, UNION_PAY_ENCODING))
                    throw new RuntimeException("银联退款交易同步返回数据验签失败");

                bol = UnionPayState.isPaySuccess(resData.get("respCode"));
            } else {
                logger.warn("银联退款交易，同步返回异常状态 -> {}", resState);
                unionPayProcessor.saveUnionPayLog(orderNo,  "银联退款交易，同步返回异常状态：" + resState);
            }
        } catch (Exception ex) {
            logger.error("银联退款交易接口调用失败", ex);
            unionPayProcessor.saveUnionPayLog(orderNo, "银联退款交易接口调用失败");
        }
        return bol;
    }

    Map<String, String> createMap(Payment payment){
        return createReqMap(payment);
    }

    String name() {
        return "银联支付";
    }

    Map<String, String> createReqMap(Payment payment) {
        Channel channel = payment.getClientType();
        if (StringUtils.isBlank(payment.getUpstreamId().getThirdpartyPaymentNo()))
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "缺少银联交易流水号，无法发起退款交易");

        Map<String, String> dataMap = new HashMap<>();
        // 版本号
        dataMap.put("version", UNION_PAY_VERSION);
        // 字符集编码 默认"UTF-8"
        dataMap.put("encoding", UNION_PAY_ENCODING);
        // 签名方法 01 RSA
        dataMap.put("signMethod", UNION_PAY_SIGN_METHOD);
        // 交易类型 04-退货
        dataMap.put("txnType", UNION_PAY_TXN_TYPE_04);
        // 交易子类型 01:自助消费 02:订购 03:分期付款 00默认
        dataMap.put("txnSubType", UNION_PAY_TXN_SUBTYPE_00);
        // 业务类型
        dataMap.put("bizType", UNION_PAY_BIZ_TYPE_000201);
        // 渠道类型，07-PC，08-手机
        dataMap.put("channelType", IUnionPayHandler.UNION_PAY_CHANNEL_TYPE_08);
        // 后台通知地址
        dataMap.put("backUrl", UnionPayConstant.getUnionPayCallbackBackUrl());
        // 接入类型，商户接入填0 0- 商户 ， 1： 收单， 2：平台商户
        dataMap.put("accessType", UNION_PAY_ACCESS_TYPE);
        // 商户号码，请改成自己的商户号
        dataMap.put("merId", unionPayProcessor.getUnionPayMerId());
        // 订单号
        dataMap.put("orderId", payment.getOutTradeNo());
        // 原交易流水号
        dataMap.put("origQryId", payment.getUpstreamId().getThirdpartyPaymentNo());
        // 订单发送时间，取系统时间
        dataMap.put("txnTime", dateFormat.format(new Date()));
        // 交易金额，单位分
        dataMap.put("txnAmt", String.valueOf(((Double) (payment.getAmount() * 100)).longValue()));
        logger.info("银联支付金额：{}", dataMap.get("txnAmt"));
        // 交易币种
        dataMap.put("currencyCode", UNION_PAY_CURRENCY_CODE);
        //请求方保留域
        dataMap.put("reqReserved", channel.getId().toString());

        return UnionPaySignature.signData(dataMap);
    }
}
