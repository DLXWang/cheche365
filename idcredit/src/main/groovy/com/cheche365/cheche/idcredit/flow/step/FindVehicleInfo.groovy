package com.cheche365.cheche.idcredit.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.flow.Constants._ROUTE_FLAG_DONE
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.idcredit.flow.Constants._STATUS_CODE_IDCREDIT_VEHICLE_INFO_FAILURE
import static com.cheche365.cheche.idcredit.util.BusinessUtils.markFailedVehicleInfo
import static com.cheche365.cheche.idcredit.util.BusinessUtils.saveApplicationLog
import static com.cheche365.cheche.idcredit.util.BusinessUtils.wasVehicleInfoFailed
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

/**
 * 查询车型信息（异步）
 * 有API调用次数的限制
 */
@Component
@Slf4j
class FindVehicleInfo implements IStep {

    private static final _API_PATH_FIND_VEHICLE_INFO = '/info/vehicle'

    @Override
    run(context) {
        if (wasVehicleInfoFailed(context)) {
            return getFatalErrorFSRV('之前查询车辆信息已经失败，当天不再重试')
        }

        Auto auto = context.auto
        def licensePlateNo = auto.licensePlateNo
        def identity = auto.identity
        def owner = auto.owner

        RESTClient client = context.client

        def args = [
            requestContentType  : URLENC,
            contentType         : JSON,
            path                : _API_PATH_FIND_VEHICLE_INFO,
            body                : [
                access_token    : context.token,
                hphm            : licensePlateNo,
                sfzh            : identity,
                xm              : owner,
                clsbdh          : 1, // 车辆识别代码
                fdjh            : 1, // 发动机号
                zcrq            : 1, // 注册日期
//              ppxh            : 1, // 品牌型号
            ]
        ]

        log.info '以{}、{}、{}为参数查询车辆信息', licensePlateNo, identity, owner
        saveApplicationLog context, args as String, this.class.simpleName

        def result = client.post args, { resp, json ->
            json
        }

        saveApplicationLog context, result as String, this.class.simpleName

        context.findVehicleInfoCurrentLimiter.increment 1

        log.debug '返回的查询结果：{}', result

        /**
         * result为-1表示查询失败（超时等），需要重新提交验证
         * key为0表示无法匹配
         *
         * 正确响应JSON：
         * {
         *   "data": {
         *     "fdjh**": "967864Q",
         *     "zcrq**": "20121129",
         *     "result": 1,
         *     "clsbdh**": "LGBM2DE41CS046923"
         *   },
         *   "error": 0
         * }
         * 错误响应JSON：
         * {
         *   "data": {
         *     "result": 1,
         *     "key": 0
         *   },
         *   "error": 0
         * }
         */
        if (!result.error && result.data.tid) {
            context.taskId = result.data.tid
            getContinueFSRV result
        } else {
            if (1 == result && 0 == result.data.key) {
                markFailedVehicleInfo context
                return [_ROUTE_FLAG_DONE, _STATUS_CODE_IDCREDIT_VEHICLE_INFO_FAILURE, null, '车辆信息不匹配']
            }

            if (40007 == result.error) {
                log.error '绿湾API调用次数达到当天上限'
            }
            getFatalErrorFSRV '查询车辆信息失败'
        }
    }

}
