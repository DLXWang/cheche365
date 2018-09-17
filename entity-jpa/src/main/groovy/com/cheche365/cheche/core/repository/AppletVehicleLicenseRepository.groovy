package com.cheche365.cheche.core.repository

import com.cheche365.cheche.core.model.AppletVehicleLicense
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository

interface AppletVehicleLicenseRepository extends PagingAndSortingRepository<AppletVehicleLicense, Long>, JpaSpecificationExecutor<AppletVehicleLicense> {
}
