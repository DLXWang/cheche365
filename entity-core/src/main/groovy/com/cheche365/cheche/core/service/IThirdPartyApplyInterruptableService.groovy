package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.model.DailyInsurance
import com.cheche365.cheche.core.model.DailyRestartInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.PurchaseOrder

/**
 * 可申请中断的第三方服务
 */
interface IThirdPartyApplyInterruptableService {

    /**
     * 申请停驶
     * @param order 订单
     * @param insurance 商业险保单
     * @param dailyInsurance
     * @param additionalParameters
     */
    void applySuspend(PurchaseOrder order, Insurance insurance, DailyInsurance dailyInsurance, Map<String, Object> additionalParameters)

    /**
     * 申请恢复保险
     * @param order 订单
     * @param insurance 商业险保单
     * @param compulsoryInsurance 交强险保单
     * @param dailyInsurance
     * @param additionalParameters 附加参数
     */
    void applyResume(PurchaseOrder order, Insurance insurance, DailyRestartInsurance dailyRestartInsurance, Map<String, Object> additionalParameters)

}
