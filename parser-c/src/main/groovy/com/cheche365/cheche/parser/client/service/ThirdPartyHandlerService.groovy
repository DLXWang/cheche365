package com.cheche365.cheche.parser.client.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Address
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.service.ISuitability
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.parser.dto.InsuringRequestObject
import com.cheche365.cheche.parser.dto.QuotingRequestObject
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.core.exception.BusinessException.Code.INTERNAL_SERVICE_ERROR
import static com.cheche365.cheche.parser.Constants._COMPULSORY_INSURANCE_PROPERTIES
import static com.cheche365.cheche.parser.Constants._INSURANCE_PROPERTIES
import static com.cheche365.cheche.parser.Constants._QUOTE_RECORD_PROPERTIES
import static com.cheche365.cheche.parser.Constants.updateBusinessObjects
import static com.cheche365.cheche.parser.client.utils.BusinessUtils.handlerExceptionResult



/**
 * cpic2b服务代理
 */
@Slf4j
class ThirdPartyHandlerService implements ISuitability<Map>, IThirdPartyHandlerService {

    private IThirdPartyHandlerFeignClient client
    private Closure checkSuitability

    ThirdPartyHandlerService(IThirdPartyHandlerFeignClient client, Closure checkSuitability) {
        this.client = client
        this.checkSuitability = checkSuitability
    }

    @Override
    void quote(QuoteRecord quoteRecord, Map<String, Object> additionalParameters) {
        def requestBody = new QuotingRequestObject(
            quoteRecord: quoteRecord.clone().with {
                insuranceCompany = new InsuranceCompany(
                    id: insuranceCompany.id,
                    code: insuranceCompany.code,
                    name: insuranceCompany.name
                )
                channel = new Channel(
                    id: channel.id,
                    name: channel.name,
                    apiPartner: channel.apiPartner
                )
                quote = null
                applicant = new User(id: applicant.id, name: applicant.name)
                it
            },
            additionalParameters: (Map) additionalParameters.clone().with { additionalParams ->
                additionalParams.remove 'quote_limit_checker'
                additionalParams.supportCompanies = additionalParams.supportCompanies?.collect { insuranceCompany ->
                    new InsuranceCompany(
                        id: insuranceCompany.id,
                        code: insuranceCompany.code,
                        name: insuranceCompany.name
                    )
                }
                additionalParams.quoteCompanies = additionalParams.quoteCompanies?.collect { insuranceCompany ->
                    new InsuranceCompany(
                        id: insuranceCompany.id,
                        code: insuranceCompany.code,
                        name: insuranceCompany.name
                    )
                }
                additionalParams.referredCompanies = additionalParams.referredCompanies?.collect { insuranceCompany ->
                    new InsuranceCompany(
                        id: insuranceCompany.id,
                        code: insuranceCompany.code,
                        name: insuranceCompany.name
                    )
                }
                additionalParams
            },
        )
        log.info '客户端请求报价参数：{}', requestBody
        log.info '客户端请求报价additionalParameters参数：{}', requestBody.additionalParameters

        try {
            client.quote(requestBody).with { result ->
                log.debug "客户端获取报价结果：{}", result
                log.debug '客户端获取报价结果附加参数：{}', result.additionalParameters
                additionalParameters << result.additionalParameters
                if (0 == result.code) {
                    log.info '客户端获取报价成功'
                    mergeQuoteRecord quoteRecord, result.quoteRecord
                    log.debug '报价结果：{}', quoteRecord
                } else {
                    log.info '客户端获取报价失败，code：{}，message：{}，errorData：{}', result.code, result.message, result.errorData
                    handlerExceptionResult result
                }
            }
        } catch (ex) {
            if (ex instanceof BusinessException) {
                log.info '客户端获取报价业务异常,异常信息：code:{}, message:{}', ex.code, ex.message
                throw ex
            } else {
                log.error '远端服务调用异常', ex
                throw new BusinessException(INTERNAL_SERVICE_ERROR, '远端服务调用异常')
            }
        }


    }

