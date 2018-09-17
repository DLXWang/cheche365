package com.cheche365.cheche.core.service;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.InstitutionRebateHistoryTempRepository;
import com.cheche365.cheche.core.repository.InstitutionTempRepository;
import com.cheche365.cheche.core.repository.InsuranceCompanyRepository;
import com.cheche365.cheche.core.util.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by xu.yelong on 2016-05-20.
 */
@Service
public class InstitutionRebateHistoryTempService {

    @Autowired
    private InstitutionRebateHistoryTempRepository institutionRebateHistoryTempRepository;

    @Autowired
    private InsuranceCompanyRepository insuranceCompanyRepository;

    @Autowired
    private InstitutionTempRepository institutionTempRepository;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void save(InstitutionRebateTemp institutionRebate, InternalUser internalUser, Integer operation) {
        InstitutionRebateHistoryTemp institutionRebateHistoryTemp = createInstitutionRebateHistoryTemp(institutionRebate, internalUser);
        institutionRebateHistoryTemp.setStartTime(new Date());
        institutionRebateHistoryTemp.setOperation(operation);
        refreshPrev(institutionRebateHistoryTemp);
        institutionRebateHistoryTempRepository.save(institutionRebateHistoryTemp);
    }

    public List<InstitutionRebateHistoryTemp> ListByAreaAndInsuranceCompanyAndDateTime(Area area, Long insuranceCompanyId, Date date) {
        InsuranceCompany insuranceCompany = insuranceCompanyRepository.findOne(insuranceCompanyId);
        return institutionRebateHistoryTempRepository.findByAreaAndInsuranceCompanyAndDateTime(area, insuranceCompany, date);
    }

    private InstitutionRebateHistoryTemp createInstitutionRebateHistoryTemp(InstitutionRebateTemp institutionRebateTemp, InternalUser internalUser) {
        InstitutionRebateHistoryTemp institutionRebateHistoryTemp = new InstitutionRebateHistoryTemp();
        String[] properties = new String[]{
            "institutionTemp", "area", "insuranceCompany", "compulsoryRebate", "commercialRebate"};
        BeanUtil.copyPropertiesContain(institutionRebateTemp, institutionRebateHistoryTemp, properties);
        institutionRebateHistoryTemp.setOperator(internalUser);
        institutionRebateHistoryTemp.setStartTime(new Date());
        return institutionRebateHistoryTemp;
    }

    //刷新上一条历史记录的结束时间
    private void refreshPrev(InstitutionRebateHistoryTemp institutionRebateHistoryTemp) {
        InstitutionRebateHistoryTemp prevHistory = institutionRebateHistoryTempRepository.findFirstByInstitutionTempAndAreaAndInsuranceCompanyOrderByStartTimeDesc(institutionRebateHistoryTemp.getInstitutionTemp(), institutionRebateHistoryTemp.getArea(), institutionRebateHistoryTemp.getInsuranceCompany());
        if (prevHistory != null) {
            prevHistory.setEndTime(new Date());
            institutionRebateHistoryTempRepository.save(prevHistory);
        }
    }

    public Date checkInstitutionRebateHistoryStartTime(InstitutionRebateHistoryTemp history) {
        logger.debug("验证回录出单机构历史费率的开始时间，出单机构:{}，城市:{}，保险公司:{}，开始时间:{}",
            history.getInstitutionTemp().getName(), history.getArea().getName(), history.getInsuranceCompany().getName(),
            DateUtils.getDateString(history.getStartTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        InstitutionRebateHistoryTemp firstHistory = institutionRebateHistoryTempRepository.findFirstByInstitutionTempAndAreaAndInsuranceCompanyOrderByStartTime(
            history.getInstitutionTemp(), history.getArea(), history.getInsuranceCompany());
        if (firstHistory == null) {
            Date currentTime = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
            return DateUtils.dateDiff(history.getStartTime(), currentTime, DateUtils.INTERNAL_DATE_SECOND) < 0 ?
                currentTime : null;
        } else {
            return DateUtils.dateDiff(history.getStartTime(), firstHistory.getStartTime(), DateUtils.INTERNAL_DATE_SECOND) < 0 ?
                firstHistory.getStartTime() : null;
        }
    }

    /**
     * 增加出单机构的历史费率信息
     *
     * @param history
     */
    public void addInstitutionRebateHistoryTemp(InstitutionRebateHistoryTemp history, InternalUser internalUser) {
        InstitutionRebateHistoryTemp firstHistory = institutionRebateHistoryTempRepository.findFirstByInstitutionTempAndAreaAndInsuranceCompanyOrderByStartTime(
            history.getInstitutionTemp(), history.getArea(), history.getInsuranceCompany());
        if (firstHistory != null) {
            history.setEndTime(firstHistory.getStartTime());
        }
        history.setOperator(internalUser);
        history.setOperation(AgentRebateHistory.OPERATION.ADD);
        institutionRebateHistoryTempRepository.save(history);
    }

    /**
     * select by companyid
     *
     * @param id
     * @return
     */
    public List<InstitutionRebateHistoryTemp> findByInstitutionId(Long id) {
        InstitutionTemp institutionTemp = institutionTempRepository.findOne(id);
        return institutionRebateHistoryTempRepository.findByInstitutionTempOrderByStartTime(institutionTemp);
    }

}
