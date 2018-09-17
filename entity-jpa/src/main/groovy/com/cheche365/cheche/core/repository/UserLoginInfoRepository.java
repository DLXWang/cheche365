package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.ApiPartner;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.model.UserLoginInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by wangfei on 2015/9/11.
 */
@Repository
public interface UserLoginInfoRepository extends PagingAndSortingRepository<UserLoginInfo,Long> {
    UserLoginInfo findFirstByUser(User user);

    @Query(value="select * from user_login_info where id >?1 and area is null limit ?2",nativeQuery = true)
    List<UserLoginInfo> findByAreaIsNull(Long id,Integer limit);

    @Query(value="select count(*) from user_login_info where area is null",nativeQuery = true)
    Long countByAreaIsNull();

    @Query(value="select * from user_login_info uli join user u on u.id = uli.user where uli.last_login_time between ?1 and ?2 and u.user_type !=2 and u.mobile is not null and u.register_channel not in(?3) limit ?4 , ?5",nativeQuery = true)
    List<UserLoginInfo> findByLastLoginTimeBetween(Date startTime,Date endTime,List excludeChannelList,Integer pageNum,Integer pageSize);

    @Query(value="SELECT u.id,pu.partner_id,u.NAME,u.mobile,uli.last_login_time" +
        " FROM user_login_info uli                                        " +
        " JOIN partner_user pu ON uli.`user` = pu.`user`                  " +
        " JOIN `user` u ON u.id = pu. USER                                " +
        " WHERE pu.partner = ?4                                           " +
        " AND uli.last_login_time BETWEEN ?1 AND ?2                       " +
        " AND uli.channel IN (?3)  limit ?5 offset ?6                     " ,nativeQuery = true)
    List<Object[]> findByLastLoginTimeAndChannel(Date startDate, Date endDate, List<Channel> channelList, ApiPartner partner, Integer pageSize, Integer startIndex);
}
