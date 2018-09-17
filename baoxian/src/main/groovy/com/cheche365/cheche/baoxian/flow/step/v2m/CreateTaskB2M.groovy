package com.cheche365.cheche.baoxian.flow.step.v2m

import com.cheche365.cheche.baoxian.flow.step.v2.ABaoXianCommonStep
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV

/**
 * 创建非续保报价，一次性提交报价阶段所需要的投保地区、车辆信息、 车主信息、供应商信息和险种信息。
 * @author taicw
 */
@Component
@Slf4j
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
class CreateTaskB2M extends ABaoXianCommonStep {

    private static final _API_PATH_CREATE_TASK_B = '/createTaskB'

    @Override
    run(context) {

        def params = generateRequestParameters(context, this)

        def result = send context,prefix + _API_PATH_CREATE_TASK_B, params


        if ('0' == result.code || '00' == result.respCode) {
            context.taskId = result.taskId
            log.info '创建非续保报价成功，任务ID ：{}', result.taskId
            getContinueFSRV result.taskId
        } else {
            log.error '创建非续保报价失败：{}', result.msg ?: result.errorMsg
            getFatalErrorFSRV result.msg ?: result.errorMsg
        }
    }

}

