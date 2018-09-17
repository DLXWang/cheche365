package com.cheche365.cheche.core.service;

import com.cheche365.cheche.common.util.DateUtils;
import com.cheche365.cheche.core.model.BusinessActivity;
import com.cheche365.cheche.core.model.OrderType;
import com.cheche365.cheche.core.repository.BusinessActivityRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.util.RuntimeUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhengwei on 4/9/15.
 */

@Service
public class PurchaseOrderIdService {

    private Logger logger = LoggerFactory.getLogger(PurchaseOrderIdService.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;


    @Autowired
    private BusinessActivityRepository businessActivityRepository;

    private static final int ASCII_OF_A = 97; // 字符'a'的ASCII码值
    private static final int RADIX_26 = 26; // 26个小写字母表示的26进制
    private static final String COMPLEMENT = "aaaa"; // 用于补充四位字符表示的数值的高位

    public static final String AGENT_INVITATION_CODE = "agent_invitation_code";

    public String getNext(OrderType orderType) {
        String prefix = getPrefix(orderType);
        return getNext(prefix);
    }

    private long getNextRandomNumber() {
        return new Random().nextInt(10) + 1;
    }

    private String getNext(String prefix) {
        Long randomNumber = getNextRandomNumber();
        return getNext(prefix, randomNumber);
    }

    private String getNext(String prefix, Long randomNumber) {
        String date = DateUtils.getCurrentDateString("yyyyMMdd");
        String key = "purchaseOrderId:" + prefix + date;
        long index = redisTemplate.opsForValue().increment(key, randomNumber);
        if (index == randomNumber) {
            redisTemplate.expire(key, 1, TimeUnit.DAYS);//一天后过期
        }
        return prefix + date + String.format("%1$06d", index).replaceAll(",", "");
    }

    private String getCebPayNextSeq() {
        String date = DateUtils.getCurrentDateString("yyyyMMdd");
        String key = "cebpay_purchaseOrderId_" + date;
        return String.format("%1$06d", setExpire(key));
    }

    public long setExpire(String key) {
        long index = 0l;
        redisTemplate.setValueSerializer(new GenericToStringSerializer<Long>(Long.class));
        if (null == redisTemplate.opsForValue().get(key)) {
            index = redisTemplate.opsForValue().increment(key, 1);
            redisTemplate.expire(key, 1, TimeUnit.DAYS);//一天后过期
        } else {
            index = redisTemplate.opsForValue().increment(key, 1);
        }
        return index;
    }

    public String getBACode() {

        long index = redisTemplate.opsForValue().increment("BA_SerialNumber", 1);

        StringBuilder builder = new StringBuilder();
        while (index > 0) {
            char c = (char) (index % RADIX_26 + ASCII_OF_A);
            builder.append(c);
            index = index / RADIX_26;
        }
        if (builder.length() < 4) {
            builder.append(COMPLEMENT.substring(builder.length()));
        }
        String code = builder.reverse().toString();
        BusinessActivity activity = businessActivityRepository.findFirstByCode(code);
        if (activity == null) {
            return code;
        } else {
            logger.warn("code:{}已存在，将重新生成", code);
            return getBACode();
        }
    }

    public String getInviteCode(){
        String code;
        do {
            code = RandomStringUtils.randomNumeric(8);
        } while (stringRedisTemplate.opsForSet().add(AGENT_INVITATION_CODE, code) == 0);
        return code;
    }


    public String getNextByTime(OrderType orderType, Date time) {
        return getNextByTime(orderType, time, getNextRandomNumber());
    }

    public String getNextByTime(OrderType orderType, Date time, Long randomNum) {
        String prefix = getPrefix(orderType);
        String date = DateUtils.getDateString(time, "yyyyMMdd");
        String key = "purchaseOrderId:" + prefix + date;
        if (redisTemplate.hasKey(key)) {
            return getNext(prefix, randomNum);
        }
        Date startTime = DateUtils.getCustomDate(time, 0, 0, 0, 0);
        Date endTime = DateUtils.getCustomDate(time, 0, 23, 59, 59);
        String maxOrderNo = purchaseOrderRepository.findMaxOrderNoByTime(startTime, endTime);//获取投保日期范围内的最大订单号
        Long value = StringUtils.isEmpty(maxOrderNo) ? 0L : Long.valueOf(maxOrderNo.replace(prefix + date, ""));
        return getNext(prefix, value);
    }


    private String getPrefix(OrderType orderType) {
        if (OrderType.Enum.INSURANCE.getId().equals(orderType.getId())) {
            return RuntimeUtil.isProductionEnv() ? "I" : "T";
        }
        //TODO return prefix for other order type
        return "";
    }

}
