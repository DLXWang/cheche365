/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.cheche365.cheche.alipay.web.executor;


import com.cheche365.cheche.alipay.common.AliPayException;
import net.sf.json.JSONObject;

/**
 * 业务执行接口
 */
public interface ActionExecutor {
    
    /**
     * 业务执行方法
     * @return
     */
    String execute(JSONObject bizContent) throws AliPayException;

}
