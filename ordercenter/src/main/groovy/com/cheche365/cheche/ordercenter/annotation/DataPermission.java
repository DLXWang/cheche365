package com.cheche365.cheche.ordercenter.annotation;

import java.lang.annotation.*;

/**
 * Created by yellow on 2017/6/13.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Inherited
public @interface DataPermission {
    String handler() default "";
    String code() default "";
}
