package com.cheche365.cheche.baoxian

import org.springframework.boot.autoconfigure.EnableAutoConfiguration

/**
 * 上海
 */
@EnableAutoConfiguration
class Baoxian310000shanghaiFT extends ABaoXianFT {

    @Override
    protected final getAreaProperties() {
        [id: 110000L, name: '北京']
    }

}
