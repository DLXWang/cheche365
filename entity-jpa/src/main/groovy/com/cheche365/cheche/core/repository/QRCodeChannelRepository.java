package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.QRCodeChannel;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by sunhuazhong on 2015/7/27.
 */
@Repository
public interface QRCodeChannelRepository extends PagingAndSortingRepository<QRCodeChannel, Long>, JpaSpecificationExecutor<QRCodeChannel> {

    QRCodeChannel findByCode(String code);

    QRCodeChannel findFirstByWechatQRCode(Long wechatQRCode);

    QRCodeChannel findFirstByWechatQRCodeAndDisable(Long wechatQRCode, boolean disable);

    @Query(value = "select count(distinct us.mobile) from wechat_user_info as wui, user as us, qrcode_channel as qc,wechat_user_channel as wuc " +
        "where wuc.wechat_user_info=wui.id and wui.user = us.id and wuc.qrcode = qc.wechat_qrcode and wuc.qrcode_channel = qc.id " +
        "and wuc.qrcode_channel = ?1 and wuc.unsubscribed <> 1  and us.bound = 1 and us.mobile is not null", nativeQuery = true)
    Integer getBoundMobileCount(Long qrcodeChannelId);

    @Query(value = "select wuc.qrcode_channel, count(distinct us.mobile) from wechat_user_info as wui, user as us, qrcode_channel as qc,wechat_user_channel as wuc " +
        "where wuc.wechat_user_info=wui.id and wui.user = us.id and wuc.qrcode = qc.wechat_qrcode and wuc.qrcode_channel = qc.id " +
        "and wuc.qrcode_channel in (?1) and wuc.unsubscribed <> 1 and us.bound = 1 and us.mobile is not null " +
        "group by wuc.qrcode_channel", nativeQuery = true)
    List<Object[]> getBoundMobileCount(List<Long> qrcodeChannelIdList);

    @Query(value = "select count(po.id) from purchase_order as po,wechat_user_info as wui,qrcode_channel as qc,wechat_user_channel as wuc " +
        "where wuc.wechat_user_info=wui.id and po.applicant = wui.user and wuc.qrcode = qc.wechat_qrcode and wuc.qrcode_channel = qc.id " +
        "and wuc.qrcode_channel=?1 and wuc.unsubscribed <> 1 and po.status in(3,4,5) and po.type = 1", nativeQuery = true)
    Integer getSuccessOrderCount(Long qrcodeChannelId);

    @Query(value = "select wuc.qrcode_channel, count(po.id) from purchase_order as po,wechat_user_info as wui,qrcode_channel as qc, wechat_user_channel as wuc " +
        "where wuc.wechat_user_info=wui.id and po.applicant = wui.user and wuc.qrcode = qc.wechat_qrcode and wuc.qrcode_channel = qc.id " +
        "and wuc.qrcode_channel in (?1) and wuc.unsubscribed <> 1 and po.status in(3,4,5) and po.type = 1 " +
        "group by wuc.qrcode_channel", nativeQuery = true)
    List<Object[]> getSuccessOrderCount(List<Long> qrcodeChannelIdList);

    @Query(value = "SELECT * FROM qrcode_channel qc WHERE qc.id = " +
        "(SELECT wuc.qrcode_channel FROM wechat_user_info wui,wechat_user_channel wuc " +
        "WHERE wuc.wechat_user_info=wui.id and wuc.qrcode_channel IS NOT NULL AND wui. USER = ?1 LIMIT 1)", nativeQuery = true)
    QRCodeChannel findQrCodeChannelCodeByUserId(Long userId);

    //后续更改（考虑二维码是否有效的问题）
   /* @Query(value = "SELECT * FROM qrcode_channel qc WHERE qc.id = (SELECT wui.qrcode_channel FROM wechat_user_info wui WHERE wui.qrcode_channel IS NOT NULL AND wui. USER = ?1 LIMIT 1)AND qc. DISABLE = 0", nativeQuery = true)
    QRCodeChannel findQrCodeChannelCodeByUserId(Long userId);*/

}
