package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.core.constants.ModelConstants._QUOTE_RECORD_STATE_ALLOWED_INSURE
import static com.cheche365.cheche.core.constants.ModelConstants._QUOTE_RECORD_STATE_FAIL
import static com.cheche365.cheche.core.constants.ModelConstants._QUOTE_RECORD_STATE_NEED_ALTER
import static com.cheche365.cheche.core.constants.ModelConstants._QUOTE_RECORD_STATE_NOT_ALLOWED_INSURE
import static com.cheche365.cheche.core.constants.ModelConstants._QUOTE_RECORD_STATE_WAITING
import static groovyx.net.http.ContentType.JSON



/**
 * @author: lp
 * @date: 2018/6/20 11:48
 * 获取报价单状态，主要支持需要上传影像的单子
 *
 */
@Component
@Slf4j
class GetQuoteRecordState extends QueryQuotation implements IStep {

    private static final _API_PATH_QUERY_AUDIT_INFORMATION = '/ecar/auditInformation/queryAuditInformation'

    @Override
    run(Object context) {
        def numbers = context.numbers
        def quoteRecordStateResult = numbers.collect {
            buildResult(context, super.postRequest(context, it.quoteNo), it)
        }

        context.newGetQuoteRecordState = quoteRecordStateResult
        getContinueFSRV quoteRecordStateResult
    }

    /**
     * 构建result返回给web
     * @param context
     * @param result
     * @return
     */
    private static buildResult(context, result, number) {
        if ('success' == result?.message.code && result?.meta?.pageSize > 0) {
            def quotationState = result.result[0].quotationState
            // [1:暂存, 2:待核保, 3:拒保, 4:退回修改, 5:核保通过,6:拒保不可申诉, 7:生效, 8:删除]
            if (quotationState == '2') {//待核保
                log.info '待核保，请等待'
                return _GET_QUOTE_RECORD_RESULT(number.orderNo, _QUOTE_RECORD_STATE_WAITING, '人工正在核保，请稍等')
            }
            if (quotationState == '4') {
                log.info '需要修改'
                def auditInfo = getAuditInformation(context, number.quoteNo)
                return _GET_QUOTE_RECORD_RESULT(number.orderNo, _QUOTE_RECORD_STATE_NEED_ALTER, '人工审核意见：' + _SORT(auditInfo, 'auditTime')[0].auditOpinion)
            }
            if (quotationState == '5') {
                log.info '核保通过'
                return _GET_QUOTE_RECORD_RESULT(number.orderNo, _QUOTE_RECORD_STATE_ALLOWED_INSURE, '核保通过')
            }
            if (quotationState == '6' || quotationState == '3') {
                log.info '拒绝核保'
                return _GET_QUOTE_RECORD_RESULT(number.orderNo, _QUOTE_RECORD_STATE_NOT_ALLOWED_INSURE, '核保拒绝')
            }
        }

        log.error '报价单号：{}，未获取正确的报价单状态：{}', number.quoteNo, result
        _GET_QUOTE_RECORD_RESULT(number.orderNo, _QUOTE_RECORD_STATE_FAIL, '未获取到正确的报价单状态')
    }

    /**
     * 如果是需要修改的单子，需要获取人工审核意见
     * @param quoteNo
     */
    private static getAuditInformation(context, quoteNo) {
        RESTClient client = context.client
        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_QUERY_AUDIT_INFORMATION,
            body              : [
                meta  : [:],
                redata: [
                    //根据报价单号是多条；投保单查询只有一条结果
                    quotationNo: quoteNo
                ]
            ]
        ]
        def result = client.post args, { resp, json -> json }
        log.debug '审核结果：{}', result
        result.result
    }


    static final _SORT = { list, sortValue ->
        list.sort { former, latter ->
            return -former[sortValue].compareTo(latter[sortValue])
        }
    }

    static final _GET_QUOTE_RECORD_RESULT = { orderNo, quoteRecordStatus, message ->
        [
            orderNo          : orderNo,
            quoteRecordStatus: quoteRecordStatus,
            message          : message
        ]
    }

}
