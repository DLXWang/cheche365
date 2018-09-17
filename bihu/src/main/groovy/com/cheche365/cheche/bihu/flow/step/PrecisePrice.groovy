package com.cheche365.cheche.bihu.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.bihu.flow.Constants._QUOTING_WAITING_SECONDS
import static com.cheche365.cheche.bihu.util.BusinessUtils.sendAndReceive
import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV

/**
 * 获取报价
 */
@Component
@Slf4j
class PrecisePrice implements IStep {

    private static final _API_PATH_PRECISE_PRICE = '/api/CarInsurance/PostPrecisePrice'

    @Override
    run(context) {
        def queryBody = generateRequestParameters context, this

        def result = sendAndReceive context, _API_PATH_PRECISE_PRICE, queryBody, this.class.name

        if (1 == result.BusinessStatus) {
            log.debug '发送报价请求成功，等待{}秒后获取报价结果', _QUOTING_WAITING_SECONDS
            sleep(_QUOTING_WAITING_SECONDS * 1000L)
            getContinueFSRV result
        } else {
            log.error "壁虎请求报价异常状态码：{}，详细信息：{}", result.BusinessStatus, result.Item.QuoteResult ?: result.StatusMessage
            getFatalErrorFSRV result.StatusMessage
        }
    }

}
