package com.cheche365.cheche.piccuk.flow.step

import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getContinueWithIgnorableErrorFSRV
import static com.cheche365.cheche.piccuk.util.BusinessUtils.changeExhaust


/**
 * 获取车型信息
 */
@Slf4j
class GetCarModelInfoByVIN extends AGetCarModelInfoByVinOrBrand {

    @Override
    protected handleModelCarList(context, result) {
        context.optionsByVinNo = changeExhaust(result)
        context.resultByVinNo = context.optionsByVinNo
        context.options.byVinNo = context.optionsByVinNo
    }

    @Override
    protected getBrandNameParameters(context) {
        [
            brandName: context.carModelMsg
        ]
    }

    @Override
    protected handleGetCarModelFailed(context) {
        log.error context.carModelMsg ?: '查询车型失败'
        getContinueWithIgnorableErrorFSRV '通过品牌型号获取车型列表',  context.carModelMsg ?: '查询车型失败'
    }

}
