package com.cheche365.cheche.parser.autotype

import com.cheche365.cheche.chinalife.app.config.ChinalifeConfig
import com.cheche365.cheche.cpic.app.config.CpicConfig
import com.cheche365.cheche.pingan.app.config.PinganConfig
import com.cheche365.cheche.sinosig.app.config.SinosigConfig
import com.cheche365.cheche.test.parser.AAutoTypesService2FT
import org.springframework.test.context.ContextConfiguration


@ContextConfiguration(classes = [
    ChinalifeConfig,
    CpicConfig,
    PinganConfig,
    SinosigConfig
])
class Concurrent110000beijingFT extends AAutoTypesService2FT {
}
