package com.cheche365.cheche.aibao.flow.step

import com.cheche365.cheche.aibao.util.BusinessUtils
import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.aibao.util.BusinessUtils.getVeriteCodes
import static com.cheche365.cheche.aibao.util.BusinessUtils.supplyInfo
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.parser.Constants.get_DATE_FORMAT5
import static com.cheche365.flow.core.util.FlowUtils.getNeedSupplementInfoFSRV



/**
 * 报价
 * Created by xuecl on 2018/09/04.
 */
@Slf4j
abstract class APriceQuote implements IStep {

    // 默认报价
    def interfaceID

    @Override
    def run(context) {
        /**
         * 1.如果询价接口返回了bzVerifyCodeImg || busiVerifyCodeImg，则说明这是一个转保业务，需重新封装调用
         * 2.如果询价接口返回了终保日期，则说明这是一个续保或者重复投保的单子。
         */
        def result = BusinessUtils.sendParamsAndReceive(context, getRequestParams(context), log, getInterfaceID())
        log.debug('{}判断是否为转保业务', context.aiBaoTransactionNo)
        // 封装转保验证码图片
        def imagesBase64 = [:]
        // 保存验证码图片到 context
        def veriteCodes = recognizeCaptcha(context, result, imagesBase64)
        if (!imagesBase64) {
            if ('0000' == result.head.errorCode) {
                return dealSuccessResult(context, result)
            } else {
                log.error '询价申请失败 resultMessage : {}', result.head.errorMsg
                return getFatalErrorFSRV(result.head.errorMsg ?: '询价失败，请联系人工处理')
            }
        } else {
            log.debug('{}询价判定为转保业务，需重新封装调用', context.aiBaoTransactionNo)
            if (veriteCodes?.size() == imagesBase64.size()) {
                log.info '验证码识别结果：{}', veriteCodes.toString()
                getLoopContinueFSRV(veriteCodes, null)
            } else {
                log.warn '验证码识别失败，推送到前台'
                getNeedSupplementInfoFSRV { supplyInfo(imagesBase64) }
            }
        }
    }

    /**
     * 自动识别验证码
     */
    private static recognizeCaptcha(context, result, imagesBase64) {
        def mainInfo = result.body.mainInfo
        // 封装转保验证码图片
        if (mainInfo?.bzVerifyCodeImg) imagesBase64 << [bzVerifyCodeImg: mainInfo.bzVerifyCodeImg]
        if (mainInfo?.busiVerifyCodeImg) imagesBase64 << [busiVerifyCodeImg: mainInfo.busiVerifyCodeImg]
        if (!imagesBase64) return null
        // 识别验证码
        context.veriteCodes = imagesBase64.collectEntries { image ->
            image.value = context.decaptchaService.recognizeCaptcha image.value, [decaptchaInputTopic: context.decaptchaInputTopicKey] ?: ''
            image
        }
    }

    /**
     * 设置请求参数
     */
    protected getRequestParams(context) {
        def commerStartDate = context.additionalParameters?.supplementInfo?.commercialStartDate                   // 商业险起保日期
        def compulsoryStartDate = context.additionalParameters?.supplementInfo?.compulsoryStartDate               // 交强险起保日期
        def veriteCodes = getVeriteCodes(context)
        def params = [mainInfo: [
            busiStartDate           : commerStartDate ? _DATE_FORMAT5.format(commerStartDate) : '', // 商业险起保日期
            busiInsureFlag          : commerStartDate ? '1' : '0',                            // 是否投保商业险0：否；1：是，如果不传默认承保
            bzStartDate             : compulsoryStartDate ? _DATE_FORMAT5.format(compulsoryStartDate) : '',// 交强险起保日期
            bzInsureFlag            : compulsoryStartDate ? '1' : '0',                    // 是否投保交强险0：否；1：是，如果不传默认承保
            effectiveImmediatelyFlag: '0',                                                // 即时生效标示1：是，0：否
            insurenceCode           : 'CH10000009',                                        // 承保公司 人保车险
            bzVerifyCode            : veriteCodes.bzVerifyCode,                           // 交强险验证码，转保验证的时候才需要使用
            busiVerifyCode          : veriteCodes.busiVerifyCode                          // 商业险验证码，转保验证的时候才需要使用
//            beneficiary:'',                                                             // 约定受益人，贷款车需上传该字段信息
//            busiEndDate:'2019-06-15',                                                   // 商业险终止日期，如果不传默认保期一年
//            bzEndDate:'2019-06-15',                                                      // 交强险终止日期，如果不传默认一年
        ]]
        params << getSpecificParams(context)
    }

    /**
     * 处理返回结果
     */
    protected abstract dealSuccessResult(context, result)

    /**
     * 设置专有的请求参数
     */
    protected abstract getSpecificParams(context)

}
