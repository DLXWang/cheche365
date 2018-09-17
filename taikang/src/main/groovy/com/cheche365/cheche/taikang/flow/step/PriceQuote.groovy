package com.cheche365.cheche.taikang.flow.step

import com.cheche365.cheche.common.flow.IStep
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT5
import static com.cheche365.cheche.parser.Constants._INSURANCE_MAPPINGS
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCommercial
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCompulsoryAndAutoTax
import static com.cheche365.cheche.parser.util.BusinessUtils.getQuoteKindItemParams
import static com.cheche365.cheche.parser.util.BusinessUtils.isCommercialQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecord
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecordBZ
import static com.cheche365.cheche.taikang.util.BusinessUtils._I2O_PREMIUM_CONVERTER
import static com.cheche365.cheche.taikang.util.BusinessUtils._KIND_CODE_CONVERTERS_CONFIG
import static com.cheche365.cheche.taikang.util.BusinessUtils.getAllKindItems
import static com.cheche365.cheche.taikang.util.BusinessUtils.updateDecaptchaText
import static com.cheche365.cheche.taikang.util.BusinessUtils.getBizConfig
import static com.cheche365.cheche.taikang.util.BusinessUtils.getEndTime
import static com.cheche365.cheche.taikang.util.BusinessUtils.getInnerCode
import static com.cheche365.cheche.taikang.util.BusinessUtils.getFirstCheck
import static com.cheche365.cheche.taikang.util.BusinessUtils.preProcessCaptcha
import static com.cheche365.cheche.taikang.util.BusinessUtils.sendParamsAndReceive
import static com.cheche365.cheche.taikang.util.BusinessUtils.str2double
import static com.cheche365.cheche.taikang.util.BusinessUtils.getCaptchaImageSupplementInfo
import static com.cheche365.flow.core.util.FlowUtils.getNeedSupplementInfoFSRV
import static com.cheche365.cheche.common.util.ContactUtils.randomMobile
import groovy.util.logging.Slf4j



/**
 * 报价接口
 * Created by yz on 2018/04/16.
 */
@Slf4j
class PriceQuote implements IStep {

    private static final _FUNCTION = 'priceQuote'

    @Override
    run(Object context) {

        //预处理web端传入的验证码
        if (context.additionalParameters.supplementInfo?.commercialCaptchaImage || context.additionalParameters.supplementInfo?.compulsoryCaptchaImage) {
            log.debug 'TK预核保获取前台报价商业险验证码：{},交强险验证码：{}', context.additionalParameters.supplementInfo?.commercialCaptchaImage, context.additionalParameters.supplementInfo?.compulsoryCaptchaImage
            def result = sendParamsAndReceive context, _FUNCTION, getRequestParams(context, null), log
            if (getFirstCheck(result)) {
                preProcessCaptcha(context, result)
                log.info 'TK报价获取前台验证码转换后checkList：{}', context.quoteCheckList
            } else {
                if ('200' == result.apply_content.reponseCode) {
                    //非转保业务
                    dealSuccessfulResult(context, result)
                    return getLoopBreakFSRV('TK报价完成')
                } else {
                    log.info '询价失败 resultMessage：{}', result.apply_content.messageBody
                    return getFatalErrorFSRV(result.apply_content.messageBody ?: '询价失败，请联系人工处理')
                }
            }
        }

        //开始报价
        def result = sendParamsAndReceive context, _FUNCTION, getRequestParams(context, context.quoteCheckList), log

        if ('200' == result.apply_content.reponseCode) {
            //报价成功
            dealSuccessfulResult(context, result)
            getLoopBreakFSRV('非转保业务报价完成')
        } else {
            if (result.apply_content.messageBody.contains('校验码有误')) {
                //报价验证码校验错误，推送
                def needSupplementInfo = getCaptchaImageSupplementInfo(context.quoteCheckList, context.currentBase64)
                getNeedSupplementInfoFSRV { needSupplementInfo }
            } else if (getFirstCheck(result) && getFirstCheck(result).check.checkCode) {
                //验证码识别，循环报价或者推送验证码信息
                recognizeCaptcha(result, context)
            } else {
                //常规异常
                getFatalErrorFSRV(result.apply_content.messageBody ?: '询价失败，请联系人工处理')
            }
        }
    }

