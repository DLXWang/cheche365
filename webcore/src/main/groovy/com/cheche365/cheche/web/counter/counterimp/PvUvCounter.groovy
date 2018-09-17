package com.cheche365.cheche.web.counter.counterimp

import com.cheche365.cheche.core.model.BusinessActivity
import com.cheche365.cheche.core.service.counter.CounterParam
import com.cheche365.cheche.web.counter.icounter.APICounter
import org.springframework.context.annotation.Lazy
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

import javax.servlet.http.HttpServletRequest

import static com.cheche365.cheche.core.constants.CounterConstants.*
@Lazy
@Service
class PvUvCounter extends APICounter {

    PvUvCounter(HttpServletRequest request, StringRedisTemplate template) {
        super(request, template);
    }

    @Override
    void oneMoreCount(BusinessActivity businessActivity) {
        def param = new CounterParam(baId: businessActivity.getId(), key: KEY_PVUV)
        String ip = getIp();
        def areaId = IPAreaConverter.convert(ip)


        addCount(param, ['noArea'],businessActivity)
        addCount(param, ['withArea', areaId as String],businessActivity)
    }

    def addCount(CounterParam param, List<String> prefix,BusinessActivity businessActivity){
        String firstLevel = param.firstLevel([COUNTER_PREFIX])
        def ipsKey = param.ipsFirstLevel([IPS_PREFIX] + prefix)

        setHashExpire(firstLevel);

        template.opsForHash().with {
            if(!template.opsForSet().isMember(ipsKey, ip)) {
                setHashExpire(ipsKey,getSystemZeroTime());
                increment(firstLevel, param.secondLevel(['uv'] + prefix) ,1);
                template.opsForSet().add(ipsKey, ip)
            }
            increment(firstLevel, param.secondLevel(['pv'] + prefix), 1)
        }
    }

    @Override
    String apiName() {
        return KEY_PVUV;
    }


}
