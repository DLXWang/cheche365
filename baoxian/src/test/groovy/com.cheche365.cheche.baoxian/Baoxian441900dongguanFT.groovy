package com.cheche365.cheche.baoxian

import org.springframework.boot.autoconfigure.EnableAutoConfiguration

/**
 * 北京
 */
@EnableAutoConfiguration
class Baoxian441900dongguanFT extends ABaoXianFT {

    @Override
    protected final getAreaProperties() {
        [id: 441900L, name: '东莞']
    }

}
