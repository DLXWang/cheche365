package com.cheche365.cheche.huanong.flow.step

import com.cheche365.cheche.common.flow.IStep
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.huanong.util.BusinessUtils.format



/**
 * 报价和核保报价对比，发现不一致，则推送到前端
 * Created by LIU GUO on 2018/7/2.
 */
class PushNewQuoteResult implements IStep {

    @Override
    run(Object context) {
        def formatInfo = format context.differenceInfo
        //持久化差异信息，供前台使用
        context.formattedDifferenceInfo = formatInfo
        getFatalErrorFSRV '两次报价结果不一致，不支持该车报价'
    }
}
