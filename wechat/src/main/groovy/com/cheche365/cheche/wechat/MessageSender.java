package com.cheche365.cheche.wechat;

import com.cheche365.cheche.core.WechatConstant;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.wechat.message.PaymentRequest;
import com.cheche365.cheche.wechat.message.RefundRequest;
import com.cheche365.cheche.wechat.message.json.CustomerMessage;
import com.cheche365.cheche.wechat.message.json.Result;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cheche365.cheche.core.WechatConstant.BASE_URL;
import static com.cheche365.cheche.core.WechatConstant.PAY_BASE_URL;

/**
 * Created by liqiang on 3/20/15.
 */
@Component
public class MessageSender {

    private Logger logger = LoggerFactory.getLogger(MessageSender.class);

    @Autowired
    private AccessTokenManager accessTokenManager;

    public AccessToken fetchAccessToken(String appId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("grant_type", "client_credential");
        parameters.put("appid", appId);
        parameters.put("secret", WechatConstant.getAppSecretByAppId(appId));
        AccessToken accessToken = getMessageForObject(WechatConstant.ACCESS_TOKEN_PATH, parameters, AccessToken.class, false, appId);
        if (logger.isDebugEnabled()) {
            logger.debug("accessToken: " + accessToken.getToken());
            logger.debug("expire in : " + accessToken.getExpiresIn());
        }
        return accessToken;
    }

