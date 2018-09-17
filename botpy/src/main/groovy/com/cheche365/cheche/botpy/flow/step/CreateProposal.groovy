package com.cheche365.cheche.botpy.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.botpy.flow.Constants._HOLDER_TYPE_MAPPINGS
import static com.cheche365.cheche.botpy.flow.Constants._IDENTITYTYPE_MAPPINGS
import static com.cheche365.cheche.botpy.util.BusinessUtils.getMappingsValue
import static com.cheche365.cheche.botpy.util.BusinessUtils.getNotificationIdForPath
import static com.cheche365.cheche.botpy.util.BusinessUtils.sendParamsAndReceive
import static com.cheche365.cheche.botpy.util.BusinessUtils.setNotificationIdForPath
import static com.cheche365.cheche.botpy.util.BusinessUtils.getAddressDetail
import static com.cheche365.cheche.botpy.util.BusinessUtils.getAddress
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getEnvProperty
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.parser.util.BusinessUtils.getCommercialInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.getCompulsoryInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.isCommercialQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted
import static groovyx.net.http.Method.POST
import static java.util.concurrent.TimeUnit.HOURS



/**
 * 创建投保单
 */
@Slf4j
class CreateProposal extends ASyncResult implements IStep {


    private static final _API_PATH_CREATE_PROPOSAL = '/proposals'

    @Override
    run(context) {

        def params = getRequestParams(context)
        def result = sendParamsAndReceive context, _API_PATH_CREATE_PROPOSAL, params, POST, log

        if (result.error) {
            log.info '创建投保单失败， {}', result.error
            getFatalErrorFSRV result.error
        } else {
            context.globalContext.bindIfAbsentWithTTL(result.proposal_id, context.order.orderNo, 24, HOURS)
            context.proposal_id = result.proposal_id // 投保单号

            handleBlackList(context)
            context.globalContext.bindIfAbsentWithTTL(result.proposal_id, context.order.orderNo, 24, HOURS)
            setNotificationIdForPath context, result, _API_PATH_CREATE_PROPOSAL
            log.info '创建投保单成功， 单号为：{}', getNotificationIdForPath(context, _API_PATH_CREATE_PROPOSAL)
            syncResult(context, _API_PATH_CREATE_PROPOSAL, 'Propose')
        }
    }


    private getRequestParams(context) {

        def auto = context.auto
        def order = context.order
        def insurance = context.insurance
        def compulsoryInsurance = context.compulsoryInsurance

        def autoId = auto.identity                      // 车主身份证
        def owner = auto.owner                          // 车主姓名

        def applicantId = (insurance ?: compulsoryInsurance ?: order).applicantIdNo ?: autoId // 投保人身份证
        def insureId = (insurance ?: compulsoryInsurance ?: order).insuredIdNo ?: autoId      // 被保险人身份证
        def applicantName = (insurance ?: compulsoryInsurance ?: order).applicantName ?: owner // 投保人姓名
        def insuredName = (insurance ?: compulsoryInsurance ?: order).insuredName ?: owner      // 被保险人姓名
        def userMobile   // 用户手机
        if (context.channelCode == 'didicda') {
            userMobile = context.additionalParameters.agent?.customer?.mobile
            log.info("didi channel use mobile={}", userMobile)
        } else {
            userMobile = context.extendedAttributes?.verificationMobile ?: order.applicant.mobile
            log.info("not didi channel use mobile={}", userMobile)
        }
        def email = getEnvProperty context, 'email'
        def deliveryAddress = order?.deliveryAddress
        def address = getAddress deliveryAddress
        //详细地址
        def addressDetail = getAddressDetail deliveryAddress
        def applicantIdType = (insurance ?: compulsoryInsurance ?: order)?.applicantIdentityType?.id ?: auto.identityType?.id
        def insuredIdType = (insurance ?: compulsoryInsurance ?: order)?.insuredIdentityType?.id ?: auto.identityType?.id

        def extraParams = (context.accurateEngages ? [engages: context.accurateEngages] : [:]) +
            (isCommercialQuoted(context.accurateInsurancePackage) ?
                [biz_start_date: getCommercialInsurancePeriodTexts(context, _DATETIME_FORMAT3, false).first] : [:]) +
            (isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage) ?
                [force_start_date: getCompulsoryInsurancePeriodTexts(context, _DATETIME_FORMAT3, false).first] : [:])

        [
            notify_url  : context.callBackUrl,
            quotation_id: context.quotation_id,
            out_trade_no: order.orderNo,
            insurant    : [
                name          : insuredName,
                holder_type   : _HOLDER_TYPE_MAPPINGS[insuredIdType] ?: '01', //持有人类型代码 01个人
                document_type : _IDENTITYTYPE_MAPPINGS[insuredIdType] ?: '01', //关系人证件类型代码 01身份证
                document_no   : insureId,
                phone         : (insurance ?: compulsoryInsurance)?.insuredMobile ?: userMobile,
                email         : (insurance ?: compulsoryInsurance)?.insuredEmail ?: email,
                address       : address,
                address_detail: addressDetail,
            ],
            applicant   : [
                name          : applicantName,
                holder_type   : _HOLDER_TYPE_MAPPINGS[applicantIdType] ?: '01', //持有人类型代码 01个人
                document_type : _IDENTITYTYPE_MAPPINGS[applicantIdType] ?: '01', //关系人证件类型代码 01身份证
                document_no   : applicantId,
                phone         : (insurance ?: compulsoryInsurance)?.applicantMobile ?: userMobile,
                email         : (insurance ?: compulsoryInsurance)?.applicantEmail ?: email,
                address       : address,
                address_detail: addressDetail,
            ],
            owner       : [
                name          : owner,
                holder_type   : getMappingsValue(context, 'identityType', 'HOLDER_TYPE') ?: '01', //持有人类型代码 01个人
                document_type : getMappingsValue(context, 'identityType') ?: '01', //关系人证件类型代码 01身份证
                document_no   : autoId,
                phone         : userMobile,
                email         : email,
                address       : address,
                address_detail: addressDetail,
            ]
        ] + extraParams
    }

    private handleBlackList(context) {

        if (context.previous_proposal_id && (context.proposal_id != context.previous_proposal_id)) {
            log.info '订单{}出现多个proposal_id，原始值: {}, 新值: {}', context.order.orderNo, context.previous_proposal_id, context.proposal_id
            context.globalContext.add('proposal_id_blacklist', context.previous_proposal_id)
        }
        context.previous_proposal_id = context.proposal_id


    }

    @Override
    protected getApiPath(context, path) {
        _API_PATH_CREATE_PROPOSAL
    }

    @Override
    protected resolveResult(context, result, type) {
        def data = result?.data
        if (result.is_done) {
            if (result.is_success) {
                log.info '跟踪单号为：{}，当前投保单状态为：{}，结果为：{}', data.tracking_no, data.proposal_status, data.message
                getContinueFSRV data.proposal_status
            } else {
                getFatalErrorFSRV data.message + data.comment
            }
        } else {
            getFatalErrorFSRV result
        }
    }


}
