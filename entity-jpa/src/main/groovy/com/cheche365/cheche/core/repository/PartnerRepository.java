package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Partner;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by sunhuazhong on 2015/8/25.
 */
@Repository
public interface PartnerRepository extends PagingAndSortingRepository<Partner, Long> , JpaSpecificationExecutor<Partner> {
    List<Partner> findByEnable(boolean enable);

    Partner findFirstByName(String name);

    @Query(value = "select * from partner", nativeQuery = true)
    List<Partner> findAll();
}
