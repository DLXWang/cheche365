package com.cheche365.cheche.bihu.service

import com.cheche365.cheche.bihu.model.InsureObject
import com.cheche365.cheche.bihu.model.InsureResult
import com.cheche365.cheche.bihu.model.OrderObject
import com.cheche365.cheche.bihu.model.OrderResult
import com.cheche365.cheche.bihu.model.QuoteObject
import com.cheche365.cheche.bihu.model.QuoteResult
import com.cheche365.cheche.parser.service.AFunctionalGeneralService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

import static com.cheche365.cheche.bihu.Constants._INSURANCE_COMPANY_MAPPING

/**
 * 壁虎函数式接口服务实现
 */
@Service
@Slf4j
class BihuQuoteService
    extends AFunctionalGeneralService<
        QuoteObject,
        QuoteResult,
        InsureObject,
        InsureResult,
        OrderObject,
        OrderResult> {

    private bihuService

    BihuQuoteService(@Qualifier('bihuService') bihuService) {
        this.bihuService = bihuService
    }


    @Override
    QuoteResult quote(QuoteObject quoteObject) {
        def additionalParameters = quoteObject.additionalParameters
        // 参数中的additionalParameters.insuranceCompanyCodes不应该为空
        bihuService.quote quoteObject.quoteRecord, additionalParameters

        def results = _INSURANCE_COMPANY_MAPPING.collectEntries { ic, qr ->
            [
                (ic): [
                    code   : additionalParameters[ic].code,
                    message: additionalParameters[ic].message,
                    result : additionalParameters[ic].result,
                ]
            ]
        }
        new QuoteResult(results: results)
    }

    @Override
    protected createContext(object) {
        [:]
    }

}
