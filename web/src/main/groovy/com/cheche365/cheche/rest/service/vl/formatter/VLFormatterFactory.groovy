package com.cheche365.cheche.rest.service.vl.formatter

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class VLFormatterFactory {

    @Autowired
    List<VLFormatter> formatters

    VLFormatter getVLFormatter(context) {
        formatters.find { it.support(context) }
    }
}
