package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.RebateChannel;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by sunhuazhong on 2016/5/25.
 */
@Repository
public interface RebateChannelRepository extends PagingAndSortingRepository<RebateChannel, Long> {
}
