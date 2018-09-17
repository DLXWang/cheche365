package com.cheche365.cheche.cpic.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.htmlParser
import static groovyx.net.http.ContentType.BINARY
import static groovyx.net.http.ContentType.URLENC

/**
 * 获取基础信息
 * Created by liheng on 2017/3/20 020.
 */
@Component
@Slf4j
class InitVehicleBaseInfo implements IStep {

    private static final _URL_PATH_INIT_VEHICLE_BASE_INFO = 'cpiccar/salesNew/businessCollect/initVehicleBaseInfo'

    @Override
    run(context) {
        RESTClient client = context.client
        def args = [
            requestContentType: URLENC,
            contentType       : BINARY,
            path              : _URL_PATH_INIT_VEHICLE_BASE_INFO
        ]

        def initVehicleBaseInfo = client.get args, { resp, stream ->
            def inputs = htmlParser.parse(stream).depthFirst().INPUT

            inputs.findResult { input ->
                if ('tbRandom' == input.@id) {
                    [(input.@id): input.@value]
                }
            }
        }

        if (initVehicleBaseInfo) {
            context.initVehicleBaseInfo = initVehicleBaseInfo
            log.info '获取tbRandom成功：{}', initVehicleBaseInfo.tbRandom
            getContinueFSRV initVehicleBaseInfo
        } else {
            getFatalErrorFSRV '获取tbRandom失败'
        }
    }

}
