package com.cheche365.cheche.picc.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

/**
 * 以补充纳税的方式交强险
 */

@Component
@Slf4j
class CalculateForBZTaxTypeB extends ACalculateForBZTaxTypeSupport {

    @Override
    protected isQuoting() {
        true
    }

    @Override
    protected getPayloadFlag() {
        false
    }

    @Override
    protected getTaxType() {
        _BZ_TAX_TYPE_B
    }

}
