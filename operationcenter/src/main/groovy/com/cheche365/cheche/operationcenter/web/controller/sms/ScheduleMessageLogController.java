package com.cheche365.cheche.operationcenter.web.controller.sms;

import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.ScheduleMessageLog;
import com.cheche365.cheche.manage.common.service.sms.ScheduleMessageLogService;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.sms.MessageLogQuery;
import com.cheche365.cheche.manage.common.web.model.sms.ScheduleMessageLogViewModel;
import com.cheche365.cheche.operationcenter.web.model.DataTablesPageViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lyh on 2015/10/14.
 */
@RestController
@RequestMapping("/operationcenter/sms/schedule/log")
public class ScheduleMessageLogController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ScheduleMessageLogService scheduleMessageLogService;

    /**
     * 根据条件查询条件短信日志
     *
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    @VisitorPermission("op0304")
    public DataTablesPageViewModel<ScheduleMessageLogViewModel> search(MessageLogQuery query) {
        try {
            Page<ScheduleMessageLog> page = scheduleMessageLogService.getScheduleMessageLogByPage(query);
            List<ScheduleMessageLogViewModel> modelList = new ArrayList<>();
            for (ScheduleMessageLog scheduleMessageLog : page.getContent()) {
                modelList.add(ScheduleMessageLogViewModel.createViewData(scheduleMessageLog));
            }
            PageInfo pageInfo = scheduleMessageLogService.createPageInfo(page);
            return new DataTablesPageViewModel<>(pageInfo.getTotalElements(), pageInfo.getTotalElements(), query.getDraw(), modelList);
        } catch (Exception e) {
            logger.error("ScheduleMessageLogController search has error", e);
        }
        return null;
    }

}
