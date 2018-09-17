package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.GiftCodeExchangeWay
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Created by zhengwei on 03/11/2017.
 */
@Repository
interface GiftCodeExchangeWayRepository extends PagingAndSortingRepository<GiftCodeExchangeWay,Long> {

}
