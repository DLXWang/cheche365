package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants.get_SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSupplementInfoFSRV
import static com.cheche365.flow.core.util.FlowUtils.getDoInsuranceFailedFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV



/**
 * 查询报价信息
 * [1:暂存, 2:待核保, 4:退回修改, 5:核保通过, 7:生效, 8:删除]
 */
@Component
@Slf4j
class QueryQuotationPolicy extends QueryQuotation implements IStep {

    @Override
    run(Object context) {
        def result = super.postRequest(context, context.quotationNo)
        if ('success' == result?.message.code && result?.meta?.pageSize > 0) {
            def states = []
            result.result.each {
                states += it.quotationState
            }
            for (quotationState in states) {
                if (quotationState == '2') {//待核保
                    log.info '待核保，请等待'
                    return getDoInsuranceFailedFSRV([quotationNo: context.quotationNo, type: this.class.name],
                        '努力核保中，大约10分钟后查看结果')
                }
                if (quotationState == '4') {
                    log.info '退回修改'
                    context.proposal_status = '需上传影像'
                    return getSupplementInfoFSRV([mergeMaps(_SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING, [meta:
                                                                                                                  [orderNo: context.order.orderNo]])])
                }
                if (quotationState == '5') {
                    log.info '核保通过'
                    context.proposal_status = '核保成功'
                    return getContinueFSRV('核保通过')
                } else {
                    return getKnownReasonErrorFSRV('太平洋返回状态为：' + quotationState)
                }
            }
        } else {
            log.debug '未查询到结果'
            getKnownReasonErrorFSRV '未查询到结果'
        }

    }
}
