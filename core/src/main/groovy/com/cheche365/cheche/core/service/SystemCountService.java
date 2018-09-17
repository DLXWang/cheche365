package com.cheche365.cheche.core.service;

import com.cheche365.cheche.common.util.DoubleUtils;
import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.context.ApplicationContextHolder;
import com.cheche365.cheche.core.util.CacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by WF on 2015/9/25.
 */
@Service
@Transactional
public class SystemCountService{

    private Logger logger = LoggerFactory.getLogger(SystemCountService.class);
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private AppointmentInsuranceService appointmentInsuranceService;

    public void init() {
        if(CacheUtil.getValueToObject(redisTemplate, WebConstants.PUT_VALUE_ORDER_COUNT) == null ){
            int initNumber = 466235;
            CacheUtil.putValue(redisTemplate, WebConstants.PUT_VALUE_ORDER_COUNT, initNumber);
        }
        if(CacheUtil.getValueToObject(redisTemplate,WebConstants.PUT_VALUE_ORDER_COUNT_MONEY) == null ){
            Double initNumber = 884952.72;
            CacheUtil.putDoubleValue(redisTemplate, WebConstants.PUT_VALUE_ORDER_COUNT_MONEY, initNumber);
        }
    }


    public HashMap<String, Object> getSystemCountMap() {

        init();
        HashMap<String, Object> mapCount = new HashMap<>();
        mapCount=getCurrentCount(mapCount);
        formatCount(mapCount);
        return mapCount;
    }

    public void formatCount(Map<String,Object> mapCount){
        DecimalFormat df = new DecimalFormat("#,###");
        for(String key:mapCount.keySet()){
            mapCount.put(key,df.format(mapCount.get(key)));
        }

    }

    public  HashMap<String, Object> getCurrentCount( HashMap<String, Object> mapCount) {
        int currentValue = (int) CacheUtil.getValueToObject(redisTemplate, WebConstants.PUT_VALUE_ORDER_COUNT);
        int newValue =  currentValue + (int)(Math.random()*10+1);
        if(newValue > currentValue){
            CacheUtil.putValue(redisTemplate, WebConstants.PUT_VALUE_ORDER_COUNT, newValue);
        }

        BigDecimal insured=BigDecimal.valueOf(Math.max(newValue, currentValue));
        mapCount.put("insured", insured);
        mapCount.put("satisfyQuote",insured.multiply(new BigDecimal((2))));

        Double initMoney = 100.24;
        Double currentMoneyValue = (Double)CacheUtil.getValueToObject(redisTemplate,WebConstants.PUT_VALUE_ORDER_COUNT_MONEY);
        Double newMoneyValue =  DoubleUtils.displayDoubleValue(currentMoneyValue + (Math.random() * 10 + 1) * initMoney);
        if(newMoneyValue > currentMoneyValue){
            CacheUtil.putDoubleValue(redisTemplate, WebConstants.PUT_VALUE_ORDER_COUNT_MONEY, newMoneyValue);
        }
       // mapCount.put("savedMoney", BigDecimal.valueOf(Math.max(newMoneyValue, currentMoneyValue)));
        mapCount.put("savedMoney", insured.multiply(new BigDecimal(825)));

        Random rand = new Random();
        Long count = this.appointmentInsuranceService.getAppointmentsCount();
        int appion = 2015 + count.intValue() * 3 + rand.nextInt(3);
        boolean firstTime =  CacheUtil.getValueToObject(redisTemplate,WebConstants.PUT_VALUE_WITH) == null ;
        int valueTwo = firstTime ? appion :  (int) CacheUtil.getValueToObject(redisTemplate,WebConstants.PUT_VALUE_WITH);
        if(valueTwo < appion || firstTime){
            CacheUtil.putValue(redisTemplate, WebConstants.PUT_VALUE_WITH, appion);
        }
        mapCount.put("appointments", BigDecimal.valueOf(Math.max(valueTwo, appion)));

        mapCount.put("totalAmount", insured.multiply(new BigDecimal(2980)));
        mapCount.put("claimCount", insured.divide(new BigDecimal(1000)).setScale(0,BigDecimal.ROUND_HALF_UP) );


        return  mapCount;
    }
}
