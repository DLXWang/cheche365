package com.cheche365.cheche.core.util

import com.cheche365.cheche.common.util.DateUtils
import groovy.util.logging.Slf4j

@Slf4j
class InsuranceDateUtil {

    static getEffectiveDate(original) {
        def date =  (original instanceof Long) ? new Date(original) : original
        if (date && date instanceof Date){
            DateUtils.compareSameDate(date, new Date()) ? date + 1 : date
        } else {
            log.error("非日期类型，不予处理，date:{}", date)
        }
    }
}
