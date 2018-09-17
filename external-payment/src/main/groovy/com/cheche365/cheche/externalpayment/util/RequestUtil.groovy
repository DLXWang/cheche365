package com.cheche365.cheche.externalpayment.util

import javax.servlet.http.HttpServletRequest

/**
 * Created by Administrator on 2018/1/11.
 */
class RequestUtil {
    public static Map<String, String> getAllRequestParam(final HttpServletRequest request) {
        Map<String, String> res = new HashMap<>();
        Enumeration<?> temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String) temp.nextElement();
                String value = request.getParameter(en).trim();
                if(value){
                    res.put(en, value);
                }
            }
        }
        return res;
    }
}
