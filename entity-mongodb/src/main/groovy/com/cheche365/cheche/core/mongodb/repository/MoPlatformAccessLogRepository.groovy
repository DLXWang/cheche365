package com.cheche365.cheche.core.mongodb.repository

import com.cheche365.cheche.core.model.MoPlatformAccessLog
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

/**
 * Created by wenling on 2017/8/9.
 */
@Repository
interface MoPlatformAccessLogRepository extends MongoRepository<MoPlatformAccessLog, Long> {
}
