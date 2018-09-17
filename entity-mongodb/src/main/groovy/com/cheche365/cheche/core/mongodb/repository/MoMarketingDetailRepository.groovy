package com.cheche365.cheche.core.mongodb.repository

import com.cheche365.cheche.core.model.MoMarketingDetail
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

/**
 * Created by taichangwei on 2018/5/16.
 */
@Repository
interface MoMarketingDetailRepository extends MongoRepository<MoMarketingDetail, String>{

    MoMarketingDetail findByMarketingCode(String marketingCode)

}
