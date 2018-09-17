package com.cheche365.cheche.unionpay.payment.front

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.unionpay.UnionPayConstant
import com.unionpay.acp.sdk.HttpClient
import com.unionpay.acp.sdk.SDKConfig
import com.unionpay.acp.sdk.SDKUtil
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Created by wangfei on 2015/7/17.
 */
@Component("mobileAppHandler")
class MobileAppHandler extends UnionPayFrontTradeHandler {
    private Logger logger = LoggerFactory.getLogger(MobileAppHandler.class);


    @Override
    String preTrade(Map prepayParams) {
        Map<String, String> request = createReqMap(prepayParams);
        HttpClient httpClient = new HttpClient(SDKConfig.getConfig().getAppRequestUrl(),
            UnionPayConstant.UNION_PAY_CONNECTION_TIMEOUT, UnionPayConstant.UNION_PAY_CONNECTION_TIMEOUT);

        String result;
        try {
            int resState = httpClient.send(request, UNION_PAY_ENCODING);
            if (200 == resState) {
                result = httpClient.getResult();
            } else {
                logger.warn("银联支付app推送订单信息接口，返回异常状态 -> {}", resState);
                throw new IllegalArgumentException("银联支付app推送订单信息接口，返回异常状态 -> " + resState);
            }
        } catch (Exception ex) {
            logger.error("银联支付app推送订单信息接口调用失败", ex);
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "银联支付异常，请稍候重试");
        }

        return this.afterSend(result);
    }

    private String afterSend(String result) {

        if (StringUtils.isBlank(result))
            return "";

        Map<String, String> resData = SDKUtil.convertResultStringToMap(result);
        if(!SDKUtil.validate(resData, UNION_PAY_ENCODING)) {
            logger.warn("银联支付app推送订单信息接口返回报文验签失败");
            throw new BusinessException(BusinessException.Code.INTERNAL_SERVICE_ERROR, "银联支付异常，请稍候重试");
        }

        return StringUtils.trimToEmpty(resData.get("tn"));
    }

    @Override
    void createChannelSpecData(Map<String, String> dataMap) {
        dataMap.put("backUrl", UnionPayConstant.getUnionPayCallbackBackUrl());
    }

}
