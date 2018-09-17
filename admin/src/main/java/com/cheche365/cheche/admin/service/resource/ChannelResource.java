package com.cheche365.cheche.admin.service.resource;

import com.cheche365.cheche.admin.web.model.channel.ChannelViewData;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.repository.ChannelRepository;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.manage.common.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunhuazhong on 2015/7/13.
 */
@Component
public class ChannelResource extends BaseService<Channel, Object> {

    @Autowired
    private ChannelRepository channelRepository;

    public List<Channel> listAll() {
        return super.getAll(channelRepository);
    }

    public List<ChannelViewData> createViewData(List<Channel> channels) {
        if (channels == null)
            return null;

        String[] contains = new String[]{"id", "name", "description"};
        List<ChannelViewData> viewDataList = new ArrayList<>();
        channels.forEach(channel -> {
            ChannelViewData viewData = new ChannelViewData();
            BeanUtil.copyPropertiesContain(channel, viewData, contains);
            viewDataList.add(viewData);
        });
        return viewDataList;
    }
}
