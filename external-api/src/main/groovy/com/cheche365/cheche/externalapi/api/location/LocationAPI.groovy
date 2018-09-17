package com.cheche365.cheche.externalapi.api.location

import com.cheche365.cheche.externalapi.ExternalAPI
import org.springframework.stereotype.Service

/**
 * Created by zhengwei on 12/02/2018.
 * 调用百度服务，根据经纬度获得地址详情，目前小程序在用。<br>
 * 如 输入参数： 38.76623,116.43213 <br>
 * 得到如下结果：<br>
 *  {"status":0,"result":{"location":{"lng":116.43212999999995,"lat":38.766230098491629},"formatted_address":"河北省沧州市任丘市","business":"","addressComponent":{"country":"中国","country_code":0,"country_code_iso":"CHN","country_code_iso2":"CN","province":"河北省","city":"沧州市","city_level":2,"district":"任丘市","town":"","adcode":"130982","street":"","street_number":"","direction":"","distance":""},"pois":[],"roads":[],"poiRegions":[],"sematic_description":"崇村东805米","cityCode":149}}
 */

@Service
class LocationAPI extends ExternalAPI {

    String call(String location){

        super.call([
                qs: [
                        ak: '2IBKO6GVxbYZvaR2mf0GWgZE',
                        output: 'json',
                        pois: '0',
                        location: location
                ]
        ])
    }

    @Override
    String host() {
        'http://api.map.baidu.com'
    }

    @Override
    String path() {
        'geocoder/v2/'
    }

    @Override
    Class responseType() {
        String
    }

    @Override
    String method() {
        'GET'
    }
}
