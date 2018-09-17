package com.cheche365.cheche.cpic.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

/**
 * 计算交强险报价
 * Created by xushao on 2015/7/22.
 */
@Component
@Slf4j
class CalcTravelTax extends ACalcTravelTax {

    private final _API_PATH_CALCPREMIUM = 'cpiccar/salesNew/quotation/calcTravelTax'

    @Override
    protected getApiPath() {
        _API_PATH_CALCPREMIUM
    }

}
