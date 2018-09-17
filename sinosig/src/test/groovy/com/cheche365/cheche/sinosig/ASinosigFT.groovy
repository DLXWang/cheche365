package com.cheche365.cheche.sinosig

import com.cheche365.cheche.sinosig.app.config.SinosigConfig
import com.cheche365.cheche.test.parser.AThirdPartyHandlerServiceFT
import org.springframework.test.context.ContextConfiguration

/**
 * Created by suyq on 2015/8/19.
 * 阳光报价测试用例基类
 */
@ContextConfiguration(classes = SinosigConfig)
abstract class ASinosigFT extends AThirdPartyHandlerServiceFT {

    @Override
    protected final getInsuranceCompanyProperties() {
        [id: 15000, code: 'SINOSIG', name: '阳光保险']
    }

}
