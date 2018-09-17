package com.cheche365.cheche.rest.listener;

import com.cheche365.cheche.common.math.NumberUtils;
import com.cheche365.cheche.core.message.TMLoginUserMessage;
import com.cheche365.cheche.core.model.Area;
import com.cheche365.cheche.core.model.UserLoginInfo;
import com.cheche365.cheche.core.repository.AreaRepository;
import com.cheche365.cheche.core.repository.UserLoginInfoRepository;
import com.cheche365.cheche.core.util.AddressUtil;
import com.cheche365.cheche.core.util.CacheUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class RedisUserLoginListener implements MessageListener {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private AreaRepository areaRepository;
    @Autowired
    private UserLoginInfoRepository userLoginInfoRepository;


    @Override
    public void onMessage(Message message, byte[] pattern) {
        String param = String.valueOf(message.toString());
        Assert.notNull(param, "message is not null!");
        try {
            if (redisTemplate.opsForSet().remove(TMLoginUserMessage.QUEUE_SET, param) > 0) {
                UserLoginInfo userLoginInfo = CacheUtil.doJacksonDeserialize(param, UserLoginInfo.class);
                String ip = userLoginInfo.getLastLoginIp();
                if (StringUtils.isNotBlank(ip)) {
                    String cityCode = AddressUtil.ip2Location(ip);
                    if (cityCode != null && NumberUtils.isNumber(cityCode)) {
                        Area area = areaRepository.findFirstByCityCode(Integer.valueOf(cityCode));
                        userLoginInfo.setArea(area);
                        userLoginInfoRepository.save(userLoginInfo);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
