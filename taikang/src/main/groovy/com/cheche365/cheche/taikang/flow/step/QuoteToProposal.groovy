package com.cheche365.cheche.taikang.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.populateNewQuoteRecordAndInsurances
import static com.cheche365.cheche.taikang.util.BusinessUtils.sendParamsAndReceive
import static com.cheche365.flow.core.util.FlowUtils.getNeedSupplementInfoFSRV
import static com.cheche365.cheche.taikang.util.BusinessUtils.getCaptchaImageSupplementInfo
import static com.cheche365.cheche.taikang.util.BusinessUtils.getFirstCheck
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT5



/**
 * 下单及提交核保
 */
@Slf4j
class QuoteToProposal implements IStep {

    protected static final _FUNCTION = 'quoteToProposal'

    @Override
    Object run(Object context) {

        log.info('开始执行泰康核保接口，交易流水为 {}', context.proposalFormId)

        def result = sendParamsAndReceive context, _FUNCTION, generateRequestParameters(context, this), log

        def applyContent = result.apply_content
        if ('200' == applyContent.reponseCode) {
            dealResult(context, applyContent)
            getContinueFSRV result.apply_content.messageBody
        } else {
            //如果返回需要输入校验码或者校验码输入有误，则推送校验码到前台
            if (result.apply_content.messageBody.contains('校验码有误')) {
                log.info '验证码校验失败，推送验证码到前台'
                def needSupplementInfo = getCaptchaImageSupplementInfo(context.quoteCheckList, context.currentBase64)
                return getNeedSupplementInfoFSRV { needSupplementInfo }
            }
            if (getFirstCheck(result) && getFirstCheck(result).check.checkCode) {
                log.info '推送验证码到前台'
                def needSupplementInfo = getCaptchaImageSupplementInfo(result.apply_content.data.checkList, getFirstCheck(result).check.checkCode)
                return getNeedSupplementInfoFSRV { needSupplementInfo }
            }
            getFatalErrorFSRV result.apply_content.messageBody ?: '核保失败'
        }
    }

    protected static dealResult(context, def applyContent) {
        //更新订单起保和终保日期,订单时间有变化供前台展示用
        if (context.commercialExpireDate) {
            context.insurance.effectiveDate = _DATE_FORMAT5.parse(context.commercialBeginDate)
            context.insurance.expireDate = _DATE_FORMAT5.parse(context.commercialExpireDate)
        }

        if (context.compulsoryExpireDate) {
            context.compulsoryInsurance.effectiveDate = _DATE_FORMAT5.parse(context.compulsoryBeginDate)
            context.compulsoryInsurance.setExpireDate = _DATE_FORMAT5.parse(context.compulsoryExpireDate)
        }

        def data = applyContent.data
        context.newQuoteRecordAndInsurances = populateNewQuoteRecordAndInsurances context, data.proposalInfo.proposalNoBI, null, data.proposalInfo.proposalNoCI, null
        log.info '核保泰康返回proposalNoBI = {}， proposalNoCI = {} ', data.proposalInfo.proposalNoBI, data.proposalInfo.proposalNoCI
        //针对所有地区将proposalNo放入additionalParameters.persistentState中，web会做处理
        context.formId = data.proposalNo
        //更新保单状态
        context.waitIdentityCaptcha = true
    }

}
