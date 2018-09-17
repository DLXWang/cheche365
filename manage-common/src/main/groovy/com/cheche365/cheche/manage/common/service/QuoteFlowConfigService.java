package com.cheche365.cheche.manage.common.service;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.ChannelRepository;
import com.cheche365.cheche.core.repository.InsuranceCompanyRepository;
import com.cheche365.cheche.core.repository.QuoteFlowConfigRepository;
import com.cheche365.cheche.core.service.QuoteConfigService;
import com.cheche365.cheche.manage.common.model.QuoteFlowConfigOperateLog;
import com.cheche365.cheche.manage.common.repository.QuoteFlowConfigOperateLogRepository;
import com.cheche365.cheche.manage.common.web.model.QuoteFlowConfigExcelModel;
import com.cheche365.cheche.manage.common.web.model.QuoteFlowConfigQuery;
import com.cheche365.cheche.manage.common.web.model.QuoteFlowConfigSearchQuery;
import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * Created by yellow on 2017/7/26.
 */
@Service
@Slf4j
public class QuoteFlowConfigService extends BaseService {

    @Autowired
    private QuoteFlowConfigRepository quoteFlowConfigRepository;
    @Autowired
    private QuoteFlowConfigOperateLogRepository logRepository;
    @Autowired
    private InternalUserManageService internalUserManageService;

    private Logger log = LoggerFactory.getLogger(QuoteFlowConfigService.class);
    @Autowired
    QuoteConfigService quoteConfigService;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    InsuranceCompanyRepository insuranceCompanyRepository;


    public Page<QuoteFlowConfig> getConfigListByPage(QuoteFlowConfigSearchQuery query) {
        return findConfigListBySpecAndPaginate(super.buildPageable(query.getCurrentPage(), query.getPageSize(),
            Sort.Direction.DESC, "id"), query);

    }

