package com.cheche365.cheche.baoxian.flow.step

import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * 根据车型品牌名称查询车型列表信息，主要针对国产车查询
 * @author taicw
 */
@Component
@Slf4j
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
class GetCarModelInfosByName extends AGetCarModelInfos {

    @Override
    protected getVehicleNameOrVinNo(context) {
        context.auto.autoType.code - '牌'
    }


    @Override
    protected getCarModelFSRV(context, result) {
        if (('0' == result.code || '00' == result.respCode) && result.carModelInfos) {
            context.carModelList = result.carModelInfos ?: []
            getContinueFSRV null
        } else {
            context.carModelList = []
            log.warn '通过车型名称查询车型列表失败，继续VIN码查询车型列表。错误信息：{}', result.msg
            getContinueFSRV result.msg
        }
    }
}
