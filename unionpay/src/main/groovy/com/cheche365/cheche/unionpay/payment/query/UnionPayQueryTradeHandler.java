package com.cheche365.cheche.unionpay.payment.query;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.LogType;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.unionpay.UnionPayConstant;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangfei on 2015/7/14.
 */
@Component
public class UnionPayQueryTradeHandler implements IUnionPayHandler {
    private Logger logger = LoggerFactory.getLogger(UnionPayQueryTradeHandler.class);

    @Autowired
    private UnionPayProcessor unionPayProcessor;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    private Map<String, String> createMap(String orderId, String queryId, Channel channel) {
        Map<String, String> dataMap = new HashMap<>();
        // 版本号
        dataMap.put("version", UNION_PAY_VERSION);
        // 字符集编码 默认"UTF-8"
        dataMap.put("encoding", UNION_PAY_ENCODING);
        // 签名方法 01 RSA
        dataMap.put("signMethod", UNION_PAY_SIGN_METHOD);
        // 交易类型 00-查询
        dataMap.put("txnType", UNION_PAY_TXN_TYPE_00);
        // 交易子类型 01:自助消费 02:订购 03:分期付款 00默认（查询用）
        dataMap.put("txnSubType", UNION_PAY_TXN_SUBTYPE_00);
        // 业务类型 000000 默认值
        dataMap.put("bizType", UNION_PAY_BIZ_TYPE_000000);
        // 接入类型，商户接入填0 0- 商户 ， 1： 收单， 2：平台商户
        dataMap.put("accessType", UNION_PAY_ACCESS_TYPE);
        // 商户号码，请改成自己的商户号
        dataMap.put("merId", unionPayProcessor.getUnionPayMerId());
        // 订单发送时间，取系统时间
        dataMap.put("txnTime", dateFormat.format(new Date()));
        // 订单号
        if (StringUtils.isNotBlank(orderId)) {
            dataMap.put("orderId", orderId);
        }
        // 交易查询流水号
        if (StringUtils.isNotBlank(queryId)) {
            dataMap.put("queryId", queryId);
        }

        return UnionPaySignature.signData(dataMap);
    }

    private Map<String, String> beforeSend(String orderId, String queryId, Channel channel) {
        Map<String, String> request = this.createMap(orderId, queryId, channel);
        logger.info("银联支付订单{}查询交易，请求数据：{}", orderId,  request.toString());
        unionPayProcessor.saveUnionPayLog(orderId, "银联支付单笔查询交易，请求数据：" + request.toString());

        return request;
    }

    private Map<String, String> afterSend(String orderId, String result) {
        if (StringUtils.isBlank(result))
            return null;

        logger.info("银联支付订单{}查询交易，返回数据：{}", orderId,  result);
        unionPayProcessor.saveUnionPayLog(orderId, "银联支付单笔查询交易，返回数据：" + result);

        Map<String, String> resData = SDKUtil.convertResultStringToMap(result);
        if(!SDKUtil.validate(resData, UNION_PAY_ENCODING))
            throw new RuntimeException("银联支付单笔查询交易验签失败");

        return resData;
    }

    public Map<String, String> sendReqData(String orderId, String queryId, Channel channel) {
        Map<String, String> request = this.beforeSend(orderId, queryId, channel);
        HttpClient httpClient = new HttpClient(SDKConfig.getConfig().getSingleQueryUrl(),
            UnionPayConstant.UNION_PAY_CONNECTION_TIMEOUT, UnionPayConstant.UNION_PAY_CONNECTION_TIMEOUT);

        String result = "";
        try {
            int resState = httpClient.send(request, UNION_PAY_ENCODING);
            if (200 == resState) {
                result = httpClient.getResult();
            } else {
                logger.warn("银联支付单笔查询交易，返回异常状态 -> {}", resState);
                unionPayProcessor.saveUnionPayLog(orderId, "银联支付单笔查询交易，返回异常状态：" + resState);
                return null;
            }
        } catch (Exception ex) {
            logger.error("银联支付单笔查询接口调用失败", ex);
            unionPayProcessor.saveUnionPayLog(orderId, "银联支付单笔查询接口调用失败");
        }

        return this.afterSend(orderId, result);
    }

}
