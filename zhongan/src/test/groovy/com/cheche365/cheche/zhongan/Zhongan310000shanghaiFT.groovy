package com.cheche365.cheche.zhongan

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration

/**
 * 上海
 */
//@EnableJpaRepositories('com.cheche365.cheche.core.model.repository')
//@EnableAutoConfiguration
@EnableAutoConfiguration(exclude = HibernateJpaAutoConfiguration.class)
class Zhongan310000shanghaiFT extends AZhonganFT {


    @Override
    protected final getAreaProperties() {
        [id: 310000L, name: '上海']
    }

}
