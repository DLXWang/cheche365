package com.cheche365.cheche.botpy

import com.cheche365.cheche.botpy.app.config.BotpyTestConfig
import com.cheche365.cheche.test.parser.AThirdPartyHandlerServiceFT
import groovy.util.logging.Slf4j
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(
    classes = [BotpyTestConfig]
)
@Slf4j
abstract class ABotpyFT extends AThirdPartyHandlerServiceFT {

//    @Override
//    protected getInsuranceCompanyProperties() {
//        [id: 20000, code: 'PINGAN', name: '平安保险']
//    }


    @Override
    protected getInsuranceCompanyProperties() {
        [id: 25000, code: 'CPIC', name: '太平洋保险']
    }


//    @Override
//    protected final getInsuranceCompanyProperties() {
//        [id: 10000, code: 'PICC', name: '人保财险']
//    }

}


