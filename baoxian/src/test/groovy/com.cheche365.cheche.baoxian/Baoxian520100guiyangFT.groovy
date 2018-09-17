package com.cheche365.cheche.baoxian

import org.springframework.boot.autoconfigure.EnableAutoConfiguration

/**
 * 北京
 */
@EnableAutoConfiguration
class Baoxian520100guiyangFT extends ABaoXianFT {

    @Override
    protected final getAreaProperties() {
        [id: 520100L, name: '贵阳']
    }

}
