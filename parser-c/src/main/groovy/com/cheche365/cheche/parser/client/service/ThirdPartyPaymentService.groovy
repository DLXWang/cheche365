package com.cheche365.cheche.parser.client.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.service.ISuitability
import com.cheche365.cheche.core.service.IThirdPartyPaymentService
import com.cheche365.cheche.parser.dto.RequestObjectForList
import com.cheche365.cheche.parser.dto.RequestObjectForMap
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.core.exception.BusinessException.Code.INTERNAL_SERVICE_ERROR
import static com.cheche365.cheche.parser.client.utils.BusinessUtils.handlerExceptionResult



/**
 * 支付相关接口客户端
 */
@Slf4j
class ThirdPartyPaymentService implements ISuitability<Map>, IThirdPartyPaymentService {

    private IThirdPartyPaymentFeignClient client
    private Closure checkSuitability

    ThirdPartyPaymentService(IThirdPartyPaymentFeignClient client, Closure checkSuitability) {
        this.client = client
        this.checkSuitability = checkSuitability
    }

    @Override
    def getPaymentChannels(Map applyPolicyNos, Map<String, Object> additionalParameters) {
        def requestBody = new RequestObjectForMap(
            requestMap: applyPolicyNos,
            additionalParameters: additionalParameters
        )
        log.info '客户端请求支付渠道查询参数：{}', requestBody

        try {
            client.getPaymentChannels(requestBody).with { result ->
                log.debug "客户端获取支付渠道：{}", result
                if (0 == result.code) {
                    result.resultInfos
                } else {
                    log.info '获取支付渠道失败'
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
    def getPaymentInfo(Map applyPolicyNos, Map<String, Object> additionalParameters) {
        def requestBody = new RequestObjectForMap(
            requestMap: applyPolicyNos,
            additionalParameters: (Map) additionalParameters.clone().with { additionalParams ->
                additionalParams.quoteRecord = ((QuoteRecord) additionalParams.quoteRecord).clone().with {
                    insuranceCompany = new InsuranceCompany(
                        id: insuranceCompany.id,
                        code: insuranceCompany.code,
                        name: insuranceCompany.name)
                    channel = new Channel(
                        id: channel.id,
                        name: channel.name,
                        apiPartner: channel.apiPartner
                    )
                    quote = null
                    applicant = new User(name: applicant.name)
                    it
                }
                additionalParams
            },
        )
        log.info '客户端请求支付信息参数：{}', requestBody

        try {
            client.getPaymentInfo(requestBody).with { result ->
                log.debug "客户端获取支付信息：{}", result
                if (0 == result.code) {
                    result.resultInfos
                } else {
                    log.info '获取支付信息失败'
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
    def checkPaymentState(List paymentInfo, Map<String, Object> additionalParameters) {
        log.debug 'paymentInfo:{}', paymentInfo
        log.debug 'additionalParameters:{}', additionalParameters
        def requestBody = new RequestObjectForList(
            requestList: paymentInfo,
            additionalParameters: (Map) additionalParameters.clone().with { additionalParams ->
                additionalParams.quoteRecord = ((QuoteRecord) additionalParams.quoteRecord).clone().with {
                    insuranceCompany = new InsuranceCompany(
                        id: insuranceCompany.id,
                        code: insuranceCompany.code,
                        name: insuranceCompany.name)
                    channel = new Channel(
                        id: channel.id,
                        name: channel.name,
                        apiPartner: channel.apiPartner
                    )
                    quote = null
                    applicant = new User(name: applicant.name)
                    it
                }
                additionalParams
            },
        )
        log.info '客户端请求支付状态查询参数：{}', requestBody

        try {
            client.checkPaymentState(requestBody).with { result ->
                log.debug "客户端获取支付状态：{}", result
                if (0 == result.code) {
                    result.resultInfos
                } else {
                    log.info '获取支付状态失败'
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
    def cancelPay(Map applyPolicyNos, Map<String, Object> additionalParameters) {
        def requestBody = new RequestObjectForMap(
            requestMap: applyPolicyNos,
            additionalParameters: additionalParameters
        )
        log.info '客户端请求取消支付：{}', requestBody

        try {
            client.cancelPayment(requestBody).with { result ->
                log.debug "客户端取消支付：{}", result
                if (0 == result.code) {
                    result.resultInfos
                } else {
                    log.info '取消支付失败'
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
