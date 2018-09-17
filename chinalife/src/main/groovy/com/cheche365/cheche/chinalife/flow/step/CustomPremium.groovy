package com.cheche365.cheche.chinalife.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.chinalife.util.BusinessUtils.getCustomPremiumParams
import static com.cheche365.cheche.chinalife.util.BusinessUtils.getQuoteKindItems
import static com.cheche365.cheche.chinalife.util.BusinessUtils.populateQuoteRecord
import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants._ERROR_MESSAGE_COMMERCIAL_PERIOD_CHECK_FAILURE
import static com.cheche365.cheche.parser.Constants._FLOW_PARTICIPANT_MESSAGE_2
import static com.cheche365.cheche.parser.util.FlowUtils.getInsurancesNotAllowedFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * Created by suyq on 2015/9/14.
 * 计算套餐
 */
@Component
@Slf4j
class CustomPremium implements IStep {
    private static final _URL_CUSTOM_PREMIUM = '/online/saleNewCar/carProposalgetCustomPremium.do'

    @Override
    run(Object context) {
        context.flowParticipant?.sendMessage _FLOW_PARTICIPANT_MESSAGE_2
        def client = context.client

        //获取计算套餐的参数
        def customPremiumParams = getCustomPremiumParams context
        log.info '报价套餐请求参数：{}', customPremiumParams

        def args = [
            contentType: JSON,
            path       : _URL_CUSTOM_PREMIUM,
            body       : generateRequestParameters(context, this) + customPremiumParams
        ]

        def result = client.post args, { resp, json ->
            json
        }

        def quoteKindItems = getQuoteKindItems result.temporary.quoteMain.geQuoteItemkinds
        quoteKindItems.premium = result.temporary.quoteMain.geQuoteRisks[0].sumPremium //总保费
        log.debug '原始quoteKindItems：{}', result
        log.info '套餐报价结果quoteKindItems：{}', quoteKindItems

        if (quoteKindItems.premium) {
            def quoteRecord = populateQuoteRecord context, quoteKindItems
            log.info '组装后的新QuoteRecord：{}', quoteRecord
            getContinueFSRV quoteRecord
        } else {
            if (result.temporary?.resultInfoDesc?.contains('重复投保')) {
                getInsurancesNotAllowedFSRV _ERROR_MESSAGE_COMMERCIAL_PERIOD_CHECK_FAILURE
            } else {
                getFatalErrorFSRV '计算套餐失败'
            }
        }
    }

}
