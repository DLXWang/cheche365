package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.AreaType
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface AreaTypeRepository extends PagingAndSortingRepository<AreaType, Long> {
    AreaType findFirstByName(String name)
}
