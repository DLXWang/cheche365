package com.cheche365.cheche.piccuk

import com.cheche365.cheche.piccuk.app.config.PiccUKTestConfig
import com.cheche365.cheche.test.parser.AThirdPartyHandlerServiceFT
import org.springframework.test.context.ContextConfiguration



@ContextConfiguration(classes = [PiccUKTestConfig])
abstract class APiccFT extends AThirdPartyHandlerServiceFT {

    @Override
    protected final getInsuranceCompanyProperties() {
        [id : 10000, code : 'PICCUK', name : '人保UK']
    }

}
