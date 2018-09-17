package com.cheche365.cheche.picc.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

/**
 * 交强险核保
 */
@Component
@Slf4j
class CheckBZ extends ACalculateForBZSupport {

    @Override
    protected isQuoting() {
        false
    }

}
