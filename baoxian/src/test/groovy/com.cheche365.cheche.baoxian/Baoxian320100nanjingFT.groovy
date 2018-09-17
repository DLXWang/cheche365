package com.cheche365.cheche.baoxian

import org.springframework.boot.autoconfigure.EnableAutoConfiguration

/**
 * 北京
 */
@EnableAutoConfiguration
class Baoxian320100nanjingFT extends ABaoXianFT {

    @Override
    protected final getAreaProperties() {
        [id: 320100L, name: '南京']
    }

}
