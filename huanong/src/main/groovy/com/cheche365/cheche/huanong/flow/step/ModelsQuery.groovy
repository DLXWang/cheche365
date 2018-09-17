package com.cheche365.cheche.huanong.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.huanong.util.BusinessUtils.createRequestParams
import static com.cheche365.cheche.huanong.util.BusinessUtils._RESPONSE_CODE_DESC_MAPPINGS
import static com.cheche365.cheche.huanong.util.BusinessUtils.sendParamsAndReceive
import static com.cheche365.cheche.parser.Constants._ERROR_MESSAGE_AUTO_TYPE_CODE_ERROR
import static com.cheche365.flow.core.util.FlowUtils.getAutoTypeCodeErrorFSRV
import static com.cheche365.cheche.huanong.flow.Constants._SUCCESS



/**
 * 车型查询
 * create by wangpeng
 *
 */
@Slf4j
class ModelsQuery implements IStep {

    private static final _TRAN_CODE = 'ModelsQuery'//华农接口标识

    @Override
    run(Object context) {
        def result = sendParamsAndReceive context, getRequestParams(context), _TRAN_CODE, log

        log.debug '车型查询返回结果：{}', result
        if (result.head.responseCode == _SUCCESS) {
            if (result.simpleStadarCar) {
                log.debug '车型查询成功，进入选车阶段 VehicleList：{} ', result.simpleStadarCar
                context.optionsByCode = result.simpleStadarCar
                context.resultByCode = result
                getContinueFSRV '查车成功，进行匹配'
            } else {
                log.error '在保险公司没有查询到满足条件的车辆'
                getAutoTypeCodeErrorFSRV null, _ERROR_MESSAGE_AUTO_TYPE_CODE_ERROR
            }
        } else {
            def errorMsg = _RESPONSE_CODE_DESC_MAPPINGS[result.head.responseCode]
            log.error errorMsg //转义华农返回错误信息
            getFatalErrorFSRV errorMsg ?: result.head.responseMsg
        }
    }

    private static getRequestParams(context) {
        def auto = context.auto
        def params = [
            ModelName     : auto.autoType.code,//车型名称 我们从人保获取的精友型编码和华农的精友库编码不一致，所以，只能用车型名称查询才不会返回查询不到车型
            ModelCode     : '',//行业车型编码--精友和行业车型编码至少有一个不能为空
            LocalModelCode: '',//精友车型编码/本地车型代码  context.additionalParameters.supplementInfo.autoModel
            plateNo       : auto.licensePlateNo,//号牌号码
            frameNo       : auto.vinNo,//车架号
            engineNo      : auto.engineNo,//发动机号
            reqType       : '0',//请求方式--0是车型查询,1是车型校验,车型校验只用北京的时候是1
            checkNo       : '',//交管车辆查询码--北京江苏专用
            checkCode     : '',//交管车辆查询校验码--北京江苏专用
            Rows          : '20',//每页行数--默认20行
            PageNo        : '1',//第几页--默认1页
        ]

        createRequestParams context, _TRAN_CODE, params
    }

}
