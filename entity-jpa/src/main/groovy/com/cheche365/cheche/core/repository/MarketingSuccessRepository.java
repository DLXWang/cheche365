package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.Marketing;
import com.cheche365.cheche.core.model.MarketingSuccess;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Repository
public interface MarketingSuccessRepository extends PagingAndSortingRepository<MarketingSuccess, Long>, JpaSpecificationExecutor<MarketingSuccess>{

    MarketingSuccess findFirstByMobileAndMarketingId(String mobile, Long marketingId);

    MarketingSuccess findFirstByDetail(Long detailId);

    List<MarketingSuccess> findByMobileAndMarketingId(String mobile, Long marketingId);

    @Query(value = "select * from marketing_success where mobile = ?1 and marketing_id = ?2 and detail is null ", nativeQuery = true)
    List<MarketingSuccess> findAvailableReduceRecords(String mobile, Long marketingId);

    @Query(value = "select * from marketing_success where mobile = ?1 and synced = ?2 and effect_date <= NOW() and failure_date >= NOW()", nativeQuery = true)
    List<MarketingSuccess> findByMobileAndSynced(String mobile, boolean synced);

    List<MarketingSuccess> findByDetailTableNameAndDetail(String detailTableName, Long detail);

    @Query(value = "select a.name,t.source_channel as sourceChannel,count(distinct t.mobile) as pCount from marketing a,marketing_success t " +
        "where t.marketing_id=a.id and t.effect_date >= ?1 and t.effect_date <= ?2 " +
        "GROUP BY t.source_channel,a.id", nativeQuery = true)
    List statisticsByMarketing(Date beginDate, Date endDate);

    @Query(value = "select distinct ms.mobile, m.name, ms.effect_date " +
        "from marketing_success ms, marketing m  " +
        "where ms.marketing_id = m.id and ms.effect_date >= ?1 and ms.effect_date < ?2 " +
        "order by ms.effect_date asc",
        nativeQuery = true)
    List<Object[]> selectDistinctMobiles(Date beginDate, Date endDate);


    @Query(value = "select suc.* from marketing_success suc, gift gift where gift.source = suc.id " +
        "and suc.marketing_id = ?1 and gift.source_type = ?2 " +
        "and (case when ?3 = 1 then gift.create_time >= ?4 else 1 = 1 end) " +
        "and gift.create_time <= ?5 " +
        "and suc.channel not in (?6) " +
        "group by gift.applicant", nativeQuery = true)
    List<MarketingSuccess> findDataByMarketingAndTime(Long marketingId, Long sourceTypeId, int type, Date startTime, Date endTime, List<Long> orderCenterChannelIdList);

    @Query("select suc from MarketingSuccess suc where suc.marketing = ?1 " +
        "and suc.effectDate <= ?2 and suc.channel not in (?3) order by suc.id")
    org.springframework.data.domain.Page<MarketingSuccess> findPageDataByMarketingAndTimeUnUseGift(
        Marketing marketing, Date endTime, List<Channel> orderCenterChannelList, Pageable pageable);

    @Query("select suc from MarketingSuccess suc where suc.marketing = ?1 " +
        "and suc.effectDate >= ?2 and suc.effectDate <= ?3 " +
        "and suc.channel not in (?4) order by suc.id")
    org.springframework.data.domain.Page<MarketingSuccess> findPageDataByMarketingAndTimeUnUseGift(
        Marketing marketing, Date startTime, Date endTime, List<Channel> orderCenterChannelList, Pageable pageable);

    @Query(value = "select count(1) from marketing_success ms where ms.marketing_id = 48 " +
        "and exists (select 1 from purchase_order po where po.id = ms.detail and po.status in (3,4,5) " +
        "and exists (select 1 from quote_record q where q.id = po.obj_id and insurance_company = 20000)) ", nativeQuery = true)
    int countPinganMarketingReduce();

    @Query(value = "select count(1)  from marketing_success ms where ms.marketing_id = 48 " +
        "and exists (select 1 from purchase_order po where po.id = ms.detail and po.status in (3,4,5) " +
        "and exists (select 1 from quote_record q where q.id = po.obj_id and insurance_company <> 20000))", nativeQuery = true)
    int countOtherMarketingReduce();

    @Query(value = "select count(1) from marketing_success ms where ms.marketing_id = 48 and ms.mobile = ?1 and ms.detail is not null ", nativeQuery = true)
    int countAttendedMarketingReduce(String mobile);

    @Query(value = "select id from marketing_success where effect_date <= ?1 order by id desc limit 1", nativeQuery = true)
    Long findMaxIdByTime(Date createTime);

    LinkedList<MarketingSuccess> findByMarketing(Marketing marketing);

    @Query(value = "select * from marketing_success  where marketing_id= ?1 and id>?2 ", nativeQuery = true)
    List<MarketingSuccess> findByMarketingIdAndLargeThanId(Long marketingId, Long id);

    @Query(value = "select * from marketing_success  where marketing_id in ?1 and create_time between ?2 and ?3 order by marketing_id ", nativeQuery = true)
    List<MarketingSuccess> findByMarketingIdAndCreateTime(List<Long> marketingId, Date startDate, Date endDate);

    @Query(value = "select * from marketing_success where  marketing_id  = '114'and date(create_time) = date( date_sub(now(), interval 1 day))and detail is not null", nativeQuery = true)
    List<MarketingSuccess>  findMarketing201801001();

    @Query(value = "select ms.* from marketing_success ms,marketing mk where  ms.marketing_id  =mk.id and mk.code = ?1 and ms.mobile = ?2", nativeQuery = true)
    List<MarketingSuccess> findByMarketingCodeAndMobile(String marketingCode, String mobile);

    @Query(value = "select * from marketing_success where  marketing_id  = '115'and create_time < DATE(NOW()) and detail in (1,2,3) GROUP BY mobile,detail ORDER BY detail,create_time", nativeQuery = true)
    List<MarketingSuccess> findMarketing201802001();

    @Query(value = "select ms.* from marketing_success ms where ms.marketing_id  =?1 and  ms.source_channel = ?2 and create_time between ?3 and ?4", nativeQuery = true)
    List<MarketingSuccess> findByMarketingIdAndSourceChannel(String marketingId,String source_channel,Date startDate,Date endDate);

    @Query(value = "select * from marketing_success where  marketing_id  = '129' and create_time >= ?1", nativeQuery = true)
    List findAutoFinancingMarketingList(Date maxCreateTime);
}
