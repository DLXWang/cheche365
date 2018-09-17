package com.cheche365.cheche.pinganuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getHtmlParser
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.Method.POST

/**
 * 车型查询基类
 * Created by wangxiaofei on 2016.8.29
 */
@Component
@Slf4j
abstract class AVehicleTypeInfoQuery implements IStep {

    private static final _API_PATH_CIRC_VEHICLE_INFO_QUERY = '/icore_pnbs/do/app/quotation/circVehicleTypeInfoQuery'

    @Override
    run(context) {

        RESTClient client = context.client

        client.request(POST, TEXT) { req ->
            uri.path = _API_PATH_CIRC_VEHICLE_INFO_QUERY
            body = getRequestParams(context)

            response.success = { resp, reader ->
                def result = new JsonSlurper().parse reader
                def selectedCarModel = result.circBrandDTOProcess.values().first().circBrandDTO // TODO 此处认为只返回一辆车
                if (selectedCarModel) {
                    context.selectedCarModel = selectedCarModel
                    context.circVehicleTypeInfo = result.circVehicleTypeInfo
                    log.info '车型查询结果，{}', selectedCarModel
                    getContinueFSRV result
                } else {
                    getFatalErrorFSRV '车型查询失败'
                }
            }

            response.failure = { resp, reader ->
                def errorMsg = htmlParser.parse(reader).depthFirst().ERRORMSG.first().text()
                log.error '车型查询，状态码：{}，错误信息：{}', resp.status, errorMsg ?: '非业务异常'
                getFatalErrorFSRV "车型查询失败：${errorMsg ?: '非业务异常'}"
            }
        }
    }

    protected abstract getRequestParams(context)

}
