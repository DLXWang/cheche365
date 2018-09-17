package com.cheche365.cheche.soopay.util;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by wangfei on 2015/7/14.
 */
public class LogUtil {

    public static String getStrLog(Map<String, String> reqParam) {
        if (null == reqParam || reqParam.isEmpty())
            return "";

        Iterator it = reqParam.entrySet().iterator();
        StringBuffer buffer = new StringBuffer();

        while(it.hasNext()) {
            Map.Entry en = (Map.Entry)it.next();
            buffer.append("[").append((String) en.getKey()).append("] = [").append((String) en.getValue()).append("],");
        }
        return buffer.toString().substring(0, buffer.toString().length()-1);
    }
}
