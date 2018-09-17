package com.cheche365.cheche.soopay.util;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by wangfei on 2015/7/21.
 */
public class MapUtil {

    public static Map<String, String> removeEmptyFromMap(Map<String, String> contentData) {
        Map.Entry obj;
        Map<String, String> formatMap = new HashMap<>();
        Iterator it = contentData.entrySet().iterator();

        while (it.hasNext()) {
            obj = (Map.Entry)it.next();
            String value = (String)obj.getValue();
            if (StringUtils.isNotBlank(value))
                formatMap.put((String)obj.getKey(), value.trim());
        }

        return formatMap;
    }
}
