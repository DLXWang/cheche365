package com.cheche365.cheche.rest.processor.quote

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.exception.handler.LackOfSupplementInfoHandler
import com.cheche365.cheche.core.model.Auto
import com.cheche365.cheche.core.model.AutoType
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.IdentityType
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.QuoteFlowType
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.repository.QuoteFlowConfigRepository
import com.cheche365.cheche.core.service.AutoService
import com.cheche365.cheche.core.service.InsuranceCompanyService
import com.cheche365.cheche.core.service.InsurancePackageService
import com.cheche365.cheche.core.service.agent.QuoteHistoryService
import com.cheche365.cheche.web.service.order.discount.strategy.DiscountCalculator
import com.cheche365.cheche.core.util.AutoUtils
import com.cheche365.cheche.rest.AsyncQuoter
import com.cheche365.cheche.rest.AsyncQuoterStarter
import com.cheche365.cheche.rest.QuoterFactory
import com.cheche365.cheche.rest.model.QuoteQuery
import com.cheche365.cheche.rest.service.QuoteMarketingService
import com.cheche365.cheche.rest.util.CheckUtil
import com.cheche365.cheche.web.service.http.SessionUtils
import com.cheche365.cheche.web.util.ClientTypeUtil
import groovy.util.logging.Slf4j
import org.atmosphere.cpr.MetaBroadcaster
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

import javax.servlet.http.HttpServletRequest
import java.util.concurrent.ExecutorService

import static com.cheche365.cheche.core.model.Area.Enum.TJ
import static com.cheche365.cheche.core.model.Area.Enum.getValueByCode
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.SINOSAFE_205000
import static com.cheche365.cheche.core.model.InsuranceCompany.toInsuranceCompany
import static com.cheche365.cheche.core.exception.BusinessException.Code.INPUT_FIELD_NOT_VALID
import static com.cheche365.cheche.core.model.QuoteSource.Enum.PLATFORM_BOTPY_11
import static com.cheche365.cheche.core.util.InsuranceDateUtil.getEffectiveDate

@Service
@Slf4j
class QuoteProcessor {

    @Autowired
    private AutoService autoService
    @Autowired
    private InsurancePackageService insurancePackageService
    @Autowired
    private QuoteHistoryService quoteHistoryService
    @Autowired
    private QuoterFactory quoterFactory
    @Autowired
    private DiscountCalculator discountCalculator
    @Autowired
    private QuoteMarketingService quoteMarketingService
    @Autowired
    private QuoteFlowConfigRepository quoteFlowConfigRepository
    @Autowired
    InsuranceCompanyService companyService
    @Autowired
    @Qualifier("quotingExecutorService")
    private ExecutorService execService

    private final MetaBroadcaster broadcaster
    @Autowired
    QuoteProcessor(MetaBroadcaster metaBroadcaster) {
        if (metaBroadcaster == null) {
            throw new NullPointerException("metaBroadcaster must not be null")
        }
        this.broadcaster = metaBroadcaster
    }

    Map formatQuoteRequest(QuoteQuery query, HttpServletRequest request) {
        mergeInsurancePackage(query)

        formatAuto(query, request)

        loadSupplementInfo(query)

        quoteRequestToMap(query, request)
    }

    private Map quoteRequestToMap(QuoteQuery query, HttpServletRequest request) {
        def quoteContext = new HashMap()
        quoteContext.putAll([
            'channel'             : ClientTypeUtil.getChannel(request),
            'user'                : SessionUtils.get(request.session, SessionUtils.USER_RELATED),
            'auto'                : query.auto,
            'insurancePackage'    : query.insurancePackage,
            'additionalParameters': query.additionalParameters,
            'quoteFlag'           : query.quoteFlag,
            'pref'                : query.pref,
            'clientIdentifier'    : request.session.id,
            'uri'                 : request.requestURI,
            'supplementInfo'      : query.supplementInfo,
            'request'             : request,
            'insuranceArea'       : query.pref.insuranceAreaId ? getValueByCode(query.pref.insuranceAreaId) : query.auto.area
        ])
        quoteContext.sessionAttrs = [:] //session属性不能跨线程读取，所以copy一份
        request.session.attributeNames.each { attrName ->
            quoteContext.sessionAttrs.put(attrName, request.session.getAttribute(attrName))
        }
        quoteContext
    }

