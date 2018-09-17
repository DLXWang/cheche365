package com.cheche365.cheche.parser.client.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.service.ISuitability
import com.cheche365.cheche.core.service.IThirdPartyQuoteRecordService
import com.cheche365.cheche.parser.dto.RequestObjectForList
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.core.exception.BusinessException.Code.INTERNAL_SERVICE_ERROR
import static com.cheche365.cheche.parser.client.utils.BusinessUtils.handlerExceptionResult
/**
 * 报价单查询接口客户端
 */
@Slf4j
class ThirdPartyQuoteRecordService implements ISuitability<Map>, IThirdPartyQuoteRecordService {

    private IThirdPartyQuoteRecordFeignClient client
    private Closure checkSuitability

    ThirdPartyQuoteRecordService(IThirdPartyQuoteRecordFeignClient client, Closure checkSuitability) {
        this.client = client
        this.checkSuitability = checkSuitability
    }

    @Override
    def getQuoteRecordState(List numbers, Map<String, Object> additionalParameters) {
        def requestBody = new RequestObjectForList(
            requestList: numbers,
            additionalParameters: additionalParameters
        )
        log.info '客户端请求报价单状态查询参数：{}', requestBody

        try {
            client.getQuoteRecordState(requestBody).with { result ->
                log.debug "客户端获取报价单状态：{}", result
                if (0 == result.code) {
                    result.resultInfos
                } else {
                    log.info '获取报价单状态失败'
                    handlerExceptionResult result
                }
            }
        } catch (ex) {
            if (ex instanceof BusinessException) {
                throw ex
            } else {
                log.error '远端服务调用异常', ex
                throw new BusinessException(INTERNAL_SERVICE_ERROR, '远端服务调用异常')
            }
        }
    }

    @Override
    boolean isSuitable(Map conditions) {
        checkSuitability?.call conditions
    }

}
