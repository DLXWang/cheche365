package com.cheche365.cheche.parser.service

import com.cheche365.cheche.core.exception.KnownReasonException
import com.cheche365.cheche.core.exception.LackOfSupplementInfoException
import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.common.util.CollectionUtils.mergeMaps
import static com.cheche365.cheche.core.model.QuoteSource.Enum.REFERENCED_7
import static com.cheche365.cheche.core.model.QuoteSource.Enum.RULEENGINE2_8


/**
 * 人造保险公司服务<br>
 * 报价成功后有后处理<br>
 * 报价失败时使用预制的规则生成报价
 */
@Slf4j
class ArtificialThirdPartyHandlerService implements IThirdPartyHandlerService {

    private IThirdPartyHandlerService delegate
    private Closure preProcess
    private Closure postSuccessfulRule
    private Closure postFailedRule

    ArtificialThirdPartyHandlerService(
        IThirdPartyHandlerService delegate,
        Closure preProcess,
        Closure postSuccessfulRule,
        Closure postFailedRule) {
        this.delegate = delegate
        this.preProcess = preProcess
        this.postFailedRule = postFailedRule
        this.postSuccessfulRule = postSuccessfulRule
    }

    @Override
    void quote(QuoteRecord quoteRecord, Map<String, Object> additionalParameters) {
        try {
            preProcess(quoteRecord, additionalParameters)
            log.info '参考报价前quoteRecord为：{}', quoteRecord
            log.info '{}参考报价前additionalParameters为：{}',quoteRecord.insuranceCompany.name, additionalParameters
            delegate.quote(quoteRecord, additionalParameters)

            if (postSuccessfulRule) {
                postSuccessfulRule quoteRecord, additionalParameters
                log.info '流程参考报价结果为：{}', quoteRecord
            }
        } catch (ex) {
            log.info '流程异常为：{}，进行模糊报价', ex.message
            if (ex instanceof LackOfSupplementInfoException || ex instanceof KnownReasonException) {
                additionalParameters.putAll mergeMaps(additionalParameters, [flowState : [(quoteRecord.insuranceCompany.id) : REFERENCED_7]])
                throw ex
            } else if (postFailedRule) {
                postFailedRule quoteRecord, additionalParameters, ex
                log.info '流程模糊报价结果为：{}', quoteRecord
            } else {
                throw ex
            }
        }
    }

    @Override
    void insure(PurchaseOrder order, Insurance insurance, CompulsoryInsurance compulsoryInsurance, Map<String, Object> additionalParameters) {
        if (RULEENGINE2_8 != (insurance?.quoteRecord?.type ?: compulsoryInsurance?.quoteRecord?.type)) {
            delegate.insure order, insurance, compulsoryInsurance, additionalParameters
        } else {
            throw new UnsupportedOperationException('当前报价方式不支持核保')
        }
    }

    @Override
    void order(PurchaseOrder order, Insurance insurance, CompulsoryInsurance compulsoryInsurance, Map<String, Object> additionalParameters) {
        if (RULEENGINE2_8 != (insurance?.quoteRecord?.type ?: compulsoryInsurance?.quoteRecord?.type)) {
            delegate.order order, insurance, compulsoryInsurance, additionalParameters
        } else {
            throw new UnsupportedOperationException('当前报价方式不支持下单')
        }
    }
}
