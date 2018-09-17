package com.cheche365.cheche.pingan.flow.step.m

import com.cheche365.cheche.common.flow.IStep
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import groovyx.net.http.RESTClient
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.DateUtils.getDaysUntil
import static com.cheche365.cheche.common.util.DateUtils.getLocalDate
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getContinueWithIgnorableErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants._DATETIME_FORMAT3
import static com.cheche365.cheche.parser.Constants._DATE_FORMAT3
import static com.cheche365.cheche.parser.Constants._ERROR_MESSAGE_COMPULSORY_COMMON_CHECK_FAILURE
import static com.cheche365.cheche.parser.util.BusinessUtils.checkCompulsoryPackageOptionEnabled
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCompulsoryAndAutoTax
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCompulsoryAndAutoTaxTimeCause
import static com.cheche365.cheche.parser.util.BusinessUtils.getCompulsoryInsurancePeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecordBZ
import static com.cheche365.cheche.parser.util.BusinessUtils.setCompulsoryInsurancePeriodTexts
import static groovy.json.JsonParserType.LAX
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC



/**
 * 交强险报价
 * Created by wangxin on 2015/11/5.
 */
@Component
@Slf4j
class ForceQuote implements IStep {

    private static final _API_PATH_FORCE_QUOTE = 'autox/do/api/force-quote'

    /**
     * 交强险可投；在投保范围内，修改日期后可预约投保
     */
    private static _RH_C0000 = { context, forcePremium ->
        def lastBeginDate = forcePremium.lastBeginDate
        def lastEndDate = forcePremium.lastEndDate
        if (lastBeginDate && lastEndDate) {
            log.info '上张保单的交强险期限是{}到{}，当前报价起始日期是{}，需要修改交强险起保日期', lastBeginDate, lastEndDate, forcePremium.'forceInfo.beginDate'
            def earlyBeginDate = getLocalDate(_DATE_FORMAT3.parse(lastEndDate)).plusDays(1)
            def earlyBeginDateStr = _DATETIME_FORMAT3.format(earlyBeginDate)
            setCompulsoryInsurancePeriodTexts(context, earlyBeginDateStr)
            if (getDaysUntil(earlyBeginDate) > 90) {
                log.warn '交强险未到可投保日期,可预约投保'
                disableCompulsoryAndAutoTaxTimeCause context, 90
                getContinueWithIgnorableErrorFSRV false, forcePremium.message
            } else {
                log.info '修改交强险起保日期为{},并且重新获取报价', earlyBeginDateStr
                def forcePremium0 = (forcePremium.forcePremium ?: 0) as double
                def taxPremium = (forcePremium.taxPremium ?: 0) as double
                populateQuoteRecordBZ context, forcePremium0, taxPremium
                getContinueFSRV true
            }
        } else {
            def forcePremium0 = (forcePremium.forcePremium ?: 0) as double
            def taxPremium = (forcePremium.taxPremium ?: 0) as double
            log.info '获取交强险报价 交强险保费: {}, 车船税保费:{}', forcePremium0, taxPremium
            populateQuoteRecordBZ context, forcePremium0, taxPremium
            getContinueFSRV false
        }
    }

    /**
     * 非时间原因导致交强险不可报价，json中未获取起保日期
     * message = ‘message=抱歉，您的交强险无法报价，请检查保险起期，或车牌号、发动机号、车架号等信息填写是否正确，如有疑问请联系“在线客服”咨询。’
     */
    private static _RH_C4002 = { context, forcePremium ->
        log.warn '获取交强险报价失败 {}', forcePremium.message
        disableCompulsoryAndAutoTax context, _ERROR_MESSAGE_COMPULSORY_COMMON_CHECK_FAILURE
        getContinueWithIgnorableErrorFSRV false, forcePremium.message
    }

    /**
     * 时间原因导致的交强险不可投
     */
    private static _RH_DEFAULT = { context, forcePremium ->
        log.warn '获取交强险报价失败 {}', forcePremium.message
        disableCompulsoryAndAutoTaxTimeCause context, 90
        getContinueWithIgnorableErrorFSRV false, forcePremium.message
    }


    private static _RH_MAPPINGS = [
        C0000  : _RH_C0000,
        C4002  : _RH_C4002,
        default: _RH_DEFAULT
    ]


    @Override
    run(context) {
        if (!checkCompulsoryPackageOptionEnabled(context)) {
            log.info '该用户没有投保交强险'
            return getContinueFSRV(false)
        }
        RESTClient client = context.client
        def args = [
            path              : _API_PATH_FORCE_QUOTE,
            contentType       : TEXT,
            requestContentType: URLENC,
            body              : [
                'flowId'             : context.flowId,
                '__xrc'              : context.__xrc,
                'responseProtocol'   : 'json',
                'forceInfo.beginDate': getCompulsoryInsurancePeriodTexts(context).first as String
            ],
        ]
        def result = client.post args, { resp, text ->

            new JsonSlurper().with {
                type = LAX
                def jsonStr = text.readLines().findAll { line ->
                    !line.contains('message')
                }.sum()
                parseText(jsonStr)
            }
        }

        if ('C0000' == result.resultCode) {
            //判断交强险是否可投保
            if (result.forcePremium?.resultCode) {
                def fsrvOrFlag = checkCompulsory(context, result.forcePremium)
                if (fsrvOrFlag) {
                    return fsrvOrFlag
                }
            }
            getContinueFSRV true
        } else {
            def errorMsg = result.forcePremium.message
            context.forceQuoteMsg = errorMsg
            log.error '获取交强险报价失败 {}', errorMsg
            disableCompulsoryAndAutoTax context
            populateQuoteRecordBZ(context, 0, 0)
            getFatalErrorFSRV errorMsg
        }

    }

    /**
     * 检查交强险是否能是否可投保，并且按照返回数据里面的resultCode对套餐做不同的操作处理
     * @param forcePremium
     * @param context
     * @return
     */
    private static checkCompulsory(context, forcePremium) {
        def rh = forcePremium.resultCode ?
            _RH_MAPPINGS[forcePremium.resultCode] ?: _RH_MAPPINGS.default
            : _RH_MAPPINGS.C0000
        rh(context, forcePremium)
    }

}
