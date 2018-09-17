package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.DailyInsurance;
import com.cheche365.cheche.core.model.DailyInsuranceStatus;
import com.cheche365.cheche.core.repository.DailyInsuranceRepository;
import com.cheche365.cheche.core.repository.DailyRestartInsuranceRepository;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Luly on 2016/12/1.
 */
@Service
public class DailyInsuranceStatusRefreshService {
    Logger logger = LoggerFactory.getLogger(TelMarketingCenterQuoteDataImportService.class);
    @Autowired
    private DailyInsuranceRepository dailyInsuranceRepository;

    public void updateDataStatusToStoped(){
        List<DailyInsurance> applyStopList = dailyInsuranceRepository.findByStatusAndBeginDate(DailyInsuranceStatus.Enum.STOP_APPLY.getId(), TaskConstants.PAGE_SIZE);
        while (CollectionUtils.isNotEmpty(applyStopList)) {
            logger.debug("申请停驶的用户数量为{}",applyStopList.size());
            this.processData(applyStopList,DailyInsuranceStatus.Enum.STOPPED);
            if (applyStopList.size() < TaskConstants.PAGE_SIZE) {
                break;
            }
            applyStopList = dailyInsuranceRepository.findByStatusAndBeginDate(DailyInsuranceStatus.Enum.STOP_APPLY.getId(), TaskConstants.PAGE_SIZE);
        }
    }

    public void updateDataStatusToRestart(){
        List<DailyInsurance> applyRestartList = dailyInsuranceRepository.findByStatusAndRestartDate(TaskConstants.PAGE_SIZE);
        while (CollectionUtils.isNotEmpty(applyRestartList)) {
            logger.debug("停驶时间到期的用户数量为{}",applyRestartList.size());
            this.processData(applyRestartList,DailyInsuranceStatus.Enum.RESTARTED);
            if (applyRestartList.size() < TaskConstants.PAGE_SIZE) {
                break;
            }
            applyRestartList = dailyInsuranceRepository.findByStatusAndRestartDate(TaskConstants.PAGE_SIZE);
        }
    }

    private void processData(List<DailyInsurance> dailyInsuranceList,DailyInsuranceStatus dailyInsuranceStatus) {
        List<DailyInsurance> newList = new ArrayList<>();
        for (DailyInsurance dailyInsurance : dailyInsuranceList) {
            dailyInsurance.setStatus(dailyInsuranceStatus);
            dailyInsurance.setUpdateTime(new Date());
            dailyInsurance.setDescription(dailyInsurance.getDescription()+";"+ DateUtils.getDateString(new Date(),DateUtils.DATE_SHORTDATE_PATTERN)+" "+dailyInsuranceStatus.getStatus());
            newList.add(dailyInsurance);
        }
        dailyInsuranceRepository.save(newList);
    }
}
