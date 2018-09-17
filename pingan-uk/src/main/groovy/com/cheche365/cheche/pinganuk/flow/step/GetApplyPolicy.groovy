package com.cheche365.cheche.pinganuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV

/**
 * 查询投保单
 * Created by wangmz on 2016.11.03
 */
@Component
@Slf4j
class GetApplyPolicy implements IStep {

    @Override
    run(context) {

        def applyPolicyList = context.applyPolicyList
        if (applyPolicyList) {
            getContinueFSRV 'B2' == applyPolicyList.finalStatus ? '代缴费状态' : '其他状态'
        } else {
            getLoopBreakFSRV null
        }
    }

}
