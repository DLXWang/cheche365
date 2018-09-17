package com.cheche365.cheche.manage.common.service.offlinedata

import com.cheche365.cheche.manage.common.model.InsuranceOfflineDataModel


/**
 * 线下数据服务接口
 * Created by suyaqiang on 2017/9/19.
 */
interface IOfflineDataService {

    /**
     * 异步调用检查和数据导出服务
     * @param lines
     * @param options
     */
    @Deprecated
    void exportDataAsync(List<InsuranceOfflineDataModel> lines, Map options)

    /**
     * 数据检查
     * @param lines
     * @param options
     * @return
     */
    Map check(Collection<InsuranceOfflineDataModel> lines, Map options)

    /**
     * 先检查数据，然后将线下数据填充相关数据并转换为域对象
     * @param lines
     * @param options
     * [
     *      id:String,                // 要执行数据的标识
     *      startInsuredDate : Date, // 可选，如果有则校验出单日期大于此日期
     *      endInsuredDate : Date    // 可选，如果有则校验出单日期小于此日期
     *      biStartDate : Date,      //可选， 起保日期最小值。从壁虎获取的起保日期如果小于biStartDate则认为无效
     *      chunkSize,               // 可选， 数据分块大小。如果有值，会将合法数据按此大小分隔，并放入redis队列中
     *      exInsuranceCompanies, // 可选，扩展的保险公司映射Map,例如：[泰康（原始数据中保险公司的名字） :InsuranceCompany.Enum.TK （数据库中的保险公司）]
     *      exPaymentChannels,    // 可选，扩展的支付方式映射Map,例如：[支付宝支付的 （原始数据中支付方式的名字）  :ALIPAY （数据库中的支付方式）,   支付宝转账:ALIPAY]
     * ]
     * @return
     */
    Map exportData(Collection<InsuranceOfflineDataModel> lines, Map options)

}
