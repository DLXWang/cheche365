package com.cheche365.cheche.core.mongodb.repository

import com.cheche365.cheche.core.model.MoChannelPageConfig
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

/**
 * Created by liheng on 2018/3/7 007.
 */
@Repository
interface MoChannelPageConfigRepository extends MongoRepository<MoChannelPageConfig, String> {

    @Query(value = '{"channelId":?0}')
    MoChannelPageConfig findByChannelId(Long channelId)
}
