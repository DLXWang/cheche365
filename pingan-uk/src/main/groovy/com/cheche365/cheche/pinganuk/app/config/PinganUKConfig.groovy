package com.cheche365.cheche.pinganuk.app.config

import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.service.IOCRService
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.core.service.IThirdPartyPaymentService
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import com.cheche365.cheche.pinganuk.service.PinganUKPaymentService
import com.cheche365.cheche.pinganuk.service.PinganUKService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment



/**
 * PINGANUK的Spring配置注释类
 */
@Configuration
class PinganUKConfig extends APinganUKConfig {

    @Bean
    IThirdPartyHandlerService pinganUKService(
        ApplicationContextHolder _appContextHolder, // <-- 绝对不许删，看上面javadoc
        Environment env,
        IInsuranceCompanyChecker insuranceCompanyChecker,
        IThirdPartyDecaptchaService decaptchaService,
        @Qualifier('acquisitionService') IOCRService getInformationService
    ) {
        new PinganUKService(env, insuranceCompanyChecker, decaptchaService, getInformationService)
    }

    @Bean
    IThirdPartyPaymentService pinganUKPaymentService(IThirdPartyDecaptchaService decaptchaService) {
        new PinganUKPaymentService(decaptchaService)
    }

}
