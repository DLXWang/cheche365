package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.core.repository.DailyInsuranceRepository;
import com.cheche365.cheche.core.repository.DailyRestartInsuranceRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.scheduletask.model.DailyInsuranceBasicReportModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenxiangyin on 2017/4/26
 */
@Service
public class DailyInsuranceBasicReportService {
    Logger logger = LoggerFactory.getLogger(DailyInsuranceBasicReportService.class);
    @Autowired
    private DailyInsuranceRepository dailyInsuranceRepository;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private DailyRestartInsuranceRepository dailyRestartInsuranceRepository;
    /**
     * 发送打回邮件
     * 根据打回状态，发送给客服或者内勤
     */
    public List<DailyInsuranceBasicReportModel> getEmailContent() {
        List<DailyInsuranceBasicReportModel> content = new ArrayList<>();
        DailyInsuranceBasicReportModel result = new DailyInsuranceBasicReportModel();
        //申请过停驶的车辆数
        BigInteger stoppedNum = dailyInsuranceRepository.findStoppedNum();
        //当日正在停驶的车辆数`
        BigInteger stoppingNum = dailyInsuranceRepository.findStoppingNum();
        //停驶总天数 停驶总次数 停驶总返还车险保费
        List<Object[]> stopDaysList = dailyInsuranceRepository.findStopDays();
        Object[] stopDays = stopDaysList.get(0);
        //保单总量 原始保费总额（商业+较强+车船税） 原始保费总额（仅商业险）
        List<Object[]> orderNumAndPriceList = purchaseOrderRepository.findOrderNumAndPrice();
        Object[] orderNumAndPrice = orderNumAndPriceList.get(0);
        //未申请过停驶的车辆数
        BigInteger unstoppedNum = purchaseOrderRepository.findUnstoppedNum();
        //复驶总天数
        BigInteger restartDays = dailyRestartInsuranceRepository.findAllRestartDays();
        result.setStoppedNum(stoppedNum.toString());
        result.setStoppingNum(stoppingNum.toString());
        result.setUnstoppedNum(unstoppedNum.toString());
        result.setOrderNum(orderNumAndPrice[0].toString());
        result.setAllPaidAmount(orderNumAndPrice[1].toString());
        result.setLicensePaidAmount(orderNumAndPrice[2].toString());
        result.setAllStopDays(stopDays[0].toString());
        result.setAllStopTimes(stopDays[1].toString());
        result.setAllReturnMoney(stopDays[2].toString());
        result.setAllRestartDays(restartDays.toString());
        content.add(result);
        return content;
    }
}
