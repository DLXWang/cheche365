package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.repository.ApiPartnerPropertiesRepository
import com.cheche365.cheche.core.util.RuntimeUtil
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.Canonical

import javax.persistence.*

@Entity
@JsonIgnoreProperties(ignoreUnknown = true, value = ["value"])
@Canonical
class ApiPartnerProperties {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id
    @ManyToOne
    ApiPartner partner
    @Column(name = '`key`')
    String key
    @Column(name = '`value`')
    String value

    static class Key {
        public static String SYNC_SIGN_METHOD = 'sync.sign.method' //value 可选项: HMAC-SHA1 、RSA-SHA1 、 CUSTOM(自定义)
        public static String SYNC_APP_ID = 'sync.app.id'
        public static String SYNC_APP_SECRET = 'sync.app.secret'
        public static String SYNC_USER_AUTO_URL = 'sync.user.auto.url'
        public static String SYNC_ORDER_URL = 'sync.order.url' // 创建、更新订单同步为同一个url
        public static String SYNC_AGENT_URL = 'sync.agent.url'
        public static String SYNC_ORDER_EMAIL = 'sync.order.email' // 订单同步邮箱
        public static String SYNC_ORDER_CREATE_URL = 'sync.order.create.url'
        public static String SYNC_ORDER_UPDATE_URL = 'sync.order.update.url'
        public static String SYNC_ENABLE_BUFFERING = 'sync.enable.buffering' // header是否包含content-length，默认不设

    }

    static ApiPartnerProperties findByPartnerAndKey(ApiPartner partner, String key) {
        ApplicationContextHolder.getApplicationContext().getBean(ApiPartnerPropertiesRepository)
            .findByPartner(partner).find { it.key == (RuntimeUtil.isProductionEnv() ? 'production.' + key : key) }
    }

}
