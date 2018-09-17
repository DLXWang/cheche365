package com.cheche365.cheche.core.mongodb.repository;

import com.cheche365.cheche.core.model.MoSyncOrderLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author shanxf
 * @Date 2018/4/21  11:27
 */
@Repository
public interface MoSyncOrderLogRepository extends MongoRepository<MoSyncOrderLog,String> {

    List<MoSyncOrderLog> findByOrderNoOrderByCreateTimeDesc(String orderNo);
}
