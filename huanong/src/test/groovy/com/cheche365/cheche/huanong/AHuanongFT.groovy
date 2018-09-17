package com.cheche365.cheche.huanong

import com.cheche365.cheche.huanong.app.config.HuaNongTestConfig
import com.cheche365.cheche.test.parser.AThirdPartyHandlerServiceFT
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [HuaNongTestConfig])
abstract class AHuanongFT extends AThirdPartyHandlerServiceFT {

    @Override
    protected final getInsuranceCompanyProperties() {
        [id: 160000, code: 'HN', name: '华农']
    }

}
