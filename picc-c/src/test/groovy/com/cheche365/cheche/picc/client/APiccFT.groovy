package com.cheche365.cheche.picc.client

import com.cheche365.cheche.picc.client.app.config.PiccClientTestConfig
import com.cheche365.cheche.test.parser.AThirdPartyHandlerServiceFT
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [PiccClientTestConfig])
abstract class APiccFT extends AThirdPartyHandlerServiceFT {

    @Override
    protected final getInsuranceCompanyProperties() {
        [id: 10000, code: 'PICC', name: '人保财险']
    }

}
