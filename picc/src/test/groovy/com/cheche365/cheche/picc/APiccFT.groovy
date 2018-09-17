package com.cheche365.cheche.picc

import com.cheche365.cheche.picc.app.config.PiccNonProductionConfig
import com.cheche365.cheche.picc.app.config.PiccProductionConfig
import com.cheche365.cheche.picc.config.PiccMinTestConfig
import com.cheche365.cheche.test.parser.AThirdPartyHandlerServiceFT
import org.springframework.test.context.ContextConfiguration



@ContextConfiguration(classes = [PiccMinTestConfig, PiccNonProductionConfig, PiccProductionConfig])
abstract class APiccFT extends AThirdPartyHandlerServiceFT {

    @Override
    protected final getInsuranceCompanyProperties() {
        [id: 10000, code: 'PICC', name: '人保财险']
    }

}
