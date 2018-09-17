package com.cheche365.cheche.baoxian

import org.springframework.boot.autoconfigure.EnableAutoConfiguration

/**
 * 北京
 */
@EnableAutoConfiguration
class Baoxian340100hefeiFT extends ABaoXianFT {

    @Override
    protected final getAreaProperties() {
        [id: 340100L, name: '合肥']
    }

}
