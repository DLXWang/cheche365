package com.cheche365.cheche.taikang.app.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


@Configuration
@Profile('!production')
class TaiKangNonProductionConfig {
}
