package com.cheche365.cheche.picc.flow.step.v2

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT1
import static com.cheche365.cheche.parser.Constants._FLOW_PARTICIPANT_MESSAGE_1
import static com.cheche365.cheche.parser.Constants.get_VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.htmlParser
import static com.cheche365.cheche.parser.util.BusinessUtils.setCommercialInsurancePeriodTexts
import static com.cheche365.cheche.picc.util.BusinessUtils.getNextDays4Commercial
import static com.cheche365.flow.core.util.FlowUtils.getProvideValuableHintsFSRV
import static groovyx.net.http.ContentType.BINARY
import static groovyx.net.http.ContentType.URLENC



/**
 * 获取Unique ID及客户类型等标志
 */
@Component
@Slf4j
class NormalProposal implements IStep {

    private static final _URL_PROPOSAL = '/newecar/proposal/normalProposal'
    private static final _NORMAL_PROPOSAL_NAMES = [
        'uniqueID',
        'isRenewal',        // 续保客户
        'reuseFlag',         // 历史客户
        // 以下字段可以抓取，可从CityCodeMappings 中移除
        'areaCode', // 地区代码
        'cityCode',  // 城市代码
        'startDateBI', // 商业险起保日期
    ]

    @Override
    run(context) {
        context.flowParticipant?.sendMessage _FLOW_PARTICIPANT_MESSAGE_1

        RESTClient client = context.client

        def result = client.request(Method.POST) { req ->
            requestContentType = URLENC
            contentType = BINARY
            uri.path = _URL_PROPOSAL
            body = [
                areaCode   : context.areaCode,
                cityCode   : context.cityCode,
                licenseNo  : context.auto.licensePlateNo,
                licenseFlag: context.newCarFlag ? 0 : 1,
                entryId    : context.entryId,
            ]

            response.success = { resp, stream ->
                def inputs = htmlParser.parse(stream).depthFirst().INPUT

                inputs.findResults { input ->
                    if (input.@id in _NORMAL_PROPOSAL_NAMES) {
                        [(input.@id): input.@value]
                    }
                }.sum()
            }

            response.failure = { resp, reader ->
                log.warn '连接失败，建议重试'
                [:]
            }
        }

        if (result.uniqueID) {
            context.renewable = '1' == result.isRenewal  // 续保标志
            context.historical = '1' == result.reuseFlag // 历史客户标志
            context << result

            log.info 'UniqueID：{}', result.uniqueID
            log.info '续保标志isRenewal：{}', result.isRenewal
            log.info '历史客户标志reuseFlag：{}', result.reuseFlag

            if (context.extendedAttributes?.transferFlag) {
                log.info '选择了过户或者留牌换车，走转保流程'
                return getContinueFSRV(1)
            }

            if (!context.renewable && !context.historical) {
                log.info '该客户是转保客户，走转保流程'
                return getContinueFSRV(1)
            }

            if (context.renewable) {
                log.info '该客户是续保客户，走续保流程'
                return getContinueFSRV(2)
            }

            if (context.historical) {
                log.info '该客户是历史客户，走历史客户流程'
                return getContinueFSRV(3)
            }
        } else if (result.startDateBI) {
            setCommercialInsurancePeriodTexts context, result.startDateBI, _DATETIME_FORMAT1, getNextDays4Commercial(context)
        } else {
            log.error '获取UniqueID失败，通常是HTML网页获取有误导致的，建议重试'
            def hints = [
                _VALUABLE_HINT_LICENSE_PLATE_NO_TEMPLATE_QUOTING.with {
                    it.originalValue = context.auto.licensePlateNo
                    it
                }
            ]
            getProvideValuableHintsFSRV { hints }
        }
    }
}
