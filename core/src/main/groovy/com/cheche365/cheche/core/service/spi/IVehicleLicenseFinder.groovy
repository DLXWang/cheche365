package com.cheche365.cheche.core.service.spi

import com.cheche365.cheche.core.model.InsuranceInfo

/**
 * Created by zhengwei on 29/11/2017.
 */
interface IVehicleLicenseFinder {

    InsuranceInfo find(String licensePlateNo, String owner)

    String name()
}
