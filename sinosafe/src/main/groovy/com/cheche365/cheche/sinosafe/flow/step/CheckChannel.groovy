package com.cheche365.cheche.sinosafe.flow.step

import com.cheche365.cheche.common.flow.IStep
import com.cheche365.cheche.core.model.Auto
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.Constants._DATE_FORMAT3
import static com.cheche365.cheche.common.Constants._DATE_FORMAT5
import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.common.util.ContactUtils.randomMobile
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getFatalErrorFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopBreakFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getLoopContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getObjectByCityCode
import static com.cheche365.cheche.parser.Constants._AUTO_TAX
import static com.cheche365.cheche.parser.Constants._GLASS_TYPE
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_COMMERCIAL_CAPTCHA_IMAGE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._SUPPLEMENT_INFO_COMPULSORY_CAPTCHA_IMAGE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.Constants._VALUABLE_HINT_AUTO_TYPE_TEMPLATE_QUOTING
import static com.cheche365.cheche.parser.util.BusinessUtils.addQFSMessage
import static com.cheche365.cheche.parser.util.BusinessUtils.disableCompulsoryAndAutoTax
import static com.cheche365.cheche.parser.util.BusinessUtils.getNotQuotedPolicyCauseFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.getValuableHintsFSRV
import static com.cheche365.cheche.parser.util.BusinessUtils.isCommercialQuoted
import static com.cheche365.cheche.parser.util.BusinessUtils.isCompulsoryOrAutoTaxQuoted
import static com.cheche365.cheche.sinosafe.flow.Constants._USE_CHARACTER_MAPPINGS
import static com.cheche365.cheche.sinosafe.util.BusinessUtils.CreateNewQR
import static com.cheche365.cheche.sinosafe.util.BusinessUtils._QUOTE_PRICE_MAPPINGS
import static com.cheche365.cheche.sinosafe.util.BusinessUtils.createRequestParams
import static com.cheche365.cheche.sinosafe.util.BusinessUtils.getCoverageInfoList
import static com.cheche365.cheche.sinosafe.util.BusinessUtils.getPayTaxVou
import static com.cheche365.cheche.sinosafe.util.BusinessUtils.parameterHandle
import static com.cheche365.cheche.sinosafe.util.BusinessUtils.premiumToDouble
import static com.cheche365.cheche.sinosafe.util.BusinessUtils.sendAndReceive2Map
import static com.cheche365.flow.core.util.FlowUtils.getKnownReasonErrorFSRV
import static com.cheche365.flow.core.util.FlowUtils.getNeedSupplementInfoFSRV



/**
 * 报价
 * create by sufc
 *
 */
@Slf4j
class CheckChannel implements IStep {

    private static final _RENRENCHEPARAMETER = 'renrenche'
    private static final _NONE = 'none'


    @Override
    Object run(Object context) {
        //获取渠道代码
        getContinueFSRV context.channel == _RENRENCHEPARAMETER ? _RENRENCHEPARAMETER : _NONE
}
}
