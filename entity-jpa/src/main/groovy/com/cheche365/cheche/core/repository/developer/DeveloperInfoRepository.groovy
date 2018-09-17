package com.cheche365.cheche.core.repository.developer

import com.cheche365.cheche.core.model.developer.DeveloperInfo
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Created by zhengwei on 08/03/2018.
 */

@Repository
interface DeveloperInfoRepository extends CrudRepository<DeveloperInfo, Long> {

    @Query(value = "select * from developer_info where id> ?1 order by id ", nativeQuery = true)
    List<DeveloperInfo> findByIdGreaterThan(Long id)
}
