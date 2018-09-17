package com.cheche365.cheche.core.service;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.InstitutionRebateHistoryRepository;
import com.cheche365.cheche.core.repository.InstitutionRepository;
import com.cheche365.cheche.core.repository.InsuranceCompanyRepository;
import com.cheche365.cheche.core.util.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by xu.yelong on 2016-05-20.
 */
@Service
public class InstitutionRebateHistoryService {

    @Autowired
    private InstitutionRebateHistoryRepository institutionRebateHistoryRepository;

    @Autowired
    private InsuranceCompanyRepository insuranceCompanyRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    public void save(InstitutionRebate institutionRebate, InternalUser internalUser, Integer operation) {
        InstitutionRebateHistory institutionRebateHistory = createInstitutionRebateHistory(institutionRebate, internalUser);
        institutionRebateHistory.setStartTime(new Date());
        institutionRebateHistory.setOperation(operation);
        refreshPrev(institutionRebateHistory);
        institutionRebateHistoryRepository.save(institutionRebateHistory);
    }

    public List<InstitutionRebateHistory> ListByAreaAndInsuranceCompanyAndDateTime(Area area,Long insuranceCompanyId,Date date){
        InsuranceCompany insuranceCompany=insuranceCompanyRepository.findOne(insuranceCompanyId);
        return institutionRebateHistoryRepository.findByAreaAndInsuranceCompanyAndDateTime(area,insuranceCompany,date);
    }

    private InstitutionRebateHistory createInstitutionRebateHistory(InstitutionRebate institutionRebate, InternalUser internalUser) {
        InstitutionRebateHistory institutionRebateHistory = new InstitutionRebateHistory();
        String[] properties = new String[]{
            "institution", "area", "insuranceCompany", "compulsoryRebate", "commercialRebate"};
        BeanUtil.copyPropertiesContain(institutionRebate, institutionRebateHistory, properties);
        institutionRebateHistory.setOperator(internalUser);
        institutionRebateHistory.setStartTime(new Date());
        return institutionRebateHistory;
    }

    //刷新上一条历史记录的结束时间
    private void refreshPrev(InstitutionRebateHistory institutionRebateHistory) {
        InstitutionRebateHistory prevHistory = institutionRebateHistoryRepository.findFirstByInstitutionAndAreaAndInsuranceCompanyOrderByStartTimeDesc(institutionRebateHistory.getInstitution(), institutionRebateHistory.getArea(), institutionRebateHistory.getInsuranceCompany());
        if (prevHistory != null) {
            prevHistory.setEndTime(new Date());
            institutionRebateHistoryRepository.save(prevHistory);
        }
    }

    public InstitutionRebateHistory checkInstitutionRebateHistoryStartTime(InstitutionRebateHistory history){
        logger.debug("验证回录出单机构历史费率的开始时间，出单机构:{}，城市:{}，保险公司:{}，开始时间:{}",
            history.getInstitution().getName(), history.getArea().getName(), history.getInsuranceCompany().getName(),
            DateUtils.getDateString(history.getStartTime(), DateUtils.DATE_LONGTIME24_PATTERN));
        return institutionRebateHistoryRepository.findByInstitutionAndAreaAndInsuranceCompanyAndStartTime(
            history.getInstitution().getId(), history.getArea().getId(), history.getInsuranceCompany().getId(), history.getStartTime());
//        if(firstHistory == null) {
//            Date currentTime = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
//            return DateUtils.dateDiff(history.getStartTime(), currentTime, DateUtils.INTERNAL_DATE_SECOND) < 0?
//                currentTime : null;
//        } else {
//            return DateUtils.dateDiff(history.getStartTime(), firstHistory.getStartTime(), DateUtils.INTERNAL_DATE_SECOND) < 0?
//                firstHistory.getStartTime() : null;
//        }
    }

    /**
     * 增加出单机构的历史费率信息
     * @param history
     */
    @Transactional
    public void addInstitutionRebateHistory(InstitutionRebateHistory history,InternalUser internalUser) {
        InstitutionRebateHistory nextHistory = institutionRebateHistoryRepository.findNextByInstitutionAndAreaAndInsuranceCompany(
            history.getInstitution().getId(), history.getArea().getId(), history.getInsuranceCompany().getId(),history.getStartTime());
        if (nextHistory != null) {
            history.setEndTime(nextHistory.getStartTime());
        } else {
            InstitutionRebateHistory lastHistory = institutionRebateHistoryRepository.findLastByInstitutionAndAreaAndInsuranceCompany(
                    history.getInstitution().getId(), history.getArea().getId(), history.getInsuranceCompany().getId(),history.getStartTime());
            if (lastHistory != null) {
                lastHistory.setEndTime(history.getStartTime());
                lastHistory.setOperator(internalUser);
                lastHistory.setOperation(AgentRebateHistory.OPERATION.UPD);
                institutionRebateHistoryRepository.save(lastHistory);
            }
        }
        history.setOperator(internalUser);
        history.setOperation(AgentRebateHistory.OPERATION.ADD);
        institutionRebateHistoryRepository.save(history);
    }

    /**
     * select by companyid
     * @param id
     * @return
     */
    public List<InstitutionRebateHistory> findByInstitutionId(Long id){
        Institution institution = institutionRepository.findOne(id);
        return institutionRebateHistoryRepository.findByInstitutionOrderByStartTime(institution);
    }

    public InstitutionRebateHistory findByInstitutionAndDateTimeAndAreAndCompany(Long id,Date confirmDate,Long areaId,Long companyId){
        if (confirmDate == null) {
            confirmDate = new Date();
        }
        return institutionRebateHistoryRepository.findByInstitutionAndDateTimeAndAreAndCompany(id,confirmDate,areaId,companyId);
    }

}
