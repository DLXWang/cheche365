package com.cheche365.cheche.chinalife

import com.cheche365.cheche.chinalife.config.ChinalifeDecaptchaTestConfig
import com.cheche365.cheche.test.parser.AThirdPartyHandlerServiceFT
import org.springframework.test.context.ContextConfiguration



@ContextConfiguration( classes = ChinalifeDecaptchaTestConfig )
abstract class AChinalifeDecaptchaFT extends AThirdPartyHandlerServiceFT {

    @Override
    protected getInsuranceCompanyProperties() {
        [id: 40000, code: 'CHINALIFE', name: '人寿财险']
    }

}
