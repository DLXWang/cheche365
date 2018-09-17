package com.cheche365.cheche.botpy.flow.step

import com.cheche365.cheche.core.exception.BusinessException
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.botpy.flow.Constants._API_PATH_CREATE_QUOTE
import static com.cheche365.cheche.botpy.flow.Handlers._KIND_CODE_CONVERTERS_CONFIG
import static com.cheche365.cheche.botpy.util.BusinessUtils.getQuotedItems
import static com.cheche365.cheche.botpy.util.BusinessUtils.str2double
import static com.cheche365.cheche.botpy.util.BusinessUtils.isAccountError
import static com.cheche365.cheche.botpy.util.BusinessUtils.getAutoModelByResult
import static com.cheche365.cheche.botpy.util.BusinessUtils.isVehicleModelError
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.parser.Constants._GLASS_TYPE
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCommercial
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCompulsoryAndAutoTax
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecord
import static com.cheche365.cheche.parser.util.BusinessUtils.populateQuoteRecordBZ
import static com.cheche365.cheche.parser.util.BusinessUtils.checkVehicleSupplementInfo
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV



/**
 * 轮询报价结果
 */
@Component
@Slf4j
class PollQuote extends APollResponse {

    private static final _API_PATH_POLL_QUOTE = '/requests/quotations/'

    @Override
    protected dealResultFsrv(context, result) {
        def quoteResult = result.quotations.first()
        if (quoteResult.is_success) {
            log.debug "金斗云报价成功，报价结果: {} ", quoteResult

            context.quotation_id = quoteResult.quotation_id
            populateQR context, _KIND_CODE_CONVERTERS_CONFIG, quoteResult.biz_info, quoteResult.force_info, quoteResult.model

            getLoopBreakFSRV(result)
        } else {
            log.info "报价失败， 错误原因：{}, 详细错误：{}", quoteResult.message, quoteResult.comment
            def reason = quoteResult.comment ?: quoteResult.message
            if (isAccountError(quoteResult.message) || isAccountError(quoteResult.comment)) {
                throw new BusinessException(BusinessException.Code.NOTIFICATION, -1, reason, null, 'botpy')
            }
            //车型有误时自动匹配一次车型
            if (!context.isAutoModelByResult) {
                //从context的两个车型列表中进行匹配，因此模糊匹配时autoModel直接为null，若模糊匹配时也进行修正则需要注意
                def autoModel = getAutoModelByResult(context, result)
                if (autoModel) {
                    context.isAutoModelByResult = 1
                    context.additionalParameters.supplementInfo.autoModel = autoModel.vehicle_data_id
                    context.additionalParameters.optionsSource = autoModel.optionsSource
                    return getContinueFSRV('自动修正车型')
                }
            }
            //用户没有选择车型时，推回车型列表，提示补充信息，若有选择车型时提示错误信息
            if (!context.additionalParameters.referToOtherAutoModel && !context.isUserSelectModel && isVehicleModelError(reason)) {
                return checkVehicleSupplementInfo(context)
            }
            getKnownReasonErrorFSRV(reason as String)
        }
    }

    @Override
    protected getApiPath() {
        _API_PATH_POLL_QUOTE
    }

    @Override
    protected getRequestIdPath() {
        _API_PATH_CREATE_QUOTE
    }

    @Override
    protected getPollTimes() {
        20
    }

    @Override
    protected getPollSleepTime() {
        2500
    }

    private static populateQR(context, kindCodeConvertersConfig, bizInfo, forceInfo, model) {
        def insuranceCompany = context.accurateInsurancePackage

        if (forceInfo) {
            populateQuoteRecordBZ(context, str2double(forceInfo.premium), str2double(forceInfo.tax))
        } else {
            disableCompulsoryAndAutoTax context
        }

        if (bizInfo) {
            def allKindItems = getQuotedItems(bizInfo.detail, insuranceCompany[_GLASS_TYPE], model)
            populateQuoteRecord(context, allKindItems, kindCodeConvertersConfig, str2double(bizInfo.total), null)
        } else {
            disableCommercial context
        }

    }
}
