package com.cheche365.cheche.picc.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

/**
 * 获取交强险报价
 */
@Component
@Slf4j
class CalculateForBZ extends ACalculateForBZSupport {

    @Override
    protected isQuoting() {
        true
    }

}
