package com.cheche365.cheche.baoxian

import org.springframework.boot.autoconfigure.EnableAutoConfiguration

/**
 * 北京
 */
@EnableAutoConfiguration
class Baoxian440300shenzhenFT extends ABaoXianFT {

    @Override
    protected final getAreaProperties() {
        [id: 440300L, name: '深圳']
    }

}
