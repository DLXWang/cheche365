package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.model.AreaContactInfo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by sunhuazhong on 2015/11/13.
 */
@Repository
public interface AreaContactInfoRepository extends PagingAndSortingRepository<AreaContactInfo, Long> , JpaSpecificationExecutor<AreaContactInfo> {
    AreaContactInfo findFirstByArea(Area area);
}
