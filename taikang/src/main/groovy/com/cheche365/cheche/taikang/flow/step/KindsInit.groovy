package com.cheche365.cheche.taikang.flow.step

import com.cheche365.cheche.common.flow.IStep
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import groovy.util.logging.Slf4j
import static com.cheche365.cheche.taikang.util.BusinessUtils.sendParamsAndReceive



/**
 * 险别初始化
 * Created by yz on 2018/04/16.
 */
@Slf4j
class KindsInit implements IStep {

    private static final _FUNCTION = 'KindsInit'

    @Override
    run(Object context) {
        // 险别初始化接口FUNCTION
        def transferDate = context?.additionalParameters?.supplementInfo?.transferDate

        // 封装车辆的条件参数
        def carInfo = [
            chgOwnerFlag          : transferDate ? 1 : 0,                             // 过户车标识1-是、0-不是
            carOwnerIdentifyType  : '01',                                             // 持有人类型代码 01个人
            carOwnerIdentifyNumber: context.auto.identity,                            // 持有人身份证号
            carModelKey           : context.selectedCarModel?.carModel?.carModelKey,  // 取车型查询返回字段
            carOwner              : context.auto.owner                                // 持有人姓名
        ]

        if (transferDate) {
            carInfo << [transferDate: _DATE_FORMAT3.format(transferDate)]
        }
        def params = [cars: carInfo]
//         如果第一次返回结果中有图片验证码（checkCode），则说明是转保业务，需要根据第一次的返回结果
//         给checks赋上值，再次请求一次数据
        def result = sendParamsAndReceive context, _FUNCTION, params, log
        if ('200' == result.apply_content.reponseCode || data.checks) {
            def data = result.apply_content.data
            if (!data.kinds && !data.checks) {
                return getFatalErrorFSRV('该车不支持报价')
            }
            log.info '险别初始化正常'
            // 给交强商业赋起始日期
            def startDateCIP = _DATE_FORMAT3.parse(data.startDateCIP)
            def startDateBZ = _DATE_FORMAT3.parse(data.startDateBZ)
            def compulsoryStartDate = context.additionalParameters.supplementInfo?.compulsoryStartDate
            def commercialStartDate = context.additionalParameters.supplementInfo?.commercialStartDate
            context.preCommercialStartDate = commercialStartDate
            context.preCompulsoryStartDate = compulsoryStartDate
            if (compulsoryStartDate && compulsoryStartDate < startDateBZ) {
                context.additionalParameters.supplementInfo.compulsoryStartDate = startDateBZ
            }
            if (commercialStartDate && commercialStartDate < startDateCIP) {
                context.additionalParameters.supplementInfo.commercialStartDate = startDateCIP
            }
            context.kindCheckList = data.kinds
            getContinueFSRV result
        } else {
            log.info '险别初始化失败 resultMessage : {}', result.apply_content.messageBody
            getFatalErrorFSRV result.apply_content.messageBody ?: '险别初始化失败'
        }
    }
}
