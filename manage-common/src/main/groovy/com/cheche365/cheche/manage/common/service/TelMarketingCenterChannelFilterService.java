package com.cheche365.cheche.manage.common.service;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.repository.ChannelRepository;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterChannelFilter;
import com.cheche365.cheche.manage.common.model.TelMarketingCenterTaskType;
import com.cheche365.cheche.manage.common.repository.TelMarketingCenterChannelFilterRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wangshaobin on 2016/8/29.
 */
@Service
public class TelMarketingCenterChannelFilterService {

    @Autowired
    private TelMarketingCenterChannelFilterRepository taskExcludeChannelSettingRepository;

    @Autowired
    private ChannelRepository channelRepository;


    private List<Channel> nullChannels;

    @PostConstruct
    private void init(){
        nullChannels = new ArrayList<>();
        Channel channel = new Channel();
        channel.setId(9999L);
        nullChannels.add(channel);
    }


    public List<Channel> findExcludeChannelsByTaskType(TelMarketingCenterTaskType taskType){
        List<Channel> channels = null;
        TelMarketingCenterChannelFilter excludeChannels = taskExcludeChannelSettingRepository.findFirstByTaskType(taskType.getId());
        if(excludeChannels != null && StringUtils.isNotEmpty(excludeChannels.getExcludeChannels())){
            String channelIds = excludeChannels.getExcludeChannels();
            String[] channelIdArr = channelIds.split(",");
            channels = changeChannelIdArraysToChannel(channelIdArr);
        }else{
            channels = returnNullChannel();
        }
        return channels;
    }

    private List<Channel> changeChannelIdArraysToChannel(String[] channelIds){
        List<Channel> channels = null;
        if(channelIds.length <= 0){
            channels = returnNullChannel();
        }else{
            channels = channelRepository.findByIds(Arrays.asList(channelIds));
        }
        return channels;
    }

    private List<Channel> returnNullChannel(){
        return nullChannels;
    }

}
