package com.cheche365.cheche.parser.ms.controller

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.parser.dto.InsuringRequestObject
import com.cheche365.cheche.parser.dto.InsuringResponseObject
import com.cheche365.cheche.parser.dto.QuotingRequestObject
import com.cheche365.cheche.parser.dto.QuotingResponseObject
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

import static com.cheche365.cheche.common.Constants._DATE_FORMAT3



/**
 * 第三方保险服务REST接口
 */
@RestController
@RequestMapping('/insurancePlatform')
@Slf4j
class ThirdPartyBusinessResource {

    @Autowired(required = false)
    private IThirdPartyHandlerService service


    @PostMapping('/quotes')
    @ResponseBody
    def quote(@RequestBody QuotingRequestObject body) {

        log.info '根据：{} 获取报价信息', body
        def quoteRecord = body.quoteRecord
        def additionalParams = body.additionalParameters
        log.info '更改过的additionalParams：{}', additionalParams
        
        try {
            service.quote(quoteRecord, additionalParams)
            new QuotingResponseObject(
                code: 0,
                quoteRecord: quoteRecord,
                additionalParameters: additionalParams
            ).with { respObj ->
                log.info '{}报价成功：{}', quoteRecord.auto.licensePlateNo, respObj
                respObj
            }
        } catch (ex) {
            if (ex instanceof BusinessException) {
                log.warn '报价返回业务异常：', ex
                new QuotingResponseObject(
                    code: ex.code.codeValue,
                    additionalParameters: additionalParams,
                    errorData: ex.errorObject,
                    message: ex.message
                ).with { respObj ->
                    log.info '{}业务异常报价失败：{}', quoteRecord.auto.licensePlateNo, respObj
                    respObj
                }
            } else {
                log.error '报价返回非业务异常：', ex
                new QuotingResponseObject(
                    code: -1,
                    additionalParameters: additionalParams,
                    message: ex.message
                ).with { respObj ->
                    log.info '{}非业务异常报价失败：{}', quoteRecord.auto.licensePlateNo, respObj
                    respObj
                }
            }
        }
    }


    @PostMapping(value = '/insurances')
    @ResponseBody
    def insure(@RequestBody InsuringRequestObject body) {

        log.info '根据：{} 获取核保信息', body
        def order = body.order
        def additionalParams = body.additionalParameters
        log.info '更改过的additionalParams：{}', additionalParams

        def quoteRecord = body.quoteRecord
        def insurance = body.insurance ? body.insurance.with {
            it.quoteRecord = quoteRecord
            it
        } : null
        def compulsoryInsurance = body.compulsoryInsurance ? body.compulsoryInsurance.with {
            it.quoteRecord = quoteRecord
            it
        } : null

        try {
            service.insure(order, insurance, compulsoryInsurance, additionalParams)
            log.info '核保成功'
            new InsuringResponseObject(
                code: 0,
                order: order,
                insurance: insurance,
                compulsoryInsurance: compulsoryInsurance,
                additionalParameters: additionalParams
            ).with { respObj ->
                log.info '{}核保成功：{}', quoteRecord.auto.licensePlateNo, respObj
                respObj
            }
        } catch (ex) {
            log.error '核保失败', ex
            if (ex instanceof BusinessException) {
                new InsuringResponseObject(
                    additionalParameters: additionalParams,
                    code: ex.code.codeValue,
                    errorData: ex.errorObject,
                    message: ex.message
                ).with { respObj ->
                    log.info '{}业务异常核保失败：{}', quoteRecord.auto.licensePlateNo, respObj
                    respObj
                }
            } else {
                new InsuringResponseObject(
                    additionalParameters: additionalParams,
                    code: -1,
                    message: ex.message
                ).with { respObj ->
                    log.info '{}非业务异常核保失败：{}', quoteRecord.auto.licensePlateNo, respObj
                    respObj
                }
            }
        }

    }

    @PostMapping(value = '/orders')
    @ResponseBody
    def order(@RequestParam Map fromParam) {
        [:]
    }

}
