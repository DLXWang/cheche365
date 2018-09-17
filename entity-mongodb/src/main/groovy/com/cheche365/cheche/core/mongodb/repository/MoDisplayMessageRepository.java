package com.cheche365.cheche.core.mongodb.repository;

import com.cheche365.cheche.core.model.MoDisplayMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by Shanxf on 2017/11/16.
 */

@Repository
public interface MoDisplayMessageRepository extends MongoRepository<MoDisplayMessage,String> {

    @Query(value = "{'messageType._id':?0}")
    MoDisplayMessage findByMessageType(Long messageTypeId);
}
