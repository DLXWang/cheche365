package com.cheche365.cheche.piccuk.flow.step

import groovy.util.logging.Slf4j

import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSelectedCarModelFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV

/**
 * 获取车型
 */
@Slf4j
class GetCarModelInfo extends AGetCarModelInfo {

    @Override
    protected handleCarModelInfoResult(context, carModelInfo) {
        if (carModelInfo && carModelInfo[0].id) {
            getSelectedCarModelFSRV context, carModelInfo, carModelInfo
        } else {
            log.debug '查询车型失败，{}', carModelInfo.refCode2
            getValuableHintsFSRV(context,
                [
                    _VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING,
                    _VALUABLE_HINT_ENGINE_NO_TEMPLATE_QUOTING
                ])
        }
    }

}
