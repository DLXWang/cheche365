package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.ActivityMonitorData;
import com.cheche365.cheche.core.model.BusinessActivity;
import com.cheche365.cheche.core.model.CustomerField;
import com.cheche365.cheche.core.repository.ActivityAreaRepository;
import com.cheche365.cheche.core.repository.ActivityMonitorDataRepository;
import com.cheche365.cheche.core.repository.BusinessActivityRepository;
import com.cheche365.cheche.core.repository.CustomerFieldRepository;
import com.cheche365.cheche.email.model.EmailInfo;
import com.cheche365.cheche.scheduletask.model.ActivityMonitorDataInfo;
import com.cheche365.cheche.scheduletask.model.BusinessActivityInfo;
import com.cheche365.cheche.scheduletask.model.MessageInfo;
import com.cheche365.cheche.scheduletask.model.PurchaseOrderInfo;
import com.cheche365.cheche.scheduletask.service.task.CPSChannelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 合作渠道定时任务
 * 定时时间：每天早上8点
 * 接收人：longjm@cheche365.com,zhoujx@cheche365.com,dihw@cheche365.com,wangxiang@cheche365.com
 * Created by sunhuazhong on 2015/5/22.
 */
@Service("cPSChannelTask")
public class CPSChannelTask extends BaseTask {

    Logger logger = LoggerFactory.getLogger(CPSChannelTask.class);

    @Autowired
    private BusinessActivityRepository businessActivityRepository;

    @Autowired
    private ActivityAreaRepository activityAreaRepository;

    @Autowired
    private ActivityMonitorDataRepository activityMonitorDataRepository;

    @Autowired
    private CustomerFieldRepository customerFieldRepository;

    @Autowired
    private CPSChannelService cpsChannelTaskService;

    private String emailconfigPath = "/emailconfig/operate_status_cps_channel.yml";


