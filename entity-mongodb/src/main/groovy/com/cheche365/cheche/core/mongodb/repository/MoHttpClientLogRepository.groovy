package com.cheche365.cheche.core.mongodb.repository

import com.cheche365.cheche.core.model.MoHttpClientLog
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MoHttpClientLogRepository extends MongoRepository<MoHttpClientLog, String> {

    List<MoHttpClientLog> findByObjIdOrderByCreateTime(String objId)
}
