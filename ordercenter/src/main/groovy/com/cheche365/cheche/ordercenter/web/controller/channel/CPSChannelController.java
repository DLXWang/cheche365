package com.cheche365.cheche.ordercenter.web.controller.channel;

import com.cheche365.cheche.ordercenter.service.channel.IChannelService;
import com.cheche365.cheche.manage.common.web.model.PageViewModel;
import com.cheche365.cheche.ordercenter.web.model.channel.ChannelViewData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Created by wangfei on 2015/5/22.
 */
@RestController
@RequestMapping("/orderCenter/cps")
public class CPSChannelController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IChannelService cpsChannelService;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public PageViewModel<ChannelViewData> list(@RequestParam(value = "keyword",required = false) String keyword,
                     @RequestParam(value = "currentPage",required = true) Integer currentPage,
                     @RequestParam(value = "pageSize",required = true) Integer pageSize) {
        if (currentPage == null || currentPage < 1 ) {
            logger.info("list cpsChannels, currentPage can not be null or less than 1");
            return null;
        }

        if (pageSize == null || pageSize < 1 ) {
            logger.info("list cpsChannels, pageSize can not be null or less than 1");
            return null;
        }

        return cpsChannelService.listChannels(currentPage, pageSize, keyword);
    }

    @RequestMapping(value = "/add",method = RequestMethod.GET)
    public boolean add(@Valid ChannelViewData viewData, BindingResult result){
        if (result.hasErrors()) {
            logger.info("add new cpcChannel, validation has error");
            return false;
        }

        return cpsChannelService.addChannel(viewData);
    }

    @RequestMapping(value = "/findOne",method = RequestMethod.GET)
    public ChannelViewData findOne(@RequestParam(value = "channelId",required = true) Long channelId) {
        if (channelId == null || channelId < 1) {
            logger.info("find cpsChannel by id, id can not be null or less than 1.");
            return null;
        }

        return cpsChannelService.findById(channelId);
    }

    @RequestMapping(value = "/update",method = RequestMethod.GET)
    public boolean update(@Valid ChannelViewData viewData, BindingResult result) {
        if (result.hasErrors()) {
            logger.info("update cpcChannel, validation has error");
            return false;
        }

        if (viewData.getId() == null || viewData.getId() < 1) {
            logger.info("update cpsChannel, id can not be null or less than 1");
            return false;
        }

        return cpsChannelService.updateChannel(viewData);
    }

    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public boolean delete(@RequestParam(value = "channelId",required = true) Long channelId) {
        if (channelId == null || channelId < 1) {
            logger.info("delete cpsChannel by id, id can not be null or less than 1.");
            return false;
        }

        return cpsChannelService.deleteChannel(channelId);
    }

}
