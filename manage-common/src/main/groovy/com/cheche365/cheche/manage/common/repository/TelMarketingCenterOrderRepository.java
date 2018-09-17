package com.cheche365.cheche.manage.common.repository;

import com.cheche365.cheche.manage.common.model.TelMarketingCenterOrder;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by xu.yelong on 2016/11/24.
 */
@Repository
public interface TelMarketingCenterOrderRepository extends PagingAndSortingRepository<TelMarketingCenterOrder,Long> {
}
