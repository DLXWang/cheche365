package com.cheche365.cheche.pinganuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV


/**
 * 悟空采集身份信息
 */
@Component
@Slf4j
class PickInfoWithGshell implements IStep {

    @Override
    run(context) {

        try {
            def result = context.getInformationService.getInformation '', [owner: context.order.applicantName ?: context.auto.owner, identity: context.order.applicantIdNo ?: context.auto.identity]
            if (result) {
                log.info '悟空身份信息采集成功'
                getContinueFSRV result
            } else {
                log.error '悟空身份信息采集失败'
                getFatalErrorFSRV  '悟空身份信息采集失败，稍后重试'
            }
        } catch (ex) {
            log.error '悟空身份信息采集时出错：{}', ex.message
            getFatalErrorFSRV  '悟空身份信息采集失败，稍后重试'
        }
    }

}
