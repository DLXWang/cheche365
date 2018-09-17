package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.ActivityMonitorDataRepository;
import com.cheche365.cheche.core.repository.BusinessActivityRepository;
import com.cheche365.cheche.core.repository.CustomerFieldRepository;
import com.cheche365.cheche.core.service.AccessStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by guoweifu on 2015/12/4.
 */
@Service
public class BusinessActivityMonitorDataService {

    Logger logger = LoggerFactory.getLogger(BusinessActivityMonitorDataService.class);


    @Autowired
    private BusinessActivityRepository businessActivityRepository;

    @Autowired
    private ActivityMonitorDataRepository activityMonitorDataRepository;

    @Autowired
    private CustomerFieldRepository customerFieldRepository;

    @Autowired
    private AccessStatisticsService accessStatisticsService;

    /**
     * 更新商务活动监控数据
     * 将redis中的数据保存到数据库中
     */
    public void updateBusinessActivityMonitorData(){
        // 当前时间
        Date currentTime = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
        List<BusinessActivity> businessActivityList = businessActivityRepository.ListEnableRefreshData();
        if (!CollectionUtils.isEmpty(businessActivityList)) {
            for (BusinessActivity businessActivity : businessActivityList) {
                try {

                    Calendar calendar = Calendar.getInstance();
                    int hour=calendar.get(Calendar.HOUR_OF_DAY);
                    // 获取监控数据的开始时间
//                    Date startTime = businessActivity.getRefreshTime() == null ?
//                        DateUtils.getCustomDate(new Date(),0,0,0,0): businessActivity.getRefreshTime();
                    // 根据开始时间和结束时间获取该时间范围内的监控数据
                    List<ActivityMonitorData> activityMonitorDataList = getActivityMonitorDataList(hour, businessActivity);
                    logger.debug("商务活动(" + businessActivity.getCode() + ")获取到的监控数据数量：" + activityMonitorDataList.size());
                    // 保存指定商务商务活动的监控数据
                    if (!CollectionUtils.isEmpty(activityMonitorDataList)) {
                        logger.debug("监控数据内容：");
                        activityMonitorDataList.forEach(activityMonitorData -> {
                            logger.debug("商务活动PV：" + activityMonitorData.getPv()
                                + "，UV：" + activityMonitorData.getUv()
                                + "，SpecialMonitor：" + activityMonitorData.getSpecialMonitor());
                            // Double类型数据小数点截取到后两位
                            if (activityMonitorData.getSubmitAmount() != null) {
                                activityMonitorData.setSubmitAmount(DoubleUtils.displayDoubleValue(activityMonitorData.getSubmitAmount()));
                            }
                            if (activityMonitorData.getPaymentAmount() != null) {
                                activityMonitorData.setPaymentAmount(DoubleUtils.displayDoubleValue(activityMonitorData.getPaymentAmount()));
                            }
                            if (activityMonitorData.getNoAutoTaxAmount() != null) {
                                activityMonitorData.setNoAutoTaxAmount(DoubleUtils.displayDoubleValue(activityMonitorData.getNoAutoTaxAmount()));
                            }
                            activityMonitorData.setMonitorTime(currentTime);
                            // 设置自定义字段的值
                            setCustomerMonitorData(businessActivity, activityMonitorData);
                            // 保存监控数据
                            activityMonitorDataRepository.save(activityMonitorData);
                        });
                    }
                    // 修改商务活动的数据更新时间
                    businessActivity.setRefreshTime(currentTime);
                    logger.debug("定时任务：商务活动ID:{}，名称:{}，刷新时间:{}，结束日期:{}",
                        businessActivity.getId(),
                        businessActivity.getName(),
                        DateUtils.getDateString(currentTime, DateUtils.DATE_LONGTIME24_PATTERN),
                        DateUtils.getDateString(businessActivity.getEndTime(), DateUtils.DATE_LONGTIME24_PATTERN));
                    if (businessActivity.getEndTime().getTime() <= currentTime.getTime()) {
                        logger.debug("定时任务：商务活动ID:{}，名称:{}，标记为不再刷新，刷新时间:{}，结束日期:{}",
                            businessActivity.getId(),
                            businessActivity.getName(),
                            DateUtils.getDateString(currentTime, DateUtils.DATE_LONGTIME24_PATTERN),
                            DateUtils.getDateString(businessActivity.getEndTime(), DateUtils.DATE_LONGTIME24_PATTERN));
                        businessActivity.setRefreshFlag(false);
                    }
                    businessActivityRepository.save(businessActivity);
                } catch (Exception ex) {
                    logger.error("business activity(" + businessActivity.getCode() + ") monitor data task error.", ex);
                }
            }
        }
    }

    private List<ActivityMonitorData> getActivityMonitorDataList(int hour, BusinessActivity businessActivity){
        if(businessActivity.getCooperationMode().getId().equals(CooperationMode.Enum.MARKETING.getId())){
            return accessStatisticsService.totalAPI(hour,businessActivity,"NO_AREA");
        }else{
            return accessStatisticsService.totalAPI(hour,businessActivity,"AREA");
        }

    }

