package com.cheche365.cheche.core.annotation

import groovy.util.logging.Slf4j
import org.apache.commons.lang3.time.DateUtils
import java.util.Date;

import java.lang.reflect.Type

/**
 * @Author shanxf
 * @Date 2017/11/30  18:09
 */

@Slf4j
class EqualsFiledHandler {

    def static boolean objectEquals(Object origin,Object target){
        return origin.getClass().getDeclaredFields().findAll{it -> it.isAnnotationPresent(EqualsField)}?.any{it ->
                origin."$it.name" && (!target."$it.name"|| !fieldEquals(it.getGenericType(),origin."$it.name",target."$it.name"))
        }
    }

    private static boolean fieldEquals(Type type, Object origin, Object target){

        if (type == Date){
            return DateUtils.truncate(origin, Calendar.DAY_OF_MONTH).equals(DateUtils.truncate(target, Calendar.DAY_OF_MONTH));
        } else {
            return origin.equals(target);
        }
    }
}
