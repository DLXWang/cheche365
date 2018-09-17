package com.cheche365.cheche.parser.client.service

import com.cheche365.cheche.core.exception.BadQuoteParameterException
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.exception.InsuranceBothNotAllowedException
import com.cheche365.cheche.core.exception.KnownReasonException
import com.cheche365.cheche.core.exception.LackOfSupplementInfoException
import com.cheche365.cheche.core.exception.ShowInsuranceChangeAdviceException
import com.cheche365.cheche.core.exception.UnsupportedFlowException
import com.cheche365.cheche.core.model.AutoType
import com.cheche365.cheche.core.model.VehicleLicense
import com.cheche365.cheche.core.service.ISuitability
import com.cheche365.cheche.core.service.IThirdPartyAutoTypeService
import com.cheche365.cheche.core.service.TDescriptive
import com.cheche365.cheche.parser.dto.AutoTypeRequestObject
import com.cheche365.cheche.parser.dto.InsuranceResponseObject
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.core.exception.BusinessException.Code.BAD_QUOTE_PARAMETER
import static com.cheche365.cheche.core.exception.BusinessException.Code.COMMON_KNOWN_REASON_ERROR
import static com.cheche365.cheche.core.exception.BusinessException.Code.INSURANCE_BOTH_NOT_ALLOWED
import static com.cheche365.cheche.core.exception.BusinessException.Code.INTERNAL_SERVICE_ERROR
import static com.cheche365.cheche.core.exception.BusinessException.Code.KNOWN_REASON_ERROR
import static com.cheche365.cheche.core.exception.BusinessException.Code.QUOTE_NEED_SUPPLY_INFO
import static com.cheche365.cheche.core.exception.BusinessException.Code.SHOW_INSURANCE_CHANGE_ADVICE
import static com.cheche365.cheche.core.exception.BusinessException.Code.UNSUPPORTED_FLOW
import static com.cheche365.cheche.core.model.QuoteSource.Enum.PLATFORM_BOTPY_11


@Slf4j
class ThirdPartyAutoTypeService implements IThirdPartyAutoTypeService, ISuitability<Map>, TDescriptive {

    private IAutoTypeFeignClient client

    ThirdPartyAutoTypeService(IAutoTypeFeignClient client) {
        this.client = client
    }


    @Override
    boolean isSuitable(Map conditions) {
        PLATFORM_BOTPY_11 != conditions.quoteSource
    }

    @Override
    String getDescription() {
        '人保车型查询接口'
    }

    @Override
    List<AutoType> getAutoTypes(VehicleLicense vehicleLicense, Map additionalParameters) {
        def requestBody = new AutoTypeRequestObject(
            vehicleLicense: vehicleLicense,
            additionalParameters: additionalParameters,
        )
        log.debug '客户端车型查询请求参数：{}', requestBody

        try {
            client.getAutoTypes(requestBody).with { result ->
                log.debug "客户端查询车型结果：{}", result
                if (0 == result.code) {
                    log.info '客户端查询车型成功'
                    result.autoTypes
                } else {
                    log.error '客户端查询车型失败'
                    handleException result
                }
            }
        } catch (e) {
            log.error '车型列表远端服务调用异常', e
            throw new BusinessException(INTERNAL_SERVICE_ERROR, '车型列表远端服务调用异常')
        }

    }

    private static handleException(InsuranceResponseObject result) {
        if (INTERNAL_SERVICE_ERROR.codeValue == result.code) {
            throw new BusinessException(INTERNAL_SERVICE_ERROR, result.message as String)
        } else if (SHOW_INSURANCE_CHANGE_ADVICE.codeValue == result.code) {
            throw new ShowInsuranceChangeAdviceException(result.message, result.errorData)
        } else if (BAD_QUOTE_PARAMETER.codeValue == result.code) {
            throw new BadQuoteParameterException(result.message, result.errorData)
        } else if (INSURANCE_BOTH_NOT_ALLOWED.codeValue == result.code) {
            throw new InsuranceBothNotAllowedException(result.message, result.errorData)
        } else if (QUOTE_NEED_SUPPLY_INFO.codeValue == result.code) {
            throw new LackOfSupplementInfoException(result.message, result.errorData)
        } else if (COMMON_KNOWN_REASON_ERROR.codeValue == result.code) {
            throw new KnownReasonException(KNOWN_REASON_ERROR, result.message)
        } else if (UNSUPPORTED_FLOW.codeValue == result.code) {
            throw new UnsupportedFlowException(result.message)
        } else {
            throw new BusinessException(INTERNAL_SERVICE_ERROR, "失败, code: ${result.code}, message: ${result.message}")
        }
    }

}