    @Override
    void insure(PurchaseOrder order, Insurance insurance, CompulsoryInsurance compulsoryInsurance, Map<String, Object> additionalParameters) {
        log.debug "do insure order:{}", order
        def address = order.deliveryAddress
        log.debug "do insure order address.street={},address.province={},address.city={}", address.street, address.province, address.city
        def requestBody = new InsuringRequestObject(
            order: new PurchaseOrder(
                deliveryAddress: new Address(street: address.street, province: address.province, city: address.city),
                orderNo: order.orderNo,
                auto: order.auto,
                applicantIdentityType: order.applicantIdentityType,
                applicantIdNo: order.applicantIdNo,
                applicantName: order.applicantName,
                insuredIdentityType: order.insuredIdentityType,
                insuredIdNo: order.insuredIdNo,
                insuredName: order.insuredName,
                applicant: new User(
                    name: order.applicant.name,
                    mobile: order.applicant.mobile
                )
            ),
            quoteRecord: ((QuoteRecord) (insurance ?: compulsoryInsurance).quoteRecord).clone().with {
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
            },
            insurance: new Insurance(),
            compulsoryInsurance: new CompulsoryInsurance(),
            additionalParameters: (Map) additionalParameters.clone().with { additionalParams ->
                additionalParams.remove 'quote_limit_checker'
                additionalParams.supportCompanies = additionalParams.supportCompanies?.collect { insuranceCompany ->
                    new InsuranceCompany(
                        id: insuranceCompany.id,
                        code: insuranceCompany.code,
                        name: insuranceCompany.name
                    )
                }
                additionalParams.quoteCompanies = additionalParams.quoteCompanies?.collect { insuranceCompany ->
                    new InsuranceCompany(
                        id: insuranceCompany.id,
                        code: insuranceCompany.code,
                        name: insuranceCompany.name
                    )
                }
                additionalParams.referredCompanies = additionalParams.referredCompanies?.collect { insuranceCompany ->
                    new InsuranceCompany(
                        id: insuranceCompany.id,
                        code: insuranceCompany.code,
                        name: insuranceCompany.name
                    )
                }
                additionalParams
            },
        )

        log.info '客户端请求核保参数：{}', requestBody
        log.info '客户端请求核保additionalParameters参数：{}', requestBody.additionalParameters

        try {
            client.insure(requestBody).with { result ->
                log.debug "客户端获取核保结果：{}", result
                log.debug '客户端获取核保结果附加参数：{}', result.additionalParameters
                additionalParameters << result.additionalParameters
                if (0 == result.code) {
                    log.info '客户端获取核保成功'
                    mergeInsurances insurance, compulsoryInsurance, result.insurance, result.compulsoryInsurance
                    log.debug '核保结果：{}', result
                } else {
                    log.info '客户端获取核保失败，code：{}，message：{}，errorData：{}', result.code, result.message, result.errorData
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
    void order(PurchaseOrder order, Insurance insurance, CompulsoryInsurance compulsoryInsurance, Map<String, Object> additionalParameters) {
        throw new UnsupportedOperationException('当前服务实现不支持承保操作')
    }

    @Override
    boolean isSuitable(Map conditions) {
        checkSuitability?.call conditions
    }

    private static void mergeQuoteRecord(quoteRecord, newQuoteRecord) {
        updateBusinessObjects([quoteRecord], [newQuoteRecord], [_QUOTE_RECORD_PROPERTIES])
    }

    private static void mergeInsurances(insurance, compulsoryInsurance, newInsurance, newCompulsoryInsurance) {
        updateBusinessObjects([insurance, compulsoryInsurance], [newInsurance, newCompulsoryInsurance], [_INSURANCE_PROPERTIES, _COMPULSORY_INSURANCE_PROPERTIES])
    }

}
