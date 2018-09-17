package com.cheche365.cheche.botpy.client

import groovy.util.logging.Slf4j

/**
 * 车型列表服务测试类
 */
@Slf4j
class Botpyats110000beijingFT extends ABotpyFT {
    @Override
    protected final getAreaProperties() {
        [id: 110000L, name: '北京']
    }
}
