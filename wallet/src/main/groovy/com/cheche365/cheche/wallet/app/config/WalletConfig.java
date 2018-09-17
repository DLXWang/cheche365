package com.cheche365.cheche.wallet.app.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.Resource;

/**
 * Created by mjg on 6/6/17.
 */

@Configuration
@ComponentScan({
    "com.cheche365.cheche.wallet",
    "com.cheche365.cheche.wallet.model",
    "com.cheche365.cheche.wallet.web.controller",
    "com.cheche365.cheche.wallet.core.app.config",
    "com.cheche365.cheche.soopay.app.config"
})

public class WalletConfig {
}
