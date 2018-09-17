package com.cheche365.cheche.core.util

import com.cheche365.cheche.core.exception.BusinessException

import java.text.SimpleDateFormat

/**
 * Created by Chenqc on 2017/1/17.
 */
class HolidayCheckUtil {

    /**
     * 检查当前系统时间是否为春节（春节时间：20170126至20170203，即2017年1月26日00:00:00至2017年2月3日00:00:00）
     * 若在此时间内则抛出异常（由于春节放假，1月26日至2月2日期间将暂停下单和配送服务，2月3日将恢复正常，给您带来的不便敬请谅解。）
     */
    static void checkSpringFestival() {
        long nowTime = System.currentTimeMillis()
        SimpleDateFormat sdf = new SimpleDateFormat('yyyyMMdd')

        sdf.parse('20170125').getTime() <= nowTime && nowTime <= sdf.parse('20170203').getTime() ? {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "由于春节放假，1月25日至2月2日期间将暂停下" +
                "单和配送服务，2月3日将恢复正常，给您带来的不便敬请谅解。")
        }.call() : null
    }


}
