package com.cheche365.cheche.scheduletask.model

import groovy.transform.ToString

/**
 * Created by yinJianBin on 2017/10/13.
 */
@ToString
class InsuranceOfflineDataErrorEmainInfo extends AttachmentData {
    String order       //序号
    String policyNo    //保单号
    String licenseNo   //车牌号
    String errorMessage //错误原因
}
