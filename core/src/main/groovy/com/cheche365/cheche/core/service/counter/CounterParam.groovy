package com.cheche365.cheche.core.service.counter

import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle

/**
 * Created by zhengwei on 6/16/17.
 */
class CounterParam {
    String key
    Integer hour
    String day
    Long baId
    Long areaId

    CounterParam() {
        def cal = Calendar.getInstance()
        hour = cal.get(Calendar.HOUR_OF_DAY)
        day = cal.format("yyyy-MM-dd")
    }

    @Override
    String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    String firstLevel(List<String> prefix){
        (prefix + [key, day, baId as String]).join('_')
    }

    String ipsFirstLevel(List<String> prefix){
        (prefix + [key,  baId as String]).join('_')
    }

    def secondLevel(List<String> subKey){
        subKey << (hour as String)
        subKey.join('_')
    }

    def noAreaPVKey(){
        secondLevel(['pv', 'noArea'])
    }

    def noAreaUVKey(){
        secondLevel(['uv', 'noArea'])
    }

    def areaPVKey(areaId){
        secondLevel(['pv', 'withArea', areaId as String])
    }

    def areaUVKey(areaId){
        secondLevel(['uv', 'withArea', areaId as String])
    }

}
