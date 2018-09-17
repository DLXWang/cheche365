package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.InsuranceCompany;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@CacheConfig(cacheManager = "jdkCacheManager")
@Cacheable(value = "insuranceCompany",keyGenerator = "cacheKeyGenerator", condition="#root.methodName.startsWith('find')")
public interface InsuranceCompanyRepository extends PagingAndSortingRepository<InsuranceCompany, Long> {

    InsuranceCompany findByName(String name);

    List<InsuranceCompany> findAll();

    InsuranceCompany findById(Long id);
}
