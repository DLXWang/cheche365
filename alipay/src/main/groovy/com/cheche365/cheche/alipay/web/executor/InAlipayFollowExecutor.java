/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.cheche365.cheche.alipay.web.executor;

import com.cheche365.cheche.alipay.common.AliPayException;
import com.cheche365.cheche.alipay.util.AlipayMsgBuildUtil;
import com.cheche365.cheche.alipay.web.AlipayOAuthManager;
import com.cheche365.cheche.core.repository.AlipayUserInfoRepository;
import com.cheche365.cheche.core.service.AlipayUserInfoService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 关注服务窗执行器
 */
@Component("followExecutor")
public class InAlipayFollowExecutor implements ActionExecutor {

    @Autowired
    private AlipayUserInfoService alipayUserInfoService;

    @Override
    public String execute(JSONObject bizContent) throws AliPayException {
        final String fromUserId = bizContent.getString("FromUserId");
        alipayUserInfoService.updateFollowFlag(fromUserId, true);
        return AlipayMsgBuildUtil.buildBaseAckMsg(fromUserId);
    }


}
