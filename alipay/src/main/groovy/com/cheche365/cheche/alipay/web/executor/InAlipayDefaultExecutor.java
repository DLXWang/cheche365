/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.cheche365.cheche.alipay.web.executor;

import com.cheche365.cheche.alipay.common.AliPayException;
import com.cheche365.cheche.alipay.util.AlipayMsgBuildUtil;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * 默认执行器(该执行器仅发送ack响应)
 */
@Component("defaultExecutor")
public class InAlipayDefaultExecutor implements ActionExecutor {

    @Override
    public String execute(JSONObject bizContent) throws AliPayException {
        final String fromUserId = bizContent.getString("FromUserId");
        return AlipayMsgBuildUtil.buildBaseAckMsg(fromUserId);
    }
}
