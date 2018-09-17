package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.MessageType;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by zhaozhong on 2015/9/1.
 */
@Repository
public interface MessageTypeRepository extends PagingAndSortingRepository<MessageType, Long>{

    MessageType findFirstByType(String type);
}
