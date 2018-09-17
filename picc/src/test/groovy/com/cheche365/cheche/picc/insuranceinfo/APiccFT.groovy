package com.cheche365.cheche.picc.insuranceinfo

import com.cheche365.cheche.picc.app.config.PiccNonProductionConfig
import com.cheche365.cheche.picc.app.config.PiccProductionConfig
import com.cheche365.cheche.picc.config.PiccMinTestConfig
import com.cheche365.cheche.test.parser.AThirdPartyInsuranceInfoServiceFT
import org.springframework.test.context.ContextConfiguration



@ContextConfiguration( classes = [PiccMinTestConfig, PiccNonProductionConfig, PiccProductionConfig] )
abstract class APiccFT extends AThirdPartyInsuranceInfoServiceFT {

}
