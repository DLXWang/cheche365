package com.cheche365.cheche.piccuk.tob

import com.cheche365.cheche.piccuk.tob.app.config.PiccUK2bConfig
import com.cheche365.cheche.test.parser.AThirdPartyHandlerServiceFT
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [PiccUK2bConfig])
abstract class APicc2bFT extends AThirdPartyHandlerServiceFT {

    @Override
    protected final getInsuranceCompanyProperties() {
        [id: 10000, code: 'PICCUK', name: '人保UK']
    }

}
