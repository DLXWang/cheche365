package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.ParentIdentityType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ParentIdentityTypeRepository extends CrudRepository<ParentIdentityType, Long> {

}
