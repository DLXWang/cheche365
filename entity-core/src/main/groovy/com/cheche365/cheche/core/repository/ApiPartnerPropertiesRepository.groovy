package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.ApiPartner
import com.cheche365.cheche.core.model.ApiPartnerProperties
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ApiPartnerPropertiesRepository extends PagingAndSortingRepository<ApiPartnerProperties, Long> {

    List<ApiPartnerProperties> findByPartner(ApiPartner partner)

    ApiPartnerProperties findByKeyAndValue(String key,String value)

}