    private static dealSuccessfulResult(context, result) {
        /**
         * 返回值需要保存2个 单号（交强，商业）到context 给核保使用
         */
        result.apply_content.data.riskList?.each {
            riskInfo ->
                def effectStartTime = riskInfo.risk.effectStartTime + ':00'
                def riskCode = riskInfo.risk.riskCode
                def quotationNo = riskInfo.risk.quotationNo
                if (riskCode == '0803') {
                    context.postCommercialStartDate = _DATE_FORMAT5.parse(effectStartTime)
                    //泰康返回终保时间 2018-02-02 24:00 ，前台展示为2018-02-02 因此截取
                    context.commercialExpireDate = riskInfo.risk.effectEndTime[0..9] + ' 23:59:59'
                    context.commercialBeginDate = effectStartTime
                    context.quotationNoBI = quotationNo

                }
                if (riskCode == '0807') {
                    //未提供相关formatter
                    context.postCompulsoryStartDate = _DATE_FORMAT5.parse(effectStartTime)
                    context.compulsoryExpireDate = riskInfo.risk.effectEndTime[0..9] + ' 23:59:59'
                    context.compulsoryBeginDate = effectStartTime
                    context.quotationNoCI = quotationNo

                }
        }
        //为防止前台误传时间，给post起始时间默认值
        if (!context.postCommercialStartDate) {
            context.postCommercialStartDate = context.preCommercialStartDate
        }
        if (!context.postCompulsoryStartDate) {
            context.postCompulsoryStartDate = context.preCompulsoryStartDate
        }
        //外转内
        def sumTax = str2double(result.apply_content.data.sumTax)
        def seats = str2double(context.selectedCarModel?.carModel?.seatCount) as int
        populateQR context, _KIND_CODE_CONVERTERS_CONFIG, result.apply_content.data.riskList, context.auto.seats ? context.auto.seats - 1 : seats - 1, sumTax
    }

    private static getRequestParams(context, checkList) {
        //是否是过户车
        def transferDate = context.additionalParameters.supplementInfo?.transferDate
        //封装报价车辆基础信息
        def carInfo = [
            carModelKey    : context.selectedCarModel?.carModel?.carModelKey,
            loanVehicleFlag: '0', //暂时默认为0 是否车贷多年
            chgOwnerFlag   : transferDate ? 1 : 0
        ]
        //封装车辆的条件参数
        if (transferDate) {
            carInfo << [transferDate: _DATE_FORMAT3.format(transferDate)]
        }
        //封装险种信息
        def riskList = createKindCodes context
        //封装干系人信息
        def privyList = createPrivyList context

        def params = [car      : carInfo,
                      riskList : riskList,
                      privyList: [privyList]
        ]
        //转保业务
        if (checkList) {
            def checinfos = []
            checinfos << checkList.last()
            params << [checkList: checinfos]
        }
        params
    }

    private static createPrivyList(context) {
        [
            privy: [
                insuredFlag   : '0010000',  //询价为固定值，车主信息
                insuredName   : context.auto.owner,
                identifyType  : ((context.auto.identityType?.id ?: 1) as String).padLeft(2, '0'),
                identifyNumber: context.auto.identity,
                mobile        : context.additionalParameters.supplementInfo?.verificationMobile ?: context.applicant?.mobile ?: randomMobile
            ]
        ]
    }

