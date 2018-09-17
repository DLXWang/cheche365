package com.cheche365.cheche.botpy.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.botpy.flow.Handlers._KIND_CODE_CONVERTERS_CONFIG
import static com.cheche365.cheche.botpy.util.BusinessUtils.getIdCardDTO
import static com.cheche365.cheche.botpy.util.BusinessUtils.getNotificationIdForPath
import static com.cheche365.cheche.botpy.util.BusinessUtils.getQuotedItems
import static com.cheche365.cheche.botpy.util.BusinessUtils.sendParamsAndReceive
import static com.cheche365.cheche.botpy.util.BusinessUtils.setNotificationIdForPath
import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants._GLASS_TYPE
import static com.cheche365.cheche.parser.Constants._INSURANCE_MAPPINGS
import static com.cheche365.cheche.parser.Constants._IOP_PREMIUM_NOTHING
import static com.cheche365.cheche.parser.Constants._PASSENGER_AMOUNT
import static com.cheche365.cheche.parser.Constants._QUOTE_RECORD_MAPPINGS
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_VERIFICATION_CODE_TEMPLATE_INSURING
import static com.cheche365.cheche.parser.Constants.get_SUPPLEMENT_INFO_VERIFICATION_CODE_TEMPLATE_INSURING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSupplementInfoFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV
import static groovyx.net.http.Method.PUT



/**
 * 校验身份证验证码
 */
@Component
@Slf4j
class CheckIdentityCaptcha extends ASyncResult implements IStep {

    private static final _API_PATH_CHECK_IDENTITY_CAPTCHA = '/proposals/'

    @Override
    run(context) {
        def verificationCode = context.additionalParameters.supplementInfo?.verificationCode
        if (!verificationCode) {
            log.info '需身份验证码核实信息'

            return getSupplementInfoFSRV([mergeMaps(_SUPPLEMENT_INFO_VERIFICATION_CODE_TEMPLATE_INSURING, [meta: [orderNo: context.order.orderNo]])])
        }

        def applicant = context.order.applicant
        def body = [
            id        : getIdCardDTO(context),
            phone     : applicant.mobile,
            code      : verificationCode, // 验证码
            privy_type: 'applicant' // 只支持投保人和被投保人一致的情况
        ]

        def path = context.proposal_id + '/id-code'
        def result = sendParamsAndReceive context, _API_PATH_CHECK_IDENTITY_CAPTCHA + path, body, PUT, log

        if (result.error) {
            log.info '校验身份证验证码失败，notification_id：{}', result.error
            getValuableHintsFSRV context, [mergeMaps(_VALUABLE_HINT_VERIFICATION_CODE_TEMPLATE_INSURING, [meta: [orderNo: context.order.orderNo]], [originalValue: context.extendedAttributes.verificationCode])]
        } else {
            setNotificationIdForPath context, result, path
            log.info '校验身份证验证码返回结果， notification_id：{}', getNotificationIdForPath(context, path)
            syncResult(context, path, 'VerifyIDCode')
        }

    }

    @Override
    protected getApiPath(context, path) {
        path
    }

    @Override
    protected resolveResult(context, result, type) {
        def data = result.data
        if (result.is_done && result.is_success) {
            log.info '跟踪单号为：{}，当前投保单状态为：{}，结果为：{}', data.tracking_no, data.proposal_status, data.message
            context.newQuoteRecordAndInsurances = populateNewQuoteRecordAndInsurances(context, data, null, null)
            getContinueFSRV data.proposal_status
        } else {
            log.error "重新推送补充身份证验证码，失败原因： {}，{}", data.comment, data.message
            getValuableHintsFSRV context, [mergeMaps(_VALUABLE_HINT_VERIFICATION_CODE_TEMPLATE_INSURING, [meta: [orderNo: context.order.orderNo]], [originalValue: context.extendedAttributes.verificationCode])]
        }
    }

    private static populateNewQuoteRecordAndInsurances(context, data, commercialPolicyNo, compulsoryPolicyNo) {
        def newInsurance = new Insurance()
        def newCompulsoryInsurance = new CompulsoryInsurance()

        if (data.biz_info) {
            def allKindItems = getQuotedItems(data.biz_info.detail, context.accurateInsurancePackage[_GLASS_TYPE], data.model)
            newInsurance.with { insurance ->
                _KIND_CODE_CONVERTERS_CONFIG.each { outerKindCode, innerKindCode, _0, _1, outer2InnerConverter, extConfig ->
                    if (!innerKindCode) {
                        return
                    }

                    def quoteItem = allKindItems[outerKindCode]
                    def ipMeta = _INSURANCE_MAPPINGS[innerKindCode]
                    def qrMeta = _QUOTE_RECORD_MAPPINGS[innerKindCode]
                    def (amount, premium, iopPremium, other) = outer2InnerConverter(
                        context,
                        innerKindCode,
                        quoteItem,
                        qrMeta.amountName,
                        qrMeta.premiumName,
                        ipMeta.isIop,
                        qrMeta.iopPremiumName,
                        extConfig)

                    if (premium) {
                        if (qrMeta.amountName) {
                            insurance[qrMeta.amountName] = amount
                        }
                        insurance[qrMeta.premiumName] = premium
                    }
                    // _IOP_PREMIUM_NOTHING：控制iop没有给值，被认为无法投保IOP的情况
                    if (iopPremium && _IOP_PREMIUM_NOTHING != iopPremium) {
                        def iopPremiumName = context.iopAlone ? qrMeta.premiumName : qrMeta.iopPremiumName
                        insurance[iopPremiumName] = iopPremium
                    }

                    //乘客数
                    if (_PASSENGER_AMOUNT == innerKindCode) {
                        insurance.passengerCount = other
                    }
                }
                insurance.premium = (data.biz_info?.total ?: 0) as Double
                insurance.proposalNo = data.ic_nos.biz_prop
                insurance.policyNo = commercialPolicyNo
                insurance
            }
        }

        newCompulsoryInsurance.with { compulsoryInsurance ->
            proposalNo = data.ic_nos.force_prop
            policyNo = compulsoryPolicyNo
            compulsoryPremium = (data.force_info?.premium ?: 0) as Double
            autoTax = (data.force_info?.tax ?: 0) as Double
            compulsoryInsurance
        }

        [null, newInsurance, newCompulsoryInsurance]
    }

}
