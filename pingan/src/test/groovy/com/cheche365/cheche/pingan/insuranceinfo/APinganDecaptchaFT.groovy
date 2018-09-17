package com.cheche365.cheche.pingan.insuranceinfo

import com.cheche365.cheche.pingan.app.config.PinganDecaptchaTestConfig
import com.cheche365.cheche.test.parser.AThirdPartyInsuranceInfoServiceFT
import org.springframework.test.context.ContextConfiguration



@ContextConfiguration( classes = PinganDecaptchaTestConfig )
class APinganDecaptchaFT extends AThirdPartyInsuranceInfoServiceFT {

    @Override
    protected getDefaultServiceIndex() {
        1
    }

}
