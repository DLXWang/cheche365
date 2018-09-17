package com.cheche365.cheche.baoxian.flow.step

import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getSelectedCarModelFSRV

/**
 * 根据VIN码查询车型列表信息，主要针对进口车查询
 * @author taicw
 */
@Component
@Slf4j
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
class GetCarModelInfosByVIN extends AGetCarModelInfos {

    @Override
    protected getVehicleNameOrVinNo(context) {
        context.auto.vinNo
    }

    @Override
    protected getCarModelFSRV(context, result) {

        if (('0' == result.code || '00' == result.respCode) && result.carModelInfos) {
            context.carModelList = context.carModelList + result.carModelInfos
            getSelectedCarModelFSRV context, context.carModelList, result
        } else {
            if (context.carModelList) {
                log.warn '车型名称查询车型列表成功，VIN码查询车型列表失败'
                getSelectedCarModelFSRV context, context.carModelList, result
            } else {
                log.error '车型名称和VIN码查询车型列表都失败：{}', result.msg
                getFatalErrorFSRV result.msg
            }
        }
    }
}
