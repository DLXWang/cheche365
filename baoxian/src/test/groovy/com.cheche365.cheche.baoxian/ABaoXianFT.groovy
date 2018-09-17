package com.cheche365.cheche.baoxian

import com.cheche365.cheche.baoxian.app.config.BaoXianConfig
import com.cheche365.cheche.core.app.config.CoreConfig
import com.cheche365.cheche.parser.app.config.ParserConfig
import com.cheche365.cheche.test.parser.AThirdPartyHandlerServiceFT
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer
import org.springframework.test.context.ContextConfiguration
import com.cheche365.cheche.baoxian.app.config.BaoXianNonProductionConfig

@ContextConfiguration(classes = [
    BaoXianNonProductionConfig,
    BaoXianConfig,
    ParserConfig,
    CoreConfig,
],initializers = ConfigFileApplicationContextInitializer)
abstract class ABaoXianFT extends ABaoXianComplicateFT {

    @Override
    protected getInsuranceCompanyProperties(){
        def companyId = env.getProperty('test.parser.insuranceCompanyID', '10000') as long
        [id: companyId]
    }

}
