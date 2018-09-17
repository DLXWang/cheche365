package com.cheche365.cheche.sinosafe

import com.cheche365.cheche.sinosafe.app.config.SinosafeConfig
import com.cheche365.cheche.sinosafe.app.config.SinosafeNonProductionConfig
import com.cheche365.cheche.test.parser.AThirdPartyHandlerServiceFT

import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [SinosafeConfig,SinosafeNonProductionConfig])
abstract class ASinosafeFT extends AThirdPartyHandlerServiceFT {

    @Override
    protected final getInsuranceCompanyProperties() {
        [id: 50000, code: 'sinosafe', name: '华安保险']
    }

}
