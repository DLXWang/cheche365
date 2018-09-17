package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static groovyx.net.http.ContentType.JSON
@Component
@Slf4j
class QueryQuickOfferByPlate implements IStep {

    private static final _API_PATH_QUERY_QUICK_OFFER_BY_PLATE = '/ecar/quickoffer/queryQuickOfferByPlate'
    @Override
    Object run(Object context) {
        RESTClient client = context.client
        def auto  = context.auto
        def queryResult = client.request(Method.POST) { req ->
            requestContentType = JSON
            contentType = JSON
            uri.path = _API_PATH_QUERY_QUICK_OFFER_BY_PLATE
            body = [
                meta:[:],
                redata: [
                    ecarvo   :[
                        plateColor: '1',
                        carVIN           : '',
                        plateNo        :auto.licensePlateNo,
                    ]
                ]
            ]
            response.success = { resp, json ->
                json
            }
            response.failure = { resp, json ->
                json
            }
        }
        context.renewable = false
        //过户车的话不用续保信息报价
        if (queryResult && queryResult.message?.code == 'success' && !context?.additionalParameters?.supplementInfo?.transferDate){
            log.debug '续保查询车信息：{}' + queryResult.result?.ecarvo?:''
            log.debug '续保交强险信息：{}' + queryResult.result?.compulsoryInsuransVo?:''
            log.debug '续保商业险信息：{}' + queryResult.result?.commercialInsuransVo?:''
            context.vehicleInfo = queryResult.result.ecarvo
            def vehicleInfo = context.vehicleInfo
            context.renewable = true
            //第二次续保车报价的时候（既：重新选车） web会把选的车带回来  这时候用新选的车报价
            def autoModel = (context.additionalParameters.supplementInfo.autoModel)?: vehicleInfo.moldCharacterCode
            context.additionalParameters.supplementInfo.autoModel = autoModel
        }
        getContinueFSRV context
    }

}