    private void setCustomerMonitorData(BusinessActivity businessActivity, ActivityMonitorData activityMonitorData) {
        // 自定义字段
        List<CustomerField> customerFieldList = customerFieldRepository.findByBusinessActivityOrderById(businessActivity);
        if(!CollectionUtils.isEmpty(customerFieldList)) {
            for(int i = 0; i < customerFieldList.size(); i++) {
                CustomerField customerField = customerFieldList.get(i);
                Double result = getCustomerFieldValue(businessActivity, activityMonitorData, customerField);
                try {
                    Method method = activityMonitorData.getClass().getMethod("setCustomerField" + (i + 1), Double.class);
                    method.invoke(activityMonitorData, result);
                } catch (Exception ex) {
                    logger.error("reflect to set customer field value error", ex);
                }
            }
        }
    }

    private Double getCustomerFieldValue(BusinessActivity businessActivity, ActivityMonitorData activityMonitorData, CustomerField customerField) {
        Double result = 0.00;
        // 基础字段A
        MonitorDataType firstField = customerField.getFirstField();
        // 基础字段B
        MonitorDataType secondField = customerField.getSecondField();
        // 运算符
        ArithmeticOperator operator = customerField.getOperator();

        Double firstFieldValue = getFieldValue(businessActivity, activityMonitorData, firstField);
        Double secondFieldValue = getFieldValue(businessActivity, activityMonitorData, secondField);
        if(ArithmeticOperator.Enum.ADD_1.getId().equals(operator.getId())) {
            result = firstFieldValue + secondFieldValue;
        } else if(ArithmeticOperator.Enum.SUB_2.getId().equals(operator.getId())) {
            result = firstFieldValue - secondFieldValue;
        } else if(ArithmeticOperator.Enum.MUL_3.getId().equals(operator.getId())) {
            result = firstFieldValue * secondFieldValue;
        } else if(ArithmeticOperator.Enum.DIV.getId().equals(operator.getId())) {
            result = secondFieldValue == 0.00? 0.00 : firstFieldValue / secondFieldValue;
        }

        return DoubleUtils.displayDoubleValue(result);
    }

    private Double getFieldValue(BusinessActivity businessActivity, ActivityMonitorData activityMonitorData, MonitorDataType monitorDataType) {
        Double fieldValue = 0.00;
        if(MonitorDataType.Enum.PV.getId().equals(monitorDataType.getId())) {
            fieldValue = displayDoubleValue(activityMonitorData.getPv());
        } else if(MonitorDataType.Enum.UV.getId().equals(monitorDataType.getId())) {
            fieldValue = displayDoubleValue(activityMonitorData.getUv());
        } else if(MonitorDataType.Enum.REGISTER.getId().equals(monitorDataType.getId())) {
            fieldValue = displayDoubleValue(activityMonitorData.getRegister());
        } else if(MonitorDataType.Enum.QUOTE.getId().equals(monitorDataType.getId())) {
            fieldValue = displayDoubleValue(activityMonitorData.getQuote());
        } else if(MonitorDataType.Enum.SUBMIT_COUNT.getId().equals(monitorDataType.getId())) {
            fieldValue = displayDoubleValue(activityMonitorData.getSubmitCount());
        } else if(MonitorDataType.Enum.SUBMIT_AMOUNT.getId().equals(monitorDataType.getId())) {
            fieldValue = DoubleUtils.displayDoubleValue(activityMonitorData.getSubmitAmount());
        } else if(MonitorDataType.Enum.PAYMENT_COUNT.getId().equals(monitorDataType.getId())) {
            fieldValue = displayDoubleValue(activityMonitorData.getPaymentCount());
        } else if(MonitorDataType.Enum.PAYMENT_AMOUNT.getId().equals(monitorDataType.getId())) {
            fieldValue = DoubleUtils.displayDoubleValue(activityMonitorData.getPaymentAmount());
        } else if(MonitorDataType.Enum.NO_AUTO_TAX_AMOUNT.getId().equals(monitorDataType.getId())) {
            fieldValue = DoubleUtils.displayDoubleValue(activityMonitorData.getNoAutoTaxAmount());
        } else if(MonitorDataType.Enum.SPECIAL_MONITOR.getId().equals(monitorDataType.getId())) {
            fieldValue = displayDoubleValue(activityMonitorData.getSpecialMonitor());
        } else if(MonitorDataType.Enum.BUDGET.getId().equals(monitorDataType.getId())) {
            fieldValue = DoubleUtils.displayDoubleValue(businessActivity.getBudget());
        } else if(MonitorDataType.Enum.REBATE.getId().equals(monitorDataType.getId())) {
            fieldValue = DoubleUtils.displayDoubleValue(businessActivity.getRebate() / 100);
        }
        return fieldValue;
    }

    private Double displayDoubleValue(Integer value){
        return Double.valueOf(new DecimalFormat("#.##").format(doubleValue(value)));
    }

    private double doubleValue(Integer value){
        return value == null ? 0.0 : Double.parseDouble(value.intValue() + "");
    }

}
