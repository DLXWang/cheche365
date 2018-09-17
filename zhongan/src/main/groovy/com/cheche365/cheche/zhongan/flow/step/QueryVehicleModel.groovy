package com.cheche365.cheche.zhongan.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants._CAR_MODEL_LIST_MAX_SIZE
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_OWNER_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.getModelsByBrands
import static com.cheche365.cheche.parser.util.BusinessUtils.getSelectedCarModelFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV
import static com.cheche365.cheche.zhongan.util.BusinessUtils.sendAndReceive



/**
 * 车型查询
 */
@Component
@Slf4j
class QueryVehicleModel implements IStep {

    private static final _SERVICE_NAME = 'zhongan.castle.policy.queryVehicleModel'

    private static final _GET_MODELS_BY_BRAND = { context, brand ->
        def params = [
            pageNo              : '1',
            pageSize            : _CAR_MODEL_LIST_MAX_SIZE,
            vehicleBrandModelKey: context.auto.autoType?.code,
            brandCode           : brand.brandCode
        ]
        def result = sendAndReceive(context, this.class.name, _SERVICE_NAME, params)

        result.vehicleModelList ?: []
    }

    @Override
    def run(Object context) {
        def params = [
            pageNo              : '1',
            pageSize            : _CAR_MODEL_LIST_MAX_SIZE,
            vehicleBrandModelKey: context.auto.autoType?.code
        ]

        def result = sendAndReceive(context, this.class.name, _SERVICE_NAME, params)
        log.info '车型查询result = {}', result

        if ('0' == result.result) {
            def modelList = result.brandList ? getModelsByBrands(context, result.brandList, result.vehicleModelList, _GET_MODELS_BY_BRAND) : null

            if (!modelList) {
                return getValuableHintsFSRV(context, [
                    _VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING,
                ])
            }
            if(context.additionalParameters.referToOtherAutoModel) {
                def selectedAutoModelValue = context.additionalParameters.supplementInfo?.selectedAutoModel
                def autoModelValue = context.additionalParameters?.supplementInfo?.autoModel
                log.info "车型查询成功，进入选车阶段，VehicleList: {},集成层给的selectedAutoModelValue :{},集成层给的autoModelValue :{}", modelList, selectedAutoModelValue, autoModelValue
                getSelectedCarModelFSRV(context, modelList, result)
            } else {
                log.info "车型查询成功，进入选车阶段，VehicleList: {},根据默认规则选取车型", modelList
                context.optionsByCode = modelList
                context.resultByCode = result
                def autoModelValue = context.additionalParameters?.supplementInfo?.autoModel
                //根据询价返回车型选择时，有可能选择的车型不在此时的列表中
                if (autoModelValue) {
                    def vehicleModel = modelList.find { it ->
                        context.getVehicleOption(context, it).value == autoModelValue
                    }
                    if (!vehicleModel) {
                        context.autoModel = autoModelValue
                        context.additionalParameters.supplementInfo.autoModel = null
                    }
                }
                getContinueFSRV '精确车型查询'
            }

        } else {
            log.info "车型查询失败，不能进入选车阶段，推送有价值的信息，车牌号：{} 车主姓名：{},品牌型号：{}", context.licensePlateNo?.owner, context?.auto?.owner, context.auto.autoType?.code
            getValuableHintsFSRV(context, [
                _VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING,
                _VALUABLE_HINT_OWNER_TEMPLATE_QUOTING
            ])
        }
    }


}
