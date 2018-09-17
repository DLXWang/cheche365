package com.cheche365.cheche.chinalife

import com.cheche365.cheche.chinalife.config.ChinalifeMinTestConfig
import com.cheche365.cheche.test.parser.AThirdPartyHandlerServiceFT
import org.springframework.test.context.ContextConfiguration



@ContextConfiguration(classes = ChinalifeMinTestConfig)
abstract class AChinalifeFT extends AThirdPartyHandlerServiceFT {

    @Override
    protected final getInsuranceCompanyProperties() {
        [id: 40000, code: 'CHINALIFE', name: '人寿财险']
    }

}
