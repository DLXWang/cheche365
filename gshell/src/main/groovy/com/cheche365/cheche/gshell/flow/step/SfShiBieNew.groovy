package com.cheche365.cheche.gshell.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.Method.POST
import static org.apache.http.entity.mime.MultipartEntityBuilder.create as createMultiEntityBuilder



/**
 * 上传身份证照片
 */
@Component
@Slf4j
class SfShiBieNew implements IStep {

    private static final _API_PATH_SF_SHIBIE_NEW = '/uploadP/sfShiBieNew'

    @Override
    run(context) {
        RESTClient client = context.client
        def result = client.request(POST) { req ->
            uri.path = _API_PATH_SF_SHIBIE_NEW
            requestContentType = URLENC
            contentType = JSON
            req.entity = createMultiEntityBuilder().addTextBody('imgUrls', context.imgIdFaceUrl).addTextBody('imgUrls', context.imgIdBackUrl).build()

            response.success = { resp, json ->
                json
            }

            response.failure = { resp ->

            }
        }

        if (result?.status) {
            log.info '成功采集身份证信息'
            context.data = result.data
            getContinueFSRV result
        } else {
            getLoopContinueFSRV result, '请上传清晰的身份证！'
        }
    }

}
