package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.Channel
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
@CacheConfig(cacheManager = 'jdkCacheManager')
@Cacheable(value = "channelRepository",keyGenerator = "cacheKeyGenerator", condition="#root.methodName.startsWith('find')")
@Caching(
    evict = [
        @CacheEvict(value = "apiPartnerRepository", allEntries = true,condition = "#root.methodName eq 'save'"),
       @CacheEvict(value = "channelRepository", allEntries = true,condition = "#root.methodName eq 'save'")
   ]
)
interface ChannelRepository extends PagingAndSortingRepository<Channel, Long>,JpaSpecificationExecutor<Channel> {
    Channel findFirstByName(String name)

    Channel findById(Long id);

    @Query(value = "select * from channel c where c.id in ?1", nativeQuery = true)
    List<Channel> findByIds(List ids);

    List<Channel> findAll()

    @Override
    <S extends Channel> S save(S entity)

    @Override
    <S extends Channel> Iterable<S> save(Iterable<S> entities)

    Channel findOne(Long aLong)

    @Query(value = "select c.* from channel c , quote_record q where q.channel = c.id and q.id = ?1", nativeQuery = true)
    Channel findByQuoteRecordId(String objId)
}
