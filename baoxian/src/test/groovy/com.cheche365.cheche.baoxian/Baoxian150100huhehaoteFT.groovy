package com.cheche365.cheche.baoxian

import org.springframework.boot.autoconfigure.EnableAutoConfiguration

/**
 * 北京
 */
@EnableAutoConfiguration
class Baoxian150100huhehaoteFT extends ABaoXianFT {

    @Override
    protected final getAreaProperties() {
        [id: 150100L, name: '呼和浩特']
    }

}
