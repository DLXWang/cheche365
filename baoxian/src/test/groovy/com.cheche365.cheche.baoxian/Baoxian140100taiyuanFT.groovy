package com.cheche365.cheche.baoxian

import org.springframework.boot.autoconfigure.EnableAutoConfiguration

/**
 * 北京
 */
@EnableAutoConfiguration
class Baoxian140100taiyuanFT extends ABaoXianFT {

    @Override
    protected final getAreaProperties() {
        [id: 140100L, name: '石家庄']
    }

}
