package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.AccessDetail
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface AccessDetailRepository extends PagingAndSortingRepository<AccessDetail, Long>,JpaSpecificationExecutor<AccessDetail> {
    List<AccessDetail> findBySource(String source)

    @Query(value = "select count(distinct referer) from access_detail where source=?1", nativeQuery = true)
    Integer accessDetailInfoNum(String source);
}
