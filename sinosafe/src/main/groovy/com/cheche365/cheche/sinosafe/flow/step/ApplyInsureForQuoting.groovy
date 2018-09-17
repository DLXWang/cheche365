package com.cheche365.cheche.sinosafe.flow.step

import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.parser.util.InsuranceUtils.adjustInsurancePackageFSRV
import static com.cheche365.cheche.sinosafe.util.BusinessUtils._ADVICE_REGULATOR_MAPPINGS
import static com.cheche365.cheche.sinosafe.util.BusinessUtils._GET_EFFECTIVE_ADVICES
import static com.cheche365.cheche.sinosafe.util.BusinessUtils.populateQR



/**
 * 核保(报价流程)
 */
@Slf4j
class ApplyInsureForQuoting extends AApplyInsure {

    @Override
    protected doDealInsureSuccess(context, result, errorMsg) {
        if (checkResponseStatus(context, errorMsg, '自动核保', '核保通过')) {
            populateQR context
            return getLoopBreakFSRV(errorMsg)
        }
        // 报价阶段存储是否上传影像状态，核保时先校验上传影像状态
        context.isUpdateImages = ['FH00T201700900', 'FH00T201700009', 'FH12T201700002', 'FH12T201700203', 'FH12T201700900'].any { code ->
            errorMsg.contains(code) || ['影像', '照片', '图片', '上传'].find { errorMsg.contains(it)
            }}
        context.updateImagesErrorMessage = context.isUpdateImages ? errorMsg : null
        // 华安存在不需要在报价处理的套餐建议，返回的fsrv为LoopBreak：直接拼凑QR返回报价成功
        def fsrv = adjustInsurancePackageFSRV _ADVICE_REGULATOR_MAPPINGS, _GET_EFFECTIVE_ADVICES, errorMsg, context
        if (fsrv[2] == context.terminalFsrvPayload) {
            populateQR context
        }
        return fsrv
    }
}
