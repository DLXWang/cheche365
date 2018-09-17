package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.model.QuoteRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ThirdPartyPaymentTemplateFactory {

    @Autowired(required = false)
    List<ThirdPartyPaymentTemplate> templates

    ThirdPartyPaymentTemplate getTemplate(QuoteRecord quoteRecord) {
        templates.find { it.acceptable(quoteRecord) }
    }

}
