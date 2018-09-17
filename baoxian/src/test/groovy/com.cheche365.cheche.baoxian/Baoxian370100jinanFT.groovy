package com.cheche365.cheche.baoxian

import org.springframework.boot.autoconfigure.EnableAutoConfiguration

/**
 * 北京
 */
@EnableAutoConfiguration
class Baoxian370100jinanFT extends ABaoXianFT {

    @Override
    protected final getAreaProperties() {
        [id: 370100L, name: '济南']
    }

}
