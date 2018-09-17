package com.cheche365.cheche.botpy.flow.step

import com.cheche365.cheche.common.flow.IStep
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.botpy.flow.Constants._API_PATH_CREATE_QUOTE
import static com.cheche365.cheche.botpy.flow.Handlers._KIND_CODE_CONVERTERS_CONFIG
import static com.cheche365.cheche.botpy.flow.Handlers._PARAMS_CONVERTER
import static com.cheche365.cheche.botpy.flow.Handlers.getAllKindItems
import static com.cheche365.cheche.botpy.util.BusinessUtils.getPrivyDTO
import static com.cheche365.cheche.botpy.util.BusinessUtils.getQuoteGroup
import static com.cheche365.cheche.botpy.util.BusinessUtils.getRequestIdForPath
import static com.cheche365.cheche.botpy.util.BusinessUtils.getVehicleDTO
import static com.cheche365.cheche.botpy.util.BusinessUtils.getExtendDTO
import static com.cheche365.cheche.botpy.util.BusinessUtils.sendParamsAndReceive
import static com.cheche365.cheche.botpy.util.BusinessUtils.setRequestIdForPath
import static com.cheche365.cheche.botpy.util.BusinessUtils.getMappingsValue
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.getCommercialSupplementInfoPeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.getCompulsorySupplementInfoPeriodTexts
import static com.cheche365.cheche.parser.util.BusinessUtils.getQuoteKindItemParams
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted
import static com.cheche365.flow.core.util.FlowUtils.getProvideValuableHintsFSRV
import static groovyx.net.http.Method.POST


/**
 * 创建报价请求
 */
@Component
@Slf4j
class CreateQuote implements IStep {

    @Override
    run(context) {
        def quoteLimitChecker = context.quoteLimitChecker
        //检查是否达到上限,达到上限直接抛出异常
        quoteLimitChecker?.meetLimit(context.qr, context.accountId)
        // 未达到报价上限
        quoteLimitChecker?.oneMore(context.qr, context.accountId)

        log.info "执行账号校验：${quoteLimitChecker?:'quoteLimitChecker is null '}"
        if (!context.carInfo?.id) {
            def hints = [
                _VALUABLE_HINT_VIN_NO_TEMPLATE_QUOTING.with {
                    it.originalValue = context.auto.vinNo
                    it
                },
                _VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING.with {
                    it.originalValue = context.auto.autoType?.code
                    it
                }
            ]
            return getProvideValuableHintsFSRV { hints }
        }

        def cityCodeMappings = context.cityCodeMappings

        def selection = createOutInsurancePackage(context, _KIND_CODE_CONVERTERS_CONFIG)

        def body = [
            prov_code       : cityCodeMappings.prov_code,
            city_code       : cityCodeMappings.city_code,
            vehicle         : getVehicleDTO(context, context.vehicleInfo), // 车辆信息
            fuel_type       : getMappingsValue(context, 'fuelType') ?: '0', // 燃油类型
            vehicle_class   : 'A01', // 车辆种类
            vehicle_nature  : getMappingsValue(context, 'useCharacter') ?: '211', // 车辆使用性质
            insurant_type   : getMappingsValue(context, 'identityType', 'HOLDER_TYPE') ?: '01', // 被保险人持有人类型代码
            selection       : selection, // 险种选择
            ics             : getQuoteGroup(context), // // 报价的公司列表
            model_type      : 'ic-model', // 报价车型类型
            ic_model_id     : context.carInfo.id, // 保险公司车型id
//            quote_non_support_ic:'', // 报价保险公司不支持的保险公司
//            renewal_model:'', // 续保车型
            biz_start_date  : getCommercialSupplementInfoPeriodTexts(context).first, // 商业险起保日期
            force_start_date: getCompulsorySupplementInfoPeriodTexts(context).first, // 交强险起保日期
            extend          : getExtendDTO(context), // 扩展信息（过户车时有用）
            owner           : getPrivyDTO(context, context.auto), // 车主信息
        ]

        def result = sendParamsAndReceive context, _API_PATH_CREATE_QUOTE, body, POST, log

        if (result.error) {
            log.error '创建报价请求失败， 后续步骤终止'
            getFatalErrorFSRV result.error
        } else {
            setRequestIdForPath context, result, _API_PATH_CREATE_QUOTE
            getContinueFSRV getRequestIdForPath(context, _API_PATH_CREATE_QUOTE)
        }

    }

    private static createOutInsurancePackage(context, kindCodeConvertersConfig) {
        context.kindCodeConvertersConfig = kindCodeConvertersConfig
        def quoteParams = getQuoteKindItemParams(context, getAllKindItems(context), kindCodeConvertersConfig, _PARAMS_CONVERTER)
        quoteParams.sum() + [force: isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage) ? 1 : 0]
    }

}
