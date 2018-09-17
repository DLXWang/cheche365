package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.Marketing
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
public interface MarketingRepository extends PagingAndSortingRepository<Marketing,Long>, JpaSpecificationExecutor<Marketing>{

    Marketing findFirstByCode(String code);

    List<Marketing> findByMarketingTypeAndBeginDateLessThanEqualAndEndDateGreaterThanEqual(String marketingType, Date beginDate, Date endDate);

    @Query(value = ''' SELECT mt.* FROM marketing mt WHERE  mt.begin_date  <=  NOW() AND mt.end_date >= NOW() AND mt.marketing_type <> 'web'
         AND CASE (SELECT COUNT(1) FROM `gift_channel` WHERE source = mt.`id` AND source_type = 2) WHEN 0 THEN 1 = 1
         ELSE (EXISTS (SELECT 1 FROM gift_channel WHERE source = mt.`id` AND source_type = 2 AND channel = ?1)) END
         order by mt.id desc ''', nativeQuery = true)
    List<Marketing> findActiveMarketingActivities(Long channelId);


    @Query(value = '''SELECT mt.* FROM marketing mt
                            WHERE mt.begin_date <= NOW() AND mt.end_date >= NOW() AND mt.id = 70 AND mt.marketing_type <> 'web' AND mt.quote_support = 1
                            AND EXISTS( SELECT 1 FROM marketing_rule mr
                            WHERE mr.marketing = mt.id AND mr.channel = ?1 AND mr.area = ?2 AND mr.insurance_company = ?3 AND mr.status = 2)
                     ''', nativeQuery = true)
    Marketing findActiveMarketing(Long channel, Long area, Long companyId);

    @Query(value = '''SELECT mt.* FROM marketing mt
                            WHERE mt.begin_date <= NOW() AND mt.end_date >= NOW() AND mt.id = 70 AND mt.marketing_type <> 'web' AND mt.quote_support = 1
                            AND EXISTS( SELECT 1 FROM marketing_rule mr
                            WHERE mr.marketing = mt.id AND mr.channel = ?1 AND mr.area = ?2 AND mr.status = 2)
                     ''', nativeQuery = true)
    Marketing findActiveMarketing(Long channel, Long area);


    @Query(value = '''SELECT * FROM marketing WHERE marketing_type<>'web' ORDER BY id DESC limit 10''', nativeQuery = true)
    List<Marketing> findLatestMarketing();
}
