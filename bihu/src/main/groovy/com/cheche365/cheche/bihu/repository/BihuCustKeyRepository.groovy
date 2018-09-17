package com.cheche365.cheche.bihu.repository

import com.cheche365.cheche.bihu.model.BihuCustKey
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface BihuCustKeyRepository extends PagingAndSortingRepository<BihuCustKey, Long>{
}
