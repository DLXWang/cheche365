package com.cheche365.cheche.aibao.app.conf

import com.cheche365.cheche.aibao.app.config.AiBaoConfig
import com.cheche365.cheche.aibao.app.config.AiBaoNonProductionConfig
import com.cheche365.cheche.core.service.FileBasedConfigService
import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.parser.app.config.ParserConfig
import com.cheche365.cheche.parserapi.app.config.ParserAPIConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment



/**
 * 测试spring配置
 */
@Configuration
@Import([AiBaoConfig, AiBaoNonProductionConfig, ParserConfig, ParserAPIConfig])
class AiBaoTestConfig {

    @Bean
    IConfigService fileBasedConfigService(Environment env) {
        new FileBasedConfigService(env)
    }

}
