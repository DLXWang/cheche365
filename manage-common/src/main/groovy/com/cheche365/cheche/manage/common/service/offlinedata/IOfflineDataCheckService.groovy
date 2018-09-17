package com.cheche365.cheche.manage.common.service.offlinedata

import com.cheche365.cheche.manage.common.model.InsuranceOfflineDataModel

/**
 * 线下数据检查服务
 * Created by suyaqiang on 2017/9/19.
 */
interface IOfflineDataCheckService {

    /**
     * 检查原始数据
     * @param lines
     * @param options 配置项
     * [
     *      startInsuredDate : Date, // 可选，如果有则校验出单日期大于此日期
     *      endInsuredDate : Date    // 可选，如果有则校验出单日期小于此日期
     * ]
     * @return
     */
    Map check(Collection<InsuranceOfflineDataModel> lines, Map options)

}
