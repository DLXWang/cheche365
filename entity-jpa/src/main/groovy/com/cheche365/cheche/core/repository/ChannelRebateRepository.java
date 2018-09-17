package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.ChannelRebate;
import com.cheche365.cheche.core.model.InsuranceCompany;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by yinJianBin on 2017/6/12.
 */
@Repository
@Cacheable(value = "channelRebateRepository",keyGenerator = "cacheKeyGenerator", condition="#root.methodName.startsWith('find')")
@Caching(
    evict = {
        @CacheEvict(value = "channelRebateRepository", allEntries = true,condition = "#root.methodName eq 'save'"),
        @CacheEvict(value = "channelRebatePolicy", allEntries = true,condition = "#root.methodName eq 'save'")

    }
)
public interface ChannelRebateRepository extends JpaSpecificationExecutor<ChannelRebate>, PagingAndSortingRepository<ChannelRebate, Long> {

    ChannelRebate findFirstByChannelAndAreaAndInsuranceCompanyAndStatus(Channel channel, Area area, InsuranceCompany insuranceCompany, Integer status);

    @Query(value = "select cr from ChannelRebate cr where cr.channel.id = ?1 and cr.area.id = ?2 and cr.insuranceCompany.id = ?3")
    ChannelRebate findByChannelAndAreaAndInsuranceCompany(Long channelId, Long areaId, Long insuranceCompanyId);

    @Query(value = "select cr.insuranceCompany from ChannelRebate cr group by cr.insuranceCompany order by cr.insuranceCompany.id asc")
    List<InsuranceCompany> getCompanys();

    @Query(value = "select cr from ChannelRebate cr where cr.readyEffectiveDate between ?1 and ?2")
    List<ChannelRebate> findByReadyEffectiveDate(Date startDate, Date endDate);

    List<ChannelRebate> findByAreaAndChannelAndStatus( Area area,Channel channel, Integer status);

    @Query(value = "select * from channel_rebate where channel = ?1 and area =?2 and insurance_company in (?3) and status = 1",nativeQuery = true)
    List<ChannelRebate> findChannelRebates(Channel channel, Area area, List<InsuranceCompany> insuranceCompany);

    @Override
    <S extends ChannelRebate> S save(S entity);

    @Override
    <S extends ChannelRebate> Iterable<S> save(Iterable<S> entities);
}
