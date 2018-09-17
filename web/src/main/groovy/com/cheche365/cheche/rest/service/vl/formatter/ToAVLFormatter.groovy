package com.cheche365.cheche.rest.service.vl.formatter

import groovy.util.logging.Slf4j
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.exception.Constants.getFIELD_ORDER_TOA

@Service
@Order(2)
@Slf4j
class ToAVLFormatter extends ToAOldVLFormatter{

    @Override
    def support(Object context) {
        context.channel.isStandardAgent() && context.apiVersion >= 'v1.9'
    }

    @Override
    def needFillAutoModels(Object context) {
        false
    }

    @Override
    def visibleFields() {
        FIELD_ORDER_TOA
    }

    @Override
    def quoteEnoughFields() {
        FIELD_ORDER_TOA
    }
}
