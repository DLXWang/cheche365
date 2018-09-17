package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.Gift;
import com.cheche365.cheche.core.model.PartnerGiftSync;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zhaozhong on 2016/3/23.
 */
@Repository
public interface PartnerGiftSyncRepository extends JpaRepository <PartnerGiftSync, Long>{

    PartnerGiftSync findByGiftAndChannel(Gift gift, Channel channel);

    @Query(value = "select a.* from partner_gift_sync a, gift b where a.gift = b.id and a.channel = ?1 and a.partner_gift_status = 1 and b.`status` <> 1" , nativeQuery = true)
    List<PartnerGiftSync> listUnSyncPartnerGiftSync(Channel channel);
}