    public Page<QuoteFlowConfig> findConfigListBySpecAndPaginate(Pageable pageable, QuoteFlowConfigSearchQuery dataQuery) {
        return quoteFlowConfigRepository.findAll(new Specification<QuoteFlowConfig>() {
            @Override
            public Predicate toPredicate(Root<QuoteFlowConfig> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<QuoteFlowConfig> criteriaQuery = cb.createQuery(QuoteFlowConfig.class);
                //条件构造
                List<Predicate> predicateList = new ArrayList<>();
                if (dataQuery.getInsureCompanys() != null) {
                    Path<String> scope = root.get("insuranceCompany").get("id");
                    predicateList.add(cb.equal(scope, dataQuery.getInsureCompanys()));
                }
                if (dataQuery.getAreas() != null) {
                    Path<String> source = root.get("area").get("id");
                    predicateList.add(cb.equal(source, dataQuery.getAreas()));
                }
                if (dataQuery.getChannels() != null) {
                    Path<String> plan = root.get("channel").get("id");
                    predicateList.add(cb.equal(plan, dataQuery.getChannels()));
                }
                if (dataQuery.getEnable() != null) {
                    Path<String> unit = root.get("enable");
                    predicateList.add(cb.equal(unit, (dataQuery.getEnable())));
                }
                Path<String> configType = root.get("configType");
                predicateList.add(cb.equal(configType, QuoteFlowConfig.ConfigType.QUOTATION.getIndex()));
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

    public Page<QuoteFlowConfigOperateLog> getLogListByPage(QuoteFlowConfigSearchQuery query) {
        return findLogListBySpecAndPaginate(super.buildPageable(query.getCurrentPage(), query.getPageSize(),
            Sort.Direction.DESC, "id"), query.getConfigId());

    }

    public Page<QuoteFlowConfigOperateLog> findLogListBySpecAndPaginate(Pageable pageable, Long id) {
        return logRepository.findAll(new Specification<QuoteFlowConfigOperateLog>() {
            @Override
            public Predicate toPredicate(Root<QuoteFlowConfigOperateLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<QuoteFlowConfigOperateLog> criteriaQuery = cb.createQuery(QuoteFlowConfigOperateLog.class);
                List<Predicate> predicateList = new ArrayList<>();
                Path<String> configId = root.get("quoteFlowConfig").get("id");
                predicateList.add(cb.equal(configId, id));
                Predicate[] predicates = new Predicate[predicateList.size()];
                predicates = predicateList.toArray(predicates);
                return criteriaQuery.where(predicates).getRestriction();
            }
        }, pageable);
    }

    @Transactional
    public void add(QuoteFlowConfigQuery param) {
        InternalUser user = param.getUser();
        if (param.getUser() == null) {
            user = internalUserManageService.getCurrentInternalUser();
        }
        List<QuoteFlowConfig> configList = new ArrayList<>();
        List<QuoteFlowConfigOperateLog> configOperateLogList = new ArrayList<>();
        int i;
        Boolean addPayConfig;
        for (Long areaId : param.getArea()) {
            List<QuoteFlowConfig> flowConfigs = quoteFlowConfigRepository.findByAreaAndConfigType(areaId, Long.valueOf(QuoteFlowConfig.ConfigType.QUOTATION.getIndex()));
            List<QuoteFlowConfig> payConfigs = quoteFlowConfigRepository.findByAreaAndConfigType(areaId, Long.valueOf(QuoteFlowConfig.ConfigType.CHECHE_PAY.getIndex()));
            for (Long insuranceCompanyId : param.getInsureCompanys()) {
                for (Long channelId : param.getChannels()) {
                    QuoteFlowConfig config = newConfig(areaId, channelId, insuranceCompanyId, QuoteFlowConfig.ConfigType.QUOTATION.getIndex(), param.getQuoteWay(), param.getEnable());
                    //2:自有 4:API 6:泛华 页面传值
                    addPayConfig = addPayConfig(param.getQuoteWay());
                    if ((i = flowConfigs.indexOf(config)) > -1) {//如果有这条数据 给id
                        String logComment = getComment(flowConfigs.get(i), config, QuoteFlowConfig.ConfigType.QUOTATION.getIndex());
                        config.setId(flowConfigs.get(i).getId());
                        configOperateLogList.add(createLog(
                            logComment,
                            config,
                            QuoteFlowConfigOperateLog.OperationType.OVERRIDE.getIndex(),
                            QuoteFlowConfigQuery.OperationTime.NOW.getIndex(),
                            null, user));
                    }
                    configList.add(config);
                    QuoteFlowConfig payConfig = newConfig(
                        areaId,
                        channelId,
                        insuranceCompanyId,
                        QuoteFlowConfig.ConfigType.CHECHE_PAY.getIndex(),
                        QuoteFlowConfig.PayValue.THIRD_PARTNER.getIndex(),
                        param.getEnable());
                    if (addPayConfig) {//支付
                        if ((i = payConfigs.indexOf(payConfig)) > -1) {
                            payConfig.setId(payConfigs.get(i).getId());
                            log.debug("update config channel->{},company->{},area->{},type->{}", payConfig.getChannel().getId(), payConfig.getInsuranceCompany().getId(), payConfig.getArea().getId(), payConfig.getConfigType());
                        }
                        configList.add(payConfig);
                    } else {
                        if ((i = payConfigs.indexOf(payConfig)) > -1) {
                            quoteFlowConfigRepository.delete(payConfigs.get(i));
                        }
                    }
                }
            }
        }
        quoteFlowConfigRepository.save(configList);
        logRepository.save(configOperateLogList);
    }

    @Transactional
    public void edit(QuoteFlowConfigQuery param) {
        InternalUser user = param.getUser();
        if (param.getUser() == null) {
            user = internalUserManageService.getCurrentInternalUser();
        }
        QuoteFlowConfig config = quoteFlowConfigRepository.findOne(param.getId());//config是
        QuoteFlowConfig payConfig = quoteFlowConfigRepository.findByAreaAndInsuranceCompanyAndChannelAndConfigType(
            config.getArea(),
            config.getInsuranceCompany(),
            config.getChannel(),
            Long.valueOf(QuoteFlowConfig.ConfigType.CHECHE_PAY.getIndex()));
        if (param.getOperateTime().equals(QuoteFlowConfigQuery.OperationTime.NOW.getIndex())) {//立即执行的
            config.setConfigValue(Long.valueOf(param.getQuoteWay()));
            if (addPayConfig(param.getQuoteWay())) {//如果不是parser默认行为需要支付方式
                if (payConfig == null) {
                    payConfig = newConfig(
                        config.getArea().getId(),
                        config.getChannel().getId(),
                        config.getInsuranceCompany().getId(),
                        QuoteFlowConfig.ConfigType.CHECHE_PAY.getIndex(),
                        QuoteFlowConfig.PayValue.THIRD_PARTNER.getIndex(),
                        param.getEnable());
                }
                payConfig.setEnable(param.getEnable());
                payConfig.setConfigValue(Long.valueOf(QuoteFlowConfig.PayValue.THIRD_PARTNER.getIndex()));
                quoteFlowConfigRepository.save(payConfig);
            } else {
                if (payConfig != null) {
                    quoteFlowConfigRepository.delete(payConfig.getId());
                }
            }
            config.setConfigValue(Long.valueOf(param.getQuoteWay()));
            config.setEnable(param.getEnable());
            quoteFlowConfigRepository.save(config);
        }
        logRepository.save(createLog(
            param.getReason(),
            config,
            QuoteFlowConfigOperateLog.OperationType.ON_OOF_LINE.getIndex(),
            param.getOperateTime(), param.getEnable() ? 1 : 0, user));
        logRepository.save(createLog(
            param.getReason(),
            config,
            QuoteFlowConfigOperateLog.OperationType.ACCESS_MODE.getIndex(),
            param.getOperateTime(), param.getQuoteWay(), user));
    }

    private QuoteFlowConfigOperateLog createLog(String comment, QuoteFlowConfig newConfig, Integer operationType, Integer executeTime, Integer operationValue, InternalUser user) {
        QuoteFlowConfigOperateLog log = new QuoteFlowConfigOperateLog();
        log.setCreateTime(new Date());
        if (executeTime.equals(QuoteFlowConfigQuery.OperationTime.NOW.getIndex())) {//立即执行
            log.setExecutionTime(new Date());
        } else {//次日零点
            log.setExecutionTime(DateUtils.getDayStartTime(DateUtils.calculateDateByDay(new Date(), 1)));
        }
        log.setOperationType(operationType);
        log.setOperator(user);
        log.setOperationValue(operationValue);
        log.setQuoteFlowConfig(newConfig);
        log.setComment(comment);
        return log;
    }

    private String getComment(QuoteFlowConfig originalConfig, QuoteFlowConfig newConfig, Integer configType) {
        if (QuoteFlowConfig.ConfigType.QUOTATION.getIndex().equals(configType)) {//报价方式
            return "覆盖：状态从 " + (originalConfig.getEnable() ? "上线" : "下线") + " 到 " + (newConfig.getEnable() ? "上线" : "下线") + " ,接入方式从 " + getValueByIndex(Integer.parseInt(originalConfig.getConfigValue().toString())) + " 到 " + getValueByIndex(Integer.parseInt(newConfig.getConfigValue().toString()));
        } else {//支付方式
            return "修改支付：状态到" + newConfig.getEnable() + ",接入方式到" + newConfig.getConfigValue();
        }
    }

    private String getValueByIndex(Integer index) {
        return QuoteFlowConfig.ConfigValue.getName(index);
    }

    private QuoteFlowConfig newConfig(Long areaId, Long channelId, Long insuranceCompanyId, Integer configType, Integer configValue, Boolean enable) {
        QuoteFlowConfig config = new QuoteFlowConfig();
        config.setArea(Area.Enum.getValueByCode(areaId));
        if (Channel.toChannel(channelId) == null) {
            config.setChannel(channelRepository.findById(channelId));
        } else {
            config.setChannel(Channel.toChannel(channelId));
        }
        if (toInsuranceCompany(insuranceCompanyId) == null) {
            config.setInsuranceCompany(insuranceCompanyRepository.findOne(insuranceCompanyId));
        } else {
            config.setInsuranceCompany(toInsuranceCompany(insuranceCompanyId));
        }

        config.setConfigType(Long.valueOf(configType));
        config.setConfigValue(Long.valueOf(configValue));
        config.setEnable(enable);
        return config;
    }

    private Boolean addPayConfig(Integer  quoteWay) {
        return false; //sp16集成层根据报价方式判断支付跳转，不依赖qfc表的配置项，稳定后出单中心可删除支付配置相关代码
//        return (quoteWay.equals(QuoteFlowConfig.ConfigValue.API.getIndex())
//            || quoteWay.equals(QuoteFlowConfig.ConfigValue.FANHUA.getIndex())) ? true : false;
    }

    public String quoteType(QuoteSource quoteSource) {
        if (quoteSource == null)
            return "真实";
        return quoteSource.equals(QuoteSource.Enum.REFERENCED_7)?"参考":quoteSource.equals(QuoteSource.Enum.RULEENGINE2_8)?"模糊":"真实";
    }
    /**
     * @param dataList
     */
    @Transactional
    public List<QuoteFlowConfigExcelModel> importData(List<List<String>> dataList) {
        InternalUser user = internalUserManageService.getCurrentInternalUser();
        List<QuoteFlowConfigExcelModel> viewList = new ArrayList<>();
        List<QuoteFlowConfig> configList = new ArrayList<>();
        List<QuoteFlowConfigOperateLog> logList = new ArrayList<>();
        for (List<String> rowList : dataList) {
            QuoteFlowConfigExcelModel model = new QuoteFlowConfigExcelModel();
            model.setCityName(rowList.get(0));
            model.setInsuranceComp(rowList.get(1));
            model.setType(rowList.get(2));
            model.setChannel(rowList.get(3));
            model.setChannelName(rowList.get(4));
            model.setStatus(rowList.get(5));
            model.setQuoteWay(rowList.get(6));

            Area area = Area.Enum.findByName(model.getCityName());
            if(area == null){
                model.setExcelErr("城市名称错误");
                viewList.add(model);
                continue;
            }

            InsuranceCompany company = InsuranceCompany.findByName(model.getInsuranceComp());
            if(company == null){
                model.setExcelErr("保险公司错误");
                viewList.add(model);
                continue;
            }

            Channel channel = Channel.findByDescription(model.getChannelName());
            if(channel == null){
                model.setExcelErr("渠道名称错误");
                viewList.add(model);
                continue;
            }

            Integer configValue;
            if(model.getQuoteWay().equals("鳄鱼报价")||model.getQuoteWay().equals("保险公司UK")){
                configValue = QuoteFlowConfig.ConfigValue.AGENTPARSER.getIndex();
            }else{
                configValue = QuoteFlowConfig.ConfigValue.getId(model.getQuoteWay());
                if(configValue == null){
                    model.setExcelErr("报价方式错误");
                    viewList.add(model);
                    continue;
                }
            }
            Boolean enable = true;
            if(!model.getStatus().equals("上线")){
                if(model.getStatus().equals("下线")){
                    enable = false;
                }else{
                    model.setExcelErr("状态错误");
                    viewList.add(model);
                    continue;
                }
            }
            //报价的新数据
            QuoteFlowConfig newConfig = newConfig(area.getId(),channel.getId(),company.getId(),QuoteFlowConfig.ConfigType.QUOTATION.getIndex(),configValue,enable);
            QuoteFlowConfig config = quoteFlowConfigRepository.findByAreaAndInsuranceCompanyAndChannelAndConfigType(area,company,channel,Long.parseLong(QuoteFlowConfig.ConfigType.QUOTATION.getIndex().toString()));
            if(config != null){
                if(config.getEnable() == enable && config.getConfigValue().toString().equals(configValue.toString())){
                    model.setExcelErr("数据完全一样");
                    viewList.add(model);
                    continue;
                }
                //2:自有 4:API 6:泛华 页面传值
                String logComment = "EXCEL导入：" + getComment(config, newConfig, QuoteFlowConfig.ConfigType.QUOTATION.getIndex());
                logList.add(createLog(
                    logComment,
                    config,
                    QuoteFlowConfigOperateLog.OperationType.OVERRIDE.getIndex(),
                    QuoteFlowConfigQuery.OperationTime.NOW.getIndex(),
                    null, user));
                newConfig.setId(config.getId());
            }
            configList.add(newConfig);

            //支付的新数据（增或者删）
            QuoteFlowConfig newPayConfig = newConfig( area.getId(), channel.getId(), company.getId(), QuoteFlowConfig.ConfigType.CHECHE_PAY.getIndex(), QuoteFlowConfig.PayValue.THIRD_PARTNER.getIndex(), enable);
            QuoteFlowConfig payConfig = quoteFlowConfigRepository.findByAreaAndInsuranceCompanyAndChannelAndConfigType(area,company,channel,Long.parseLong(QuoteFlowConfig.ConfigType.CHECHE_PAY.getIndex().toString()));
            Boolean addPayConfig = addPayConfig(configValue);
            if (addPayConfig) {//支付
                if (payConfig != null) {
                    newPayConfig.setId(payConfig.getId());
                    log.debug("update config channel->{},company->{},area->{},type->{}", payConfig.getChannel().getId(), payConfig.getInsuranceCompany().getId(), payConfig.getArea().getId(), payConfig.getConfigType());
                }
                configList.add(newPayConfig);
            } else {
                if (payConfig != null) {
                    quoteFlowConfigRepository.delete(payConfig.getId());
                }
            }
        }
        quoteFlowConfigRepository.save(configList);
        logRepository.save(logList);
        return viewList;
    }

    public List<Area> getProvinceByChannelId(long channelId) {
        List<Area> areaList = quoteFlowConfigRepository.findAreasByChannel(channelRepository.findById(channelId));
        return areaList;
    }
}
