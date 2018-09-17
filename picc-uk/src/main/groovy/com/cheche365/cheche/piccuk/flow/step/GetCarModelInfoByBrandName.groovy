package com.cheche365.cheche.piccuk.flow.step

import groovy.util.logging.Slf4j

import static com.cheche365.cheche.piccuk.util.BusinessUtils.changeExhaust
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV



/**
 * 通过品牌型号获取车型信息
 */
@Slf4j
class GetCarModelInfoByBrandName extends AGetCarModelInfoByVinOrBrand {


    @Override
    protected handleModelCarList(context, result) {
        context.optionsByCode = changeExhaust(result)
        context.resultByCode = context.optionsByCode
        context.options.byCode = context.optionsByCode
    }

    @Override
    protected getBrandNameParameters(context) {
        [
            brandName: context.auto.autoType.code
        ]
    }

    @Override
    protected handleGetCarModelFailed(context) {
        log.error '查询车型失败'
        getKnownReasonErrorFSRV '查询车型失败'
    }

}
