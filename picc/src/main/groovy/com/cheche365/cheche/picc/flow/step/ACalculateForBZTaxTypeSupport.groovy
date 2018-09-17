package com.cheche365.cheche.picc.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

/**
 * 获取交强险报价及核保的基类（需要纳税方式的）
 */
@Component
@Slf4j
abstract class ACalculateForBZTaxTypeSupport extends ACalculateForBZ {

    @Override
    protected confirmTaxType(quoteErrorMsg) {
        if (_BZ_TAX_TYPE_B == taxType) {
            log.info '以补充并纳税方式交强险，结果{}', quoteErrorMsg
            def m = quoteErrorMsg ==~ /.*已找到.*完税记录.*不能以补税投保.*/
            m ? _BZ_TAX_TYPE_N : _BZ_TAX_TYPE_B
        } else {
            _BZ_TAX_TYPE_N
        }

    }

}
