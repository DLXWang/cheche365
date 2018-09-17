package com.cheche365.cheche.unionpay.payment.revoke;

import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.LogType;
import com.cheche365.cheche.core.model.Payment;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.unionpay.UnionPayConstant;
import com.cheche365.cheche.unionpay.UnionPayState;
import com.cheche365.cheche.unionpay.payment.IUnionPayHandler;
import com.cheche365.cheche.unionpay.payment.UnionPayProcessor;
import com.cheche365.cheche.unionpay.payment.UnionPaySignature;
import com.unionpay.acp.sdk.HttpClient;
import com.unionpay.acp.sdk.SDKConfig;
import com.unionpay.acp.sdk.SDKUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangfei on 2016/3/17.
 */
@Component
public class UnionPayRevokeTradeHandler implements IUnionPayHandler {
    private Logger logger = LoggerFactory.getLogger(UnionPayRevokeTradeHandler.class);

    @Autowired
    private UnionPayProcessor unionPayProcessor;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    public boolean revoke(PurchaseOrder purchaseOrder) {
        String orderNo = purchaseOrder.getOrderNo();
        logger.debug("订单{}发起银联撤销交易", orderNo);

        Map<String, String> reqMap = createReqMap(purchaseOrder);
        logger.debug("订单{}银联撤销交易，请求数据：{}", orderNo, reqMap.toString());
        unionPayProcessor.saveUnionPayLog(orderNo, "银联撤销交易，请求数据：" + reqMap.toString());

        HttpClient httpClient = new HttpClient(SDKConfig.getConfig().getBackRequestUrl(),
            UnionPayConstant.UNION_PAY_CONNECTION_TIMEOUT, UnionPayConstant.UNION_PAY_CONNECTION_TIMEOUT);

        try {
            int resState = httpClient.send(reqMap, UNION_PAY_ENCODING);
            if (200 == resState) {
                String result = httpClient.getResult();
                logger.debug("订单{}银联撤销交易，同步返回数据：{}", orderNo, result);
                unionPayProcessor.saveUnionPayLog(orderNo, "银联撤销交易，同步返回数据：" + result);

                Map<String, String> resData = SDKUtil.convertResultStringToMap(result);
                if(!SDKUtil.validate(resData, UNION_PAY_ENCODING))
                    throw new RuntimeException("银联撤销交易同步返回数据验签失败");

                return UnionPayState.isPaySuccess(resData.get("respCode"));
            } else {
                logger.warn("银联撤销交易，同步返回异常状态 -> {}", resState);
                unionPayProcessor.saveUnionPayLog(orderNo, "银联撤销交易，同步返回异常状态：" + resState);
            }
        } catch (Exception ex) {
            logger.error("银联撤销交易接口调用失败", ex);
            unionPayProcessor.saveUnionPayLog(orderNo, "银联撤销交易接口调用失败");
        }

        return false;
    }

    public Map<String, String> createReqMap(PurchaseOrder purchaseOrder) {
        Channel channel = purchaseOrder.getSourceChannel();
        Payment payment = purchaseOrderService.getPaymentByPurchaseOrder(purchaseOrder);
        if (StringUtils.isBlank(payment.getThirdpartyPaymentNo()))
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "缺少银联交易流水号，无法发起撤销交易");

        Map<String, String> dataMap = new HashMap<>();
        // 版本号
        dataMap.put("version", UNION_PAY_VERSION);
        // 字符集编码 默认"UTF-8"
        dataMap.put("encoding", UNION_PAY_ENCODING);
        // 签名方法 01 RSA
        dataMap.put("signMethod", UNION_PAY_SIGN_METHOD);
        // 交易类型 04-退货
        dataMap.put("txnType", UNION_PAY_TXN_TYPE_31);
        // 交易子类型 01:自助消费 02:订购 03:分期付款 00默认
        dataMap.put("txnSubType", UNION_PAY_TXN_SUBTYPE_00);
        // 业务类型
        dataMap.put("bizType", UNION_PAY_BIZ_TYPE_000201);
        // 渠道类型，07-PC，08-手机
        dataMap.put("channelType",IUnionPayHandler.UNION_PAY_CHANNEL_TYPE_08);
        // 后台通知地址
        dataMap.put("backUrl", UnionPayConstant.getUnionPayCallbackBackUrl());
        // 接入类型，商户接入填0 0- 商户 ， 1： 收单， 2：平台商户
        dataMap.put("accessType", UNION_PAY_ACCESS_TYPE);
        // 商户号码，请改成自己的商户号
        dataMap.put("merId", unionPayProcessor.getUnionPayMerId());
        // 订单号
        dataMap.put("orderId", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        // 原交易流水号
        dataMap.put("origQryId", payment.getThirdpartyPaymentNo());
        // 订单发送时间，取系统时间
        dataMap.put("txnTime", dateFormat.format(new Date()));
        // 交易金额，单位分
        if ("true".equals(UnionPayConstant.UNION_PAY_CHECHE_TEST)) {
            dataMap.put("txnAmt", "10");
            logger.info("银联支付测试环境支付金额：1角");
        } else {
            dataMap.put("txnAmt", String.valueOf(((Double) (purchaseOrder.getPaidAmount() * 100)).longValue()));
            logger.info("银联支付金额：{}", dataMap.get("txnAmt"));
        }
        // 交易币种
        dataMap.put("currencyCode", UNION_PAY_CURRENCY_CODE);
        //请求方保留域
        dataMap.put("reqReserved", channel.getId().toString());

        return UnionPaySignature.signData(dataMap);
    }
}
