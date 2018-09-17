package com.cheche365.cheche.baoxian.flow.step

import com.cheche365.cheche.baoxian.flow.step.v2.ABaoXianCommonStep
import groovy.transform.InheritConstructors
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_ID_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV



/**
 * 创建续保报价，具备车辆信息查询功能和历史保单查询功能
 * @author taicw
 */
@Component
@InheritConstructors(
    constructorAnnotations = true,
    parameterAnnotations = true
)
abstract class ACreateTaskA extends ABaoXianCommonStep {

    private static final _API_PATH_CREATE_TASK_A = '/createTaskA'


    @Override
    run(context) {

        def params = getParams(context)

        def result = send context,prefix + _API_PATH_CREATE_TASK_A, params

        if (('0' == result.code || '00' == result.respCode) && result.taskId) {
            context.taskId = result.taskId
            context.historicalInfo = [
                prvId      : result.prvId, //上年投保公司id
                prvName    : result.prvName, //上年投保公司名称
                carInfo    : result.carInfo, //车辆信息
                insureInfo : result.insureInfo //上年保单信息
            ]
            log.info '创建续保报价，返回的车辆信息：{} 上年保单信息：{} 任务ID：{}', result.carInfo, result.insureInfo, result.taskId
            getContinueFSRV '创建续保成功'
        } else if ('01' == result.respCode && result.errorMsg?.contains('报价过于频繁')) {
            log.error result.errorMsg
            getFatalErrorFSRV result.errorMsg
        } else if ('01' == result.respCode && '请输入正确的证件号码' == result.errorMsg) {
            getValuableHintsFSRV context, [_VALUABLE_HINT_ID_TEMPLATE_QUOTING]
        } else {
            log.warn '创建续保报价，返回车辆信息失败：{}', result.msg
            getContinueFSRV '创建续保失败'
        }
    }

    protected getParams(context){
        def auto = context.auto
        def area = context.area
         [
            insureAreaCode : area.id,
            channelUserId  : context.channelID ?: context.channelId,
            carInfo        : [
                carLicenseNo : auto.licensePlateNo
            ],
            carOwner         : [
                name : auto.owner,
            ]
        ]
    }

}
