package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.DeliveryInfo;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by sunhuazhong on 2015/11/13.
 */
@Repository
public interface DeliveryInfoRepository extends PagingAndSortingRepository<DeliveryInfo, Long> {

}
