package com.cheche365.cheche.core.mongodb.repository

import com.cheche365.cheche.core.model.BihuInsuranceInfo
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
public interface BihuInsuranceInfoRepository extends MongoRepository<BihuInsuranceInfo, String>{


    @Query(value = '{"UserInfo.LicenseNo":?0}')
    List<BihuInsuranceInfo> findByLicenseNo(String licenseNo)
}
