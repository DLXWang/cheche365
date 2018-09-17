package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.FuelType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FuelTypeRepository extends CrudRepository<FuelType, Long> {

}
