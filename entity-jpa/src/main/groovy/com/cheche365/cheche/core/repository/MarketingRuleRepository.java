package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.*;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MarketingRuleRepository extends PagingAndSortingRepository<MarketingRule, Long>,JpaSpecificationExecutor<MarketingRule> {
    MarketingRule findFirstByAreaAndChannelAndInsuranceCompanyAndStatus(Area area, Channel channel, InsuranceCompany insuranceCompany, MarketingRuleStatus status);

    List<MarketingRule> findByAreaAndChannelAndInsuranceCompanyOrderByCreateTimeDesc(Area area, Channel channel, InsuranceCompany insuranceCompany);

    MarketingRule findFirstByAreaAndChannelAndInsuranceCompanyOrderByVersionDesc(Area area, Channel channel, InsuranceCompany insuranceCompany);

    MarketingRule findFirstByAreaAndChannelAndInsuranceCompanyAndStatusAndMarketing(Area area, Channel channel, InsuranceCompany insuranceCompany, MarketingRuleStatus status, Marketing marketing);

    List<MarketingRule> findByStatus(MarketingRuleStatus marketingRuleStatus);

    @Query(value="select * from marketing_rule where area=?1 and channel=?2 and insurance_company=?3 and status= 2 limit 1 ",nativeQuery = true )
    MarketingRule findFirstEffective(Long areaId, Long channelId, Long companyId);

    @Query(value="select * from marketing_rule where status= ?1 and expire_date <= ?2",nativeQuery = true )
    List<MarketingRule> findByStatusAndExpireDate(Long status, String expireDate);

    @Query(value="select * from marketing_rule where status= ?1 and ( effective_date is null or effective_date <= ?2 )",nativeQuery = true )
    List<MarketingRule> findByStatusAndEffectiveDate(Long status, String effectiveDate);

    @Query(value=" select * from marketing_rule where status = 2 and effective_date = ?1 and channel = 13  "+
        " union "+
        " select * from marketing_rule where status = 3 and expire_date = ?1 and channel = 13  "+
        " and not exists (select * from marketing_rule where status = 2 and effective_date = ?1 and channel = 13) ",nativeQuery = true )
    List<MarketingRule> findListNeedSyncByDate(Date date);
}
