package com.cheche365.cheche.unionpay.payment.front

import com.cheche365.cheche.core.model.LogType
import com.cheche365.cheche.unionpay.payment.IUnionPayHandler
import com.cheche365.cheche.unionpay.payment.UnionPayProcessor
import com.cheche365.cheche.unionpay.payment.UnionPaySignature
import com.cheche365.cheche.unionpay.payment.pay.UnionPayHandle
import com.unionpay.acp.sdk.SDKConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
abstract class UnionPayFrontTradeHandler implements IUnionPayHandler {
    private Logger logger = LoggerFactory.getLogger(UnionPayFrontTradeHandler.class);

    @Autowired
    private UnionPayProcessor unionPayProcessor;

    @Autowired
    private UnionPayHandle unionPayHandle;

    String preTrade(Map prepayParams) {
        Map<String, String> dataMap = this.createReqMap(prepayParams);
        String formHtml = this.createHtml(SDKConfig.getConfig().getFrontRequestUrl(), dataMap);
        logger.info("银联支付前台类交易，提交form表单 -> {}", formHtml);

        return formHtml;
    }

    void callBack(Map<String, String> respMap) {
        unionPayProcessor.saveUnionPayLog(respMap.get("orderId"), "银联支付前台类交易，后台通知数据：" + respMap.toString());
        unionPayHandle.doPayService(respMap);
    }

    void callFront(Map<String, String> respMap) {
        unionPayProcessor.saveUnionPayLog(respMap.get("orderId"), "银联支付前台类交易，前台通知数据：" + respMap.toString());
    }

    Map<String, String> createReqMap(Map prepayParams) {
        Map<String, String> dataMap = this.createBasicData(prepayParams);
        this.createChannelSpecData(dataMap);
        return UnionPaySignature.signData(dataMap);
    }

    protected void createChannelSpecData(Map<String, String> dataMap) {}

    protected Map createBasicData(Map prepayParams) {
        Map<String, String> dataMap = [:]
        // 版本号
        dataMap.put("version", UNION_PAY_VERSION);
        // 字符集编码 默认"UTF-8"
        dataMap.put("encoding", UNION_PAY_ENCODING);
        // 签名方法 01 RSA
        dataMap.put("signMethod", UNION_PAY_SIGN_METHOD);
        // 交易类型 01-消费
        dataMap.put("txnType", UNION_PAY_TXN_TYPE_01);
        // 交易子类型 01:自助消费 02:订购 03:分期付款
        dataMap.put("txnSubType", UNION_PAY_TXN_SUBTYPE_01);
        // 业务类型
        dataMap.put("bizType", UNION_PAY_BIZ_TYPE_000201);
        // 接入类型，商户接入填0 0- 商户 ， 1： 收单， 2：平台商户
        dataMap.put("accessType", UNION_PAY_ACCESS_TYPE);
        // 商户号码
        dataMap.put("merId", unionPayProcessor.getUnionPayMerId());
        // 订单发送时间，取系统时间
        dataMap.put("txnTime", dateFormat.format(new Date()));
        // 交易币种
        dataMap.put("currencyCode", UNION_PAY_CURRENCY_CODE);
        //请求方保留域
        dataMap.put("reqReserved", prepayParams.channel.id as String);
        // 商户订单号，8-40位数字字母
        dataMap.put("orderId", prepayParams.serialNumber);
        // 渠道类型，07-WEB，08-手机
        dataMap.put("channelType", UNION_PAY_CHANNEL_TYPE_08);
        // 交易金额，单位分
        dataMap.put("txnAmt", String.valueOf(((Double) (prepayParams.amount * 100)).longValue()));
        logger.info("银联支付金额：{}", dataMap.get("txnAmt"));

        return dataMap
    }

    String createHtml(String action, Map<String, String> hiddenMap) {

        logger.debug("生成银联支付html {}", this.getClass().getName());
        StringBuffer sf = new StringBuffer();
        sf.append("<form id = \"unionPayForm\" action=\"" + action + "\" method=\"post\">");
        if (null != hiddenMap && 0 != hiddenMap.size()) {
            Set set = hiddenMap.entrySet();
            Iterator it = set.iterator();

            while (it.hasNext()) {
                Map.Entry ey = (Map.Entry)it.next();
                String key = (String)ey.getKey();
                String value = (String)ey.getValue();
                sf.append("<input type=\"hidden\" name=\"" + key + "\" id=\"" + key + "\" value=\"" + value + "\"/>");
                logger.debug("key: {}  value: {}", key, value);
            }
        }
        sf.append("</form>");
        return sf.toString();
    }

}