    QuoteRecord doSyncQuote(InsuranceCompany insuranceCompany, Map quoteContext) {
        QuoteRecord quoteRecord = this.quoterFactory.getQuoter(insuranceCompany, quoteContext).doQuote()
        this.saveAuto(quoteContext.auto, quoteRecord, quoteContext.additionalParameters)
        quoteRecord
    }

    QuoteRecord doSimplifiedQuote(Map quoteContext) {
        this.quoterFactory.getSimplifiedQuoter(broadcaster, quoteContext).doQuote()
    }

    void doAsyncQuote(Map quoteContext) {
        List<AsyncQuoter> asyncQuoters = quoterFactory.getAsyncQuoterList(broadcaster, quoteContext)
        this.execService.submit(new AsyncQuoterStarter()
            .setExecSvc(this.execService)
            .setAsyncQuoterList(asyncQuoters)
            .setQuoteProcessor(this))
        CheckUtil.checkQuoteable(quoteContext.channel, quoteContext.sessionAttrs)
    }

    private void mergeInsurancePackage(QuoteQuery query) {
        Boolean renewal = query.pref.flowType && (QuoteFlowType.Enum.RENEWAL_CHANNEL.id == query.pref.flowType) && !query.insurancePackage
        if (renewal) { //续保通道，险种套餐为空时不做处理
            return
        }

        if (!query.insurancePackage) {
            query.insurancePackage = insurancePackageService.getDefaultPackage()
        }

        query.insurancePackage.formatEmptyAmount()
        query.insurancePackage = insurancePackageService.mergeInsurancePackage(query.insurancePackage)
    }

    private void formatAuto(QuoteQuery query, HttpServletRequest request) {
        Boolean newCarFlag = query.isNewCarFlag()
        if (newCarFlag) {
            if (!query.pref.insuranceAreaId) {
                throw new BusinessException(INPUT_FIELD_NOT_VALID, "请输入有效的行驶地区编码")
            }
            query.auto.licensePlateNo = Auto.NEW_CAR_PLATE_NO
        }

        if (!newCarFlag) {
            if (!query.auto.area && query.auto.licensePlateNo) {
                query.auto.area = AutoUtils.getAreaOfAuto(query.auto.licensePlateNo)
            }
            if (query.auto.area?.id) {
                query.auto.area = getValueByCode(query.auto.area.id)
            }
            if (!query.auto.area) {
                throw new BusinessException(INPUT_FIELD_NOT_VALID, "请输入有效的车牌所属地区编码")
            }
        }

        if (query.auto.identityType?.id) {
            query.auto.identityType = IdentityType.toIdentityType(query.auto.identityType.id)
        }

        if (!query.auto.identityType) {
            query.auto.identityType = IdentityType.Enum.IDENTITYCARD
        }

        autoService.decryptAuto(query.auto, SessionUtils.get(request.session, SessionUtils.USER_RELATED), request.session.id)

    }

    static def loadSupplementInfo(QuoteQuery query) {
        query.additionalParameters = query.additionalParameters ?: [:]
        query.supplementInfo = query.supplementInfo ?: [:]
        //品牌型号改版之后放在auto.autoType.code下面，需要merge到supplmentInfo下面，需要先生成这个结构
        query.supplementInfo = [auto: [autoType:[:]]]
        query.auto.autoType = query.auto.autoType ?: new AutoType()

        LackOfSupplementInfoHandler.readRequest(query)
    }

    void saveQuoteRequest(Map quoteContext) {
        if (quoteContext.user && quoteContext.insurancePackage) {
            Auto autoSaved = this.autoService.saveOrMerge(quoteContext.auto, quoteContext.user, false, new StringBuilder())
            quoteHistoryService.saveQuoteHistory(quoteContext, autoSaved)
        }
    }

