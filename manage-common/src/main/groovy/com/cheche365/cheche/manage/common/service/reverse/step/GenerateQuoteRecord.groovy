package com.cheche365.cheche.manage.common.service.reverse.step

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.repository.*
import com.cheche365.cheche.core.util.BeanUtil
import com.cheche365.cheche.manage.common.service.OrderInsurancePackageService
import com.cheche365.cheche.manage.common.service.reverse.OrderReverse
import com.umpay.api.util.StringUtil
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV

/**
 * Created by yellow on 2017/11/6.
 */
@Service
@Slf4j
class GenerateQuoteRecord implements TPlaceInsuranceStep {

    @Transactional
    @Override
    Object run(Object context) {
        log.debug("------生成报价------")
        OrderReverse model = context.model
        PurchaseOrderRepository purchaseOrderRepository = context.purchaseOrderRepository
        QuoteRecordRepository quoteRecordRepository = context.quoteRecordRepository
        AgentRepository agentRepository=context.agentRepository
        AreaRepository areaRepository=context.areaRepository
        UserRepository userRepository=context.userRepository
        User user
        if(model.getRecommender() != null){
            user = agentRepository.findOne(model.getRecommender()).getUser()
        }else{
            user = userRepository.findByMobile(model.getMobile())
        }
        Area area = areaRepository.findOne(model.getArea())
        QuoteRecord prevRecord
        if (StringUtil.isNotEmpty(model.orderNo)) {
            prevRecord = quoteRecordRepository.findOne(purchaseOrderRepository.findFirstByOrderNo(model.orderNo).objId)
        }
        context.quoteRecord = createQuoteRecord(prevRecord, context, user, area)
        getContinueFSRV true
    }

    def createQuoteRecord(QuoteRecord oldQuoteRecord, Object context, User user, Area area) {
        OrderReverse model = context.model
        InsuranceCompanyRepository insuranceCompanyRepository = context.insuranceCompanyRepository
        QuoteRecordRepository quoteRecordRepository = context.quoteRecordRepository
        OrderInsurancePackageService orderInsurancePackageService = context.orderInsurancePackageService
        ChannelRepository channelRepository = context.channelRepository
        Auto auto = context.auto
        QuoteRecord quoteRecord = new QuoteRecord()
        InsuranceCompany company = insuranceCompanyRepository.findOne(model.getInsuranceCompany())
        if (null != oldQuoteRecord) {
            BeanUtil.copyPropertiesContain(oldQuoteRecord, quoteRecord)
            quoteRecord.setUpdateTime(new Date())
        } else {
            quoteRecord.setCreateTime(new Date())
        }
        quoteRecord.setAuto(auto)
        quoteRecord.setApplicant(user)
        quoteRecord.setInsuranceCompany(company)
        quoteRecord.setInsurancePackage(orderInsurancePackageService.createInsurancePackage(model))
        quoteRecord.setArea(area)

        quoteRecord.setPremium(model.getCommercialPremium())
        if (model.getCommercialEffectiveDate() != null) {
            quoteRecord.setEffectiveDate(DateUtils.getDate(model.getCommercialEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN))
        }
        if (model.getCompulsoryEffectiveDate() != null) {
            quoteRecord.setCompulsoryEffectiveDate(DateUtils.getDate(model.getCompulsoryEffectiveDate(), DateUtils.DATE_SHORTDATE_PATTERN))
        }
        if (model.getCommercialExpireDate() != null) {
            quoteRecord.setExpireDate(DateUtils.getDate(model.getCommercialExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN))
        }
        if (model.getCompulsoryExpireDate() != null) {
            quoteRecord.setCompulsoryExpireDate(DateUtils.getDate(model.getCompulsoryExpireDate(), DateUtils.DATE_SHORTDATE_PATTERN))
        }

        quoteRecord.setType(QuoteSource.Enum.TELEMARKETING_3)
        quoteRecord.setChannel(channelRepository.findOne(model.getChannel()))
        def contains = [
            "compulsoryPremium", "autoTax", "thirdPartyPremium", "thirdPartyAmount",
            "damagePremium", "damageAmount", "theftPremium", "theftAmount", "enginePremium", "driverPremium", "driverAmount",
            "passengerPremium", "passengerAmount", "spontaneousLossPremium", "spontaneousLossAmount", "glassPremium", "scratchAmount",
            "scratchPremium", "unableFindThirdPartyPremium","designatedRepairShopPremium", "damageIop", "thirdPartyIop", "theftIop", "engineIop", "driverIop", "passengerIop", "spontaneousLossIop", "scratchIop"
        ] as String[]
        BeanUtil.copyPropertiesContain(model, quoteRecord, contains)
        quoteRecord.setIopTotal(model.iop)
        quoteRecordRepository.save(quoteRecord)
    }
}
