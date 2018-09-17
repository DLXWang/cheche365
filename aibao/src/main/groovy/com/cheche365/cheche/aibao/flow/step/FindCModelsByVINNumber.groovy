package com.cheche365.cheche.aibao.flow.step

import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV



/**
 * 依据精友车型编码查询
 * Created by liuguo on 2018/09/5.
 */
@Slf4j
class FindCModelsByVINNumber extends ACreateFindICModels {

    @Override
    protected vehicleModelConditions(context) {
        [
            seachType: '1',//车型查询操作类型,
            searchKey: context.auto.vinNo,
        ]
    }

    @Override
    protected dealResultFsrv(context, result) {
        if (result.head.errorCode != '0000') {
            log.debug '根据车架号查车异常:{}，查车失败，后续流程终止', result.head.errorMsg
            getContinueFSRV '根据车架号查车失败，失败原因:${result.head.errorMsg}'
        } else {
            ArrayList carModelList = result.body?.vehicles as ArrayList
            if (carModelList) {
                log.debug '根据车架号车型查询成功，进入选车阶段'
                context.optionsByVinNo = carModelList
                context.resultByVinNo = result
                getContinueFSRV '查车完成，进入选车阶段'
            } else {
                log.info '根据车架号查车返回车型列表为空'
                getContinueFSRV '根据车架号查车返回车型列表为空'
            }
        }
    }

}
