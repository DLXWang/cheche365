package com.cheche365.cheche.rest.service.vl.formatter

import groovy.util.logging.Slf4j
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

@Service
@Order(1)
@Slf4j
class OCVLFormatter extends DefaultVLFormatter {

    @Override
    def support(context) {
        context.channel.isOrderCenterChannel()
    }

    @Override
    def needFilterVLMismatch(Object context) {
        false
    }

    @Override
    def needEncryptVL(Object context) {
        false
    }

}