    public <T> T getMessageForObject(String path, Map<String, Object> parameters, Class<T> responseClass, boolean addAccessTokenToParameters, String appId) {
        RestTemplate restTemplate = createRestTemplate();
        URI url = buildURL(path, parameters, addAccessTokenToParameters, false, appId);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML));
        headers.setContentType(MediaType.valueOf("text/plain;charset=UTF-8"));
        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, responseClass);
        return responseEntity.getBody();
    }

    public <T> T postMessage(String path, Map<String, Object> parameters, Object request, Class<T> responseClass, Channel channel) {

        RestTemplate restTemplate = createRestTemplate();
        URI url = buildURL(path, parameters, true, false, WechatConstant.getAppId(channel));
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML));
        HttpEntity entity = new HttpEntity(request, headers);

        ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, responseClass);
        return responseEntity.getBody();
    }

    private RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        restTemplate.setInterceptors(Arrays.asList(new ClientHttpRequestInterceptor[]{new LoggingRequestInterceptor()}));
        HttpMessageConverter oldMessageConverter = null;
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        for (HttpMessageConverter messageConverter : messageConverters) {
            if (messageConverter instanceof StringHttpMessageConverter) {
                oldMessageConverter = messageConverter;
            } else if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
                //微信/sns/userinfo , /sns/oauth2/access_token  API response的content type是text/plain,so add MediaType.TEXT_PLAIN
                MappingJackson2HttpMessageConverter converter = ((MappingJackson2HttpMessageConverter) messageConverter);
                converter.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                List<MediaType> supportMediaTypes = new ArrayList<MediaType>();
                //supportedMediaTypes is unmodifiableList
                converter.getSupportedMediaTypes().stream().forEach(mediaType -> {
                    supportMediaTypes.add(mediaType);
                });
                supportMediaTypes.add(MediaType.TEXT_PLAIN);
                converter.setSupportedMediaTypes(supportMediaTypes);
            }
        }
        if (oldMessageConverter != null) {
            StringHttpMessageConverter newStringMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
            List<MediaType> mediaTypes = new ArrayList<>();
            mediaTypes.add(MediaType.valueOf("text/plain;text/html;charset=utf-8"));
            newStringMessageConverter.setSupportedMediaTypes(mediaTypes);
            newStringMessageConverter.setWriteAcceptCharset(false);
            int index = messageConverters.indexOf(oldMessageConverter);
            messageConverters.set(index, newStringMessageConverter);
        }
        return restTemplate;
    }

    private URI buildURL(String path, Map<String, Object> parameters, boolean addAccessTokenToParameters, boolean payment, String appId) {
        if (addAccessTokenToParameters) {
            String accessToken = accessTokenManager.getAccessToken(appId);
            parameters.put("access_token", accessToken);
        }

        String baseUrl = payment ? WechatConstant.payBaseUrl() : WechatConstant.baseUrl(path);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl).path(path);
        if (null != parameters) {
            parameters.forEach((key, value) -> builder.queryParam(key, value));
        }

        URI url = builder.build().toUri();
        if (logger.isDebugEnabled()) {
            logger.debug("url is : " + url);
        }
        return url;
    }

    public void sendTextMessage(String openID, String content, Channel channel) {
        Map<String, Object> parameters = new HashMap<>();
        CustomerMessage customerMessage = new CustomerMessage();
        customerMessage.setTouser(openID);
        CustomerMessage.Text text = new CustomerMessage.Text();
        text.setContent(content);
        customerMessage.setText(text);
        Result result = postMessage("/cgi-bin/message/custom/send", parameters, customerMessage, Result.class, channel);
        if (logger.isDebugEnabled()) {
            logger.debug("error code is :" + result.getErrcode());
            logger.debug("error message is :" + result.getErrmsg());
        }
    }

    public void sendNewsMessage(String openID, List<CustomerMessage.Article> articles, Channel channel) {
        Map<String, Object> parameters = new HashMap<>();
        CustomerMessage customerMessage = new CustomerMessage();
        customerMessage.setTouser(openID);
        CustomerMessage.News news = new CustomerMessage.News();
        news.setArticles(articles);
        customerMessage.setNews(news);
        Result result = postMessage("/cgi-bin/message/custom/send", parameters, customerMessage, Result.class, channel);
        if (logger.isDebugEnabled()) {
            logger.debug("error code is :" + result.getErrcode());
            logger.debug("error message is :" + result.getErrmsg());
        }
    }

    public String postPayMessage(String path, Map<String, Object> parameters, PaymentRequest request) {
        RestTemplate restTemplate = createRestTemplate();
        URI url = buildURL(path, parameters, false, true, WechatConstant.getAppId(Channel.Enum.WE_CHAT_3));//don't need pass access token as parameter when calling payment api
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML));
        headers.setAcceptCharset(Arrays.asList(Charset.forName("utf-8")));
        HttpEntity entity = new HttpEntity(request.toXmlString(), headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return responseEntity.getBody();
    }

    public String refund(String url, RefundRequest refundRequest, String mchId) {
        String result = "";
        try {
            if (StringUtils.isEmpty(mchId)) {
                throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "mch_id为null");
            }
            HttpClient httpClient = buildHttpClientWithKey(mchId);
            if (httpClient != null) {
                String xml = refundRequest.toXmlString();
                logger.debug("refund request param {}", xml);
                result = doPost(httpClient, xml, url);
                logger.debug("refund response result {}", result);

            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("weixin refund fail.", ExceptionUtils.getRootCauseMessage(e));
        }
        return result;
    }

    private HttpClient buildHttpClientWithKey(String mchId) throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        HttpClient httpClient = null;
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            InputStream is = this.getClass().getResourceAsStream(WechatConstant.CERTIFICATE_PATH + mchId + WechatConstant.CERTIFICATE_FILENAME);//加载本地的证书进行https加密传输
            logger.info("微信证书path：{} 输出流:{}", WechatConstant.CERTIFICATE_PATH + mchId + WechatConstant.CERTIFICATE_FILENAME, is);
            keyStore.load(is, mchId.toCharArray());//设置证书密码
            SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, mchId.toCharArray()).build();
            SSLConnectionSocketFactory sslSf = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            httpClient = HttpClients.custom().setSSLSocketFactory(sslSf).build();
        } catch (Exception e) {
            logger.error("get httpclient fail!", e);
            throw e;
        }
        return httpClient;
    }

    private String doPost(HttpClient httpclient, String contents, String url) throws IOException {
        StringBuffer response = new StringBuffer();
        try {
            HttpPost httpPost = new HttpPost(url);
            if (StringUtils.isNotBlank(contents)) {
                httpPost.setEntity(new StringEntity(contents));
            }
            httpPost.setHeader("Accept", "application/json,application/xml");
            httpPost.setHeader("Accept-Language", "zh-cn,zh");
            httpPost.setHeader("Accept-Charset", "utf-8");
            HttpResponse httpResponse = httpclient.execute(httpPost);
            org.apache.http.HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()))) {
                    String item = "";
                    while ((item = bufferedReader.readLine()) != null) {
                        response.append(item);
                    }
                }
            }
            EntityUtils.consume(entity);
        } catch (Exception e) {
            logger.error("doPost exception:" + ExceptionUtils.getStackTrace(e), e);
            throw e;
        }
        return response.toString();
    }

}
