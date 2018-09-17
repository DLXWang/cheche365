package com.cheche365.cheche.cpic.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

/**
 * 初始化交强险报价
 * Created by xushao on 2015/7/22.
 */
@Component
@Slf4j
class InitTravelTax extends AInitTravelTax {

    private static final _URL_PATH_INITTRAVELTAX = 'cpiccar/salesNew/quotation/initTravelTax'

    @Override
    protected getApiPath() {
        _URL_PATH_INITTRAVELTAX
    }

}
