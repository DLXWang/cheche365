package com.cheche365.cheche.core.mongodb.repository;

import com.cheche365.cheche.core.model.MoFanhuaSyncMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by zhangtc on 2017/12/4.
 */
@Repository
public interface MoFanhuaSyncMessageRepository extends MongoRepository<MoFanhuaSyncMessage, Long> {

    List<MoFanhuaSyncMessage> findByMessageType(Integer messageType);

    @Query(value = "{'messageType':?0, '$or':[{'createTime': {'$exists': false}},{'createTime': {'$lt': ?1}}]}")
    Page<MoFanhuaSyncMessage> findByMessageTypeAndCreateTime(Integer messageType, Date date, Pageable pageable);
}
