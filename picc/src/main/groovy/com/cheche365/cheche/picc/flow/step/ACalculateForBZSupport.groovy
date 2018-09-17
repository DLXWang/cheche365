package com.cheche365.cheche.picc.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

/**
 * 获取交强险报价及核保的基类（不需要纳税方式的）
 */
@Component
@Slf4j
abstract class ACalculateForBZSupport extends ACalculateForBZ {

    @Override
    protected getPayloadFlag() {
    }

    @Override
    protected getTaxType() {
    }

    @Override
    protected confirmTaxType(quoteErrorMsg) {
    }


}
