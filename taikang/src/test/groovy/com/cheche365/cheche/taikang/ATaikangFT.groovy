package com.cheche365.cheche.taikang

import com.cheche365.cheche.taikang.app.config.TaiKangConfig
import com.cheche365.cheche.taikang.app.config.TaiKangNonProductionConfig
import com.cheche365.cheche.taikang.app.config.TaikangTestConfig
import com.cheche365.cheche.test.parser.AThirdPartyHandlerServiceFT
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [TaiKangConfig, TaiKangNonProductionConfig, TaikangTestConfig])
abstract class ATaikangFT extends AThirdPartyHandlerServiceFT {

    @Override
    protected final getInsuranceCompanyProperties() {
        [id: 80000, code: 'TK', name: '泰康在线']
    }

}
