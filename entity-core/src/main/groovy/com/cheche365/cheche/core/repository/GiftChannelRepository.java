package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.GiftChannel;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by mahong on 2016/2/16.
 */
@Repository
public interface GiftChannelRepository extends PagingAndSortingRepository<GiftChannel, Long> {
}
