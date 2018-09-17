package com.cheche365.cheche.picc.flow.step.v2

import groovy.util.logging.Slf4j

import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_OWNER_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSelectedCarModelFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV



/**
 * 报价时用的步骤
 * 精友地区根据品牌型号获取车型列表
 */
@Slf4j
class FindCarModelJYQuery extends AFindCarModelJYQuery {

    @Override
    protected handleResultFSRV(context, result) {
        def brandModelList = result.body?.queryVehicle
        if (brandModelList) {
            if (context.autoJYModel == null) {
                def autoModel = context.additionalParameters?.supplementInfo?.autoModel
                context.autoJYModel = autoModel ? autoModel.tokenize('_')[0] : null
            }
            log.info "picc报价时的查车步骤 autoJYModel: {}", context.autoJYModel

            log.info "picc报价时的查车步骤 selectedAutoModel: {}", context.additionalParameters.supplementInfo?.selectedAutoModel
            getSelectedCarModelFSRV(context, brandModelList, result, [
                updateContext: { ctx, res, fsrv ->
                    // 用于findCarModel步骤做参数使用
                    ctx.carInfo = fsrv[2]
                }, supplementInfoAutoModel: context.autoJYModel])
        } else {
            log.error '无法获得车型，通常是车辆/人员信息有误导致的：{}', result
            getValuableHintsFSRV(context,
                [
                    _VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING,
                    _VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING,
                    _VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING,
                    _VALUABLE_HINT_OWNER_TEMPLATE_QUOTING
                ])
        }
    }

}
