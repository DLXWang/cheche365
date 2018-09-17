package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.MessageStatus;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by liuzh on 2015/10/14.
 */
@Repository
interface MessageStatusRepository extends PagingAndSortingRepository<MessageStatus, Long> {
    MessageStatus findFirstByStatus(String status)
}
