package com.cheche365.cheche.baoxian

import org.springframework.boot.autoconfigure.EnableAutoConfiguration

/**
 * 北京
 */
@EnableAutoConfiguration
class Baoxian220100changchunFT extends ABaoXianFT {

    @Override
    protected final getAreaProperties() {
        [id: 220100L, name: '长春']
    }

}
