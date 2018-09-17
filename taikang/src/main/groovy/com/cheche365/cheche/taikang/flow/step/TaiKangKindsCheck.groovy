package com.cheche365.cheche.taikang.flow.step

import com.cheche365.cheche.common.flow.IStep
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.taikang.util.BusinessUtils.getUnsupportedInsurancePackagePropDescriptions
import groovy.util.logging.Slf4j



/**
 * 校验险别
 * Created by LIU GUO on 2018/6/4.
 */
@Slf4j
class TaiKangKindsCheck implements IStep {

    @Override
    Object run(Object context) {

        def checkList = context.kindCheckList        //可用险别列表
        def userInsurance = context.accurateInsurancePackage  //用户套餐

        def resultlist = getUnsupportedInsurancePackagePropDescriptions checkList, userInsurance

        if (!resultlist) {
            log.info '险别校验通过，流程继续。'
            getContinueFSRV resultlist
        } else {
            log.info '险别校验异常，不支持该险别：{}', resultlist
            getFatalErrorFSRV "险别校验异常，不支持该险别:${resultlist.join(',')}"
        }
    }
}
