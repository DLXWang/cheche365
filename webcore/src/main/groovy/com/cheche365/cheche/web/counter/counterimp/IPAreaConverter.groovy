package com.cheche365.cheche.web.counter.counterimp

import com.cheche365.cheche.core.util.AddressUtil
import groovy.util.logging.Slf4j

/**
 * Created by zhengwei on 6/17/17.
 */
@Slf4j
class IPAreaConverter {

    public static final Map<String, Long> lruMapCache = new LinkedHashMap<String, Long>(10000, 0.75f, true) {
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > 10000;
        }
    };

    def static convert(String ip) {
        Long areaId = null;
        try {
            if(lruMapCache.containsKey(ip)){
                areaId = lruMapCache.get(ip);
            } else {
                AddressUtil.convertByAreaId(AddressUtil.ip2Location(ip))?.id?.with {
                    areaId = it
                    lruMapCache.put(ip, areaId)
                }
            }
            return areaId ?: 110000l
        } catch (Exception e) {
            log.debug("根据ip查询出错，数据统计到未知来源");
        }
    }
}
