package com.cheche365.cheche.cpic.util

/**
 * Created by houjinxin on 2015/6/15.
 */
class CityUtils {

    public static final _CITY_CODE_MAPPINGS = [
        110000L: 110100L,
        120000L: 120100L,
        310000L: 310100L,
        500000L: 500100L
    ]

    static getCityCode(innerCityCode) {
        _CITY_CODE_MAPPINGS[innerCityCode] ?: innerCityCode
    }

}
