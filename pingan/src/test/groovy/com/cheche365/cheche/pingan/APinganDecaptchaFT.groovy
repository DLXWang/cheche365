package com.cheche365.cheche.pingan

import com.cheche365.cheche.pingan.app.config.PinganDecaptchaTestConfig
import com.cheche365.cheche.test.parser.AThirdPartyHandlerServiceFT
import org.springframework.test.context.ContextConfiguration



@ContextConfiguration(classes = PinganDecaptchaTestConfig)
abstract class APinganDecaptchaFT extends AThirdPartyHandlerServiceFT {

    @Override
    protected getInsuranceCompanyProperties() {
        [id: 20000, code: 'PINGAN', name: '平安保险']
    }

}
