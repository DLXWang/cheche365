package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.RoleType
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleTypeRepository extends PagingAndSortingRepository<RoleType, Long> {
    RoleType findFirstByName(String name)
}
