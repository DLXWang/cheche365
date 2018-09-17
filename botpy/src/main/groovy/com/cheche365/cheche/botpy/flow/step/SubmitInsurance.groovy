package com.cheche365.cheche.botpy.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.botpy.util.BusinessUtils.*
import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.FlowUtils.*
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSupplementInfoFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.populateNewQuoteRecordAndInsurances
import static groovyx.net.http.Method.PATCH
import static java.util.concurrent.TimeUnit.HOURS

/**
 * 提交核保
 */
@Component
@Slf4j
class SubmitInsurance extends ASyncResult implements IStep {


    private static final _API_PATH_SUBMIT_INSURANCE = '/proposals/'


    @Override
    run(context) {

        def path = _API_PATH_SUBMIT_INSURANCE + context.proposal_id
        def result = sendParamsAndReceive context, path, [status: 'UW'], PATCH, log

        if (result.error) {
            log.info '提交核保失败， {}', result.error
            getFatalErrorFSRV result.error
        } else {
            setNotificationIdForPath context, result, path
            context.globalContext.bindIfAbsentWithTTL(result.notification_id, context.order.orderNo, 2, HOURS)
            //为了核保回调状态失败时，集成层能拿到订单
            log.info '提交核保成功， 单号为：{}', getNotificationIdForPath(context, path)
            syncResult(context, path, 'Underwriting')
        }
    }

    @Override
    protected getApiPath(context, path) {
        path
    }

    @Override
    protected resolveResult(context, result, type) {
        def data = result?.data
        if (result.is_done) {
            if (result.is_success) {
                log.info '跟踪单号为：{}，当前投保单状态为：{}，message：{}，comment：{}', data.tracking_no, data.proposal_status, data.message, data.comment
                context.proposal_status = data.proposal_status
                context.newQuoteRecordAndInsurances = populateNewQuoteRecordAndInsurances(context, data.ic_nos.biz_prop, null, data.ic_nos.force_prop, null)
                getLoopBreakFSRV data.proposal_status
            } else if (data.is_manually) {
                getFatalErrorFSRV((([data.comment] + data.audit_records.comment) - null).join(','))
            } else {
                def accurateEngages = data.audit_records.comment.findResults { comment ->
                    context.engages.findResults { engage ->
                        comment?.contains(engage.code) ? engage : null
                    } ?: null
                }

                if (accurateEngages) {
                    log.info '金斗云提交核保需要添加特约险'
                    context.accurateEngages = accurateEngages.inject([], { prev, value ->
                        prev + value
                    })
                    getLoopContinueFSRV accurateEngages, '需要添加特约险'
                } else {
                    needUploadImageHandler(context, data)
                }
            }
        } else {
            getFatalErrorFSRV result
        }
    }

    private static needUploadImageHandler(context, data) {

        if (notNeedUploadImage(data)) {
            getFatalErrorFSRV((([data.comment] + data.audit_records.comment) - null).join(','))
        } else {
            log.info '核保失败， comment: {}， message：{}', data.comment, data.message

            context.isNeedUpdateImage = true
            getSupplementInfoFSRV(
                    [mergeMaps(_SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING, [meta: [orderNo: context.order.orderNo]])])
        }

    }

}
