package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.model.DailyRestartInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.PurchaseOrder

/**
 * 按天车险服务
 */
interface IThirdPartyDailyInsuranceService extends IThirdPartyApplyInterruptableService, IThirdPartyInterruptableService {

    /**
     * 确认停驶后承保承保
     * @param order 订单
     * @param insurance 商业险保单
     * @param compulsoryInsurance 交强险保单
     * @param dailyInsurance
     * @param additionalParameters 附加参数
     */
    void order(PurchaseOrder order, Insurance insurance, DailyRestartInsurance dailyRestartInsurance, Map<String, Object> additionalParameters)

}
