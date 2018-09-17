package com.cheche365.cheche.botpy.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV



/**
 * 选择报价流程
 */
@Component
@Slf4j
class SelectICModelsFlow implements IStep {
    @Override
    run(context) {
        if(context.additionalParameters.supplementInfo.autoModel) {
            //是否是用户自己选择的车型
            //由于后续续保修正车型、自动修正车型等会设置autoModel，故在此处专门设置此字段用于判断
            context.isUserSelectModel = 1
        }
        if(context.additionalParameters.referToOtherAutoModel) {
            getContinueFSRV '车型模糊匹配'
        } else {
            getContinueFSRV '精确车型查询'
        }
    }

}
