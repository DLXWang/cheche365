package com.cheche365.cheche.baoxian.flow.step.v2m

import com.cheche365.cheche.common.flow.IStep
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.baoxian.flow.Constants._COMPANY_I2O_MAPPINGS
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * Created by wangxin on 2018/3/20.
 */
@Slf4j
class CheckTaskId implements IStep {

    @Override
    run ( context ) {
        def carOwner = context.auto.owner
        def licenseNo = context.auto.licensePlateNo
        def providersStr = context.globalContext.get("${context.area.id}_providers".toString())

        def provider = providersStr ? new JsonSlurper().parseText(providersStr)?.find { provider ->
            provider.prvId.startsWith(_COMPANY_I2O_MAPPINGS[(context.insuranceCompany.id)])
        } : [prvId: '']
        def taskId = context.globalContext.get("${licenseNo}_${carOwner}_${provider?.prvId}".toString())
        if (taskId) {
            log.info '从redis中获取缓存的taskId：{}，直接修改信息并重新提交报价', taskId
            context.taskId = taskId
            context.selectedCarModel = new JsonSlurper().parseText(context.globalContext.get("${licenseNo}_${carOwner}_vehicleId".toString()))
            log.info '从redis中获取缓存的taskId：{}，对应车辆信息：{}', taskId, context.selectedCarModel
            context.providers = [provider]
            context.provider = provider
            getContinueFSRV '有taskID缓存'
        } else {
            log.info '未从redis中获取缓存的taskId，进行首次报价'
            getContinueFSRV '没有taskID缓存'
        }
    }
}
