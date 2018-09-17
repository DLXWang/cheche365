package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.model.DailyInsurance
import com.cheche365.cheche.core.model.DailyRestartInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.PurchaseOrder

/**
 * 同步停复驶信息到第三方服务
 * Created by liheng on 2017/6/30 030.
 */
interface IThirdPartySyncInterruptableService extends IThirdPartyApplyInterruptableService {

    /**
     * 同步停驶记录
     * @param order 订单
     * @param insurance 商业险保单
     * @param dailyInsurance 停驶记录
     * @param additionalParameters 附加参数
     */
    void syncSuspend(PurchaseOrder order, Insurance insurance, DailyInsurance dailyInsurance, Map<String, Object> additionalParameters)

    /**
     * 同步复驶记录
     * @param order 订单
     * @param insurance 商业险保单
     * @param dailyRestartInsurance 复驶记录
     * @param additionalParameters 附加参数
     */
    void syncResume(PurchaseOrder order, Insurance insurance, DailyRestartInsurance dailyRestartInsurance, Map<String, Object> additionalParameters)

}
