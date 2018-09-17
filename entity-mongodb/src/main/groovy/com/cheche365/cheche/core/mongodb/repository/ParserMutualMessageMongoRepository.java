package com.cheche365.cheche.core.mongodb.repository;

import com.cheche365.cheche.core.model.MoParserMutualMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by wangshaobin on 2017/8/18.
 */
public interface ParserMutualMessageMongoRepository extends MongoRepository<MoParserMutualMessage, Long> {
}
