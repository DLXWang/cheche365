package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.QuoteStatus
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface QuoteStatusRepository extends PagingAndSortingRepository<QuoteStatus, Long> {
    QuoteStatus findFirstByName(String name)
}
