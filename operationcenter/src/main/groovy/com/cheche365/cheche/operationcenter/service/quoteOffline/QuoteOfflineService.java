package com.cheche365.cheche.operationcenter.service.quoteOffline;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.InsuranceCompany;
import com.cheche365.cheche.core.model.QuoteFlowConfig;
import com.cheche365.cheche.core.repository.QuoteFlowConfigRepository;
import com.cheche365.cheche.core.service.QuoteConfigService;
import com.cheche365.cheche.manage.common.model.QuoteFlowConfigOperateLog;
import com.cheche365.cheche.manage.common.repository.QuoteFlowConfigOperateLogRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import com.cheche365.cheche.operationcenter.model.QuoteOfflineAddQuery;
import com.cheche365.cheche.operationcenter.model.QuoteOfflineQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cheche365.cheche.core.model.InsuranceCompany.toInsuranceCompany;

/**
 * Created by chenxiangyin on 2017/7/7.
 */
@Service
public class QuoteOfflineService extends BaseService {
    @Autowired
    private QuoteFlowConfigRepository quoteFlowConfigRepository;
    @Autowired
    private QuoteFlowConfigOperateLogRepository logRepository;
    @Autowired
    private InternalUserManageService internalUserManageService;
    @Autowired
    private QuoteConfigService quoteConfigService;

    public Page<QuoteFlowConfig> getLogByPage(QuoteOfflineQuery query) {
        return findLogListBySpecAndPaginate(super.buildPageable(query.getCurrentPage(), query.getPageSize(),
            Sort.Direction.DESC, "id"), query);
    }

    public Page<QuoteFlowConfig> findLogListBySpecAndPaginate(Pageable pageable, QuoteOfflineQuery dataQuery) {
        return quoteFlowConfigRepository.findAll(new Specification<QuoteFlowConfig>() {
            @Override
            public Predicate toPredicate(Root<QuoteFlowConfig> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<QuoteFlowConfig> criteriaQuery = cb.createQuery(QuoteFlowConfig.class);
                //条件构造
                List<Predicate> predicateList = new ArrayList<>();
                if (!StringUtil.isNull(dataQuery.getInsureComp())) {
                    Path<String> scope = root.get("insuranceCompany").get("id");
                    predicateList.add(cb.equal(scope,Long.parseLong(dataQuery.getInsureComp())));
                }
                if (!StringUtil.isNull(dataQuery.getArea())) {
                    Path<String> source = root.get("area").get("id");
                    predicateList.add(cb.equal(source,Long.parseLong(dataQuery.getArea())));
                }
                if (!StringUtil.isNull(dataQuery.getChannel())) {
                    Path<String> plan = root.get("channel").get("id");
                    predicateList.add(cb.equal(plan,Long.parseLong(dataQuery.getChannel())));
                }
                if (dataQuery.getStatus() != null) {
                    Path<String> unit = root.get("enable");
                    predicateList.add(cb.equal(unit,(dataQuery.getStatus()== 0) ? false : true));
                }
                Path<String> configType = root.get("configType");
                predicateList.add(cb.equal(configType,QuoteFlowConfig.ConfigType.QUOTATION.getIndex()));
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }
    @Transactional
    public Boolean add(QuoteOfflineQuery query){
        List<QuoteFlowConfig> configList = new ArrayList<>();

        String[] areas = StringUtil.split(query.getArea(),",");
        String[] channels = StringUtil.split(query.getChannel(),",");
        String[] insuranceComp = StringUtil.split(query.getInsureComp(),",");
        try{
            for(String areaId:areas){
                Area area = Area.Enum.getValueByCode(Long.parseLong(areaId));
                for(String channelId:channels){
                    Channel channel = Channel.toChannel(Long.parseLong(channelId));
                    for(String insuranceCompanyId:insuranceComp){
                        InsuranceCompany insuranceCompany = toInsuranceCompany(Long.parseLong(insuranceCompanyId));
                        QuoteFlowConfig config = quoteFlowConfigRepository.findOneByAreaCompChannel(area,insuranceCompany,channel.getParent());
                        //TODO youh
                        if(config != null){
                            if(!(config.getConfigValue().equals(query.getConfigValue()) && config.getEnable().equals(query.getEnable()))){
                                QuoteFlowConfigOperateLog log = new QuoteFlowConfigOperateLog();
                                String enable = config.getEnable().toString();
                                String configValue = config.getConfigValue().toString();
                                config.setConfigType(1L);
                                config.setConfigValue(query.getConfigValue());
                                config.setEnable(query.getEnable());
                                quoteFlowConfigRepository.save(config);
                                log.setOperationType(2);
                                log.setCreateTime(new Date());
                                log.setExecutionTime(new Date());
                                log.setOperator(internalUserManageService.getCurrentInternalUser());
                                log.setQuoteFlowConfig(config);
                                log.setComment("覆盖：状态从" + enable + "到" + query.getEnable() + "接入方式从" + configValue  + "到" +  query.getConfigValue());
                                logRepository.save(log);
                            }
                        }else{
                            config = new QuoteFlowConfig();
                            config.setChannel(channel);
                            config.setInsuranceCompany(insuranceCompany);
                            config.setArea(area);
                            config.setConfigType(1L);
                            config.setConfigValue(query.getConfigValue());
                            config.setEnable(query.getEnable());
                            quoteFlowConfigRepository.save(config);
                        }
                    }
                }
            }
        }catch (Exception e){
            //TODO out exception 记日志
            return false;
        }
        return true;
    }

    @Transactional
    //TODO 没有持久化config
    public void editQuoteOffline(QuoteOfflineQuery query) {
        QuoteFlowConfig config = quoteFlowConfigRepository.findOne(query.getId());
        Date queryTime = new Date();
        Long configValue = config.getConfigValue();
        Boolean enable = config.getEnable();
//        if(query.getOperateTime().equals(QuoteOfflineAddQuery.NOW.getIndex())){//立即执行的
//            config.setConfigValue(query.getConfigValue());
//            config.setEnable(query.getEnable());
//        }else{
//            queryTime = DateUtils.getDayStartTime(DateUtils.calculateDateByDay(new Date(),1));
//        }
        //log:接入方式
        //TODO 不需要逻辑判断 去除if
        if(!configValue.equals(query.getConfigValue())){
            QuoteFlowConfigOperateLog log = this.crateLog(config,queryTime);
            log.setOperationType(1);
            log.setOperationValue(Integer.parseInt(query.getConfigValue().toString()));
            log.setComment(query.getReason());
            logRepository.save(log);
        }
        //log:上下线
        if(!enable.equals(query.getEnable())){
            QuoteFlowConfigOperateLog log = this.crateLog(config,queryTime);
            log.setOperationType(0);
            log.setOperationValue(query.getEnable()? 1:0);
            log.setComment(query.getReason());
            logRepository.save(log);
        }
        //InternalUser internalUser = orderCenterInternalUserManageService.getCurrentInternalUser();
    }
    private QuoteFlowConfigOperateLog crateLog(QuoteFlowConfig config, Date queryTime){
        QuoteFlowConfigOperateLog log = new QuoteFlowConfigOperateLog();
        log.setQuoteFlowConfig(config);
        log.setOperator(internalUserManageService.getCurrentInternalUser());
        log.setCreateTime(new Date());
        log.setExecutionTime(queryTime);
        return log;
    }
}
