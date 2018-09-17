package com.cheche365.cheche.parser.ms.controller

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.service.IThirdPartyQuoteRecordService
import com.cheche365.cheche.parser.dto.RequestObjectForList
import com.cheche365.cheche.parser.dto.ResponseObjectForMap
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController



/**
 * 第三方保险报价记录REST接口
 */
@RestController
@RequestMapping('/insurancePlatform')
@Slf4j
class ThirdPartyQuoteRecordResource {

    @Autowired(required = false)
    private IThirdPartyQuoteRecordService service


    @PostMapping('/quoteRecordState')
    @ResponseBody
    def getQuoteRecordState(@RequestBody RequestObjectForList body) {

        log.info '根据：{} 获取报价单状态', body
        def numbers = body.requestList
        def additionalParams = body.additionalParameters

        try {
            def quoteRecordState = service.getQuoteRecordState(numbers, additionalParams)
            log.info '获取报价单状态成功'
            new ResponseObjectForMap(
                code: 0,
                resultInfos: (Map)quoteRecordState
            ).with { respObj ->
                log.info '获取报价单状态成功：{}', respObj
                respObj
            }
        } catch (ex) {
            if (ex instanceof BusinessException) {
                log.warn '获取报价单状态返回业务异常：', ex
                new ResponseObjectForMap(
                    code: ex.code.codeValue,
                    errorData: ex.errorObject,
                    message: ex.message
                )
            } else {
                log.error '获取报价单状态返回非业务异常：', ex
                new ResponseObjectForMap(
                    code: -1,
                    message: ex.message
                )
            }
        }
    }

}
