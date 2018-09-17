package com.cheche365.cheche.parser.client.utils

import com.cheche365.cheche.core.exception.BadQuoteParameterException
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.exception.InsuranceBothNotAllowedException
import com.cheche365.cheche.core.exception.KnownReasonException
import com.cheche365.cheche.core.exception.LackOfSupplementInfoException
import com.cheche365.cheche.core.exception.ShowInsuranceChangeAdviceException
import com.cheche365.cheche.core.exception.UnsupportedFlowException
import com.cheche365.cheche.parser.dto.InsuranceResponseObject

import static com.cheche365.cheche.core.exception.BusinessException.Code.BAD_QUOTE_PARAMETER
import static com.cheche365.cheche.core.exception.BusinessException.Code.COMMON_KNOWN_REASON_ERROR
import static com.cheche365.cheche.core.exception.BusinessException.Code.INSURANCE_BOTH_NOT_ALLOWED
import static com.cheche365.cheche.core.exception.BusinessException.Code.INTERNAL_SERVICE_ERROR
import static com.cheche365.cheche.core.exception.BusinessException.Code.INTERNAL_SERVICE_ERROR
import static com.cheche365.cheche.core.exception.BusinessException.Code.INTERNAL_SERVICE_ERROR
import static com.cheche365.cheche.core.exception.BusinessException.Code.KNOWN_REASON_ERROR
import static com.cheche365.cheche.core.exception.BusinessException.Code.QUOTE_NEED_SUPPLY_INFO
import static com.cheche365.cheche.core.exception.BusinessException.Code.SHOW_INSURANCE_CHANGE_ADVICE
import static com.cheche365.cheche.core.exception.BusinessException.Code.UNSUPPORTED_FLOW



class BusinessUtils {

    static handlerExceptionResult(InsuranceResponseObject result) {

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
