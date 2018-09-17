package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.InsuranceBasicInfo
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Created by cheche on 16/02/2017.
 */
@Repository
interface InsuranceBasicInfoRepository extends PagingAndSortingRepository<InsuranceBasicInfo, Long> {

    @Query(value = ''' select ibi.* from insurance_info ii,insurance_basic_info ibi,vehicle_license vl
                       where ii.vehicle_license = vl.id and ii.insurance_basic_info = ibi.id
                       and vl.license_plate_no = ?1 and vl.owner = ?2 order by ibi.id desc limit 1 ''', nativeQuery = true)
    InsuranceBasicInfo findLastByByLicensePlateNoAndOwner(String licensePlatNo, String owner)

}
