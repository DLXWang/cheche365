package com.cheche365.cheche.parser.ms.controller

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.service.IThirdPartyPaymentService
import com.cheche365.cheche.parser.dto.RequestObjectForList
import com.cheche365.cheche.parser.dto.RequestObjectForMap
import com.cheche365.cheche.parser.dto.ResponseObjectForList
import com.cheche365.cheche.parser.dto.ResponseObjectForMap
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController



/**
 * 第三方保险服务支付相关REST接口
 */
@RestController
@RequestMapping('/insurancePlatform')
@Slf4j
class ThirdPartyPaymentResource {

    @Autowired(required = false)
    private IThirdPartyPaymentService service


    @PostMapping('/paymentChannels')
    @ResponseBody
    def getPaymentChannels(@RequestBody RequestObjectForMap body) {

        log.info '根据：{} 获取支付渠道信息', body
        def applyPolicyNos = body.requestMap
        def additionalParams = body.additionalParameters

        try {
            def paymentChannels = service.getPaymentChannels(applyPolicyNos, additionalParams)
            log.info '获取支付渠道信息成功'
            new ResponseObjectForMap(
                code: 0,
                resultInfos: (Map) paymentChannels
            ).with { respObj ->
                log.info '{}等获取支付渠道信息成功：{}', applyPolicyNos.entrySet().first().value, respObj
                respObj
            }
        } catch (ex) {
            if (ex instanceof BusinessException) {
                log.warn '获取支付渠道信息返回业务异常：', ex
                new ResponseObjectForMap(
                    code: ex.code.codeValue,
                    errorData: ex.errorObject,
                    message: ex.message
                )
            } else {
                log.error '获取支付渠道信息返回非业务异常：', ex
                new ResponseObjectForMap(
                    code: -1,
                    message: ex.message
                )
            }
        }
    }


    @PostMapping(value = '/paymentInfo')
    @ResponseBody
    def getPaymentInfo(@RequestBody RequestObjectForMap body) {

        log.info '根据：{} 获取支付信息', body
        def applyPolicyNos = body.requestMap
        def additionalParams = body.additionalParameters

        try {
            def paymentInfo = service.getPaymentInfo(applyPolicyNos, additionalParams)
            log.info '获取支付信息成功'
            new ResponseObjectForMap(
                code: 0,
                resultInfos: (Map) paymentInfo
            ).with { respObj ->
                log.info '{}等获取支付信息成功：{}', applyPolicyNos.entrySet().first().value, respObj
                respObj
            }
        } catch (ex) {
            log.error '获取支付信息失败', ex
            if (ex instanceof BusinessException) {
                new ResponseObjectForMap(
                    code: ex.code.codeValue,
                    errorData: ex.errorObject,
                    message: ex.message
                )
            } else {
                new ResponseObjectForMap(
                    code: -1,
                    message: ex.message
                )
            }
        }

    }

    @PostMapping(value = '/paymentState')
    @ResponseBody
    def checkPaymentState(@RequestBody RequestObjectForList body) {

        log.info '根据：{} 检查支付状态', body
        def paymentInfo = body.requestList
        def additionalParams = body.additionalParameters

        try {
            def paymentState = service.checkPaymentState(paymentInfo, additionalParams)
            log.info '获取支付状态成功'
            new ResponseObjectForList(
                code: 0,
                resultInfos: (List) paymentState
            ).with { respObj ->
                log.info '获取支付状态成功：{}', respObj
                respObj
            }
        } catch (ex) {
            log.error '获取支付状态失败', ex
            if (ex instanceof BusinessException) {
                new ResponseObjectForList(
                    code: ex.code.codeValue,
                    errorData: ex.errorObject,
                    message: ex.message
                )
            } else {
                new ResponseObjectForList(
                    code: -1,
                    message: ex.message
                )
            }
        }

    }

    @PostMapping(value = '/cancelPayment')
    @ResponseBody
    def cancelPay(@RequestBody RequestObjectForMap body) {

        log.info '根据：{} 取消支付', body
        def applyPolicyNos = body.requestMap
        def additionalParams = body.additionalParameters

        try {
            def payInfoCanceled = service.cancelPay(applyPolicyNos, additionalParams)
            log.info '取消支付成功'
            new ResponseObjectForMap(
                code: 0,
                resultInfos: (Map) payInfoCanceled
            ).with { respObj ->
                log.info '获取支付状态成功：{}', respObj
                respObj
            }
        } catch (ex) {
            log.error '取消支付失败', ex
            if (ex instanceof BusinessException) {
                new ResponseObjectForMap(
                    code: ex.code.codeValue,
                    errorData: ex.errorObject,
                    message: ex.message
                )
            } else {
                new ResponseObjectForMap(
                    code: -1,
                    message: ex.message
                )
            }
        }

    }

}
