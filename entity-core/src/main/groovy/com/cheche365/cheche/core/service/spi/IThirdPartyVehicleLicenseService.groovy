package com.cheche365.cheche.core.service.spi

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.VehicleLicense

/**
 * 外部行驶本服务
 */
interface IThirdPartyVehicleLicenseService {

    /**
     * 根据Auto中的信息（车牌号、车主姓名或者身份证）获得行驶本信息
     * @param area
     * @param auto
     * @param additionalParameters
     * @return 找不到返回null
     */
    VehicleLicense getVehicleLicense(Area area, Auto auto, Map additionalParameters)

}
