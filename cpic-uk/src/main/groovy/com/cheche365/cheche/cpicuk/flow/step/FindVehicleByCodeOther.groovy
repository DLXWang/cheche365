package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * 调整品牌型号
 */
@Component
@Slf4j
class FindVehicleByCodeOther implements IStep {

    private static final _API_PATH_FIND_VEHICLE_BY_CODE_OTHER = '/ecar/ecar/findVehicleByCodeOther'

    @Override
    run(context) {
        if (context.additionalParameters.optionsSource != 'byCode') {
            //新流程选了vinno列表 或者老流程进入了vinno的step
            RESTClient client = context.client
            def args = [
                requestContentType: JSON,
                contentType       : JSON,
                path              : _API_PATH_FIND_VEHICLE_BY_CODE_OTHER,
                body              : [
                    redata: [
                        moldCharacterCode: context.selectedCarModel.moldCharacterCode
                    ]
                ]
            ]
            log.debug '请求体：\n{}', args.body

            def result = client.post args, { resp, json ->
                json
            }

            if (result && result.result?.models) {
                def models = result.result?.models
                log.debug '选择了vinNo列表的车，调整前选择车型：{}', context.selectedCarModel.name
                (models.size() > 0 && models[0].name) ? context.selectedCarModel.name = models[0].name : ''
                log.debug '选择了vinNo列表的车，调整后选择车型：{}', context.selectedCarModel.name
            }
        }
        getContinueFSRV context
    }

}
