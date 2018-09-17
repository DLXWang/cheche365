package com.cheche365.cheche.scheduletask.task;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.scheduletask.service.task.ChannelRebateDateRefreshService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * 渠道费率定时生效
 * Created by yellow on 2017/8/3.
 */
@Service
public class ChannelRebateDateRefreshTask extends BaseTask {
    Logger logger = LoggerFactory.getLogger(ChannelRebateDateRefreshTask.class);

    @Autowired
    private ChannelRebateDateRefreshService channelRebateDateRefreshService;
    private Map<String, Object> jobDataMap = null; //动态参数map

    @Override
    protected void doProcess() throws Exception {
        Date currentDate = new Date();
        Date startDate = DateUtils.getCustomDate(currentDate, -1, 23, 59, 59);
        Date endDate = DateUtils.getDayEndTime(currentDate);

        if (jobDataMap != null) {
            String startDateStr = (String) jobDataMap.get("startDateStr");
            String endDateStr = (String) jobDataMap.get("endDateStr");
            if (StringUtils.isNotEmpty(startDateStr) && StringUtils.isNotEmpty(endDateStr)) {
                logger.debug("获取到动态配置的参数 jobDataMap :{}", jobDataMap.toString());
                startDate = DateUtils.getDate(startDateStr, DateUtils.DATE_LONGTIME24_PATTERN);
                endDate = DateUtils.getDate(endDateStr, DateUtils.DATE_LONGTIME24_PATTERN);
            }
        }

        channelRebateDateRefreshService.refresh(startDate, endDate);
    }
}
