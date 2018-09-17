package com.cheche365.cheche.core.mongodb.repository;

import com.cheche365.cheche.core.model.MoAttendMarketingPartner;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Shanxf on 2017/11/16.
 */

@Repository
public interface MoAttendMarketingPartnerRepository extends MongoRepository<MoAttendMarketingPartner,String> {
    @Query(value = "{'marketingCode':?0}")
    List<MoAttendMarketingPartner> findByMarketingCode(String marketingCode);
}

