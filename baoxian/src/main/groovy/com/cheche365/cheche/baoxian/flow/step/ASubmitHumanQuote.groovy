package com.cheche365.cheche.baoxian.flow.step

import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.parser.util.FlowUtils.getShowInsuranceChangeAdviceFSRV



/**
 * 对套餐进行修改后重新申请报价基类
 * Created by wangxin on 2017/2/13.
 */
@Component
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
abstract class ASubmitHumanQuote extends ASubmitQuote {

    private static final _API_PATH_SUBMIT_QUOTE = '/submitHumanQuote'

    @Override
    protected getAPI() {
        _API_PATH_SUBMIT_QUOTE
    }

    @Override
    protected getParams(context) {
        [
            taskId: context.taskId,
            prvId : context.provider.prvId,
            taskState : '18',
        ]
    }


    @Override
    protected getReturnFSRV(context,result){
        if ('0' == result.code) {
            log.info '提交报价成功'
            getSuccessfulFsrv()
        } else {
            log.error '提交报价失败：{}', result.msg
            getShowInsuranceChangeAdviceFSRV result.msg
        }
    }

    abstract protected getSuccessfulFsrv()

}
