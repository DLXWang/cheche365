package com.cheche365.cheche.baoxian

import org.springframework.boot.autoconfigure.EnableAutoConfiguration

/**
 * 北京
 */
@EnableAutoConfiguration
class Baoxian440100guangzhouFT extends ABaoXianFT {

    @Override
    protected final getAreaProperties() {
        [id: 440100L, name: '广州']
    }

}
