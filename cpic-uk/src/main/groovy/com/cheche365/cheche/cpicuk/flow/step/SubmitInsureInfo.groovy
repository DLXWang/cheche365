package com.cheche365.cheche.cpicuk.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.FlowUtils.generateRequestParameters
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.cpicuk.util.BusinessUtils.getSupplementCaptchaImageType
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING
import static com.cheche365.cheche.parser.util.BusinessUtils.getSupplementInfoFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.populateNewQuoteRecordAndInsurances
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static com.cheche365.flow.core.util.FlowUtils.getNeedSupplementInfoFSRV
import static groovyx.net.http.ContentType.JSON



/**提交投保信息
 * Created by chukh on 2018/5/8.
 */
@Component
@Slf4j
class SubmitInsureInfo implements IStep {

    private static final _API_PATH_SUBMIT_INSURE_INFO = '/ecar/insure/submitInsureInfo'

    @Override
    run(Object context) {
        RESTClient client = context.client
        client.request(Method.POST) { req ->
            requestContentType = JSON
            contentType = JSON
            uri.path = _API_PATH_SUBMIT_INSURE_INFO
            body = generateRequestParameters(context, this)
            log.debug '请求体：\n{}', body
            response.success = { resp, json ->
                def result = json
                log.debug '提交核保结果：{}', result
                if (result.message.code == 'success') {
                    //不论核保意见是拒保还是自核通过，都会生成投保单号
                    def commercial = result.result.commercial
                    def compulsory = result.result.compulsory
                    if (commercial) {
                        //商业险 保单号和提示信息
                        def insuredNo = result.result.commercialInsuransVo.insuredNo
                        def msg = result.result.commercialInsuransVo.questionAnswer
                        context.commercialInsureNo = insuredNo
                        log.debug '商业险投保单号为：{}，核保结果：{}', insuredNo, msg
                    }
                    if (compulsory) {
                        //交强险
                        def insuredNo = result.result.compulsoryInsuransVo.insuredNo
                        def msg = result.result.compulsoryInsuransVo.questionAnswer
                        context.compulsoryInsureNo = insuredNo
                        log.debug '交强险投保单号为：{}，核保结果：{}', insuredNo, msg
                    }
                    context.newQuoteRecordAndInsurances = populateNewQRI(context)

                    //没有自核通过
                    if (result.result?.checkInfoVo) {
                        def questionAnswer = result.result.checkInfoVo.questionAnswer
                        def answerFlag = result.result.checkInfoVo.answerFlag
                        if (questionAnswer && answerFlag == 'F') {
                            log.debug '提交审核失败，太平洋给出的提示为：{}', questionAnswer
                            if (questionAnswer.contains('验车')) {
                                //推补充信息 上传影像
                                context.proposal_status = '需上传影像'
                                return getSupplementInfoFSRV([mergeMaps(_SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING, [meta: [orderNo: context.order.orderNo]])])
                            }
                            return getKnownReasonErrorFSRV(questionAnswer)
                        } else if (result.result.checkInfoVo.checkCode) {
                            // 处理商业险转保验证码（小概率出现）
                            getSupplementCaptchaImageType(context, result.result.checkInfoVo.checkCode, result.result.checkInfoVo.isTrafficQuestion)
                            return getNeedSupplementInfoFSRV({ context.needSupplementInfos })
                        } else {
                            log.debug '提交审核失败，请与客服联系'
                            return getKnownReasonErrorFSRV('提交审核失败，请与客服联系')
                        }
                    } else {
                        //自核通过的情形
                        log.debug '自动核保成功'
                        //核保流程分支
                        context.proposal_status = '核保成功'
                        getContinueFSRV context.proposal_status
                    }
                } else {
                    log.error '核保失败：{}', result.message.message
                    getKnownReasonErrorFSRV result.message.message
                }
            }
            response.failure = { resp, body ->
                log.error '核保异常：{}', body
                // 处理验证码处理失败
                if (body.message?.message?.contains('录入的校验码有误')) {
                    log.debug '录入的校验码有误, 再次获取核保信息'
                    context.getInsureCheckCode = true
                    return getContinueFSRV('重新获取核保验证码')
                }
                getKnownReasonErrorFSRV body.message.message
            }
        }
    }

    private static populateNewQRI(context) {
        def (_1, newInsurance, newCompulsoryInsurance) = populateNewQuoteRecordAndInsurances(context, context.commercialInsureNo, null, context.compulsoryInsureNo, null)

        [_1, newInsurance, newCompulsoryInsurance]
    }

}
