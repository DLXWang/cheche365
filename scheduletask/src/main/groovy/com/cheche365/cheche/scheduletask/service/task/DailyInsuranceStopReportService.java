package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.core.model.DailyInsuranceDetail;
import com.cheche365.cheche.core.repository.DailyInsuranceDetailRepository;
import com.cheche365.cheche.core.repository.DailyInsuranceRepository;
import com.cheche365.cheche.core.repository.DailyRestartInsuranceRepository;
import com.cheche365.cheche.scheduletask.model.DailyInsuranceStopReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mujiguang on 2017/7/26
 */
@Service
public class DailyInsuranceStopReportService {
    Logger logger = LoggerFactory.getLogger(DailyInsuranceStopReportService.class);
    @Autowired
    private DailyInsuranceRepository dailyInsuranceRepository;
    @Autowired
    private DailyInsuranceDetailRepository dailyInsuranceDetailRepository;
    @Autowired
    private DailyRestartInsuranceRepository dailyRestartInsuranceRepository;
    /**
     * 发送打回邮件
     * 根据打回状态，发送给客服或者内勤
     */
    public Map<String, List<DailyInsuranceStopReport>> getEmailContent() {
        Map<String, List<DailyInsuranceStopReport>> stopApplyListMap = new HashMap<>();
        List<DailyInsuranceStopReport> content = new ArrayList<>();
        List<DailyInsuranceStopReport> content2 = new ArrayList<>();

        //申请停驶记录
        List<Object[]> stopApplyList = dailyInsuranceRepository.findStopInsuranceByCurDate();
        stopApplyList.forEach(stopApply -> {
            DailyInsuranceStopReport stopResult = new DailyInsuranceStopReport();
            stopResult.setLicensePlateNo(String.valueOf(stopApply[0]));
            stopResult.setCommercialPolicyNo(String.valueOf(stopApply[1]));
            stopResult.setStopBeginDate(String.valueOf(stopApply[2]));
            stopResult.setStopEndDate(String.valueOf(stopApply[3]));
            stopResult.setRefundAmt(String.valueOf(stopApply[4]));
            stopResult.setOptStopTime(String.valueOf(stopApply[5]));
            List<DailyInsuranceDetail> dailyList = dailyInsuranceDetailRepository.queryByDailyInsurance(Long.valueOf(stopApply[6].toString()));
            stopResult.setInsuranceDetail("");
            dailyList.forEach(detail -> {
                stopResult.setInsuranceDetail(stopResult.getInsuranceDetail() + detail.getName() + "=" + detail.getRefundPremium() + ",");
            });
            content.add(stopResult);
        });
        //申请复驶记录
        List<Object[]> reStartApplyList = dailyRestartInsuranceRepository.findRestartApplyList();
        reStartApplyList.forEach(restart -> {
            DailyInsuranceStopReport reStartResult = new DailyInsuranceStopReport();
            reStartResult.setLicensePlateNo(String.valueOf(restart[0]));
            reStartResult.setCommercialPolicyNo(String.valueOf(restart[1]));
            reStartResult.setReStartDate(String.valueOf(restart[2]));
            reStartResult.setRePayAmt(String.valueOf(restart[3]));
            reStartResult.setOptReStartTime(String.valueOf(restart[4]));
            content2.add(reStartResult);
        });

        stopApplyListMap.put("stopList",content);
        stopApplyListMap.put("restartList",content2);
        return stopApplyListMap;
    }
}
