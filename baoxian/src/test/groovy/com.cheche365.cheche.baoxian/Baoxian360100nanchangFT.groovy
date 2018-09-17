package com.cheche365.cheche.baoxian

import org.springframework.boot.autoconfigure.EnableAutoConfiguration

/**
 * 北京
 */
@EnableAutoConfiguration
class Baoxian360100nanchangFT extends ABaoXianFT {

    @Override
    protected final getAreaProperties() {
        [id: 360100L, name: '南昌']
    }

}
