package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.InsuranceBasicInfo


/**
 * 保险信息服务接口
 * Created by Huabin on 2016/11/6.
 */
interface IInsuranceInfoService {

    /**
     * 返回保险基本信息
     * @param area 车辆所在地区
     * @param auto 车辆信息
     * @param additionalParameters 额外的附加信息
     * @return
     */
    InsuranceBasicInfo getInsuranceBasicInfo(Area area, Auto auto, Map additionalParameters)

    /**
     * 返回保险信息
     * @param area 车辆所在地区
     * @param auto 车辆信息
     * @param additionalParameters 额外的附加信息
     * @return 保险信息
     */
    Object getInsuranceInfo(Area area, Auto auto, Map additionalParameters)
}
