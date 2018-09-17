package com.cheche365.cheche.baoxian

import org.springframework.boot.autoconfigure.EnableAutoConfiguration

/**
 * 北京
 */
@EnableAutoConfiguration
class Baoxian210100shenyangFT extends ABaoXianFT {

    @Override
    protected final getAreaProperties() {
        [id: 210100L, name: '沈阳']
    }

}
