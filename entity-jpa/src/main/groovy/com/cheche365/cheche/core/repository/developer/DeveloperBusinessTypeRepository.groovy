package com.cheche365.cheche.core.repository.developer

import com.cheche365.cheche.core.model.developer.DeveloperBusinessType
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Created by zhengwei on 08/03/2018.
 */

@Repository
interface DeveloperBusinessTypeRepository extends CrudRepository<DeveloperBusinessType, Long>{

    @Query(value = "select * from developer_business_type where parent = ?1", nativeQuery = true)
    List<DeveloperBusinessType> findByParent(Long parent)

    @Query(value = "select * from developer_business_type where parent is null", nativeQuery = true)
    List<DeveloperBusinessType> findTopLevelTypes()
}
