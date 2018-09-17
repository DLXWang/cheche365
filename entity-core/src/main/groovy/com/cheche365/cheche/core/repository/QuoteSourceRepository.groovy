package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.QuoteSource
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface QuoteSourceRepository extends PagingAndSortingRepository<QuoteSource, Long> {
}
