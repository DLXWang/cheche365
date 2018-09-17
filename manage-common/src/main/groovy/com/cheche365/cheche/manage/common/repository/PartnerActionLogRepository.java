package com.cheche365.cheche.manage.common.repository;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.Partner;
import com.cheche365.cheche.manage.common.model.PartnerActionLogHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by zhangpengcheng on 2018/4/14.
 */
@Repository
public interface PartnerActionLogRepository extends PagingAndSortingRepository<PartnerActionLogHistory, Long>, JpaSpecificationExecutor<PartnerActionLogHistory> {
    Page<PartnerActionLogHistory> findByPartner(Partner partner  , Pageable pageable);

    Page<PartnerActionLogHistory> findByPartnerAndChannel(Partner partner ,Channel channel,  Pageable pageable);

    @Query(value = " select * from partner_action_log_history palh left join internal_user iu on palh.operator = iu.id " +
        " left join partner pn on palh.partner = pn.id " +
        " where pn.id = ?1 and iu.name like %?2% "+
        " ORDER BY ?#{#pageable}", nativeQuery = true)
    Page<PartnerActionLogHistory> findByPartnerOperator(Partner partner , String operator, Pageable pageable);

    @Query(value = " select * from partner_action_log_history palh left join internal_user iu on palh.operator = iu.id " +
        " left join partner pn on palh.partner = pn.id " +
        " left join channel cn one palh.channel = cn.id " +
        " where pn.id = ?1 and and cn.id = ?2 iu.name like %?3% "+
        " ORDER BY ?#{#pageable}", nativeQuery = true)
    Page<PartnerActionLogHistory> findByPartnerChannelOperator(Partner partner,Channel channel,String operator,Pageable pageable);
}
