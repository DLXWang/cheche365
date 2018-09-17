package com.cheche365.cheche.web.counter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by mahong on 2016/3/15.
 * 使用@NonProduction标注的类、方法，只能在非生产环境调用，否则抛异常
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NonProduction {
}
