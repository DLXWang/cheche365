package com.cheche365.cheche.web.service

import groovy.util.logging.Slf4j
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service

/**
 * Created by liheng on 2018/5/9 0009.
 */
@Slf4j
@Service
class ClearCacheService {

    @Caching(
        evict = [
            @CacheEvict(value = "quoteFlowConfig", allEntries = true, condition = "null == #cacheKeys or #cacheKeys.contains('quoteFlowConfig')"),
            @CacheEvict(value = "areaGroup", allEntries = true, condition = "null == #cacheKeys or #cacheKeys.contains('areaGroup')"),
            @CacheEvict(value = "areaGroupIncludeSI", allEntries = true, condition = "null == #cacheKeys or #cacheKeys.contains('areaGroupIncludeSI')"),
            @CacheEvict(value = "moDisplayMessage", allEntries = true, condition = "null == #cacheKeys or #cacheKeys.contains('moDisplayMessage')"),
            @CacheEvict(value = "insuranceCompany", allEntries = true, condition = "null == #cacheKeys or #cacheKeys.contains('insuranceCompany')"),
            @CacheEvict(value = "apiPartnerRepository", allEntries = true, condition = "null == #cacheKeys or #cacheKeys.contains('apiPartnerRepository')"),
            @CacheEvict(value = "channelRepository", allEntries = true, condition = "null == #cacheKeys or #cacheKeys.contains('channelRepository')"),
            @CacheEvict(value = "channelRebateRepository", allEntries = true, condition = "null == #cacheKeys or #cacheKeys.contains('channelRebateRepository')"),
            @CacheEvict(value = "channelRebatePolicy", allEntries = true, condition = "null == #cacheKeys or #cacheKeys.contains('channelRebatePolicy')")
        ]
    )
    void clearCache(List cacheKeys) {
        log.info '清除{}缓存', cacheKeys ?: '全部'
    }

}
