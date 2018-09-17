package com.cheche365.cheche.internal.integration

import com.cheche365.cheche.core.constants.WebConstants
import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.filter.LoggingFilter
import com.sun.jersey.client.apache4.ApacheHttpClient4Handler
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager

import java.util.logging.Logger

/**
 * Created by zhengwei on 6/23/17.
 */
class ApiClient {

    static final String NA_API_VERSION = 'v1.0'
    static Client client

    static def naClient(){

        if(!client){
            initClient()
        }
        client.resource(Constants.NA_DOMAIN).path(NA_API_VERSION).with {
            addFilter(new LoggingFilter(Logger.getLogger(ApiClient.class.name)))
            it
        }
    }

    static def answernClient(){

        if(!client){
            initClient()
        }
        client.resource(WebConstants.domainURL).with {
            addFilter(new LoggingFilter(Logger.getLogger(ApiClient.class.name)))
            it
        }
    }

    synchronized static initClient(){
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(200);
        CloseableHttpClient closeableHttpClient = HttpClients.custom()
            .setConnectionManager(poolingHttpClientConnectionManager)
            .setDefaultRequestConfig(RequestConfig.custom().setConnectionRequestTimeout(10000).setSocketTimeout(10000).build())
            .build();

        client = new Client(new ApacheHttpClient4Handler(closeableHttpClient, new BasicCookieStore(), true))
    }

    static String formattedPath(String path, String pathParams){
        path.replaceFirst(/\{}/, pathParams)
    }

}
