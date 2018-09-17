package com.cheche365.cheche.pingan.insuranceinfo

import com.cheche365.cheche.pingan.app.config.PinganMinTestConfig
import com.cheche365.cheche.test.parser.AThirdPartyInsuranceInfoServiceFT
import org.springframework.test.context.ContextConfiguration



@ContextConfiguration( classes = PinganMinTestConfig )
class APinganFT extends AThirdPartyInsuranceInfoServiceFT {

    @Override
    protected getDefaultServiceIndex() {
        1
    }

}
