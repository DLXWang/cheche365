package com.cheche365.cheche.ordercenter.auditing;

import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * 使用JPA注解在实体创建或更新的时候把操作时间或操作人一并更新到数据库里去
 * JPA Auditing：
 * 包括：@CreatedDate @LastModifiedDate @CreatedBy @LastModifiedBy
 * Created by sunhuazhong on 2016/2/15.
 */
@Component
public class AuditingDataTimeProvider implements DateTimeProvider {
    @Override
    public Calendar getNow() {
        return GregorianCalendar.from(ZonedDateTime.now());
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(DateUtils.getDate("2016-01-01 00:00:00", DateUtils.DATE_LONGTIME24_PATTERN));
//        return calendar;
    }
}
