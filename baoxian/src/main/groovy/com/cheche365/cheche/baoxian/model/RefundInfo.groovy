package com.cheche365.cheche.baoxian.model

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.InsuranceCompany;

/**
 * 泛华退款接口传递的数据
 * Created by wangxin on 2017/4/1.
 */
class RefundInfo {

    String taskId
    InsuranceCompany insuranceCompany
    Area area
    Map additionalParameters

}
