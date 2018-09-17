package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.repository.MarketingSuccessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by guoweifu on 2016/1/13.
 */
@Service
public class MarketingCountService {

    @Autowired
    private MarketingSuccessRepository marketingSuccessRepository;


    public Map<String, Object> getContentParam() {
        //设置任务所需要的数据。
        Date startDate = DateUtils.calculateDateByDay(new Date(), -1);//统计开始时间 DateUtils.getDate("2015-08-24",DateUtils.DATE_SHORTDATE_PATTERN);//
        Date endDate = new Date();//统计结束时间 DateUtils.getDate("2015-08-27",DateUtils.DATE_SHORTDATE_PATTERN);//
        Date[] dates = new Date[2];
        dates[0] = startDate;//显示昨天的数据
        dates[1] = endDate;//显示今天的数据
        // 系统提醒邮件内容参数
        List<Map<String, String>> infoList = new ArrayList<>();
        for(int z=0;z<dates.length;z++) {
            Date tempDate = dates[z];
            Date p_start = DateUtils.getDayStartTime(tempDate);
            Date p_endDate = DateUtils.getDayEndTime(tempDate);
            List successList1 = marketingSuccessRepository.statisticsByMarketing(p_start,p_endDate);
            for (int i = 0; i < successList1.size(); i++) {
                Object[] temp = (Object[]) successList1.get(i);
                Map tempMap = new HashMap();
                tempMap.put("marketingName", temp[0]);
                if (temp[1] == null)
                    tempMap.put("sourceChannel", "");
                else
                    tempMap.put("sourceChannel", temp[1]);
                tempMap.put("pDate", DateUtils.getDateString(p_start,DateUtils.DATE_SHORTDATE_PATTERN));
                tempMap.put("pCount", temp[2]);
                infoList.add(tempMap);
            }
        }
        //模板所需要的数据
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("startDate", DateUtils.getDateString(startDate, DateUtils.DATE_LONGTIME24_PATTERN));//统计开始时间
        paramMap.put("endDate", DateUtils.getDateString(endDate, DateUtils.DATE_LONGTIME24_PATTERN));//统计结束时间
        paramMap.put("infoList", infoList);//统计信息
        return paramMap;
    }
}
