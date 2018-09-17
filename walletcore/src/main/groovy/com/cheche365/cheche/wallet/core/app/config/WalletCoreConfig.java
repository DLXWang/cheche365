package com.cheche365.cheche.wallet.core.app.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author mjg
 */
@Configuration
@ComponentScan({
    "com.cheche365.cheche.wallet.model",
    "com.cheche365.cheche.wallet.service",
    "com.cheche365.cheche.soopay.app.config"
})
@EnableJpaRepositories("com.cheche365.cheche.wallet.repository")

public class WalletCoreConfig {
}
