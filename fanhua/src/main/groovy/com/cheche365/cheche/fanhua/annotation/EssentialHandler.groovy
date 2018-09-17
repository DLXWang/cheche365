package com.cheche365.cheche.fanhua.annotation

import org.apache.commons.lang.StringUtils

import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * 校验对象参数合规
 * @Essential标识字段必填
 * 
 * Created by zhangtc on 2017/12/16.
 */
class EssentialHandler {

    static Tuple2 checkParam(Object obj) {
        for (Field it : obj.getClass().getDeclaredFields()) {
            if ((it.getModifiers() & Modifier.STATIC) == Modifier.STATIC || (it.getModifiers() & Modifier.TRANSIENT) == Modifier.TRANSIENT) {
                continue
            }
            if (it.getAnnotation(Essential) && obj."$it.name" == null) {
                return result(false, "$it.name 字段为空")
            }
            if (obj."$it.name" == null || StringUtils.isBlank(String.valueOf(obj."$it.name"))) {
                continue
            }
            if (obj."$it.name".class.name.matches('com.cheche365.*')) {
                def result = checkParam obj."$it.name"
                if (!result[0]) {
                    return result
                }
            } else if (obj."$it.name" instanceof Map) {
                for (String key : obj."$it.name".keySet()) {
                    if (!unSafeField(key, obj."$it.name".get(key))) {
                        return result(false, "$it.name 内 $key 字段格式错误")
                    }
                }

            } else if (obj."$it.name" instanceof List) {
                for (Object unit : obj."$it.name") {
                    if (unit != null) {
                        if (unit instanceof Map) {
                            for (String key : unit.keySet()) {
                                if (unSafeField(key, unit.get(key))) {
                                    return result(false, "$it.name 内 $key 字段格式错误")
                                }
                            }
                        } else {
                            def result = checkParam unit
                            if (!result[0]) {
                                return result
                            }
                        }
                    }
                }
            } else if (unSafeField(it.name, obj."$it.name")) {
                return result(false, "$it.name 字段格式错误")
            }
        }
        result(true, '')
    }

    static boolean unSafeField(String field, Object value) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return
        } else if (pick(field, ['charge', 'amount', 'prise'] as String[]) && isNotDouble(value)) {
            return true
        } else if (pick(field, ['date'] as String[]) && isNotDate(value)) {
            return true
        }
        return false
    }

    def static result(Boolean status, String msg) {
        new Tuple2(status, msg)
    }

    static boolean pick(String param, String[] names) {
        for (String name : names) {
            if (param.toLowerCase().endsWith(name)) {
                return true
            }
        }
        false
    }

    static boolean isNotDate(Object date) {
        (!date.toString().trim().matches('\\d{4}-\\d{2}-\\d{2}') && !date.toString().trim().matches('\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}'))
    }

    /**
     * copy from StringGroovyMethods use to java.lang.Object
     * @param self
     * @return
     */
    static boolean isNotDouble(Object self) {
        try {
            Double.valueOf(self.toString().trim())
            return false
        } catch (NumberFormatException ignored) {
            return true
        }
    }
}
