package com.cheche365.cheche.pingan.flow.step.m

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.Auto
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.core.constants.ModelConstants._FLOW_TYPE_RENEWAL_CHANNEL
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_INSURED_ID_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV
import static groovy.json.StringEscapeUtils.escapeJava
import static groovyx.net.http.ContentType.BINARY



/**
 * 获取报价是否含税，续保用户车辆信息
 * Created by wangxin on 2015/12/29.
 */
@Component
@Slf4j
class ToQueryInfo implements IStep {

    private static final _API_PATH_TO_QUOTE_INFO = 'autox/do/api/to-query-info'

    private static final _RH_DEFAULT = { result, context ->
        context.hasTax = 0
        log.warn '未知情况：{}', result
        getContinueFSRV true
    }

    private static final _RH_C0000 = { result, context ->
        //获取显示车辆价格是否含税的标识
        updateHasTaxFlag(result, context)
        if (context.renewable) {
            //获取车辆过户信息
            updateSpecialCarInfo(result, context)
            //获取续保车辆信息
            updateVehicleInfo(result, context)
            //获取续保用户的信息
            updateRegisterInfo(result, context)
        }
        getContinueFSRV true
    }

    private static final _RH_C006 = { result, context ->
        log.error '续保用户验证失败'
        if(_FLOW_TYPE_RENEWAL_CHANNEL == context.flowType){
            getIdentityValuableHintsFSRV context
        }else{
            context.renewable = false
            getContinueFSRV false
        }
    }

    private static final _RH_S0001 = { result, context ->
        if (context.renewable) {
            log.error '身份证验证错误'
            getIdentityValuableHintsFSRV context
        } else {
            getFatalErrorFSRV '平安系统异常'
        }
    }

    private static final _RH_S5006 = {
        log.error '访问次数过多'
        getFatalErrorFSRV '访问次数过多，建议重试'
    }

    private static final _RH_MAPPINGS = [
        C006   : _RH_C006,
        C0006  : _RH_C006,
        C0000  : _RH_C0000,
        S0001  : _RH_S0001,
        S5006  : _RH_S5006,
        default: _RH_DEFAULT
    ]

    private static final _REGISTER_INFO_PROPERTIES = [
        'register.name',
        'register.idType',
        'register.birthday',
        'register.gender'
    ]

    private static final _RENEWAL_VEHICLE_INFO_PROPERTIES = [
        'vehicle.registerDate',
        'vehicle.model',
        'vehicle.modelName',
        'vehicle.vehicleId'
    ]


    @Override
    run(context) {

        Auto auto = context.auto
        RESTClient client = context.client
        def args = [
            contentType: BINARY,
            path       : _API_PATH_TO_QUOTE_INFO,
            query      : [
                'vehicle.licenseNo'   : auto.licensePlateNo,
                'partner.mediaSources': 'SC03-Direct-00001',
                'partner.partnerName' : 'chexian-mobile',
                __xrc                 : context.__xrc,
                flowId                : context.flowId,
                //身份证后6
                'renewal.idNo'        : context.renewable && !context.forceNonRenewal ? (auto.insuredIdNo ?: auto.identity)[-6..-1] : '',
                'flag.renewalJump'    : context.renewable && !context.forceNonRenewal ? '' : 1
            ]
        ]

        def result = client.get args, { resp, stream ->
            def text = $/${stream.text}/$
            new JsonSlurper().with {
                type = JsonParserType.LAX
                it
            }.parseText text.tokenize('\n').collect { line ->
                !line.contains('regex') ? line :
                    line[line.indexOf(':') + 1..-1].with { regex ->
                        def regexStr = regex =~ /"(.*)"/
                        "\"regex\" : \"${escapeJava(regexStr[0][1])}\","
                    }
            }.join('\n')
        }
        def resultCode = result.resultCode
        def resultHandler = _RH_MAPPINGS[resultCode] ?: _RH_MAPPINGS.default
        resultHandler(result, context)
    }

    //获取续保的用户信息
    private static void updateRegisterInfo(result, context) {
        def renewalRegisterInfo = result.register.collectEntries { item ->
            [(item.name): item.value]
        }.findAll { k, _ ->
            k in _REGISTER_INFO_PROPERTIES
        }

        context.renewalRegisterInfo = renewalRegisterInfo
    }

    //获取续保的车辆信息
    private static void updateVehicleInfo(result, context) {
        def renewalVehicleInfo = result.vehicle.collectEntries { item ->
            [(item.name): item.value]
        }.findAll { k, _ ->
            k in _RENEWAL_VEHICLE_INFO_PROPERTIES
        }

        context.renewalVehicleInfo = renewalVehicleInfo
    }

    //获取价格显示是否要包含税的标志,续保和转保都使用
    private static void updateHasTaxFlag(result, context) {
        context.hasTax = Boolean.valueOf(result.switches.isUseTaxPrice ?: Boolean.TRUE.toString()) ? 1 : 0
    }

    //获取车辆过户信息,0为非过户车辆，1为过户车辆
    private static void updateSpecialCarInfo(result, context) {
        def isSpecialCarFlag = result.bizInfo.findResult { bizInfoItem ->
            if (bizInfoItem.name == 'bizInfo.specialCarFlag') {
                bizInfoItem
            }
        }
        def specialCarDate = result.bizInfo.findResult { bizInfoItem ->
            if (bizInfoItem.name == 'bizInfo.specialCarDate') {
                bizInfoItem
            }
        }
        //过户车辆标志位
        context.isSpecialCarFlag = isSpecialCarFlag?.value
        //过户车辆日期
        context.specialCarDate = specialCarDate?.value
    }

    private static getIdentityValuableHintsFSRV(context) {
        def hints = [
            _VALUABLE_HINT_INSURED_ID_TEMPLATE_QUOTING.with {
                it.hints = [
                    '输入错误',
                    '不是上年被保人身份证'
                ]
                it.originalValue = context.auto.insuredIdNo ?: context.auto.identity
                it
            }
        ]
        getValuableHintsFSRV context, hints
    }

}
