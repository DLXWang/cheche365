package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.InsuranceCompany;
import com.cheche365.cheche.core.model.QuoteFlowConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by shanxf on 2017/6/16.
 */
@Repository
@Cacheable(value = "quoteFlowConfig",keyGenerator = "cacheKeyGenerator", condition="#root.methodName.startsWith('find')")
@Caching(
    evict = {
        @CacheEvict(value = "quoteFlowConfig", allEntries = true,condition = "#root.methodName eq 'save'"),
        @CacheEvict(value = "areaGroup", allEntries = true,condition = "#root.methodName eq 'save'"),
        @CacheEvict(value = "areaGroupIncludeSI", allEntries = true,condition = "#root.methodName eq 'save'")
    }
)
public interface QuoteFlowConfigRepository extends JpaRepository<QuoteFlowConfig,Long>, JpaSpecificationExecutor<QuoteFlowConfig> {

    @Query(value = "select * from quote_flow_config where channel=?1 and insurance_company=?2 and config_type=1 and enable=1", nativeQuery = true)
    List<QuoteFlowConfig> findByChannelAndInsuranceCompany(Channel channel, InsuranceCompany insuranceCompany);

    @Query(value = "select * from quote_flow_config where channel=?1 and config_type=1 and enable=1", nativeQuery = true)
    List<QuoteFlowConfig> findByChannel(Channel channel);

    @Query(value = "select distinct qfc.area from QuoteFlowConfig qfc where qfc.channel = ?1 and qfc.configType=1 and qfc.enable=1")
    List<Area> findAreasByChannel(Channel channel);

    @Query(value = "select * from quote_flow_config where area=?1 and insurance_company=?2 and channel=?3 and config_type=1 and enable=1", nativeQuery = true)
    QuoteFlowConfig findByAreaAndInsuranceCompanyAndChannel(Area area, InsuranceCompany insuranceCompany, Channel channel);

    @Query(value = "select distinct qfc.insuranceCompany from QuoteFlowConfig qfc where qfc.area=?1 and qfc.configType=1 and qfc.enable=1")
    List<InsuranceCompany> findInsuranceCompanyByArea(Area area);

    @Query(value = "select * from quote_flow_config where area=?1 and channel=?2 and config_type=1 and enable=1", nativeQuery = true)
    List<QuoteFlowConfig> findByAreaAndChannel(Area area, Channel channel);

    @Query(value = "select distinct qfc.insuranceCompany from QuoteFlowConfig qfc where qfc.area=?1 and qfc.channel=?2  and qfc.configType=1 and qfc.enable=1")
    List<InsuranceCompany> findInsuranceCompanyByAreaAndChannel(Area area, Channel channel);

    @Query(value = "select * from quote_flow_config where area=?1 and insurance_company=?2 and channel=?3 limit 1", nativeQuery = true)
    QuoteFlowConfig findOneByAreaCompChannel(Area area, InsuranceCompany insuranceCompany, Channel channel);

    @Query(value = "select * from quote_flow_config where area=?1 and config_type=?2", nativeQuery = true)
    List<QuoteFlowConfig> findByAreaAndConfigType(Long areaId, Long configType);

    QuoteFlowConfig findByAreaAndInsuranceCompanyAndChannelAndConfigType(Area area, InsuranceCompany insuranceCompany, Channel channel, Long configType);

    @Query(value = "select distinct qfc.insuranceCompany from QuoteFlowConfig qfc where qfc.channel=?1  and qfc.configType=1 and qfc.enable=1")
    List<InsuranceCompany> findInsuranceCompanyByChannel(Channel channel);

    <S extends QuoteFlowConfig> S save(S entity);

    <S extends QuoteFlowConfig> List<S> save(Iterable<S> entities);

    //提供给集成测试初模拟报价始化quoteFlowConfig使用
    QuoteFlowConfig findByChannelAndInsuranceCompanyAndAreaAndEnable(Channel channel, InsuranceCompany insuranceCompany, Area area, Boolean enable);
}
