package com.cheche365.cheche.baoxian

import org.springframework.boot.autoconfigure.EnableAutoConfiguration

/**
 * 北京
 */
@EnableAutoConfiguration
class Baoxian130100shijiazhuangFT extends ABaoXianFT {

    @Override
    protected final getAreaProperties() {
        [id: 130100L, name: '石家庄']
    }

}
