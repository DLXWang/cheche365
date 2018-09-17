package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.ApiPartner;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by chenxiaozhe on 15-12-11.
 */
@Repository

@Cacheable(value = "apiPartnerRepository",keyGenerator = "cacheKeyGenerator", condition="#root.methodName.startsWith('find')")
@Caching(
    evict = {
        @CacheEvict(value = "apiPartnerRepository", allEntries = true,condition = "#root.methodName eq 'save'"),
        @CacheEvict(value = "channelRepository", allEntries = true,condition = "#root.methodName eq 'save'")
    }
)

public interface ApiPartnerRepository extends PagingAndSortingRepository<ApiPartner, Long>, JpaSpecificationExecutor<ApiPartner> {

    ApiPartner findFirstByAppId(String appId);

    ApiPartner findFirstByCode(String code);

    ApiPartner findFirstByDescription(String description);

    List<ApiPartner> findAll();

    @Override
    <S extends ApiPartner> S save(S entity);

    @Override
    <S extends ApiPartner> Iterable<S> save(Iterable<S> entities);

    ApiPartner findOne(Long aLong);

}
