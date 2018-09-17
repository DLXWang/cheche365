package com.cheche365.cheche.admin.web.controller.task;

import com.cheche365.cheche.admin.service.resource.ChannelResource;
import com.cheche365.cheche.admin.service.task.TaskImportMarketingSuccessDataService;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.admin.web.model.channel.ChannelViewData;
import com.cheche365.cheche.admin.web.model.task.TaskImportMarketingSuccessDataViewModel;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.Marketing;
import com.cheche365.cheche.core.model.SourceType;
import com.cheche365.cheche.manage.common.exception.FieldValidtorException;
import com.cheche365.cheche.manage.common.model.TaskImportMarketingSuccessData;
import com.cheche365.cheche.core.model.TelMarketingCenterSource;
import com.cheche365.cheche.marketing.service.MarketingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by xu.yelong on 2016-03-31.
 */
@RestController
@RequestMapping("/admin/task/importMarketingSuccessData")
public class TaskImportMarketingSuccessDataController {
    @Autowired
    private TaskImportMarketingSuccessDataService taskImportMarketingSuccessDataService;

    @Autowired
    private MarketingService marketingService;

    @Autowired
    private ChannelResource channelResource;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public PageViewModel<TaskImportMarketingSuccessDataViewModel> findAll(@RequestParam(value = "currentPage") Integer currentPage,
                                                                          @RequestParam(value = "pageSize") Integer pageSize) {
        if (currentPage == null || currentPage < 1) {
            throw new FieldValidtorException("list quotePhoto info, currentPage can not be null or less than 1");
        }

        if (pageSize == null || pageSize < 1) {
            throw new FieldValidtorException("list quotePhoto info, pageSize can not be null or less than 1");
        }
        return taskImportMarketingSuccessDataService.findAll(currentPage, pageSize);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResultModel save(@RequestParam(value = "id", required = false) Long id,
                            @RequestParam(value = "marketing") String marketingCode,
                            @RequestParam(value = "source") Long sourceId,
                            @RequestParam(value = "priority") Integer priority,
                            @RequestParam(value = "cacheKey") String cacheKey,
                            @RequestParam(value = "channel") Long channelId) {
        Marketing marketing = marketingService.getMarketingByCode(marketingCode);
        if (marketing == null) {
            return new ResultModel(false, "活动编号不存在");
        }

        TaskImportMarketingSuccessData data=taskImportMarketingSuccessDataService.findByCacheKey(cacheKey);
        if(data!=null){
            if((id!=null&&!data.getId().equals(id))||(id==null&&data.getCacheKey().equals(cacheKey))){
                return new ResultModel(false, "缓存的key已存在");
            }
        }else{
            data = new TaskImportMarketingSuccessData();
        }
        data.setId(id);
        data.setMarketing(marketing);
        data.setSource(TelMarketingCenterSource.Enum.getById(sourceId));
        data.setChannel(Channel.toChannel(channelId));
        data.setPriority(priority);
        data.setCacheKey(cacheKey);
        taskImportMarketingSuccessDataService.save(data);
        return new ResultModel(true, "保存成功");
    }

    @RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
    public ResultModel setStatus(@PathVariable Long id) {
        TaskImportMarketingSuccessData data = taskImportMarketingSuccessDataService.findOne(id);
        data.setEnable(data.getEnable() ? false : true);
        taskImportMarketingSuccessDataService.save(data);
        return new ResultModel(true,"保存成功");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public TaskImportMarketingSuccessDataViewModel findOne(@PathVariable Long id) {
        TaskImportMarketingSuccessData data = taskImportMarketingSuccessDataService.findOne(id);
        if (data != null) {
            return new TaskImportMarketingSuccessDataViewModel(data);
        }
        return null;
    }

    @RequestMapping(value = "/source", method = RequestMethod.GET)
    public List<TelMarketingCenterSource> getSources() {
        return TelMarketingCenterSource.Enum.SOURCE_LIST;
    }

    @RequestMapping(value = "/sourceType", method = RequestMethod.GET)
    public List<SourceType> getSourceType() {
        return SourceType.Enum.ALL;
    }

    @RequestMapping(value = "/channelList", method = RequestMethod.GET)
    public List<ChannelViewData> getChannelList() {
        List<Channel> channels = Channel.allChannels();
        return channelResource.createViewData(channels);
    }
}
