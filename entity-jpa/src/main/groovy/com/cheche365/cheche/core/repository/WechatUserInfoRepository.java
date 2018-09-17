package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.model.WechatUserInfo;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by liqiang on 3/24/15.
 */
@Repository
public interface WechatUserInfoRepository extends PagingAndSortingRepository<WechatUserInfo, Long> {

    WechatUserInfo findFirstByUser(User user);
    WechatUserInfo findFirstByUnionid(String unionid);
}
