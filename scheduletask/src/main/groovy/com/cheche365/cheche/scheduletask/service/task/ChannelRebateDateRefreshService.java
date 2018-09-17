package com.cheche365.cheche.scheduletask.service.task;

import com.cheche365.cheche.core.model.ChannelRebate;
import com.cheche365.cheche.core.model.ChannelRebateHistory;
import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.repository.ChannelRebateHistoryRepository;
import com.cheche365.cheche.core.repository.ChannelRebateRepository;
import com.cheche365.cheche.manage.common.service.InternalUserManageService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yellow on 2017/8/3.
 */
@Service
public class ChannelRebateDateRefreshService {
    private Logger logger = LoggerFactory.getLogger(ChannelRebateDateRefreshService.class);

    @Autowired
    ChannelRebateRepository channelRebateRepository;

    @Autowired
    ChannelRebateHistoryRepository channelRebateHistoryRepository;

    @Autowired
    InternalUserManageService internalUserManageService;

    public void refresh(Date startDate, Date endDate) {
        Date currentDate = new Date();
        List<ChannelRebate> channelRebateList = channelRebateRepository.findByReadyEffectiveDate(startDate, endDate);
        logger.debug("定时任务[ 刷新渠道费率生效日期 ]开始运行,获取到待生效的数据[{}]条", channelRebateList.size());
        List<ChannelRebate> newList = new ArrayList<>();
        List<ChannelRebateHistory> historyList = new ArrayList<>();
        for (ChannelRebate channelRebate : channelRebateList) {
            ChannelRebateHistory channelRebateHistory = new ChannelRebateHistory();
            channelRebateHistory.setExpireDate(channelRebate.getReadyEffectiveDate());
            channelRebateHistory.setEffectiveDate(channelRebate.getEffectiveDate());
            channelRebateHistory.setOnlyCommercialRebate(channelRebate.getOnlyCommercialRebate());
            channelRebateHistory.setOnlyCompulsoryRebate(channelRebate.getOnlyCompulsoryRebate());
            channelRebateHistory.setCommercialRebate(channelRebate.getCommercialRebate());
            channelRebateHistory.setCompulsoryRebate(channelRebate.getCompulsoryRebate());
            channelRebateHistory.setCreateTime(currentDate);
            channelRebateHistory.setChannelRebate(channelRebate);
            channelRebateHistory.setOperator(InternalUser.ENUM.SYSTEM);

            historyList.add(channelRebateHistory);


            channelRebate.setEffectiveDate(channelRebate.getReadyEffectiveDate());
            channelRebate.setOnlyCommercialRebate(channelRebate.getOnlyReadyCommercialRebate());
            channelRebate.setOnlyCompulsoryRebate(channelRebate.getOnlyReadyCompulsoryRebate());
            channelRebate.setCommercialRebate(channelRebate.getReadyCommercialRebate());
            channelRebate.setCompulsoryRebate(channelRebate.getReadyCompulsoryRebate());
            channelRebate.setReadyEffectiveDate(null);
            channelRebate.setOnlyReadyCommercialRebate(null);
            channelRebate.setOnlyReadyCompulsoryRebate(null);
            channelRebate.setReadyCommercialRebate(null);
            channelRebate.setReadyCompulsoryRebate(null);
            channelRebate.setUpdateTime(currentDate);

            newList.add(channelRebate);
        }


        if (CollectionUtils.isNotEmpty(channelRebateList)) {
            channelRebateRepository.save(newList);
            channelRebateHistoryRepository.save(historyList);
        }

    }
}
