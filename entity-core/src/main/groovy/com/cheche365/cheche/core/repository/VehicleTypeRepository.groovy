package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.VehicleType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Created by liheng on 2017/3/27 027.
 */
@Repository
interface VehicleTypeRepository extends CrudRepository<VehicleType, Long> {

    VehicleType findFirstByDescription(description)

    List<VehicleType> findAll()
}
