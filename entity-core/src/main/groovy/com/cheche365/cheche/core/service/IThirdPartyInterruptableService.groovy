package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.model.DailyInsurance
import com.cheche365.cheche.core.model.DailyRestartInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.PurchaseOrder

/**
 * 可中断的第三方服务
 * Created by Huabin on 2016/11/16.
 */
interface IThirdPartyInterruptableService {

    /**
     * 将保险中断一段时间
     * @param order 订单
     * @param insurance 商业险保单
     * @param compulsoryInsurance 交强险保单
     * @param dailyInsurance
     * @param additionalParameters 附加参数
     */
    void suspend(PurchaseOrder order, Insurance insurance, DailyInsurance dailyInsurance, Map<String, Object> additionalParameters)

    /**
     * 恢复保险
     * @param order 订单
     * @param insurance 商业险保单
     * @param compulsoryInsurance 交强险保单
     * @param dailyInsurance
     * @param additionalParameters 附加参数
     */
    void resume(PurchaseOrder order, Insurance insurance, DailyRestartInsurance dailyRestartInsurance, Map<String, Object> additionalParameters)

}
