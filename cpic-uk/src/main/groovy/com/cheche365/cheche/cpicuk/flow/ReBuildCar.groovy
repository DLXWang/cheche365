package com.cheche365.cheche.cpicuk.flow

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV



/**
 * 再次获取车型
 */
@Component
@Slf4j
class ReBuildCar implements IStep {

    @Override
    run(context) {
        context.additionalParameters.supplementInfo.autoModel = ''
        context.selectedCarModel = null
        context.optionsByVinNo = context.optionsByVinNo.findAll {
            it.moldCharacterCode in context.autoModels
        }
        context.resultByVinNo = context.optionsByVinNo
        context.optionsByCode = context.optionsByCode.findAll {
            it.moldCharacterCode in context.autoModels
        }
        context.resultByCode = context.optionsByCode
        /**
         * 此步骤是  调整车型才会进入的  当前的context.additionalParameters.optionsSource 是上一次 默认选车的值，自动调整后
         * 不确定正确车型在哪个list  这里处理一下 ，给一个正确的 optionsource （有可能两个列表都有，也有可能只有一个列表有)再次报价
         */
        context.additionalParameters.optionsSource = context.resultByVinNo ? 'byVinNo' : 'byCode'
        //过滤了两个list
        context.options =  [byCode : context.resultByCode ,  byVinNo: context.resultByVinNo]
        /**
         *   referToOtherAutoModel 构建
         *  context.additionalParameters.autoModel = [
         *             selected: autoModel,
         *             options : getVehicleOptions(context, null, getVehicleOption,
         *             carModelListMaxSize)
         *         ]
         *      自测需要放开
         */
        //  context.additionalParameters.referToOtherAutoModel = false
        getContinueFSRV context
    }

}
