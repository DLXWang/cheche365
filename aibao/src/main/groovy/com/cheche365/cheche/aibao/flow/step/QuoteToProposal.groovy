package com.cheche365.cheche.aibao.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.aibao.util.BusinessUtils.getUserInfo
import static com.cheche365.cheche.aibao.util.BusinessUtils.getVeriteCodes
import static com.cheche365.cheche.aibao.util.BusinessUtils.sendParamsAndReceive
import static com.cheche365.cheche.common.util.AreaUtils.getDefaultDistrictCode
import static com.cheche365.cheche.common.util.AreaUtils.getProvinceCode
import static com.cheche365.cheche.common.util.ContactUtils.getRandomMobile
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.populateNewQuoteRecordAndInsurances



/**
 * 下单及提交核保
 * Created by LIU xuechl on 2018/9/7.
 */
@Slf4j
class QuoteToProposal implements IStep {

    private static final interfaceID = '100073'

    @Override
    run(context) {
        log.info('开始执行爱保核保接口,交易流水为{}', context.aiBaoTransactionNo)
        def result = sendParamsAndReceive(context, getReqParams(context), log, interfaceID)
        log.info 'result = {}', result
        def mainInfo = result.body.mainInfo
        if ('0000' == result.head.errorCode) {
            dealResult(context, mainInfo)
            getContinueFSRV result.head.errorMsg
        } else {
            // 如果返回需要输入校验码或者校验码输入有误，则推送校验码到前台
            getFatalErrorFSRV result.head.errorMsg ?: '核保失败'
        }
    }

    /**
     * 处理返回结果
     * @param mainInfo 核保返回结果：投保单号，保单号
     */
    protected static dealResult(context, mainInfo) {
        // 保存投保单号，保单号
        context.newQuoteRecordAndInsurances = populateNewQuoteRecordAndInsurances context, mainInfo.busiProposalNo,
            null, mainInfo.bzProposalNo, null
        log.info '核保爱保返回商业险投保单号:{}， 交强险投保单号:{} ', mainInfo.busiProposalNo, mainInfo.bzProposalNo
        // 针对所有地区将 orderNo 和支付地址放入 additionalParameters.persistentState 中，web 会做处理
        context.orderNo = mainInfo.orderNo
        context.payUrl = mainInfo.payUrl
    }

    protected static getReqParams(context) {
        def auto = context.auto
        def cityCode = context.cityCode

        def insurances = context.insurance ?: context.compulsoryInsurance
        def deliveryAddress = context.order.deliveryAddress

        def userMobile = insurances.applicantMobile ?: context.order.applicant?.mobile ?: randomMobile
        def userName = insurances.applicantName ?: auto.owner ?: ''
        // 识别后的验证码
        def veriteCodes = getVeriteCodes(context)
        [
            mainInfo       : [
                returnUrl     : context.returnUrl,                                // 支付完成前台页面跳转地址(支付同步回调通知地址)
                notifyUrl     : context.notifyUrl,                                // 支付完成后端回调地址(支付异步回调通知地址)
                verifyCode    : context.verificationCode ?: '',                  // 平台投保验证码地区(北京，海南)必传
                bzVerifyCode  : veriteCodes.bzVerifyCode,                        // 交强险验证码，转保验证的时候才需要使用
                busiVerifyCode: veriteCodes.busiVerifyCode                       // 商业险验证码，转保验证的时候才需要使用
            ],
            destinationInfo: [
                idName  : userName,                                               // 联系人
                mobile  : userMobile,                                             // 联系电话
                invoice : userName,                                               // 发票抬头
                province: getProvinceCode(cityCode),                              // 配送地址(省)
                city    : cityCode,                                               // 配送地址(市)
                town    : getDefaultDistrictCode(cityCode),                       // 区县
                address  : deliveryAddress?.address ?: '',                         // 联络地址
//               email:'',                                                       // 邮箱 非必传
//               sendDate: '2018-6-9',                                           // 配送时间YYYY-MM-DD 非必传
            ],
            applicantInfo  : getUserInfo(context, true)
        ]
    }

}
