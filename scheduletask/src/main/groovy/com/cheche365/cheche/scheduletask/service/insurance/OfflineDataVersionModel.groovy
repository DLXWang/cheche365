package com.cheche365.cheche.scheduletask.service.insurance

/**
 * 根据该model来生成线下导入的数据的version
 * Created by yinJianBin on 2018/1/22.
 */
class OfflineDataVersionModel {
    String policyNo
    String licensePlateNo
    String code
    Double totalPremium
    String identity
    Double downCommercialAmount
    Double downCompulsoryAmount

    OfflineDataVersionModel() {}

    OfflineDataVersionModel(String policyNo, String licensePlateNo, String code, Double totalPremium, String identity, Double downCommercialAmount, Double downCompulsoryAmount) {
        this.policyNo = policyNo
        this.licensePlateNo = licensePlateNo
        this.code = code
        this.totalPremium = totalPremium
        this.identity = identity
        this.downCommercialAmount = downCommercialAmount
        this.downCompulsoryAmount = downCompulsoryAmount
    }
}
