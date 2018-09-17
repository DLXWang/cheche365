package com.cheche365.cheche.piccuk.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC



/**
 * 保存收款信息
 */
@Component
@Slf4j
class SaveByJF implements IStep {

    private static final _PAY_API_SAVE_BY_JF = '/cbc/jf/saveByJF.do'

    @Override
    Object run(Object context) {
        log.debug '保存支付信息'
        RESTClient client = context.client
        client.uri = context.cbc_host

        def args = [
            requestContentType: URLENC,
            contentType       : JSON,
            path              : _PAY_API_SAVE_BY_JF,
            body              : generateRequestParameters(context, this)
        ]

        context.payTypeNo = args.body.payTypeNo
        log.debug 'args: {}', args

        def result = client.post args, { resp, json ->
            json
        }

        log.debug 'result {}', result
        if (result) {
            // 成功保存
            if (result.totalRecords == 1) {
                log.debug 'totalRecords = 1 , exchangeNo {}', result.data[0].SCol1
                context.exchangeNo = result.data[0].SCol1
            }
            // 存在  查询缴费信息'
            //"msg" -> "保存失败，以下业务数据已经生成交费通知单，请修改后再保存！
            //1、单证号TDAA201841010000507909，序号1的业务数据
            //生成交费通知单号4101180716901166；"
            if (result.totalRecords == 0 && result.msg?.contains('已经生成交费通知单')) {
                log.debug 'totalRecords = 0 , exchangeNo {}', result.msg.split('生成交费通知单号')[1].split('；')[0]
                context.exchangeNo = result.msg.split('生成交费通知单号')[1].split('；')[0]
            }

            if (!context.exchangeNo) {
                log.debug '未能生成交费通知单号: {}', result.msg
                return getKnownReasonErrorFSRV(result.msg)
            }

            getContinueFSRV '缴费信息页面'
        } else {
            getKnownReasonErrorFSRV '查询微信支付信息失败'
        }

    }
}
