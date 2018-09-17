package com.cheche365.cheche.aibao.flow.step

import groovy.util.logging.Slf4j

import static com.cheche365.cheche.aibao.util.BusinessUtils._KIND_CODE_CONVERTERS_CONFIG
import static com.cheche365.cheche.aibao.util.BusinessUtils.getCarInfo
import static com.cheche365.cheche.aibao.util.BusinessUtils.getCarOwnerInfo
import static com.cheche365.cheche.aibao.util.BusinessUtils.getUserInfo
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.parser.Constants.get_DATE_FORMAT3
import static com.cheche365.cheche.parser.Constants.get_INSURANCE_PACKAGE_FIELD_NAME_DESCRIPTION_MAPPINGS



/**
 * 报价
 * Created by xuecl on 2018/08/26.
 */
@Slf4j
class DefaultPriceQuote extends APriceQuote {

    // 默认报价
    def interfaceID = '100071'

    @Override
    protected def dealSuccessResult(context, result) {
        log.info '报价成功：{}，进入险别校验', result.head.errorMsg
        ArrayList itemKindInfo = result.body.itemKindInfo
        if (!itemKindInfo) return getFatalErrorFSRV("该车不支持报价")
        fixedDate context, result.body.mainInfo
        def resultList = getUnsupportedInsurancePackagePropDescriptions itemKindInfo, context.accurateInsurancePackage
        if (!resultList) {
            log.info '默认报价校验通过，流程继续。'
            getLoopBreakFSRV '默认报价完成'
        } else {
            String errorMessage = "险别校验异常，不支持该险别:$resultList"
            log.error errorMessage
            getFatalErrorFSRV errorMessage
        }
    }

    @Override
    protected def getSpecificParams(context) {
        [insuredInfo: getUserInfo(context), carOwnerInfo: getCarOwnerInfo(context)] << getCarInfo(context)
    }

    /**
     * 校验用户传入的险别是否有效
     *
     * @param kindsList 接口返回的支持的险别
     * @param accurateInsurancePackage 用户选择的险别
     * @return
     */
    private static getUnsupportedInsurancePackagePropDescriptions(kindCodes, insurancePackage) {
        if (kindCodes) {
            def validKindCodes = kindCodes.findAll { kind -> kind.insureFlag == "1" }.kindCode
//            def validKindCodes = kindCodes.kind.kindCode
            _KIND_CODE_CONVERTERS_CONFIG.findResults { configItem ->
                def (kindCode, propName) = configItem
                // 套餐中选中的、对方不支持的险别
                if (insurancePackage[propName] && !(kindCode in validKindCodes)) {
                    _INSURANCE_PACKAGE_FIELD_NAME_DESCRIPTION_MAPPINGS[propName]
                }
            }
        }
    }

    /**
     * 根据接口返回的时间修改用户传入时间，修改规则：接口返回时间大于用户传入时间则修改为返回时间
     *
     * @param context
     * @param mainInfo 接口返回的主要信息包含险别起始时间
     * @return
     */
    private static fixedDate(context, mainInfo) {
        // 接口返回的时间
        def startDateCIP = _DATE_FORMAT3.parse((String) mainInfo.busiStartDate ?: '')
        def startDateBZ = _DATE_FORMAT3.parse((String) mainInfo?.bzStartDate ?: '')
        // 前台传入的时间
        def compulsoryStartDate = context.additionalParameters?.supplementInfo?.compulsoryStartDate
        def commercialStartDate = context.additionalParameters?.supplementInfo?.commercialStartDate
        // 记录到 context 中用于前台页面提示用
        context.postcommercialStartDate = context.precommercialStartDate = commercialStartDate
        context.postcompulsoryStartDate = context.precompulsoryStartDate = compulsoryStartDate
        // 接口返回时间大于用户传入时间则修改
        if (compulsoryStartDate && compulsoryStartDate < startDateBZ) {
            context.postcompulsoryStartDate = context.additionalParameters?.supplementInfo?.compulsoryStartDate = startDateBZ
        }
        if (commercialStartDate && commercialStartDate < startDateCIP) {
            context.postcommercialStartDate = context.additionalParameters?.supplementInfo?.commercialStartDate = startDateCIP
        }
    }
}
