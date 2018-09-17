package com.cheche365.cheche.huanong.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.PropertySource



/**
 * 华农的Spring Production配置注释类
 */
@Configuration
@Profile('!production')
class HuaNongNonProductionConfig {
}
