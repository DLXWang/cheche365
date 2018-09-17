package com.cheche365.cheche.botpy.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.botpy.util.BusinessUtils.getNotificationIdForPath
import static com.cheche365.cheche.botpy.util.BusinessUtils.sendParamsAndReceive
import static com.cheche365.cheche.botpy.util.BusinessUtils.setNotificationIdForPath
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.flow.core.util.FlowUtils.getDoInsuranceFailedFSRV
import static groovyx.net.http.Method.PATCH
import static java.util.concurrent.TimeUnit.HOURS



/**
 * 上传照片后
 * 重新提交核保
 */
@Component
@Slf4j
class SubmitInsuranceAgain implements IStep {

    private static final _API_PATH_SUBMIT_INSURANCE = '/proposals/'

    @Override
    run(context) {

        def path = _API_PATH_SUBMIT_INSURANCE + context.proposal_id
        def result = sendParamsAndReceive context, path, [sync_photo_to_ic: true], PATCH, log

        if (result.error) {
            log.info '提交核保失败， {}', result.error
            getFatalErrorFSRV result.error
        } else {
            setNotificationIdForPath context, result, path
            context.globalContext.bindIfAbsentWithTTL(result.notification_id, context.order.orderNo, 2, HOURS)
            //为了核保回调状态失败时，集成层能拿到订单
            log.info '提交核保成功， 单号为：{}', getNotificationIdForPath(context, path)
            getDoInsuranceFailedFSRV([proposal_id: context.proposal_id, type: this.class.name], '努力核保中，大约10分钟后查看结果')
        }
    }

}
