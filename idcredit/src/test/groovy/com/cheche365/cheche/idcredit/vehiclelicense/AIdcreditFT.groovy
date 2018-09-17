package com.cheche365.cheche.idcredit.vehiclelicense

import com.cheche365.cheche.idcredit.app.config.IdcreditConfig
import com.cheche365.cheche.idcredit.app.config.IdcreditProductionConfig
import com.cheche365.cheche.idcredit.app.config.IdcreditQaConfig
import com.cheche365.cheche.test.parser.AThirdPartyVehicleLicenseServiceFT
import org.springframework.test.context.ContextConfiguration


@ContextConfiguration(classes = [
    IdcreditConfig,
    IdcreditQaConfig,
    IdcreditProductionConfig
])
abstract class AIdcreditFT extends AThirdPartyVehicleLicenseServiceFT {
}
