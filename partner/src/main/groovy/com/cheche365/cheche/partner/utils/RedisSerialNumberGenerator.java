package com.cheche365.cheche.partner.utils;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.util.RuntimeUtil;
import com.cheche365.cheche.signature.client.SerialNumberFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 流水号生成器，使用redis生成
 * Created by zhaozhong on 2016/1/28.
 */
@Component
public class RedisSerialNumberGenerator implements SerialNumberFilter.SerialNumberGenerator {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private String getDataString() {
        return DateUtils.getCurrentDateString("yyyyMMdd");
    }

    /**
     * redis key，partner:sync_order:+日期
     *
     * @return
     */
    private String getRedisKey() {
        return "partner:sync_serial:" + getDataString();
    }

    /**
     * 拼装流水号
     * 规则：profile首字母+日期+5位流水号
     *
     * @param num
     * @return
     */
    private String getNumString(Long num) {
        String orderStr = "00000" + num;
        return new StringBuffer(14)
            .append(getEvnPrefix())
            .append(getDataString())
            .append(orderStr.substring(orderStr.length() - 5))
            .toString();
    }

    /**
     * 使用profile首字母作为流水号前缀，
     * dev(D),itg(I),qa(Q),production(P)
     *
     * @return
     */
    private String getEvnPrefix() {
        return String.valueOf(RuntimeUtil.getEvnProfile().charAt(0)).toUpperCase();
    }

    @Override
    public String nextNum() {
        String key = getRedisKey();
        Long num = redisTemplate.opsForValue().increment(key, 1L);
        if (1 == num) {
            redisTemplate.expire(key, 1, TimeUnit.DAYS);
        }
        return getNumString(num);
    }
}
