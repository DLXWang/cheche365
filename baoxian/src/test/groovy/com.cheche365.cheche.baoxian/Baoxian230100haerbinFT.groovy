package com.cheche365.cheche.baoxian

import org.springframework.boot.autoconfigure.EnableAutoConfiguration

/**
 * 北京
 */
@EnableAutoConfiguration
class Baoxian230100haerbinFT extends ABaoXianFT {

    @Override
    protected final getAreaProperties() {
        [id: 230100L, name: '哈尔滨']
    }

}
