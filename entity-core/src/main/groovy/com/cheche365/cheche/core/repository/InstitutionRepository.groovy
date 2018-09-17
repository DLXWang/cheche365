package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.Institution
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Created by zhangtc on 2017/12/13.
 */
@Repository
interface InstitutionRepository extends PagingAndSortingRepository<Institution, Long> {

}
