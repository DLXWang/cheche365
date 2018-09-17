package com.cheche365.cheche.baoxian.model

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.InsuranceCompany;

/**
 * 泛华预付款接口退款操作传递的数据
 * Created by wangxin on 2017/4/1.
 */
class DeductingInfo {

    String taskId
    InsuranceCompany insuranceCompany
    Area area
    Double premium
    Map additionalParameters

}
