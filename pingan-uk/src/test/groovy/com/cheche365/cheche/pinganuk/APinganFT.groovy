package com.cheche365.cheche.pinganuk

import com.cheche365.cheche.pinganuk.app.config.PinganUKMinTestConfig
import com.cheche365.cheche.test.parser.AThirdPartyHandlerServiceFT
import org.springframework.test.context.ContextConfiguration



@ContextConfiguration(classes = [PinganUKMinTestConfig])
abstract class APinganFT extends AThirdPartyHandlerServiceFT {

    @Override
    protected final getInsuranceCompanyProperties() {
        [id : 55000, code : 'PINGANUK', name : '平安UK']
    }

}
