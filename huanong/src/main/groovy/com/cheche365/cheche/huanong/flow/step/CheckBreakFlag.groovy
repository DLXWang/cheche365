package com.cheche365.cheche.huanong.flow.step

import com.cheche365.cheche.common.flow.IStep

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV



/**
 * 控制最外层循环流程
 * Created by LIU GUO on 2018/8/8.
 */
class CheckBreakFlag implements IStep {

    @Override
    run(Object context) {
        if (context.breakflag) {
            getLoopBreakFSRV '跳出外层循环'
        } else {
            getContinueFSRV '继续流程'
        }
    }

}
