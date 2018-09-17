package com.cheche365.cheche.test.core.counter

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.BusinessActivity
import com.cheche365.cheche.core.repository.AreaRepository
import com.cheche365.cheche.core.service.AccessStatisticsService
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.StringRedisTemplate
import spock.lang.Specification

/**
 * Created by zhengwei on 6/7/17.
 */
class CounterFT extends Specification {

    def "pv uv测试, 不分城市"(){

        given:

        def template = Stub(StringRedisTemplate){
            opsForHash() >> Stub(HashOperations){
                get(_, _) >> 1
            }
        }

        def service = new AccessStatisticsService(template, null)

        when:
        def result = service.countNoArea(new Date().parse('yyyy-MM-dd HH', '2017-06-15 10'), new Date().parse('yyyy-MM-dd HH', '2017-06-15 14'), new BusinessActivity(id: 147l, code: 'yes'))

        then:
        result.size() == 1
        result.first().pv == 5
        result.first().uv == 5
    }


    def "pv uv测试, 区分城市"(){

        given:

        def template = Stub(StringRedisTemplate){
            opsForHash() >> Stub(HashOperations){
                get(_, _) >> 1
            }
        }
        
        new Area.Enum(Stub(AreaRepository){
            findShortAreasList() >> [new Area(id: 110000l), new Area(id: 120000l),new Area(id: 130000l)]
        })

        def service = new AccessStatisticsService(template)

        when:
        def result = service.countByArea('2017-07-04',11, new BusinessActivity(id: 715, code: 'yes'))

        then:
        result.size() == 3
        result.every{it.pv == 3}
        result.every{it.uv == 3}
    }
}
