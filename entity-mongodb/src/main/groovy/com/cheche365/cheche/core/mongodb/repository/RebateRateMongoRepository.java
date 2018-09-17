package com.cheche365.cheche.core.mongodb.repository;

import com.cheche365.cheche.core.model.RebateRate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by wenling on 2017/8/7.
 */
@Repository
public interface RebateRateMongoRepository extends MongoRepository<RebateRate, Long> {

}
