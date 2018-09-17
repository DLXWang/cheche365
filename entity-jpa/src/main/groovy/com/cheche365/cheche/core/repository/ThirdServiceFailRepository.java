package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.ThirdServiceFail;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by chenxiaozhe on 16-1-15.
 */
@Repository
public interface ThirdServiceFailRepository extends PagingAndSortingRepository<ThirdServiceFail, Long>, JpaSpecificationExecutor<ThirdServiceFail> {
    List<ThirdServiceFail> findByOrderIdOrderByCreateTimeDesc(Long orderId);
}
