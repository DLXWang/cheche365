package com.cheche365.cheche.bihu.app.config

import com.cheche365.cheche.parser.app.config.ParserConfig
import com.cheche365.cheche.parserapi.app.config.ParserAPIConfig
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import


/**
 * 壁虎测试spring配置
 * Created by suyaqiang on 2017/12/11.
 */
@Configuration
@Import([BihuConfig, ParserConfig, ParserAPIConfig])
class BihuTestConfig {
}