    /**
     * 执行任务详细内容
     *
     * @return
     */
    @Override
    public void doProcess() throws Exception {

        // 当前时间
        Date currentTime = new Date();

        // 获取所有的未结束的合作渠道(已结束的需要延迟7天)
        List<BusinessActivity> businessActivityList = businessActivityRepository.findSendEmailData(currentTime);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(businessActivityList)) {
            boolean isError = false;
            String error_message = "";
            for (BusinessActivity businessActivity : businessActivityList) {
                try{
                    // 判断是否需要发送邮件
                    if (checkSendEmail(businessActivity, currentTime)) {
                        logger.info("商务活动:{}满足发送邮件的条件，活动开始日期:{}，活动结束日期:{}，当前日期:{}",
                            businessActivity.getCode(),
                            DateUtils.getDateString(businessActivity.getStartTime(), DateUtils.DATE_LONGTIME24_PATTERN),
                            DateUtils.getDateString(businessActivity.getEndTime(), DateUtils.DATE_LONGTIME24_PATTERN),
                            DateUtils.getDateString(currentTime, DateUtils.DATE_LONGTIME24_PATTERN)
                        );
                        //添加消息
                        messageInfoList.add(getMessageInfo(businessActivity,currentTime));
                    } else {
                        logger.info("商务活动:{}不满足发送邮件的条件，活动开始日期:{}，活动结束日期:{}，当前日期:{}",
                            businessActivity.getCode(),
                            DateUtils.getDateString(businessActivity.getStartTime(), DateUtils.DATE_LONGTIME24_PATTERN),
                            DateUtils.getDateString(businessActivity.getEndTime(), DateUtils.DATE_LONGTIME24_PATTERN),
                            DateUtils.getDateString(currentTime, DateUtils.DATE_LONGTIME24_PATTERN)
                        );
                    }
                }catch (Exception e){
                    isError = true;
                    error_message = "business activity(" + businessActivity.getCode() + ") CPSChannel task error.";
                    logger.error("business activity(" + businessActivity.getCode() + ") CPSChannel task error.", e);
                }
            }

            if(isError){
                throw new RuntimeException(error_message);
            }
        }
    }

    /**
     * 装配任务消息
     * @return
     */
    private MessageInfo getMessageInfo(BusinessActivity businessActivity,Date currentTime) throws IOException {


        List<PurchaseOrderInfo> attachmentDataList = new ArrayList<>();
        PurchaseOrderInfo purchaseOrderInfo = new PurchaseOrderInfo();
        // 商务活动基本信息和城市信息、监控数据信息
        BusinessActivityInfo businessActivityInfo = BusinessActivityInfo.createViewModel(businessActivity);
        setCityData(businessActivity, businessActivityInfo);
        setMonitorData(businessActivity, businessActivityInfo, currentTime);
        purchaseOrderInfo.setBusinessActivityInfo(businessActivityInfo);
        attachmentDataList.add(purchaseOrderInfo);
        //查询自定义字段
        List<CustomerField> customerField = customerFieldRepository.findByBusinessActivityOrderById(businessActivity);
        businessActivityInfo.setCustomerFieldList(customerField);
        //发送邮件
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("businessActivityName", businessActivityInfo.getName());
        paramMap.put("businessActivity", businessActivityInfo);

        //装配邮件信息
        EmailInfo emailInfo = assembleEmailInfo(this.emailconfigPath, paramMap);
        //添加附件
        cpsChannelTaskService.addAttachment(emailInfo, businessActivityInfo);
        //给标题添加参数
        emailInfo.setSubject(businessActivity.getName() +"-"+ DateUtils.getDateString(currentTime,"yyyy年MM月dd日") + emailInfo.getSubject());
        // 接收合作渠道的邮箱
        String[] toEmails = getToEmails(businessActivity, emailInfo.getTo());
        emailInfo.setTo(toEmails);

        return MessageInfo.createMessageInfo(emailInfo);
    }



    private void setMonitorData(BusinessActivity businessActivity, BusinessActivityInfo businessActivityInfo, Date currentTime) {
        List<ActivityMonitorDataInfo> monitorDataInfoList = new ArrayList<>();
        ActivityMonitorData firstActivityMonitorData = activityMonitorDataRepository
            .findFirstByBusinessActivityOrderByMonitorTime(businessActivity);
        if(firstActivityMonitorData != null) {
            ActivityMonitorDataInfo sumMonitorData = new ActivityMonitorDataInfo();//汇总监控数
            Date endTime = getEndTime(currentTime);// 统计结束时间
            Date startTime = firstActivityMonitorData.getMonitorTime();// 统计开始时间
            while(startTime.getTime() <= endTime.getTime()) {
                ActivityMonitorDataInfo activityMonitorDataInfo = new ActivityMonitorDataInfo();
                activityMonitorDataInfo.setMonitorTime(DateUtils.getDateString(startTime, "yyyyMMdd"));//监控时间
                Date startMonitorTime = DateUtils.getDayStartTime(startTime);//监控时间的开始时间
                Date endMonitorTime  = DateUtils.getDayEndTime(startTime);//监控时间的结束时间
                List<ActivityMonitorData> dataList = activityMonitorDataRepository
                    .findByBusinessActivityAndMonitorTimeGreaterThanEqualAndMonitorTimeLessThanEqualOrderByMonitorTime(
                        businessActivity, startMonitorTime, endMonitorTime);
                if(org.apache.commons.collections.CollectionUtils.isNotEmpty(dataList)) {
                    for(ActivityMonitorData data : dataList) {
                        ActivityMonitorDataInfo.setMonitorData(activityMonitorDataInfo, data);
                    }
                }
                monitorDataInfoList.add(activityMonitorDataInfo);
                startTime = addOneDayTime(startTime);
                ActivityMonitorDataInfo.setSumMonitorData(sumMonitorData, activityMonitorDataInfo);
            }
            businessActivityInfo.setSumMonitorData(sumMonitorData);
        }

        businessActivityInfo.setMonitorDataList(monitorDataInfoList);
    }

    private Date addOneDayTime(Date startTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    private void setCityData(BusinessActivity businessActivity, BusinessActivityInfo businessActivityInfo) {
        List<String> areaNameList = activityAreaRepository.findAreaNameByBusinessActivity(businessActivity.getId());
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(areaNameList)) {
            List<String> cityList = new ArrayList<>();
            areaNameList.forEach(areaName -> cityList.add(areaName));
            businessActivityInfo.setCity(String.join(",", cityList));
        }
    }

    /**
     * 验证当前是否需要发送邮件
     * @param businessActivity
     * @return
     */
    private boolean checkSendEmail(BusinessActivity businessActivity, Date currentTime) {
        // 统计日期
        Date currentDate = getYearMonthDayDate(currentTime);
        // 活动开始日期
        Date startDate = getYearMonthDayDate(businessActivity.getStartTime());
        // 活动结束日期
        Date endDate = getYearMonthDayDate(businessActivity.getEndTime());

        if (logger.isDebugEnabled()) {
            logger.debug("统计日期：{}", DateUtils.getDateString(currentDate, DateUtils.DATE_LONGTIME24_PATTERN));
            logger.debug("活动开始日期：{}", DateUtils.getDateString(startDate, DateUtils.DATE_LONGTIME24_PATTERN));
            logger.debug("活动结束日期：{}", DateUtils.getDateString(endDate, DateUtils.DATE_LONGTIME24_PATTERN));
        }

        long dateDiff = 0;
        // 结束日期大于等于统计日期
        if(endDate.getTime() >= currentDate.getTime()) {
            dateDiff = DateUtils.dateDiff(startDate, currentDate, DateUtils.INTERNAL_DATE_DAY);
        }
        // 结束日期小于统计日期
        else {
            // 延长日期等于结束日期+7天
            Date extendDate = getExtendDate(endDate);
            if(extendDate.getTime() == currentDate.getTime()) {
                dateDiff = 7 * 30;
            } else if(extendDate.getTime() < currentDate.getTime()) {
                dateDiff = 0;
            } else {
                dateDiff = DateUtils.dateDiff(startDate, currentDate, DateUtils.INTERNAL_DATE_DAY);
            }
        }
        logger.debug("商务活动:{}开始时间与统计时间间隔:{}天", businessActivity.getCode(), dateDiff);

        if(dateDiff == 0) {
            return false;
        }

        //邮件发送频率，1-每周；2-每月，3-不发送
        if(businessActivity.getFrequency() == 1) {
            return (dateDiff % 7) == 0;
        } else {
            return (dateDiff % 30) == 0;
        }
    }

    private Date getExtendDate(Date date) {
        Calendar calendar = Calendar.getInstance();//得到日历
        calendar.setTime(date);//把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, 7);//延长7天
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }


    /**
     * 得到前一天的时间
     * @return
     */
    private Date getEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 获取指定时间的年月日时间
     * @return
     */
    private Date getYearMonthDayDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    private String[] getToEmails(BusinessActivity businessActivity,String[] tos) {
        // 接收合作渠道结果的邮箱：联系人邮箱，以及指定人员邮箱
        List<String> emailList = new ArrayList<>();
        if(!StringUtils.isEmpty(businessActivity.getEmail())) {
            String[] toLinkManEmails = businessActivity.getEmail().split(";");
            for(int i = 0; i < toLinkManEmails.length; i++) {
                if(!emailList.contains(toLinkManEmails[i])) {
                    emailList.add(toLinkManEmails[i]);
                }
            }
        }
        for(int i = 0; i < tos.length; i++) {
            if(!emailList.contains(tos[i])) {
                emailList.add(tos[i]);
            }
        }
        String[] toEmails = new String[emailList.size()];
        return emailList.toArray(toEmails);
    }
}
