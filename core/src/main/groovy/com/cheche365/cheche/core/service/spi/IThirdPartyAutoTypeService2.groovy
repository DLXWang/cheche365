package com.cheche365.cheche.core.service.spi

import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.Auto

/**
 * 二代外部车型服务
 * Created by Huabin on 2016/8/30.
 */
interface IThirdPartyAutoTypeService2 {

    /**
     * 根据城市和车辆信息获取对应保险公司的车型列表
     * @param area
     * @param auto
     * @return
     */
    List getAutoTypes(Area area, Auto auto)

}
