package com.cheche365.cheche.baoxian

import org.springframework.boot.autoconfigure.EnableAutoConfiguration

/**
 * 北京
 */
@EnableAutoConfiguration
class Baoxian110000beijingFT extends ABaoXianFT {

    @Override
    protected final getAreaProperties() {
        [id: 110000L, name: '北京']
    }

}
