package com.cheche365.cheche.ordercenter.service.freightInsurance;

import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.ordercenter.util.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Created by yinJianBin on 2017/8/23.
 */
public class FreightRequestHandler {
    private static Logger logger = LoggerFactory.getLogger(FreightRequestHandler.class);

    private static final String URL_KEY = "abaobaoxian.domain";
    public static String FREIGHT_INSURANCE_REQUEST_URL;
    public static String ABAO_DOMAIN;


    static {
        Properties properties = new Properties();
        try {
            properties.load(FreightRequestHandler.class.getResourceAsStream("/properties/web.properties"));
            String profile = System.getProperty("spring.profiles.active");
            ABAO_DOMAIN = getProperty(properties, profile, URL_KEY);
            FREIGHT_INSURANCE_REQUEST_URL = ABAO_DOMAIN + "/abao";
        } catch (IOException e) {
            logger.error("加载资源文件 : web.properties 异常,获取请求阿保退运线地址失败", e);
        }
    }

    /**
     * @param properties
     * @param profile
     * @param key
     * @return
     */
    public static String getProperty(Properties properties, String profile, String key) {
        String profileKey = profile + "." + key;
        if (properties.containsKey(profileKey)) {
            return properties.getProperty(profileKey);
        } else {
            return properties.getProperty(key);
        }
    }


    public static String doGetRequest(String urlPath) {
        return doGetRequest(urlPath, null);
    }

    public static String doGetRequest(String urlPath, Map paramMap) {
        String requestUrl = FREIGHT_INSURANCE_REQUEST_URL + urlPath;
        String response = HttpClientUtils.doGetWithHeader(null, requestUrl, paramMap, null);
        logger.debug("后台请求阿保获取数据,requestUrl-->({}),请求参数-->({}),返回结果-->({}).", requestUrl, StringUtil.defaultNullStr(paramMap), response);
        return response;
    }
}
