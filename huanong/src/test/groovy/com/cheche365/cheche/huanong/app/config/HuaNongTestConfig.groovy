package com.cheche365.cheche.huanong.app.config

import com.cheche365.cheche.core.service.FileBasedConfigService
import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.huanong.config.HuaNongConfig
import com.cheche365.cheche.huanong.config.HuaNongNonProductionConfig
import com.cheche365.cheche.huanong.config.HuaNongProductionConfig
import com.cheche365.cheche.parser.app.config.ParserConfig
import com.cheche365.cheche.parserapi.app.config.ParserAPIConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment



/**
 * 壁虎测试spring配置
 * Created by suyaqiang on 2017/12/11.
 */
@Configuration
@Import([HuaNongConfig,HuaNongProductionConfig, HuaNongNonProductionConfig, ParserConfig, ParserAPIConfig])
class HuaNongTestConfig {

    @Bean
    IConfigService fileBasedConfigService(Environment env) {
        new FileBasedConfigService(env)
    }

}
