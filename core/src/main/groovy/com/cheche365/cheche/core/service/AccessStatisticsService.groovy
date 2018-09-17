package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.constants.CounterConstants
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.ActivityMonitorData
import com.cheche365.cheche.core.model.BusinessActivity
import com.cheche365.cheche.core.service.counter.CounterParam
import org.apache.commons.lang3.time.DateUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import static com.cheche365.cheche.core.model.Area.Enum.getActiveAreas

/**
 * Created by mahong on 2015/8/31.
 */
@Service
class AccessStatisticsService {

    Logger logger = LoggerFactory.getLogger(AccessStatisticsService.class);

    private StringRedisTemplate redisTemplate;

    private static SimpleDateFormat  sdf= new SimpleDateFormat("yyyy-MM-dd");

    AccessStatisticsService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate
    }

    List<ActivityMonitorData> totalAPI(int hour, BusinessActivity businessActivity, String statisticsType) {
        if(!businessActivity ||  !(0..23).contains(hour) ){
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "参数校验失败");
        }
        def dateParam=format(hour)
        logger.debug("开始 pv uv统计，统计日期为{},时间为{}点", dateParam[0],dateParam[1]);
        statisticsType == "NO_AREA" ?  countNoArea(dateParam[0],dateParam[1], businessActivity) : countByArea(dateParam[0],dateParam[1], businessActivity)
    }

    def countNoArea(String day,int hour, BusinessActivity ba){
        new CounterParam(baId: ba.id, key: CounterConstants.KEY_PVUV,day:day,hour:hour)
            .collect {  param ->
            redisTemplate.opsForHash().multiGet( param.firstLevel([CounterConstants.COUNTER_PREFIX]),[ param.noAreaPVKey(), param.noAreaUVKey()])
        }
        .flatten()
            .with {
            [
                new ActivityMonitorData(
                    pv: it[0]as Integer,
                    uv: it.size()>1 ? it[1]as Integer : 0,
                    businessActivity: ba
                )
            ]
        }
    }

    def countByArea(String day,int hour, BusinessActivity ba){
        new CounterParam(baId: ba.id, key: CounterConstants.KEY_PVUV,day:day,hour:hour)
            .collect { param ->
            getActiveAreas().collect() {area->
                redisTemplate.opsForHash().with {
                    [
                        area: area,
                        hourpv : get(param.firstLevel([CounterConstants.COUNTER_PREFIX]), param.areaPVKey(area.id)) as Integer,
                        houruv : get(param.firstLevel([CounterConstants.COUNTER_PREFIX]), param.areaUVKey(area.id)) as Integer
                    ]
                }
            }
        }
        .flatten()
            .groupBy {it.area}
            .collect {
            new ActivityMonitorData(
                area:it.key,
                pv: it.value.hourpv.sum{it?:0},
                uv: it.value.houruv.sum{it?:0},
                businessActivity: ba)
        }
        .findAll {it.pv}

    }

    def static format( hour){
        Date sysDate=new Date()
        hour==0 ? [sdf.format(DateUtils.addDays(sysDate, -1)),23] :[sdf.format(sysDate),hour-1]
    }


}
