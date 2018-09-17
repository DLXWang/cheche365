package com.cheche365.cheche.aibao

import com.cheche365.cheche.aibao.app.conf.AiBaoTestConfig
import com.cheche365.cheche.aibao.app.config.AiBaoConfig
import com.cheche365.cheche.aibao.app.config.AiBaoNonProductionConfig
import com.cheche365.cheche.test.parser.AThirdPartyHandlerServiceFT
import org.springframework.test.context.ContextConfiguration
import spock.lang.Unroll

@ContextConfiguration(classes = [AiBaoConfig, AiBaoNonProductionConfig,AiBaoTestConfig])
abstract class AiBaoFT extends AThirdPartyHandlerServiceFT {

    @Override
    protected final getInsuranceCompanyProperties() {
        [id: 10000L, code: 'PICC', name: '爱保在线']
    }

}
