package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.WechatQRCode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by liqiang on 7/9/15.
 */
@Repository
public interface WechatQRCodeRepository extends PagingAndSortingRepository<WechatQRCode, Long> {

    WechatQRCode findFirstByActionNameAndSceneStr(String actionName, String sceneStr);
    WechatQRCode findFirstByActionNameAndSceneId(String actionName, long sceneId);
    WechatQRCode findFirstByTicket(String ticket);
    WechatQRCode findFirstBySceneId(long sceneId);
    List<WechatQRCode> findByActionName(String actionName);

    @Query(value = "select * from wechat_qrcode where id not in (select wechat_qrcode from qrcode_channel) order by id desc", nativeQuery = true)
    List<WechatQRCode> findNoReferenceQRCode();
}
