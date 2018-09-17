package com.cheche365.cheche.web.counter.icounter;

import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.model.BusinessActivity;
import com.cheche365.cheche.core.util.CacheUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest
import java.text.SimpleDateFormat
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit;

import static com.cheche365.cheche.core.constants.CounterConstants.*;
@Service
abstract class APICounter {

    private Logger logger = LoggerFactory.getLogger(APICounter.class);

    protected StringRedisTemplate template;
    protected HttpServletRequest request;

    APICounter(HttpServletRequest request, StringRedisTemplate template){
        this.request = request;
        this.template = template;
    }


    Object count() throws Throwable {
        getBusinessActivity()?.with {oneMoreCount(it)}
    }

    protected void oneMoreCount(BusinessActivity businessActivity) {
//        try{
//            setHashExpire();
//            String invokeDateMin = getCurrentMin();
//            String hashName = generateHashName(invokeDateMin);
//            String thirdActivity = baId;
//            String userIp = getIp();
//            String hashKey = generateHashKey(this.getClass().getSimpleName(), invokeDateMin, thirdActivity, userIp);
//            template.opsForHash().increment(hashName, hashKey, 1);
//        } catch (Exception e){
//            logger.error(e.getMessage());
//        }
    }

    BusinessActivity getBusinessActivity(){
        request.getSession().getAttribute(WebConstants.SESSION_KEY_CPS_CHANNEL)?.with {
            CacheUtil.doJacksonDeserialize(it, BusinessActivity.class)
        }
    }


    String getIp() throws Exception{
        String ip
        if(null != request.getHeader(REMOTE_IP)){
            ip = request.getHeader(REMOTE_IP).split(",\\s*")[0];
        } else {
            logger.debug("无X-Forwarded-For属性，使用默认IP PV UV地区统计会收到影响");
            ip = DEFAULT_IP
        }
        return ip;
    }

    void setHashExpire(String key){
        if(template.opsForHash().size(key) == 0){
            template.opsForHash().put(key, "expireKey", "24hours");
            template.expire(key, 24, TimeUnit.HOURS);
        }
    }

    void setHashExpire(String key,Date expireDate){
        if(template.opsForSet().size(key) == 0){
            template.opsForSet().add(key,"expireKey")
            template.expireAt(key,expireDate)
        }
    }

    Date getSystemZeroTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.getTime();
    }

    abstract String apiName();


}

