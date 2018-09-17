package com.cheche365.cheche.zhongan.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.parser.util.BusinessUtils
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.parser.Constants._QUOTE_RECORD_ANNOTATION_META_CATEGORY_SHOW
import static com.cheche365.cheche.parser.Constants._QUOTE_RECORD_ANNOTATION_META_OPERATION_TYPE_READONLY_DATA
import static com.cheche365.cheche.parser.Constants._QUOTE_RECORD_SPECIAL_AGREEMENT_DESCRIPTION_COMMERCIAL
import static com.cheche365.cheche.parser.Constants._QUOTE_RECORD_SPECIAL_AGREEMENT_DESCRIPTION_COMPULSORY
import static com.cheche365.cheche.parser.util.BusinessUtils.isCommercialQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted
import static com.cheche365.cheche.zhongan.util.BusinessUtils.sendAndReceive



/**
 * 生成特别约定
 */
@Component
@Slf4j
class QuerySpecialPromise implements IStep {

    private static final _SERVICE_NAME = 'zhongan.castle.policy.querySpecialPromise'

    private static final _SPECIAL_AGREEMENT_OPTIONS = [
        (BusinessUtils.&isCommercialQuoted)         : ['businessPromiseList', _QUOTE_RECORD_SPECIAL_AGREEMENT_DESCRIPTION_COMMERCIAL],
        (BusinessUtils.&isCompulsoryOrAutoTaxQuoted): ['compelPromiseList', _QUOTE_RECORD_SPECIAL_AGREEMENT_DESCRIPTION_COMPULSORY]
    ]

    @Override
    def run(Object context) {

        def insurancePackage = context.accurateInsurancePackage

        def params = [
            insureFlowCode            : context.insureFlowCode,
            isInsureBussinessInsurance: isCommercialQuoted(context.accurateInsurancePackage) ? 'Y' : 'N',
            isInsureCompelInsurance   : isCompulsoryOrAutoTaxQuoted(context.accurateInsurancePackage) ? 'Y' : 'N'
        ]

        def result = sendAndReceive(context, this.class.name, _SERVICE_NAME, params)
        log.debug '浮动告知单生成result = {}', result

        if ('0' == result.result) {
            def payload = _SPECIAL_AGREEMENT_OPTIONS.findResults { isInsurancePackageOptionEnabled, option ->
                isInsurancePackageOptionEnabled(insurancePackage) ? option : null
            }.collect { specialAgreementListPropName, description ->
                def terms = result[specialAgreementListPropName].promiseDesc
                terms ? [terms: terms, description: description] : null
            } - null

            if (payload) {
                context.specialAgreement = [
                    payload: payload,
                    meta   : [
                        operationType: _QUOTE_RECORD_ANNOTATION_META_OPERATION_TYPE_READONLY_DATA,
                        category     : _QUOTE_RECORD_ANNOTATION_META_CATEGORY_SHOW
                    ]
                ]
            }

            getContinueFSRV result
        } else {
            getFatalErrorFSRV '浮动告知单生成错误'
        }
    }

}
