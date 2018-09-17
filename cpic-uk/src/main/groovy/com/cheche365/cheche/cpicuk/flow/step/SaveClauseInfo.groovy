package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static groovyx.net.http.ContentType.JSON



/**保存投保信息 用来创建投保单
 * Created by chukh on 2018/5/8.
 */
@Component
@Slf4j
class SaveClauseInfo implements IStep {

    private static final _API_PATH_SAVE_CLASUSE_INFO = '/ecar/insure/saveClauseInfo'

    @Override
    run(Object context) {

        if (!context.insuredTelephone)
            context.insuredTelephone = context.additionalParameters.agent?.customer?.mobile

        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_SAVE_CLASUSE_INFO,
            body              : generateRequestParameters(context, this)
        ]
        log.debug '请求体：\n{}', args.body
        //发送http请求
        def result = context.client.post args, { resp, json ->
            json
        }

        log.debug '保存核保信息：{}', result
        if (result.message.code == 'success') {
            getContinueFSRV null
        } else {
            log.error '保存投保单信息失败：{}', result.message.message
            getKnownReasonErrorFSRV result.message.message  //该状态下的报价单不可修改
        }
    }


}
