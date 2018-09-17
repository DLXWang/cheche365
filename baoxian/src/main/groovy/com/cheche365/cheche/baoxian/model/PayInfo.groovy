package com.cheche365.cheche.baoxian.model

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.InsuranceCompany



/**
 * 泛华支付接口传递的数据
 * Created by wangxin on 2017/7/14.
 */
class PayInfo {

    String taskId
    InsuranceCompany insuranceCompany
    Area area
    Map additionalParameters

}
