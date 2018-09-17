package com.cheche365.cheche.externalapi

import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.config.ClientConfig
import com.sun.jersey.api.client.filter.ClientFilter
import com.sun.jersey.api.client.filter.LoggingFilter
import com.sun.jersey.api.json.JSONConfiguration
import com.sun.jersey.client.apache4.ApacheHttpClient4Handler
import com.sun.jersey.client.apache4.config.DefaultApacheHttpClient4Config
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager

import javax.ws.rs.core.MediaType
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger

import static com.cheche365.cheche.core.util.URLUtils.formatPath

/**
 * Created by zhengwei on 06/02/2018.
 * 外部api, 泛指需要通过HTTP调用的服务，可能是非车车的的服务(如短链接服务)，也可能是车车内部系统之间相互调用。<br>
 * 基本原则，每个服务域名运行时共用一个http client实例。如：<br>
 * 有两个外部服务，http://a.com和http://b.com, 这两个域名运行时最多有两个http client对象。
 *
 */
abstract class ExternalAPI{

    static final ConcurrentHashMap<String, Client> clients = new ConcurrentHashMap<>();

    Client apiClient(){
        clients.computeIfAbsent(host(), createClient)
    }

    private createClient = {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(100);

        HttpClientBuilder httpClientBuilder= HttpClients.custom()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(RequestConfig.custom().setConnectionRequestTimeout(100000).setSocketTimeout(100000).build())
            .disableRedirectHandling()

        CloseableHttpClient closeableHttpClient = httpClientBuilder.build()

        ClientConfig clientConfig = new DefaultApacheHttpClient4Config()
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE)

        def client = new Client(new ApacheHttpClient4Handler(closeableHttpClient, new BasicCookieStore(), true), clientConfig);

        client.addFilter(new LoggingFilter(Logger.getLogger(ExternalAPI.class.getName())))
        filters()?.each {
            client.addFilter(it)
        }

        return client
    }

    String formatPath(List pathParams){
        formatPath(path(), pathParams)
    }

    def call(Map params){
        Client apiClient = apiClient()
        def resource = apiClient.resource(host()).path(params?.path ? formatPath(params?.path as List) : path())

        params?.qs?.findAll{
            it.key && it.value
        }?.each {
            resource = resource.queryParam(it.key as String, it.value as String)
        }

        params?.header?.each{
            resource = resource.header(it.key as String, it.value as String)
        }

        def responseBody
        if('GET' == method()){
            responseBody = resource
                .method(method(), responseType())
        } else {
            responseBody = resource
                .type(contentType())
                .method(method(), responseType(), params.body)
        }

        return responseBody
    }

    List<ClientFilter> filters(){
        return null
    }

    String path() {
        ''
    }

    Class responseType() {
        Map
    }

    MediaType contentType() {
        MediaType.APPLICATION_JSON_TYPE
    }

    abstract String method()

    abstract String host()
}
