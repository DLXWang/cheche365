package com.cheche365.cheche.parser

import com.cheche365.cheche.answern.app.config.AnswernConfig
import com.cheche365.cheche.answern.app.config.AnswernNonProductionConfig
import com.cheche365.cheche.cpic.app.config.CpicConfig
import com.cheche365.cheche.picc.client.app.config.PiccClientConfig
import com.cheche365.cheche.pingan.app.config.PinganConfig
import com.cheche365.cheche.sinosig.app.config.SinosigConfig
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [
    PiccClientConfig,
    AnswernConfig,
    AnswernNonProductionConfig,
    PinganConfig,
    SinosigConfig,
    CpicConfig
])
@ComponentScan("com.cheche365.cheche.core.app.config")
abstract class AReferenced2ThirdPartyHandlerServiceFT extends Referenced2ThirdPartyHandlerServiceFT {
}
