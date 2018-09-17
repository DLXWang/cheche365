package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.VehicleDataSource
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface VehicleDataSourceRepository extends CrudRepository<VehicleDataSource, Long> {

    VehicleDataSource findFirstByCode(description)

    List<VehicleDataSource> findAll()
}
