package com.cheche365.cheche.pingan.util

/**
 * 城市编码映射
 * Created by houjinxin on 2015/6/15.
 */
class CityCodeMappings {

    public static final _CITYCODE_MAPPINGS = [
        110000L: 110100L,
        120000L: 120100L,
        310000L: 310100L,
        500000L: 500100L
    ]

    public static getCityCode(areaId) {
        _CITYCODE_MAPPINGS[areaId] ?: areaId
    }

}
