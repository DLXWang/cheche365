package com.cheche365.cheche.core.repository.tide;

import com.cheche365.cheche.core.model.tide.TidePlatformInternalUser;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by yinJianBin on 2018/4/19.
 */
public interface TidePlatformInternalUserRepository extends PagingAndSortingRepository<TidePlatformInternalUser, Long>, JpaSpecificationExecutor<TidePlatformInternalUser> {

    Iterable<TidePlatformInternalUser> findAllByInternalUserId(Long internalUserId);
}