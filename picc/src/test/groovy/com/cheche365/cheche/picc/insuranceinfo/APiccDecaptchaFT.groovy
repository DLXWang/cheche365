package com.cheche365.cheche.picc.insuranceinfo

import com.cheche365.cheche.picc.app.config.PiccNonProductionConfig
import com.cheche365.cheche.picc.app.config.PiccProductionConfig
import com.cheche365.cheche.picc.config.PiccDecaptchaTestConfig
import com.cheche365.cheche.test.parser.AThirdPartyInsuranceInfoServiceFT
import org.springframework.test.context.ContextConfiguration



/**
 * 需要注入验证码服务
 */
@ContextConfiguration(classes = [PiccDecaptchaTestConfig, PiccNonProductionConfig, PiccProductionConfig])
abstract class APiccDecaptchaFT extends AThirdPartyInsuranceInfoServiceFT {

}
