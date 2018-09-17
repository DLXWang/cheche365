package com.cheche365.cheche.partner.web;


import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.model.ApiPartner;
import com.cheche365.cheche.core.model.ApiPartnerProperties;
import com.cheche365.cheche.core.mongodb.repository.MoHttpClientLogRepository;
import com.cheche365.cheche.partner.web.filter.ClientEncryptFilter;
import com.cheche365.cheche.partner.web.filter.MoLoggingFilter;
import com.cheche365.cheche.partner.web.filter.ClientSignatureFilter;
import com.cheche365.cheche.signature.Parameters;
import com.cheche365.cheche.signature.Secrets;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.client.apache4.ApacheHttpClient4Handler;
import com.sun.jersey.client.apache4.config.ApacheHttpClient4Config;
import com.sun.jersey.client.apache4.config.DefaultApacheHttpClient4Config;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static com.cheche365.cheche.core.model.ApiPartnerProperties.Key.*;
import static com.cheche365.cheche.core.model.ApiPartnerProperties.findByPartnerAndKey;

public class ClientFactory {

    private static ConcurrentHashMap<String, Client> clients = new ConcurrentHashMap<>();
    private static MoHttpClientLogRepository logRepository = ApplicationContextHolder.getApplicationContext().getBean(MoHttpClientLogRepository.class);

    public static Client getInstance(ApiPartner partner) {
        return clients.computeIfAbsent(partner.getCode(), key -> createClient(partner));
    }

    private static Client createClient(ApiPartner partner) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE).register("https", new SSLConnectionSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)).build();

            PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            poolingHttpClientConnectionManager.setMaxTotal(200);

            CloseableHttpClient closeableHttpClient = HttpClients.custom()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setConnectionReuseStrategy(new NoConnectionReuseStrategy())
                .setDefaultRequestConfig(RequestConfig.custom().setConnectionRequestTimeout(30000).setSocketTimeout(60000).build())
                .build();

            ApiPartnerProperties enableBuffering = findByPartnerAndKey(partner, SYNC_ENABLE_BUFFERING);
            Client client;
            if (null != enableBuffering && "TRUE".equals(enableBuffering.getValue())) {
                ClientConfig clientConfig = new DefaultApacheHttpClient4Config();
                clientConfig.getProperties().put(ApacheHttpClient4Config.PROPERTY_ENABLE_BUFFERING, Boolean.TRUE);
                client = new Client(new ApacheHttpClient4Handler(closeableHttpClient, new BasicCookieStore(), true), clientConfig);
            } else {
                client = new Client(new ApacheHttpClient4Handler(closeableHttpClient, new BasicCookieStore(), true));
            }

            client.removeAllFilters();

            String signMethod = findByPartnerAndKey(partner, SYNC_SIGN_METHOD).getValue();
            if ("CUSTOM".equals(signMethod)) {
                client.addFilter(new LoggingFilter(Logger.getLogger(ClientFactory.class.getName())));
            } else {
                client.addFilter(new MoLoggingFilter(logRepository, Logger.getLogger(ClientFactory.class.getName())));
                client.addFilter(new ClientSignatureFilter(client.getProviders(), new Parameters()
                    .appId(findByPartnerAndKey(partner, SYNC_APP_ID).getValue()).version()
                    .signatureMethod(signMethod), new Secrets()
                    .appSecret(findByPartnerAndKey(partner, SYNC_APP_SECRET).getValue())));
                if (partner.syncBodyNeedEncrypt()){
                    client.addFilter(new ClientEncryptFilter(findByPartnerAndKey(partner, SYNC_APP_SECRET).getValue()));
                }
            }

            return client;

        } catch (Exception ex) {
            throw new IllegalStateException("Fail to init the http client for partner use");
        }
    }

    private static class DefaultTrustManager implements X509TrustManager {
        private DefaultTrustManager() {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    }
}
