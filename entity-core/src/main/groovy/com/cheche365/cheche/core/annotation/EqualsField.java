package com.cheche365.cheche.core.annotation;

import java.lang.annotation.*;

/**
 *   Created by shanxf on 2017/11/30
 *   使用EqualsField作用于 entity 的 field  表示该方法参加该 entity 的equals 方法判断
 *   例如 com.cheche365.cheche.core.model.Auto
 */

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface EqualsField {

}
