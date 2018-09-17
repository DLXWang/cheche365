package com.cheche365.cheche.unionpay.payment.front

import com.cheche365.cheche.unionpay.UnionPayConstant
import org.springframework.stereotype.Component

/**
 * Created by wangfei on 2015/7/8.
 */
@Component("mobileWapHandler")
class MobileWapHandler extends UnionPayFrontTradeHandler {

    @Override
    void createChannelSpecData(Map<String, String> dataMap) {

        // 前台通知地址 ，控件接入方式无作用
        dataMap.put("frontUrl", UnionPayConstant.getUnionPayCallbackFrontUrl());
        // 后台通知地址
        dataMap.put("backUrl", UnionPayConstant.getUnionPayCallbackBackUrl());
    }
}
