package com.cheche365.cheche.baoxian

import org.springframework.boot.autoconfigure.EnableAutoConfiguration

/**
 * 北京
 */
@EnableAutoConfiguration
class Baoxian620100lanzhouFT extends ABaoXianFT {

    @Override
    protected final getAreaProperties() {
        [id: 620100L, name: '兰州']
    }

}
