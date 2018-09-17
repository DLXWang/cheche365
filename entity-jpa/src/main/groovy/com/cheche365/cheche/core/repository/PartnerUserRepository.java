package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.ApiPartner;
import com.cheche365.cheche.core.model.PartnerUser;
import com.cheche365.cheche.core.model.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by chenxiaozhe on 15-12-11.
 */
@Repository
public interface PartnerUserRepository extends PagingAndSortingRepository<PartnerUser, Long>, JpaSpecificationExecutor<PartnerUser> {

    PartnerUser findFirstByPartnerAndPartnerId(ApiPartner ApiPartner, String partnerId);

    PartnerUser findFirstByPartnerAndUser(ApiPartner ApiPartner, User user);

    @Query(value = "select count(1) from partner_user pu left join user u on pu.user=u.id where pu.partner=?1 and u.mobile=?2 and pu.partner_id!=?3 ", nativeQuery = true)
    long countByMobile(Long partner, String mobile, String partnerId);


    PartnerUser findFirstByUser(User user);

    @Query(value = "select * from partner_user pu where pu.user in (?1)", nativeQuery = true)
    List<PartnerUser> findByUsers(List<Long> users);

    @Query(value = "select pu.* from partner_user pu,partner_user_extend pe where pu.id=pe.partner_user and pe.object_table=?1 and pe.object_id=?2 limit 1", nativeQuery = true)
    PartnerUser findFirstByExtendObject(String objectTable,Long objectId);
}
