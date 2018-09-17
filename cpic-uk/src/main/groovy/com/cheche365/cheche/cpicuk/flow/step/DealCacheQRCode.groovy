package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV



/**
 * 处理缓存二维码
 */
@Component
@Slf4j
class DealCacheQRCode implements IStep {

    @Override
    run(context) {

        def QRCode = context.newPaymentInfo?.paymentURL
        if (QRCode) {
            context.newPaymentInfo?.paymentURL = QRCode.replaceAll('\\\\n', '\\n')
            log.debug '成功获取二维码 ：{}', context.newPaymentInfo?.paymentURL
            getContinueFSRV null
        } else {
            getKnownReasonErrorFSRV '获取二维码失败'
        }
    }

}
