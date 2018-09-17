package com.cheche365.cheche.baoxian

import org.springframework.boot.autoconfigure.EnableAutoConfiguration

/**
 * 北京
 */
@EnableAutoConfiguration
class Baoxian530100kunmingFT extends ABaoXianFT {

    @Override
    protected final getAreaProperties() {
        [id: 530100L, name: '昆明']
    }

}
