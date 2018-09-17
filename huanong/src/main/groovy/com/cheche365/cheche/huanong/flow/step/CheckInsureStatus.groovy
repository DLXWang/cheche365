package com.cheche365.cheche.huanong.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV



/**
 * 判断核保返回状态，是否需要上传影像
 * Created by LIU GUO on 2018/6/13.
 */
@Slf4j
class CheckInsureStatus implements IStep {

    @Override
    run(context) {
        getContinueFSRV context.isUpdateImages ? '上传影像' : '未创建保单'
    }

}