    /**
     * 内转外
     * @param context
     * @return
     */
    private static createKindCodes(context) {
        context.kindCodeConvertersConfig = _KIND_CODE_CONVERTERS_CONFIG
        def quoteParams = getQuoteKindItemParams(context, getAllKindItems(_KIND_CODE_CONVERTERS_CONFIG), selectNeededConfig(context, _KIND_CODE_CONVERTERS_CONFIG), _I2O_PREMIUM_CONVERTER)
        def commercialStartDate = context.additionalParameters.supplementInfo?.commercialStartDate //商业险起保日期
        def commercialEndDate = getEndTime(commercialStartDate) //商业险终保日期
        def compulsoryStartDate = context.additionalParameters.supplementInfo?.compulsoryStartDate //交强险起保日期
        def compulsoryEndDate = getEndTime(compulsoryStartDate)       //交强险终保日期
        quoteParams.inject([]) { result, kindInfo ->
            //交强险判断
            def isCompulsoryFlag = kindInfo.kindCode == 'BZ'
            def transKindCode = isCompulsoryFlag ? '0807' : '0803'
            def riskInfo = result.find {
                riskItem -> riskItem.risk?.riskCode == transKindCode
            }
            riskInfo ? riskInfo.risk.kindList << [kind: kindInfo] : result <<
                [risk:
                     [
                         riskCode       : transKindCode,    //险种代码(商业险/强险)
                         effectStartTime: isCompulsoryFlag ? _DATE_FORMAT5.format(compulsoryStartDate) : _DATE_FORMAT5.format(commercialStartDate),   //起保日期
                         effectEndTime  : isCompulsoryFlag ? compulsoryEndDate : commercialEndDate,  //终保日期
                         kindList       : [[kind: kindInfo]]   //险别信息
                     ]
                ]
            result
        }
    }

    private static selectNeededConfig(context, convertConfig) {
        def insurancePackage = context.accurateInsurancePackage
        convertConfig.findAll {
            config -> insurancePackage[config[1]] ? 1 : 0
        }
    }

    private static populateQR(context, kindCodeConvertersConfig, riskList, seatCount, sumTax) {
        def forceInfo = riskList.find { riskInfo -> riskInfo.risk.riskCode == '0807' }
        def bizInfo = riskList.find { riskInfo -> riskInfo.risk.riskCode == '0803' }
        if (isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage)) {
            //context  商业险总额  车船税sumTax
            populateQuoteRecordBZ(context, str2double(forceInfo.risk.riskSumPremium), str2double(sumTax))
        } else {
            disableCompulsoryAndAutoTax context
        }
        if (isCommercialQuoted(context.accurateInsurancePackage)) {
            //获取险别信息 外围编码 ：kindinfo
            def allKindItems = getQuotedItems(bizInfo, seatCount)
//            iop和主险对应外部编码不一致
//            context.iopAlone = true
            populateQuoteRecord(context, allKindItems, getBizConfig(kindCodeConvertersConfig, bizInfo), str2double(bizInfo.risk.riskSumPremium), null)
        } else {
            disableCommercial context
        }
    }

    private static getQuotedItems(items, seatCount) {
        items.risk.kindList.collectEntries { kind ->
            [
                (kind.kind.kindCode): [
                    amount       : str2double(kind.kind.amount),
                    premium      : !(_INSURANCE_MAPPINGS[getInnerCode(kind.kind.kindCode)].isIop) ? str2double(kind.kind.premium) : null,
                    iopPremium   : _INSURANCE_MAPPINGS[getInnerCode(kind.kind.kindCode)].isIop ? str2double(kind.kind.premium) : null,
                    quantity     : (seatCount as int) - 1,//乘客的数量
                    glassType    : kind.kind.modeCode, //玻璃类型  (默认就是1 国产)
                    nonDeductible: null
                ]
            ]
        }
    }

    /**
     * 验证码自动识别处理
     */
    private static recognizeCaptcha(result, context) {
        def checkList = result.apply_content.data.checkList
        context.quoteCheckList = checkList
        context.currentBase64 = checkList[0].check.checkCode
        try {
            def text = context.decaptchaService.recognizeCaptcha context.currentBase64, [decaptchaInputTopic: context.decaptchaInputTopicKey]
            if (text) {
                updateDecaptchaText(context, checkList[0].check.checkFlag, text)
                log.info '验证码识别结果：{}', text
                getLoopContinueFSRV(text, null)
            } else {
                log.warn '验证码识别失败，推送到前台'
                def needSupplementInfo = getCaptchaImageSupplementInfo(context.quoteCheckList, context.currentBase64)
                getNeedSupplementInfoFSRV { needSupplementInfo }
            }
        } catch (Exception e) {
            log.info('验证码识别异常：{} 推送到前台', e.getMessage())
            def needSupplementInfo = getCaptchaImageSupplementInfo(context.quoteCheckList, context.currentBase64)
            getNeedSupplementInfoFSRV { needSupplementInfo }
        }
    }
}
