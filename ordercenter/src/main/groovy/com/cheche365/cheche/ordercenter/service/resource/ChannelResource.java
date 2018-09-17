package com.cheche365.cheche.ordercenter.service.resource;

import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.repository.ChannelRepository;
import com.cheche365.cheche.manage.common.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by sunhuazhong on 2015/7/13.
 */
@Component
public class ChannelResource extends BaseService<Channel, Channel> {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public List<Channel> listAll() {
        return super.getAll(channelRepository);
    }

    public Channel findById(Long id) {
        return channelRepository.findById(id);
    }
    
}
