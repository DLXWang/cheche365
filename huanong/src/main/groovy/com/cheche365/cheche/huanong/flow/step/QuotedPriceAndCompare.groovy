package com.cheche365.cheche.huanong.flow.step

import com.cheche365.cheche.common.flow.IStep
import static com.cheche365.cheche.common.util.ContactUtils.getRandomMobile
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import groovy.util.logging.Slf4j
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.huanong.util.BusinessUtils.sendParamsAndReceive
import static com.cheche365.cheche.huanong.util.BusinessUtils.compareQuoteResult
import static com.cheche365.cheche.huanong.flow.Constants._SUCCESS



/**
 * 发送核保报价报文
 * Created by LIU GUO on 2018/7/2.
 */
@Slf4j
class QuotedPriceAndCompare implements IStep {

    private static final _TRAN_CODE = 'SubmitQuotePrice'

    @Override
    run(Object context) {
        //获取干系人信息拼装新的报价报文
        def newQuotePriceInfo = replaceCustomerInfo context, context.firstQuotePriceReqJSON

        //进行精准报价，获取报价结果
        def newQuotePriceResult = sendParamsAndReceive context, newQuotePriceInfo, _TRAN_CODE, log

        if (newQuotePriceResult.head.responseCode && !newQuotePriceResult.head.responseCode == _SUCCESS) {
            //先判断报价报文返回的正常报价，如果没有报出价格，则将华农的错误推送到前端
            getFatalErrorFSRV(newQuotePriceResult.head.responseMsg)
        } else if (!compareQuoteResult(context, newQuotePriceResult)) {
            //比对两次报价结果，商业险和交强险,获取差异信息
            //结果一致，直接进行核保
            log.info '结果一致'
            getContinueFSRV '结果一致'
        } else {
            //保存差异信息，下一步进行格式化并推送给前台
            context.differenceInfo = differenceInfo
            log.error '报价异常，请联系人工处理'
            getFatalErrorFSRV '报价异常，请联系人工处理'
        }
    }

    private static replaceCustomerInfo(context, firstQuotePriceReqJSON) {
        def order = context.order
        def auto = context.auto
        def autoId = auto.identity
        //投保人基本信息
        def applicantIdentityType = context.insurance?.applicantIdentityType ?: context.compulsoryInsurance?.applicantIdentityType
        //投保人证件类型
        def applicantName = context.insurance?.applicantName ?: context.compulsoryInsurance?.applicantName ?: order?.applicantName ?: auto.owner
        //投保人姓名
        def applicantIdNo = context.insurance?.applicantIdNo ?: context.compulsoryInsurance?.applicantIdNo ?: order?.applicantIdNo ?: autoId
        //投保人证件号
        def applicantEmail = context.insurance?.applicantEmail ?: context.compulsoryInsurance?.applicantEmail//投保人邮箱
        //投保人电话
        def applicantMobile  = context.insurance?.applicantMobile ?: context.compulsoryInsurance?.applicantMobile
        //被保人基本信息
        def insuredIdentityType = context.insurance?.insuredIdentityType ?: context.compulsoryInsurance?.insuredIdentityType
        //被保人证件类型
        def insuredName = context.insurance?.insuredName ?: context.compulsoryInsurance?.insuredName ?: order?.insuredName ?: auto.owner
        //被保人姓名
        def insuredIdNo = context.insurance?.insuredIdNo ?: context.compulsoryInsurance?.insuredIdNo ?: order?.insuredIdNo ?: autoId
        //被保人证件号
        def insuredEmail = context.insurance?.insuredEmail ?: context.compulsoryInsurance?.insuredEmail //被保人邮箱
        //被保人电话
        def insuredMobile = context.insurance?.insuredMobile ?: context.compulsoryInsurance?.insuredMobile


        //调整基本信息
        //修正投保人信息
        firstQuotePriceReqJSON.Customer.collect { customer ->
            if (customer.Role == '1') {
                customer.Name = applicantName
                customer.IdentifyType = applicantIdentityType?.id ? (applicantIdentityType?.id as String).padLeft(2, '0') : '01'//默认用身份证
                customer.IdentifyNumber = applicantIdNo
                customer.Email = applicantEmail
                customer.Mobile = applicantMobile
            }
            if (customer.Role == '2') {
                customer.Name = insuredName
                customer.IdentifyType = insuredIdentityType?.id ? (insuredIdentityType?.id as String).padLeft(2, '0') : '01'//默认用身份证
                customer.IdentifyNumber = insuredIdNo
                customer.Email = insuredEmail
                customer.Mobile = insuredMobile
            }
            customer
        }
        firstQuotePriceReqJSON.head.token = context.token//华农的token生命周期只有20分钟，防止token过期，需要重新获取token
        firstQuotePriceReqJSON
    }
}
