package com.cheche365.cheche.web.activity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by chenxiaozhe on 15-12-7.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityDate {
    /**
     * 2种写法：
     * @ActivityDate(end="2015-12-12")判断当前活动是否过期（一般用此即可）
     * @ActivityDate(end="201511002&2015-12-12,201512004&2015-12-31" )，根据活动code和时间来判断活动是否过期（如果对应多个活动，用此）
     * @return
     */
    String end();
}
