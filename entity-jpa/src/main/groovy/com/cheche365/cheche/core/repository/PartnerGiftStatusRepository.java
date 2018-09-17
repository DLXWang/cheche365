package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.PartnerGiftStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by zhaozhong on 2016/3/23.
 */
@Repository
public interface PartnerGiftStatusRepository extends JpaRepository <PartnerGiftStatus, Long>{
    PartnerGiftStatus findByName(String name);
}
