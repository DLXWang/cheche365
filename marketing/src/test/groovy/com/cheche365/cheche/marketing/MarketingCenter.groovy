package com.cheche365.cheche.marketing

import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.util.CacheUtil
import net.sf.json.JSONArray

/**
 * Created by zhengwei on 1/9/17.
 */
class MarketingCenter {

    static List<Map> expect

    def static CLASS_TO_EXPECT = [
        'expect': { expect = it }
    ]


    static {

        CLASS_TO_EXPECT.each { clazz, handler ->
            def dataInJson = MarketingCenter.class.getResource("common_data_${clazz.toLowerCase()}.json").text
            handler CacheUtil.doListJacksonDeserialize(dataInJson, Map.class)
        }

    }

}
