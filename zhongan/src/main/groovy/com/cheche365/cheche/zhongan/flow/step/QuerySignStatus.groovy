package com.cheche365.cheche.zhongan.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.flow.Constants._ROUTE_FLAG_DONE
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.zhongan.flow.Constants._STATUS_CODE_ZHONGAN_CONFIRM_ORDER_AND_UNDERWRITING_FAILURE
import static com.cheche365.cheche.zhongan.util.BusinessUtils.sendAndReceive


/**
 * 深圳地区签名查询
 */
@Component
@Slf4j
class QuerySignStatus implements IStep {

    private static final _SERVICE_NAME = 'zhongan.castle.policy.querySignStatus'

    @Override
    def run(Object context) {


        def params = [
            insureFlowCode: context.insureFlowCode
        ]

        def result = sendAndReceive(context, this.class.name, _SERVICE_NAME, params)

        log.debug "深圳地区签名查询 result :{}", result
        if ('0' == result.result) {
            log.info "深圳地区签名获取成功 : signStatus = {}", result.signStatus
            if ('12' == result.signStatus) {//用户还没有签名
                log.warn "深圳地区签名失败 result= {}", result
                return [_ROUTE_FLAG_DONE, _STATUS_CODE_ZHONGAN_CONFIRM_ORDER_AND_UNDERWRITING_FAILURE, null, '签名失败']
            }
            //否则就是已经签名的状态
            context.newSignStatus = result.signStatus
            //签名成功以后再调一次核保
            getContinueFSRV result
        } else { //众安那边如果不签名的话没往数据库中写东西，会报查不到保单号,后续测试这个情况又不出现了
            log.error "深圳地区签名失败 result= {}", result
            [_ROUTE_FLAG_DONE, _STATUS_CODE_ZHONGAN_CONFIRM_ORDER_AND_UNDERWRITING_FAILURE, null, '签名失败']
        }


    }
}
