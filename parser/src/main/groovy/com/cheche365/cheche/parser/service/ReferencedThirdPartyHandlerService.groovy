package com.cheche365.cheche.parser.service

import com.cheche365.cheche.core.exception.KnownReasonException
import com.cheche365.cheche.core.exception.LackOfSupplementInfoException
import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.Insurance
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.QuoteSource
import com.cheche365.cheche.core.service.ISuitability
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import groovy.util.logging.Slf4j

import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.CIC_45000
import static com.cheche365.cheche.core.model.QuoteSource.Enum.API_4
import static com.cheche365.cheche.core.model.QuoteSource.Enum.REFERENCED_7
import static com.cheche365.cheche.core.model.QuoteSource.Enum.RULEENGINE2_8
import static com.cheche365.cheche.core.model.QuoteSource.Enum.WEBPARSER_2


/**
 * 参考的第三方保险服务
 * 支持多个保险服务可以指向同样的参考服务
 * 即达到“在某种报价方式下将某些保险公司全部服务改为参考指定的保险服务”之目的
 * @author 张华彬
 */
@Slf4j
class ReferencedThirdPartyHandlerService implements IThirdPartyHandlerService, ISuitability {

    private static final _SERVICE_WITH_TEMPLATE = [
        (CIC_45000): API_4,
        default    : WEBPARSER_2
    ]

    /**
     * 以下为直接从构造器传入的参数
     */
    private QuoteSource quoteSource

    /**
     * 以下为构造器参数变换后的结果
     */
    private Map insuranceCompanyServiceMappings

    private List<IThirdPartyHandlerService> services

    /**
     *
     * @param quoteSource 报价方式
     * @param referred 被参考的服务
     * @param insuranceCompanyRuleMappings 保险公司-人造规则映射
     */
    ReferencedThirdPartyHandlerService(
        QuoteSource quoteSource,
        IThirdPartyHandlerService referred,
        List<IThirdPartyHandlerService> services,
        Map<Object, List<Closure>> insuranceCompanyRuleMappings
    ) {
        this.quoteSource = quoteSource
        this.services = services

        this.insuranceCompanyServiceMappings = insuranceCompanyRuleMappings.collectEntries { insuranceCompany, rules ->
            def (Closure preProcess, Closure postSuccessfulRule, Closure postFailedRule) = rules
            [
                (insuranceCompany):
                    new ArtificialThirdPartyHandlerService(referred, preProcess, postSuccessfulRule, postFailedRule)
            ]
        }
    }


    @Override
    void quote(QuoteRecord quoteRecord, Map<String, Object> additionalParameters) {
        additionalParameters.quoteTimes = 1  //验证bug的代码，稳定后删除
        log.info '{}自报价的additionalParameters：{}',quoteRecord.insuranceCompany.name,additionalParameters
        try {
            def isDirectReferred = additionalParameters.flowState?.findResult {
                it.insuranceCompanyId as Long == quoteRecord.insuranceCompany.id && it.quoteSource.id == REFERENCED_7.id
            }

            if (!isDirectReferred) {
                def quoteSource = _SERVICE_WITH_TEMPLATE.findResult { insuranceCompany, quoteSource ->
                    quoteRecord.insuranceCompany == insuranceCompany ? quoteSource : null
                } ?: _SERVICE_WITH_TEMPLATE.default

                log.info '{}参考报价流程，先进行真实报价,additionalParameters:{}',quoteRecord.insuranceCompany.name,additionalParameters
                    getRealService([
                    insuranceCompany: quoteRecord.insuranceCompany,
                    quoteSource     : quoteSource
                ]).quote quoteRecord, additionalParameters
                quoteRecord.type = quoteSource
            } else {
                log.info '{}该车辆重复走参考报价，直接跳过真实报价,additionalParameters:{}',quoteRecord.insuranceCompany.name,additionalParameters
                getArtificialService(quoteRecord.insuranceCompany).quote quoteRecord, additionalParameters
            }

        } catch (ex) {
            if (ex instanceof LackOfSupplementInfoException || ex instanceof KnownReasonException) {
                throw ex
            } else {
                log.warn '{}真实报价失败，走参考报价,additionalParameters:{}',quoteRecord.insuranceCompany.name,additionalParameters
                getArtificialService(quoteRecord.insuranceCompany).quote quoteRecord, additionalParameters
            }
        }
    }

    @Override
    void insure(PurchaseOrder order, Insurance insurance, CompulsoryInsurance compulsoryInsurance, Map<String, Object> additionalParameters) {
        getService(insurance, compulsoryInsurance).insure order, insurance, compulsoryInsurance, additionalParameters
    }

    @Override
    void order(PurchaseOrder order, Insurance insurance, CompulsoryInsurance compulsoryInsurance, Map<String, Object> additionalParameters) {
        getService(insurance, compulsoryInsurance).insure order, insurance, compulsoryInsurance, additionalParameters
    }

    @Override
    boolean isSuitable(Object conditions) {
        quoteSource == conditions.quoteSource
    }



    private getArtificialService(InsuranceCompany actualInsuranceCompany) {
        insuranceCompanyServiceMappings.findResult { insuranceCompany, service ->
            insuranceCompany == actualInsuranceCompany ? service : null
        } ?: insuranceCompanyServiceMappings.default
    }

    private IThirdPartyHandlerService getRealService(conditions) {
        services.find {
            it.isSuitable(conditions)
        }
    }

    private getService(Insurance insurance, CompulsoryInsurance compulsoryInsurance) {
        def conditions = [
            insuranceCompany: insurance?.quoteRecord?.insuranceCompany ?: compulsoryInsurance?.quoteRecord?.insuranceCompany,
            quoteSource: insurance?.quoteRecord?.type ?: compulsoryInsurance?.quoteRecord?.type
        ]

        conditions.quoteSource in [REFERENCED_7, RULEENGINE2_8] ? getArtificialService(conditions.insuranceCompany) : getRealService(conditions)
    }

}
