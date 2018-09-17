package com.cheche365.cheche.web.counter.annotation;

import java.lang.annotation.*;

/**
 * Created by zhengwei on 8/27/15.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface CountApiInvoke {

    String value();
}
