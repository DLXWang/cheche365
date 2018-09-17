package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.Gender
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface GenderRepository extends PagingAndSortingRepository<Gender, Long> {
    Gender findFirstByName(String name)
}
