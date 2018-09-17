package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.model.AutoType
import com.cheche365.cheche.core.model.VehicleLicense



/**
 * 第三方车型服务
 * 主要用于从外部服务获取车型列表
 */
interface IThirdPartyAutoTypeService {

    /**
     * 获取车型列表
     * @param vehicleLicense
     * @param additionalParameters
     * @return
     */
    List<AutoType> getAutoTypes(VehicleLicense vehicleLicense, Map additionalParameters)

}
