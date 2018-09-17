package com.cheche365.cheche.piccuk.flow.step

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV



/**
 * 保存
 */
@Component
@Slf4j
class InsertUpdateSpecialAgreement extends AInsertUpdate {

    @Override
    protected getUpdateParameters(context) {
        context.insertArgs.body
    }

    @Override
    protected getFsrv(result) {
        getLoopContinueFSRV result, '继续执行下一循环'
    }

}
