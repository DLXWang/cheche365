package com.cheche365.cheche.manage.common.app.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

/**
 * Created by yinJianBin on 2017/5/27.
 */
@Configuration
@ComponentScan([
        'com.cheche365.cheche.manage.common',
        'com.cheche365.cheche.sms.client.app.config',
        'com.cheche365.cheche.wallet.core.app.config',
        'com.cheche365.cheche.email.app.config',
        'com.cheche365.cheche.web.app.config',
        'com.cheche365.cheche.bihu.app.config',
        'com.cheche365.cheche.parser.app.config'

])
@EnableJpaRepositories('com.cheche365.cheche.manage.common.repository')

@ImportResource([
        'classpath:META-INF/spring/manage-common-context.xml'
])
class ManageCommonConfig {

    @Configuration
    @ConfigurationProperties(prefix = "sms")
    class ChecheSmsSendSwitch {
        boolean smsSendEanble = false
        String send_enable

        String getSend_enable() {
            return send_enable
        }

        void setSend_enable(String send_enable) {
            smsSendEanble = Boolean.valueOf(send_enable)
            this.send_enable = send_enable
        }
    }
}
