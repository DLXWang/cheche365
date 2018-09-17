package com.cheche365.cheche.bihu.insuranceinfo

import com.cheche365.cheche.bihu.app.config.BihuConfig
import com.cheche365.cheche.test.parser.AThirdPartyInsuranceInfoServiceFT
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer
import org.springframework.test.context.ContextConfiguration



@ContextConfiguration(
    classes = BihuConfig,
    initializers = ConfigFileApplicationContextInitializer
)
abstract class ABihuFT extends AThirdPartyInsuranceInfoServiceFT {
}
