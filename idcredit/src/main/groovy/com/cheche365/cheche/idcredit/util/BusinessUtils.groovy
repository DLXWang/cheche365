package com.cheche365.cheche.idcredit.util

import com.cheche365.cheche.core.model.ApplicationLog

import static com.cheche365.cheche.common.util.DateUtils.getSecondsUntilNow
import static com.cheche365.cheche.common.util.DateUtils.tomorrowLocalDateTime as tomorrow
import static com.cheche365.cheche.core.model.LogType.Enum.IDCREDIT
import static java.util.concurrent.TimeUnit.SECONDS

/**
 * 工具集
 * Created by Huabin on 2016/6/24.
 */
class BusinessUtils {

    /**
     * 判断是否车辆之前（当天）有过匹配错误
     * @param context
     * @return
     */
    static wasVehicleInfoFailed(context) {
        def findVehicleInfoFailedKey = getFindVehicleInfoFailedKey context
        context.globalContext.get findVehicleInfoFailedKey
    }

    /**
     * 标记在绿湾匹配不上的车辆（有效期到翌日0点）
     * @param context
     * @return
     */
    static markFailedVehicleInfo(context) {
        def findVehicleInfoFailedKey = getFindVehicleInfoFailedKey context
        def expireTime = getSecondsUntilNow tomorrow
        context.globalContext.bindIfAbsentWithTTL findVehicleInfoFailedKey, true, expireTime, SECONDS
    }

    private static getFindVehicleInfoFailedKey(context) {
        def auto = context.auto
        def licensePlateNo = auto.licensePlateNo
        def identity = auto.identity
        def owner = auto.owner
        "find-vehicle-info-failed-$licensePlateNo-$identity-$owner"
    }

    static saveApplicationLog(context, logMessage, objId) {
        def channel = context.additionalParameters.channel
        context.applicationLogRepository.save(
            new ApplicationLog(
                createTime: new Date(),
                instanceNo: context.auto.licensePlateNo,
                logLevel  : 0,
                logMessage: logMessage,
                logType   : IDCREDIT,
                objId     : objId + '：' + channel?.id,
                objTable  : IDCREDIT.name,
                user      : context.additionalParameters?.user
            )
        )
    }

}
