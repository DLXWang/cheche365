package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.IdentityType
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface IdentityTypeRepository extends PagingAndSortingRepository<IdentityType, Long> {
    IdentityType findFirstByName(String name)
}
