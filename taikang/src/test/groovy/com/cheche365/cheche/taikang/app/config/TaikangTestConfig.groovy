package com.cheche365.cheche.taikang.app.config

import com.cheche365.cheche.core.service.FileBasedConfigService
import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.parser.app.config.ParserConfig
import com.cheche365.cheche.parserapi.app.config.ParserAPIConfig
import com.cheche365.cheche.taikang.app.config.TaiKangConfig
import com.cheche365.cheche.taikang.app.config.TaiKangNonProductionConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment



/**
 * 泰康测试spring配置
 */
@Configuration
@Import([TaiKangConfig, TaiKangNonProductionConfig, ParserConfig, ParserAPIConfig])
class TaikangTestConfig {

    @Bean
    IConfigService fileBasedConfigService(Environment env) {
        new FileBasedConfigService(env)
    }

}
