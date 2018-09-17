package com.cheche365.cheche.aibao.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.aibao.util.BusinessUtils.sendParamsAndReceive



/**
 * 查车的抽象方法
 * Created by liuguo on 2018/06/5.
 */
@Slf4j
abstract class ACreateFindICModels implements IStep {

    private static final interfaceID = '100070'

    @Override
    run(context) {
        // TODO 测试用提交时删掉
//        context.additionalParameters.referToOtherAutoModel = false
        def apply_content = [
            channelIds   : context.channelIds,//渠道编码
            userId       : '',//用户id
            yearPatterns : '',//年款
            exhaustScales: '',//排量
            getPage      : '',//分页
        ] + vehicleModelConditions(context)
        def result = sendParamsAndReceive context, apply_content, log, interfaceID
        dealResultFsrv(context, result)
    }

    /**
     * 根据返回列表中的 responseFlag 添加对应请求参数
     */
    abstract protected vehicleModelConditions(context)

    /**
     * 对返回结果判断是否含有  进而判断是否继续查车
     */
    abstract protected dealResultFsrv(context, result)

}
