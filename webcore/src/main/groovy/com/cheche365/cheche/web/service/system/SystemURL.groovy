package com.cheche365.cheche.web.service.system

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.util.CacheUtil
import com.cheche365.cheche.externalapi.api.shorturl.BatchCreateAPI
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

import javax.ws.rs.core.UriBuilder
import java.util.concurrent.TimeUnit

import static com.cheche365.cheche.core.constants.WebConstants.API_VERSION

/**
 * Created by zhengwei on 04/04/2018.
 * 系统URL生成器
 */

@Slf4j
@Service
abstract class SystemURL {

    public static final String PAYMENT_URL_COMPANY_KEY = "companyId";
    public static final String PAYMENT_URL_SRC_VALUE = "smspay";
    public static final String PAYMENT_URL_READONLY_KEY = "readonly"

    @Autowired
    RedisTemplate redisTemplate

    @Autowired
    BatchCreateAPI shortUrlApi

    String generate(Map params, boolean shortUrl = false) {

        String url =
                baseBuilder(params)
                .with { builder ->
                    params.qs?.findAll{
                        it.key && it.value
                    }?.each{
                        builder.queryParam(it.key, it.value)
                    }
                    builder
                }.with { builder ->
                    (!params.fragment) ?: builder.fragment(params.fragment)
                    builder
                }
                .build()
                .toString()
                .with {
                    shortUrl ? toShortUrl(it) : it
                }

        log.debug("{}生成链接 :{}", desc(), url)
        return url
    }

    String cacheUuid(cachedValue) {
        String uuid = UUID.randomUUID().toString()
        CacheUtil.putValueWithExpire(redisTemplate, cacheKey(uuid), cachedValue, cachedHours(), TimeUnit.DAYS)
        return uuid
    }

    static UriBuilder baseBuilder(Map params) {
        if(params.host) {
            params
                .host
                .with { builder ->
                        (!params.path) ?: builder.path(params.path)
                        builder as UriBuilder
                }
        } else {
            UriBuilder.fromPath(params.path)
        }
    }

    String cacheKey(String uuid) {
        cacheKeyPrefix() + uuid
    }

    def cachedValue(String uuid) {
        uuid ? redisTemplate.opsForValue().get(cacheKey(uuid)) : null
    }

    boolean isCachedValue(valueFromClient, String uuid) {
        valueFromClient == cachedValue(uuid)
    }

    abstract String cacheKeyPrefix()

    abstract String desc()

    static int cachedHours() {
        30 * 24
    }

    String toShortUrl(String longUrl) {
        shortUrlApi.call(longUrl) ?: longUrl
    }

    static UriBuilder apiBaseUrl() {
        root().path(API_VERSION)
    }

    static UriBuilder mRoot() {
        root().path(WebConstants.M_ROOT_PATH)
    }

    static UriBuilder partner() {
        root().path('partner')
    }

    static UriBuilder mMarketing() {
        root().path('marketing')
    }

    static UriBuilder inviteRoot() {
        root().path('activePage')
    }

    static UriBuilder root() {
        UriBuilder.fromUri(WebConstants.getDomainURL())
    }

}
