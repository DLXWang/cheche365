package com.cheche365.cheche.sinosig.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getHtmlParser
import static com.cheche365.cheche.parser.util.BusinessUtils.setCommercialInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.setCompulsoryInsurancePeriodTexts
import static groovyx.net.http.ContentType.BINARY

/**
 * 检查是否可以续保
 */
@Component
@Slf4j
class CheckRenewal implements IStep {

    private static final _API_PATH_RENEWAL_NEW_INFO = 'Net/netPremiumControl!baseInfoToPremium.action'

    @Override
    run(context) {
        RESTClient client = context.client
        def args = [
            contentType: BINARY,
            path       : _API_PATH_RENEWAL_NEW_INFO,
            body       : [
                'paraMap.id' : context.token
            ]
        ]

        def inputs = client.post args, { resp, stream ->
            htmlParser.parse(stream).depthFirst().INPUT
        }

        def (isRenewal, insuApp, insuAppTra) = ['isRenewal', 'insuApp', 'insuAppTra'].collect { tagId ->
            inputs.find { input ->
                tagId == input.@id
            }.@value
        }

        context.renewable = ('1' == isRenewal)
        setCommercialInsurancePeriodTexts context, insuApp
        setCompulsoryInsurancePeriodTexts context, insuAppTra
        log.info '是否可以续保：{}', context.renewable

        getContinueFSRV context.renewable
    }
}
