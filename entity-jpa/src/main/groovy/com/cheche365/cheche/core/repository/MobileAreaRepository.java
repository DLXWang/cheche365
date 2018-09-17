package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.MobileArea;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by xu.yelong on 2016/7/15.
 */
@Repository
public interface MobileAreaRepository  extends PagingAndSortingRepository<MobileArea, Long>, JpaSpecificationExecutor<MobileArea> {

    MobileArea findByMobile(String mobile);
}
