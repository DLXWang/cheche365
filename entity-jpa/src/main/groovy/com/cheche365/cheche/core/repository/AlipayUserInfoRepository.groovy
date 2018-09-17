package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.AlipayUserInfo
import com.cheche365.cheche.core.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created by zhaozhong on 2015/10/16.
 */
@Repository
interface AlipayUserInfoRepository extends JpaRepository<AlipayUserInfo, Long>{

    AlipayUserInfo findByOpenid(String openid)

    AlipayUserInfo findFirstByUser(User bindingUser)

}
