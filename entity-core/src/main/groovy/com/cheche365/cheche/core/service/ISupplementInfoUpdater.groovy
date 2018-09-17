package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.User
import groovy.transform.TupleConstructor


/**
 * 补充信息更新器
 * Created by Huabin on 2017/5/17.
 */
interface ISupplementInfoUpdater {

    /**
     * 更新补充信息
     * @param requestObject 请求对象
     */
    void updateSupplementInfo(RequestObject requestObject)



    @TupleConstructor
    static class RequestObject {

        Auto auto
        User user
        InsuranceCompany insuranceCompany
        Channel channel

        Map supplementInfo

        Map additionalParameters

    }

}
