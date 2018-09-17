package com.cheche365.cheche.picc.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getContinueWithIgnorableErrorFSRV

/**
 * 检查是否可以快速续保
 */
@Component
@Slf4j
class CheckQuickRenewal implements IStep {

    @Override
    run(context) {

        if (context.defaultPackageJson?.errorMsg) {

            log.info '快速续保成功：{}', context.defaultPackageJson?.errorMsg
            getContinueFSRV true
        } else  {
            log.info '快速续保失败。'
            getContinueWithIgnorableErrorFSRV false, '快速续保失败'
        }

    }

}
