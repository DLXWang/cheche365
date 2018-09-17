package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.PushMessage;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by liqiang on 7/28/15.
 */


public interface PushMessageRepository extends PagingAndSortingRepository<PushMessage, Long> {

    List<PushMessage> findByTypeAndDisable(int type, boolean disable);

    PushMessage findByMessageNoAndDisable(String messageNo, boolean disable);
}
