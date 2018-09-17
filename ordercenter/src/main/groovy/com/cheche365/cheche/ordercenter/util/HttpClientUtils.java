package com.cheche365.cheche.ordercenter.util;

import com.cheche365.cheche.common.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wangfei on 2015/10/23.
 */
public class HttpClientUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);
    private static final CloseableHttpClient httpClient;
    private static final String CHARSET_UTF8 = "UTF-8";
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";
    private static final int CONNECT_TIME_OUT = 1000 * 10;  //10 seconds
    private static final int SOCKET_TIME_OUT = 1000 * 60 * 10;   //10 minutes

    static {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(CONNECT_TIME_OUT).setSocketTimeout(SOCKET_TIME_OUT).build();
        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).disableCookieManagement().build();
    }


    public static String doGetWithHeader(CloseableHttpClient client, String url, Map<String, String> params, Map<String, String> headerParam) {
        if (client == null)
            return doGet(httpClient, url, params, CHARSET_UTF8, headerParam);
        return doGet(client, url, params, CHARSET_UTF8, headerParam);
    }


    public static String doPostWithHeader(CloseableHttpClient client, String url, Map<String, String> params, Map<String, String> headerParam) {
        if (client == null)
            return doPost(httpClient, url, params, CHARSET_UTF8, headerParam);
        return doPost(client, url, params, CHARSET_UTF8, headerParam);
    }

    public static String doPostWithJsonAndHeader(CloseableHttpClient client, String url, String jsonStr, Map<String, String> headerParam) {
        if (client == null)
            return doPostWithJson(httpClient, url, jsonStr, CHARSET_UTF8, headerParam);
        return doPostWithJson(client, url, jsonStr, CHARSET_UTF8, headerParam);
    }

    public static String doPutWithJsonAndHeader(CloseableHttpClient client, String url, String jsonStr, Map<String, String> headerParam) {
        if (client == null)
            return doPutWithJson(httpClient, url, jsonStr, CHARSET_UTF8, headerParam);
        return doPutWithJson(client, url, jsonStr, CHARSET_UTF8, headerParam);
    }

    public static String doGet(CloseableHttpClient client, String url, Map<String, String> params, String charset, Map<String, String> headerParam) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        logger.info("do httpClient get request url -> {}", url);
        CloseableHttpResponse response = null;
        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = StringUtil.defaultNullStr(entry.getValue());
                    if (StringUtils.isNotEmpty(value)) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
            }
            HttpGet httpGet = new HttpGet(url);
            decorativeHeader(httpGet, headerParam);
            response = client.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpGet.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, charset);
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (null != response) {
                try {
                    response.close();
                } catch (Exception ex) {
                    logger.error("close response has error.");
                }
            }
        }
    }

    public static String doPost(CloseableHttpClient client, String url, Map<String, String> params, String charset, Map<String, String> headerParam) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        logger.info("do httpClient post request url -> {}", url);
        CloseableHttpResponse response = null;
        try {
            List<NameValuePair> pairs = null;
            if (params != null && !params.isEmpty()) {
                pairs = new ArrayList<>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
            }
            HttpPost httpPost = new HttpPost(url);
            decorativeHeader(httpPost, headerParam);
            if (pairs != null && pairs.size() > 0) {
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, charset));
            }
            response = client.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, charset);
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (null != response) {
                try {
                    response.close();
                } catch (Exception ex) {
                    logger.error("close response has error.");
                }
            }
        }
    }

    public static String doPostWithJson(CloseableHttpClient client, String url, String jsonStr, String charset, Map<String, String> headerParam) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        logger.info("do httpClient post request url -> {}", url);
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            decorativeHeader(httpPost, headerParam);
            httpPost.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
            if (StringUtils.isNotBlank(jsonStr)) {
                StringEntity se = new StringEntity(jsonStr, charset);
                se.setContentType(CONTENT_TYPE_TEXT_JSON);
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
                httpPost.setEntity(se);
            }
            response = client.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, charset);
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (null != response) {
                try {
                    response.close();
                } catch (Exception ex) {
                    logger.error("close response has error.");
                }
            }
        }
    }

    public static String doPutWithJson(CloseableHttpClient client, String url, String jsonStr, String charset, Map<String, String> headerParam) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        logger.info("do httpClient put request url -> {}", url);
        CloseableHttpResponse response = null;
        try {
            HttpPut httpPut = new HttpPut(url);
            decorativeHeader(httpPut, headerParam);
            httpPut.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
            StringEntity se = new StringEntity(jsonStr, charset);
            se.setContentType(CONTENT_TYPE_TEXT_JSON);
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
            httpPut.setEntity(se);
            response = client.execute(httpPut);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPut.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, charset);
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (null != response) {
                try {
                    response.close();
                } catch (Exception ex) {
                    logger.error("close response has error.");
                }
            }
        }
    }

    /**
     * 封装请求header信息
     *
     * @param httpRequestBase
     * @param headerMap
     */
    private static void decorativeHeader(HttpRequestBase httpRequestBase, Map<String, String> headerMap) {
        if (headerMap != null && !headerMap.isEmpty()) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                String value = entry.getValue();
                if (value != null) {
                    httpRequestBase.addHeader(entry.getKey(), entry.getValue());
                }
            }
        }
    }
}
