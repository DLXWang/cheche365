package com.cheche365.cheche.cpic.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.ContactUtils.getRandomMobile
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static groovyx.net.http.ContentType.JSON



/**
 * v3流程部分车辆 通过此步骤能够获取车型信息
 * Created by xushao on 2015/8/31.
 */
@Component
@Slf4j
abstract class ASubmitVehicleBaseInfo implements IStep {

    private static final _API_PATH_SUBMIT_VEHICLE_BASE_INFO = 'cpiccar/salesNew/businessCollect/submitVehicleBaseInfo'

    @Override
    run(context) {
        RESTClient client = context.client

        def args = [
            requestContentType: JSON,
            contentType       : JSON,
            path              : _API_PATH_SUBMIT_VEHICLE_BASE_INFO,
            body              : generateRequestParameters(context)
        ]

        def result
        try {
            result = client.post args, { resp, json ->
                json
            }
        } catch (ex) {
            log.warn '提交车辆基本信息异常：{}，尝试重试', ex.message
            return getLoopContinueFSRV(null, '提交车辆基本信息异常')
        }

        if (result?.error == '图形验证码校验错误') {
            return getFatalErrorFSRV('图形验证码校验错误')
        }
        if (result.orderNo) {
            log.info '首次提交基本信息成功，生成订单号为{}', result.orderNo
            context.opportunityId = result.opportunityId
            context.orderNo = result.orderNo
            //TODO remove baseInfoResult
            context.baseInfoResult = [random: result.random]
            context.vehicleInfo = [
                random    : result.random,
                carVin    : result.VehicleInfo?.carVIN ?: result.VehicleInfo?.carVin, //TODO 进一步测试验证是否需要获取表达式后面的carVin的值
                engineNo  : result.VehicleInfo?.engineNo,
                enrollDate: result.VehicleInfo?.registerDate,
                modeName  : result.VehicleInfo?.moldNameWithSuffix ?: result.VehicleInfo?.moldName ?: result.VehicleInfo?.vehicleModel,
            ]
            // 如果抓到车辆信息且前端没有填写品牌型号信息，将车辆信息存入context中，目前北京、上海不用填写品牌型号信息
            if (result.vehicleModelPlatList) {
                context.vehicleInfo += result.vehicleModelPlatList[0]
            }
            if (result.insurancePageStatus == "TURN_INSURANCE_CALCULATE") {
                // TODO 改成统一setDate方式 商业起保日期也要处理
                context.compulsoryInfo = result.ComplusoryInfo
                context.renewable = true
                log.info '该车是否是续保车：{}', context.renewable
                if (context.extendedAttributes?.transferFlag) {
                    log.error '不支持续保用户留牌换车'
                    //是续保用户，且在一年内过户了车辆，但是走的是转保报价（拿的去年的车辆信息，阳光不支持续保用户留牌换车）
                    return getFatalErrorFSRV('不支持续保用户留牌换车')
                }
                context.carryVehicleFlag = 1
                getLoopBreakFSRV 2 // 续保流程
            } else {
                getLoopBreakFSRV 1 // 转保流程
            }
        } else if (result.inforSupplementFlag) {
            getLoopBreakFSRV 3 // 北京地区流程补充信息
        } else {
            log.error '提交车辆基本信息失败，请稍后重试'
            getFatalErrorFSRV '获取车型失败，继续获取'
        }
    }

    protected generateRequestParameters(context) {
        Auto auto = context.auto
        def mobile = RandomMobile

        def commonParams = [
            VehicleInfo        : [
                driveArea: 2,
                plateNo  : auto.licensePlateNo
            ],
            Opportunity        : [
                licenseOwner: "一口价",
                mobile      : context.insuredphoneNo ?: mobile
            ],
            PolicyBaseInfo     : [
                provinceCode: context.provinceCode,
                cityCode    : context.cityCode,
                branchCode  : context.branchCode,
                orgdeptCode : context.orgdeptCode,
                otherSource : '02'
            ],
            verifyCode         : context.captchaText ?: "",
            verifyCode2        : context.issueCode.toString(),
            insuredphoneNo     : context.insuredphoneNo ?: mobile,
            inforSupplementFlag: 'true',
            tbRandom           : context.initVehicleBaseInfo.tbRandom,
            zjhm               : auto.identity[10..13]
        ]
        mergeMaps commonParams, getSpecialRequestParameters(context)
    }

    protected getSpecialRequestParameters(context) {
        [:]
    }
}
