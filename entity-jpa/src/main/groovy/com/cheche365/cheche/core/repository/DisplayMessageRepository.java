package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.DisplayMessage;
import com.cheche365.cheche.core.model.MessageType;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zhaozhong on 2015/9/1.
 */
@Repository
@Cacheable(value = "displayMessage",keyGenerator = "cacheKeyGenerator", condition="#root.methodName.startsWith('find')")
public interface DisplayMessageRepository extends PagingAndSortingRepository<DisplayMessage, Long> , JpaSpecificationExecutor<DisplayMessage> {

    List<DisplayMessage> findByMessageTypeOrderByWeightDesc(MessageType messageType);

}
