package com.cheche365.cheche.admin.web.controller.excludeChannel;

import com.cheche365.cheche.admin.service.excludeChannel.ExcludeChannelSettingService;
import com.cheche365.cheche.manage.common.web.model.ResultModel;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.repository.ChannelRepository;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterChannelFilter;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterChannelFilterViewModel;
import org.apache.commons.collections.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by wangshaobin on 2016/8/29.
 */
@RestController
@RequestMapping("/admin/excludeChannelSetting")
public class TelMarketingCenterChannelFilterController {
    Logger logger = LoggerFactory.getLogger(TelMarketingCenterChannelFilterController.class);
    @Autowired
    private ExcludeChannelSettingService excludeChannelSettingService;

    @Autowired
    private ChannelRepository channelRepository;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<TelMarketingCenterChannelFilterViewModel> findAll(){
        return excludeChannelSettingService.findAll();
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResultModel update(@RequestParam(value = "id", required = true) Long id,
                              @RequestParam(value = "excludeChannels", required = false) String excludeChannels){
        TelMarketingCenterChannelFilterViewModel viewModel = new TelMarketingCenterChannelFilterViewModel();
        logger.debug("开始将id为{}条目的过滤渠道修改为{}", id, excludeChannels);
        viewModel.setId(id);
        viewModel.setExcludeChannels(excludeChannels);
        if(excludeChannelSettingService.save(viewModel))
            return new ResultModel(true,"保存成功");
        return new ResultModel(false,"保存失败");
    }

    @RequestMapping(value = "/channels", method = RequestMethod.GET)
    public List<Channel> findAllChannel(){
        return IteratorUtils.toList(channelRepository.findAll().iterator());
    }

    @RequestMapping(value = "/{settingId}", method = RequestMethod.POST)
    public TelMarketingCenterChannelFilter findById(@PathVariable Long settingId){
        return excludeChannelSettingService.findById(settingId);
    }
}
