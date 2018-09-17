package com.cheche365.cheche.gshell.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import java.time.LocalDate

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants.get_DATETIME_FORMAT3
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 上传身份证照片
 */
@Component
@Slf4j
class DoSaveNew implements IStep {

    private static final _API_PATH_WEB_CHECK_SAVE = '/web/doSaveNewWc'

    @Override
    run(context) {
        RESTClient client = context.client
        def data = context.data
        def now = LocalDate.now()
        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _API_PATH_WEB_CHECK_SAVE,
            body              : [
                samCode         : getEnvProperty(context, 'gshell.samCode'),
                pingtai         : getEnvProperty(context, 'gshell.pingtai_pingan'),
                name            : data?.name ?: context.additionalParameters.owner,
                nation          : data?.nation ?: '汉',
                cardnumber      : data?.cardNumber ?: context.additionalParameters.identity,
                address         : data?.address ?: '北京市东城区美术馆后街大取灯胡同2号院',
                issuingauthority: data?.issuingauthority ?: '北京市东城区美术馆后街',
                validstartdate  : data?.startDate ?: '2018-05-05',
                validenddate    : data?.endDate ?: '2028-05-05'
            ]
        ]
        def result = client.post args, { resp, json ->
            json
        }

        if (result?.status) {
            log.info '身份证采集成功'
            //TODO:后期可能需要返回身份证信息
            context.newAcquisition = [result : true]
            getContinueFSRV result.msg
        } else {
            getFatalErrorFSRV '身份证采集失败'
        }
    }

}
