package com.cheche365.cheche.zhongan

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jms.JndiConnectionFactoryAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration

/**
 * 济南
 */
//@EnableAutoConfiguration(exclude = HibernateJpaAutoConfiguration.class)
//@EnableAutoConfiguration(exclude = [JndiConnectionFactoryAutoConfiguration.class,DataSourceAutoConfiguration.class,
//    HibernateJpaAutoConfiguration.class,JpaRepositoriesAutoConfiguration.class])
@EnableAutoConfiguration(exclude = [HibernateJpaAutoConfiguration.class,DataSourceAutoConfiguration.class,JpaRepositoriesAutoConfiguration])
class Zhongan370100jinanFT extends AZhonganFT {

    @Override
    protected getAreaProperties() {
        [id: 370100L, name: '济南市']
    }

}
