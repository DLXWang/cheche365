package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.manage.common.model.TelMarketingCenter;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterStatus;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterRepository;
import com.cheche365.cheche.scheduletask.constants.TaskConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 电销数据推送定时任务
 * Created by wangshaobin on 2016/7/25.
 */
@Service
public class TelMarketingCenterPushDataTask extends BaseTask {
    private Logger logger = LoggerFactory.getLogger(TelMarketingCenterPushDataTask.class);
    @Autowired
    private TelMarketingCenterRepository telMarketingCenterRepository;

    private Date currentDate = DateUtils.getCurrentDate(DateUtils.DATE_SHORTDATE_PATTERN);

    @Override
    protected void doProcess() throws Exception {
        //处理80-50天的数据
        handle50To80Days();
        //处理50-30天的数据
        handle30To50Days();
        //处理30-7天的数据
        handle7To30Days();
        //处理7天之内的数据
        handleBelow7Days();
        logger.debug("提前推送数据处理结束");
    }

    private void handle50To80Days(){
        Date startDate = getTargetDate(50);
        Date endDate = getTargetDate(80);
        handlePushableData(startDate,endDate,10);
    }

    private void handle30To50Days(){
        Date startDate = getTargetDate(30);
        Date endDate = getTargetDate(50);
        handlePushableData(startDate,endDate,5);
    }

    private void handle7To30Days(){
        Date startDate = getTargetDate(7);
        Date endDate = getTargetDate(30);
        handlePushableData(startDate,endDate,2);
    }

    private void handleBelow7Days(){
        Date startDate = DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN);
        Date endDate = getTargetDate(7);
        handlePushableData(startDate,endDate,1);
    }

    private Date getTargetDate(int days){
        return DateUtils.calculateDateByDay(DateUtils.getCurrentDate(DateUtils.DATE_LONGTIME24_PATTERN),days);
    }

    @Transactional
    private void handlePushableData(Date startDate,Date endDate,int days){
        int startIndex = 0;int pageSize = TaskConstants.PAGE_SIZE;
        List statusParams = Arrays.asList(TelMarketingCenterStatus.Enum.ALREADY_ORDER,
            TelMarketingCenterStatus.Enum.NO_OPEN_CITY,
            TelMarketingCenterStatus.Enum.ORDER,
            TelMarketingCenterStatus.Enum.REFUSE,
            TelMarketingCenterStatus.Enum.NOT_OWNER,
            TelMarketingCenterStatus.Enum.PURCHASED_BY_OTHER_CHANNEL,
            TelMarketingCenterStatus.Enum.OTHER_STATUS);
        List<TelMarketingCenter> centers = telMarketingCenterRepository.findPushableData(startDate, endDate, startIndex, pageSize, statusParams);
        logger.debug("从条目数{}开始的可提前{}天推送的数据的数量为{}",startIndex,days,getListSize(centers));
        while (!CollectionUtils.isEmpty(centers)){
            updateTriggerTime(centers,days);
            if(centers.size() < pageSize) {
                break;
            }
            startIndex += centers.size();
            centers = telMarketingCenterRepository.findPushableData(startDate, endDate, startIndex, pageSize, statusParams);
            logger.debug("从条目数{}开始的可提前{}天推送的数据的数量为{}",startIndex,days,getListSize(centers));
        }
    }

    private void updateTriggerTime(List<TelMarketingCenter> centers, int days){
        Date triggerDate = DateUtils.getCustomDate(currentDate, days, 12, 0, 0);
        List<TelMarketingCenter> list = new ArrayList<TelMarketingCenter>();
        for(TelMarketingCenter center : centers){
            center.setTriggerTime(triggerDate);
            list.add(center);
        }
        telMarketingCenterRepository.save(list);
    }

    private int getListSize(List list){
        return CollectionUtils.isEmpty(list) ? 0 : list.size();
    }
}
