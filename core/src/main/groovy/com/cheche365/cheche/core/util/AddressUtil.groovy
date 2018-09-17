package com.cheche365.cheche.core.util

import com.cheche365.cheche.core.model.Area
import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.ClientResponse
import com.sun.jersey.api.representation.Form
import com.sun.jersey.client.apache4.ApacheHttpClient4Handler
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.apache.commons.lang.exception.ExceptionUtils
import org.apache.commons.lang3.StringUtils
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.HttpClients

import javax.ws.rs.core.MediaType

import static com.cheche365.cheche.core.model.Area.Enum.ACTIVE_AREAS

/**
 * Created by wangyh on 2015/9/2.
 */
@Slf4j
class AddressUtil {

    static Area convertByAreaId(String cityCode) {
        if (StringUtils.isBlank(cityCode)) {
            return null
        }

        List<Area> areas = ACTIVE_AREAS.findAll { (it.cityCode as String) == cityCode }
        return areas.isEmpty() ? areas.get(0) : null
    }

    /**
     * 获取地址
     */
    static String ip2Location(String ip) {

        String url = "http://api.map.baidu.com/location/ip?ak=kyL9Mp2jKvTBxFWENHq6fp78&coor=bd09ll&ip=" + ip
        try {

            Client client = new Client(new ApacheHttpClient4Handler(
                HttpClients.createDefault(), new BasicCookieStore(), true)
            )

            String response = client.resource(url).type(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class).getEntity(String.class)

            new JsonSlurper().parseText(response).with {
                it.content?.address_detail?.city_code
            }

        } catch (IOException e) {
            log.error("访问百度ip服务获取归属地信息出错,exception:{}", ExceptionUtils.getStackTrace(e))
            return null
        }
    }

    /**
     * 获取地址,ip为海外地址时百度服务无法识别报错
     */
    static String ip2LocationTaoBao(String ip) {

        String url = "http://ip.taobao.com/service/getIpInfo2.php"
        try {

            Client client = new Client(new ApacheHttpClient4Handler(
                HttpClients.createDefault(), new BasicCookieStore(), true)
            )

            Form form = new Form()
            form.add("ip", ip)

            String response = client.resource(url).type(MediaType.APPLICATION_FORM_URLENCODED).post(
                ClientResponse.class, form
            ).getEntity(String.class)

            new JsonSlurper().parseText(response).with {
                it.data?.country_id
            }
        } catch (IOException e) {
            log.error("访问淘宝ip服务获取归属地信息出错,exception:{}", ExceptionUtils.getStackTrace(e))
            return null
        }
    }
}