    void preCheck(Map<String, Object> quoteContext, QuoteRecord quoteRecord) {
        if(quoteRecord.insuranceCompany){
            def supportAreaFlag = quoteFlowConfigRepository.findByChannelAndInsuranceCompany(
                quoteRecord.channel.parent,
                quoteRecord.insuranceCompany
            ).any {
                it.area.id == quoteRecord.area.id
            }
            if (!supportAreaFlag) {
                throw new BusinessException(INPUT_FIELD_NOT_VALID, quoteRecord.insuranceCompany.name + " 暂不支持城市：" + quoteRecord.area.name)
            }
        }

        if (quoteContext.channel.isStandardAgent() && quoteContext.pref.companyIds.size() == 1) {
            InsuranceCompany insuranceCompany = toInsuranceCompany(quoteContext.pref.companyIds[0] as Long)
            Boolean newCarFlag = quoteContext.additionalParameters?.supplementInfo?.newCarFlag
            if (newCarFlag && (
                (SINOSAFE_205000 != insuranceCompany) ||
                    (SINOSAFE_205000 == insuranceCompany) && (TJ != quoteRecord.area)
            )) {
                throw new BusinessException(INPUT_FIELD_NOT_VALID, "该地区暂不支持新车投保")
            }

            if (quoteRecord.auto.area && quoteRecord.area != quoteRecord.auto.area && !(SINOSAFE_205000 == insuranceCompany || PLATFORM_BOTPY_11 == quoteRecord.type)) {
                throw new BusinessException(INPUT_FIELD_NOT_VALID, "保险公司暂不支持异地投保")
            }
        }

    }

    void fillQuoteRecord(QuoteRecord quoteRecord, QuoteRecord resultQuoteRecord, Map<String, Object> additionalParameters) {
        resultQuoteRecord.setPaidAmount(null) //清空缓存中的优惠信息，重新计算优惠
        resultQuoteRecord.insurancePackage = insurancePackageService.mergeInsurancePackage(resultQuoteRecord.insurancePackage)
        resultQuoteRecord.setApplicant(quoteRecord.getApplicant())
        resultQuoteRecord.setChannel(quoteRecord.getChannel())
        resultQuoteRecord.auto.identityType = quoteRecord.auto.identityType
        additionalParameters?.persistentState?.taskId?.with {
            resultQuoteRecord.setQuoteSourceId(it)
        }

        resultQuoteRecord.auto.enrollDate && (resultQuoteRecord.auto.enrollDate = quoteRecord.auto.enrollDate)
        resultQuoteRecord.effectiveDate = resultQuoteRecord.effectiveDate ?: getEffectiveDate(additionalParameters.supplementInfo.commercialStartDate)
        resultQuoteRecord.compulsoryEffectiveDate = resultQuoteRecord.compulsoryEffectiveDate ?: getEffectiveDate(additionalParameters.supplementInfo.compulsoryStartDate)

        quoteMarketingService.supportMarketing(resultQuoteRecord)

        discountCalculator.calculateQuoteRecord(resultQuoteRecord)
    }

    void saveAuto(Auto auto, QuoteRecord resultQuoteRecord, Map additionalParameters) {
        Boolean hasLogin = (resultQuoteRecord.applicant != null)
        Boolean quoteSuccess = (resultQuoteRecord?.auto != null)
        Boolean doSyncVehicleLicense = quoteSuccess && !additionalParameters?.supplementInfo?.newCarFlag
        if (hasLogin) {
            Auto autoNeedSave = quoteSuccess ? resultQuoteRecord.auto : auto
            Auto autoSaved = this.autoService.saveOrMerge(autoNeedSave, resultQuoteRecord.applicant, doSyncVehicleLicense, new StringBuilder())
            log.debug("异步报价完成，更新或保存车辆信息，auto id 为 {}, license_plate_no : {}, user id : {}", autoSaved.getId(), autoSaved.getLicensePlateNo(), resultQuoteRecord.applicant.getId())
        }
        if (!hasLogin && doSyncVehicleLicense) {//未登录的情况下,报价成功后,更新行驶证表
            this.autoService.syncVehicleLicense(resultQuoteRecord.auto)
            log.debug("异步报价完成，更新行驶证表")
        }
    }

    def fillCompanyIds(Map quoteContext) {
        if (quoteContext.pref.companyIds) {
            return
        }

        Channel channel = quoteContext.channel
        Long areaId = quoteContext.insuranceArea.id
        List companyIds = companyService.findCompaniesByAreaAndChannel(areaId, channel)?.collect { it.id }

        if (!companyIds) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "当前地区暂不支持报价")
        }

        quoteContext.pref.setCompanyIds(companyIds)
    }

}
