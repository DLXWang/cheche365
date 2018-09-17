package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV



/**
 * 获取缓存二维码
 */
@Component
@Slf4j
class GetCacheQRCode implements IStep {

    @Override
    run(context) {


        def persistentState = context.loadPersistentState?.call context, context.additionalParameters?.persistentState
        if (persistentState) {
            context << persistentState
        }

        def QRCode = context.newPaymentInfo?.paymentURL
        if (QRCode) {
            log.debug '成功获取缓存二维码 ：{}', QRCode
            getContinueFSRV '成功获取二维码'
        } else {
            getContinueFSRV '获取二维码失败'
        }


    }
}
