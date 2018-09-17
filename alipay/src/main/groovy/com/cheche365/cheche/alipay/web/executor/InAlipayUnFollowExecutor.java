/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.cheche365.cheche.alipay.web.executor;

import com.cheche365.cheche.alipay.util.AlipayMsgBuildUtil;
import com.cheche365.cheche.core.service.AlipayUserInfoService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 取消关注服务窗执行器
 */
@Component("unFollowExecutor")
public class InAlipayUnFollowExecutor implements ActionExecutor {

    @Autowired
    private AlipayUserInfoService alipayUserInfoService;

    @Override
    public String execute(JSONObject bizContent) {
        final String fromUserId = bizContent.getString("FromUserId");
        alipayUserInfoService.updateFollowFlag(fromUserId, false);
        return AlipayMsgBuildUtil.buildBaseAckMsg(fromUserId);
    }
}
