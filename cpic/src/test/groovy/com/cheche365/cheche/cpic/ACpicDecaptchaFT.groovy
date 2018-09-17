package com.cheche365.cheche.cpic

import com.cheche365.cheche.cpic.config.CpicDecaptchaTestConfig
import com.cheche365.cheche.test.parser.AThirdPartyHandlerServiceFT
import org.springframework.test.context.ContextConfiguration



@ContextConfiguration( classes = CpicDecaptchaTestConfig )
abstract class ACpicDecaptchaFT extends AThirdPartyHandlerServiceFT {

    @Override
    protected getInsuranceCompanyProperties() {
        [id: 25000, code: 'CPIC', name: '太平洋保险']
    }

    @Override
    protected final long getZzzTime() {
        10000L
    }

}
