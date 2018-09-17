/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package com.cheche365.cheche.alipay.util;

import com.alipay.api.internal.util.StringUtils;
import com.cheche365.cheche.alipay.common.AliPayException;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 解析HttpServletRequest参数
 *
 * @author taixu.zqq
 * @version $Id: RequestUtil.java, v 0.1 2014年7月23日 上午10:48:10 taixu.zqq Exp $
 */
public class RequestUtil {

    /**
     * 获取所有request请求参数key-value
     *
     * @param request
     * @return
     */
    public static Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        if (null != request) {
            Set<String> paramsKey = request.getParameterMap().keySet();
            for (String key : paramsKey) {
                params.put(key, request.getParameter(key));
            }
        }
        return params;
    }

    public static JSONObject getBizContent(Map<String, String> params) throws AliPayException {
        //获取服务信息
        String service = params.get("service");
        if (StringUtils.isEmpty(service)) {
            throw new AliPayException("无法取得服务名");
        }
        //获取内容信息
        String bizContent = params.get("biz_content");
        if (StringUtils.isEmpty(bizContent)) {
            throw new AliPayException("无法取得业务内容信息");
        }
        return (JSONObject) new XMLSerializer().read(bizContent);
    }

}

