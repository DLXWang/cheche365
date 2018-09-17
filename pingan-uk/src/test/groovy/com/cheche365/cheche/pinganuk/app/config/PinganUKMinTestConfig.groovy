package com.cheche365.cheche.pinganuk.app.config

import com.cheche365.cheche.core.service.IOCRService
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.core.service.IThirdPartyPaymentService
import com.cheche365.cheche.pinganuk.service.PinganUKPaymentService
import com.cheche365.cheche.pinganuk.service.PinganUKService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment


/**
 * Pinganuk的Spring配置注释类
 */
@Configuration
@ComponentScan('com.cheche365.cheche.core.app.config')
@PropertySource('classpath:/properties/pinganuk.properties')
class PinganUKMinTestConfig extends APinganUKConfig{

    @Bean
    IThirdPartyHandlerService pinganUKService(Environment env,
                                              IThirdPartyDecaptchaService decaptchaService,
                                              IOCRService getInformationService) {
        new PinganUKService(env, null, decaptchaService, getInformationService)
    }

    @Bean
    IThirdPartyPaymentService pinganUKPaymentService(IThirdPartyDecaptchaService decaptchaService) {
        new PinganUKPaymentService(decaptchaService)
    }
}
