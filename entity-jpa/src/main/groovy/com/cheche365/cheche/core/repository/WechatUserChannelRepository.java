package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.model.WechatUserChannel;
import com.cheche365.cheche.core.model.WechatUserInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by chenqc on 2016/10/27.
 */
@Repository
public interface WechatUserChannelRepository extends PagingAndSortingRepository<WechatUserChannel, Long> {

    WechatUserChannel findFirstByOpenId(String openId);

    @Query(value = "select * from wechat_user_channel wuc " +
        " where wuc.wechat_user_info in" +
        "(select id from wechat_user_info  wui where wui.user=?1) and wuc.channel=?2 limit 1", nativeQuery = true)
    WechatUserChannel findByUserChannel(User user, Channel channel);

    @Query(value = "select * from wechat_user_channel wuc " +
        " where wuc.wechat_user_info in" +
        "(select id from wechat_user_info  wui where wui.user=?1) and wuc.channel=3 limit 1", nativeQuery = true)
    WechatUserChannel findByUser(User user);

    @Query(value = "select * from wechat_user_channel wuc  where wuc.wechat_user_info=" +
        "(select id from wechat_user_info  wui where wui.user=?1 order by  wui.id desc limit 1) " +
        "and wuc.channel=?2 limit 1", nativeQuery = true)
    WechatUserChannel findLastByUserChannel(User user, Channel channel);

    WechatUserChannel findByWechatUserInfoAndChannel(WechatUserInfo wechatUserInfo, Channel channel);

    @Query(value = "select distinct us.mobile from WechatUserInfo wu, User us,WechatUserChannel wuc " +
        "where us.bound = 1 and us.mobile is not null and  us.id in" +
        " (select wui.user from WechatUserInfo wui where wui.id in" +
        " (select wuc1.wechatUserInfo from WechatUserChannel wuc1 where  wuc1.unsubscribed <> 1 and wuc1.qrcodeChannel=?1))  order by wuc.subscribe_time")
    org.springframework.data.domain.Page<String> getBoundMobilePage(Long qrcodeChannelId, Pageable pageable);

    @Query("select po.orderNo from PurchaseOrder as po,WechatUserInfo as wui,QRCodeChannel as qc,WechatUserChannel wuc " +
        "where wuc.wechatUserInfo=wui.id and qc.id=?1 and wuc.qrcode = qc.wechatQRCode and wuc.qrcodeChannel = qc.id " +
        "and wuc.unsubscribed <> 1 and po.status in(3,4,5) " +
        "and po.applicant =wui.user order by po.createTime")
    org.springframework.data.domain.Page<String> getSuccessOrderPage(Long qrcodeChannelId, Pageable pageable);

    @Query(value = "SELECT wc.* FROM wechat_user_channel wc " +
        "LEFT JOIN wechat_user_info wi ON wc.wechat_user_info = wi.id " +
        "LEFT JOIN user u ON wi.user = u.id " +
        "WHERE wi.user = ?1 ORDER BY wc.id DESC", nativeQuery = true)
    List<WechatUserChannel> findLatestByUser(Long id);
}
