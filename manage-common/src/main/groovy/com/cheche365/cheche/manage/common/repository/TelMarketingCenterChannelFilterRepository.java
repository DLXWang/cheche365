package com.cheche365.cheche.manage.common.repository;

import com.cheche365.cheche.manage.common.model.TelMarketingCenterChannelFilter;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by wangshaobin on 2016/8/24.
 */
@Repository
public interface TelMarketingCenterChannelFilterRepository extends PagingAndSortingRepository<TelMarketingCenterChannelFilter, Long> {

    TelMarketingCenterChannelFilter findFirstByTaskType(int type);
}
