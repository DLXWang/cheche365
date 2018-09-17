package com.cheche365.cheche.picc.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT1
import static com.cheche365.cheche.parser.util.BusinessUtils.htmlParser
import static com.cheche365.cheche.parser.util.BusinessUtils.setCompulsoryInsurancePeriodTexts
import static com.cheche365.cheche.picc.util.BusinessUtils.getNextDays4Commercial
import static groovyx.net.http.ContentType.BINARY
import static groovyx.net.http.ContentType.URLENC



/**
 * 重新获取续保标志，核保时以此步骤获取的isRenewal为准
 * @author taicw
 */
@Component
@Slf4j
class LoadCalculateInfo implements IStep {
    private static final _URL_LOAD_CALCULATE_INFO = '/newecar/proposal/loadCalculateInfo'
    private static final _LOAD_CALCULATE_INFO_NAMES = [
        'isRenewal',
        'initStartTimeCI'
    ]

    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            requestContentType: URLENC,
            contentType       : BINARY,
            path              : _URL_LOAD_CALCULATE_INFO,
            body              : [
                uniqueID_sub : context.uniqueID,
                pageStep_sub : 'carInfo'
            ]
        ]

        def result = client.post args, { resp, stream ->
            def inputs = htmlParser.parse(stream).depthFirst().INPUT

            inputs.findResults { input ->
                if (input.@id in _LOAD_CALCULATE_INFO_NAMES) {
                    [(input.@id): input.@value]
                }
            }.sum()
        }
        if (result) {
            context << result
            if (result && context.isRenewal != result.isRenewal) {
                log.info '重新获取续保标志isRenewal：{}', result.isRenewal
            } else if (result.initStartTimeCI) {
                setCompulsoryInsurancePeriodTexts context, result.initStartTimeCI, _DATETIME_FORMAT1, getNextDays4Commercial(context)
            }
        }

        getContinueFSRV result
    }
}
