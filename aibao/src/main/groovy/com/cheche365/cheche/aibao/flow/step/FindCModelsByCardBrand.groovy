package com.cheche365.cheche.aibao.flow.step

import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV



/**
 * 依据车型查询
 * Created by liuguo on 2018/09/5.
 */
@Slf4j
class FindCModelsByCardBrand extends ACreateFindICModels {

    protected vehicleModelConditions(context) {
        [
            seachType: '5',//车型查询操作类型,
            searchKey: context.auto.autoType.code,
        ]
    }

    @Override
    protected dealResultFsrv(context, result) {
        if (result.head.errorCode != '0000') {
            log.info '根据厂牌型号查车异常:{}，继续根据车架号查车', result.head.errorMsg
            getContinueFSRV '根据品牌型号未匹配到车型'
        } else {
            ArrayList carModelList = result.body?.vehicles as ArrayList
            if (carModelList) {
                log.debug '根据品牌型号查车成功'
                context.optionsByCode = carModelList
                context.resultByCode = result
                getContinueFSRV '根据品牌查车成功，继续根据车架号查车'
            } else {
                log.info '根据厂牌型号返回车型列表为空，继续根据车架号查车'
                getContinueFSRV '根据品牌型号未匹配到车型'
            }
        }
    }

}
