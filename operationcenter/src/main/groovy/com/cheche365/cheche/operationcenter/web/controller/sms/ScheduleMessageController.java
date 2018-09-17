package com.cheche365.cheche.operationcenter.web.controller.sms;

import com.cheche365.cheche.core.annotation.VisitorPermission;
import com.cheche365.cheche.core.model.ScheduleMessage;
import com.cheche365.cheche.manage.common.exception.FieldValidtorException;
import com.cheche365.cheche.manage.common.model.PublicQuery;
import com.cheche365.cheche.manage.common.service.sms.ScheduleMessageService;
import com.cheche365.cheche.operationcenter.web.model.DataTablesPageViewModel;
import com.cheche365.cheche.manage.common.web.model.PageInfo;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.manage.common.web.model.sms.ScheduleMessageViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyh on 2015/10/13.
 */
@RestController
@RequestMapping("/operationcenter/sms/schedule")
public class ScheduleMessageController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ScheduleMessageService scheduleMessageService;

    /**
     * 新增条件触发短信
     *
     * @param model
     * @param result
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    @VisitorPermission("op030301")
    public ResultModel add(@Valid ScheduleMessageViewModel model, BindingResult result) {
        logger.info("add scheduleMessage start...");
        if (result.hasErrors())
            return new ResultModel(false, "请将信息填写完整");

        scheduleMessageService.addScheduleMessage(model);
        return new ResultModel();
    }

    /**
     * 修改条件触发短信
     *
     * @param scheduleMessageId
     * @param model
     * @param result
     * @return
     */
    @RequestMapping(value = "/{scheduleMessageId}", method = RequestMethod.PUT)
    @VisitorPermission("op030303")
    public ResultModel update(@PathVariable Long scheduleMessageId, @Valid ScheduleMessageViewModel model, BindingResult result) {
        logger.info("update scheduleMessage's info by id -> {}", scheduleMessageId);
        if (result.hasErrors())
            return new ResultModel(false, "请将信息填写完整");

        scheduleMessageService.updateScheduleMessage(scheduleMessageId, model);
        return new ResultModel();
    }

    /**
     * 根据条件查询条件触发短信
     *
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    @VisitorPermission("op0303")
    public DataTablesPageViewModel<ScheduleMessageViewModel> search(PublicQuery query) {
        try {
            Page<ScheduleMessage> scheduleMessagePage = scheduleMessageService.getScheduleMessageByPage(query);
            List<ScheduleMessageViewModel> modelList = new ArrayList<>();
            for (ScheduleMessage scheduleMessage : scheduleMessagePage.getContent()) {
                modelList.add(ScheduleMessageViewModel.createViewData(scheduleMessage));
            }
            PageInfo pageInfo = scheduleMessageService.createPageInfo(scheduleMessagePage);
            return new DataTablesPageViewModel<>(pageInfo.getTotalElements(), pageInfo.getTotalElements(), query.getDraw(), modelList);
        } catch (Exception e) {
            logger.error("ScheduleMessageController search has error", e);
        }
        return null;
    }

    /**
     * 获取条件短信详情
     *
     * @param scheduleMessageId
     * @return
     */
    @RequestMapping(value = "/{scheduleMessageId}", method = RequestMethod.GET)
    @VisitorPermission("op030303")
    public ScheduleMessageViewModel findOne(@PathVariable Long scheduleMessageId) {
        if (logger.isDebugEnabled()) {
            logger.debug("get scheduleMessage detail,id:" + scheduleMessageId);
        }

        if (scheduleMessageId == null || scheduleMessageId < 1) {
            throw new FieldValidtorException("find scheduleMessageId detail, id can not be null or less than 1");
        }

        return scheduleMessageService.findById(scheduleMessageId);
    }

    /**
     * 修改状态
     *
     * @param scheduleMessageId
     * @return
     */
    @RequestMapping(value = "/{scheduleMessageId}/{operationType}", method = RequestMethod.PUT)
    @VisitorPermission("op030302")
    public ResultModel changeStatus(@PathVariable Long scheduleMessageId, @PathVariable Integer operationType) {
        if (scheduleMessageId == null || scheduleMessageId < 1)
            throw new FieldValidtorException("operation scheduleMessage, id can not be null or less than 1");

        scheduleMessageService.changeStatus(scheduleMessageId, operationType);
        return new ResultModel();
    }
}
