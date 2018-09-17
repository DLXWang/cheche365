package com.cheche365.cheche.cpicuk.app.config

import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.core.service.IThirdPartyDecaptchaService
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.core.service.IThirdPartyPaymentService
import com.cheche365.cheche.core.service.IThirdPartyQuoteRecordService
import com.cheche365.cheche.core.service.ResourceProperties
import com.cheche365.cheche.cpicuk.service.CpicUKPaymentInfoService
import com.cheche365.cheche.cpicuk.service.CpicUKQuoteRecordService
import com.cheche365.cheche.cpicuk.service.CpicUKService
import com.cheche365.cheche.parser.service.IInsuranceCompanyChecker
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.core.env.Environment



/**
 * 太平洋UK配置文件类
 * **/
@SpringBootApplication
@ComponentScan([
    'com.cheche365.cheche.parser.app.config',
    'com.cheche365.cheche.cpicuk.service',
    'com.cheche365.cheche.decaptcha.app.config',
    'com.cheche365.cheche.core.service'
])
@EnableConfigurationProperties(ResourceProperties)
class CpicUKConfig {

    @Bean
    IThirdPartyHandlerService cpicUKService(
        Environment env,
        IInsuranceCompanyChecker insuranceCompanyChecker,
        IThirdPartyDecaptchaService decaptchaService,
        IConfigService configService
    ) {
        new CpicUKService(env, insuranceCompanyChecker, decaptchaService, configService)
    }


    @Bean
    IThirdPartyPaymentService cpicUKPaymentInfoService(IThirdPartyDecaptchaService decaptchaService, IConfigService configService) {
        new CpicUKPaymentInfoService(decaptchaService, configService)
    }

    @Bean
    IThirdPartyQuoteRecordService CpicUKQuoteRecordService(IThirdPartyDecaptchaService decaptchaService, IConfigService configService) {
        new CpicUKQuoteRecordService(decaptchaService, configService)
    }

}
