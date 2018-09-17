package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.model.noAuto.NoAutoUser;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoAutoUserRepository extends PagingAndSortingRepository<NoAutoUser, Long> {

    NoAutoUser findFirstByUidAndChannelOrderByCreateTimeDesc(String uid, Channel channel);

    NoAutoUser findFirstByUserAndChannelOrderByCreateTimeDesc(User user, Channel channel);
}
