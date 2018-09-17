package com.cheche365.cheche.baoxian

import org.springframework.boot.autoconfigure.EnableAutoConfiguration

/**
 * 北京
 */
@EnableAutoConfiguration
class Baoxian640100yinchuanFT extends ABaoXianFT {

    @Override
    protected final getAreaProperties() {
        [id: 640100L, name: '银川']
    }

}
