package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.DailyInsurance
import com.cheche365.cheche.core.model.DailyInsuranceStatus
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteFlowConfig
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.QuoteSource
import com.cheche365.cheche.core.repository.DailyInsuranceRepository
import com.cheche365.cheche.core.repository.QuoteFlowConfigRepository
import com.cheche365.cheche.core.repository.QuoteRecordRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.QuoteFlowConfig.ConfigValue.REFERENCE
import static com.cheche365.cheche.core.model.QuoteFlowConfig.ConfigValue.WEB_PARSER
import static com.cheche365.cheche.core.model.QuoteSource.Enum.*
import static com.cheche365.cheche.core.model.QuoteSource.Enum.findById

/**
 * Created by shanxf on 2017/6/14.
 * 创建QuoteFlowConfig  sql语句服务
 */
@Service
class QuoteConfigService {

    private Logger logger = LoggerFactory.getLogger(QuoteConfigService.class)

    @Autowired
    QuoteFlowConfigRepository quoteFlowConfigRepository

    @Autowired
    DailyInsuranceRepository dailyInsuranceRepository

    @Autowired
    QuoteRecordRepository quoteRecordRepository

    QuoteFlowConfig findQuoteFlowConfig(Channel channel, Area area, InsuranceCompany insuranceCompany) {
        Channel parentChannel = channel?.parent
        QuoteFlowConfig quoteFlowConfig = quoteFlowConfigRepository.findByAreaAndInsuranceCompanyAndChannel(area, insuranceCompany, parentChannel)
        if (!quoteFlowConfig) {
            logger.info("未查到对应的QuoteFlowConfig,channel:{},parentChannel:{},area:{},insuranceCompany:{}", channel?.id, parentChannel?.id, area?.id, insuranceCompany?.id)
        }
        quoteFlowConfig
    }

    QuoteSource findQuoteSource(Channel channel, Area area, InsuranceCompany insuranceCompany) {
        QuoteFlowConfig quoteFlowConfig = this.findQuoteFlowConfig(channel, area, insuranceCompany)
        if (!quoteFlowConfig) {
            throw new BusinessException(BusinessException.Code.OPERATION_NOT_ALLOWED, "当前地区暂不支持报价")
        }
        QuoteSource quoteSourceDB = findById(quoteFlowConfig.configValue)
        if (channel.isOrderCenterChannel() && REFERENCED_7 == quoteSourceDB) {
            logger.debug("出单中心替换代理报价为parser报价, channel ${channel.id}")
            return WEBPARSER_2
        }
        quoteSourceDB
    }

    Boolean quoteCacheNotSupported(QuoteRecord quoteRecord) {
        QuoteFlowConfig quoteFlowConfig = this.findQuoteFlowConfig(quoteRecord.channel, quoteRecord.area, quoteRecord.insuranceCompany)
        if (!quoteFlowConfig) {
            return false
        }
        QuoteSource quoteSource = findById(quoteFlowConfig.configValue)
        return API_QUOTE_SOURCES.contains(quoteSource)
    }

    /**
     * 是否支持手工报价
     * @param channel
     * @param area
     * @param insuranceCompany
     * @return
     */
    Boolean isSupportManualQuote(Channel channel, Area area, InsuranceCompany insuranceCompany) {
        if (insuranceCompany == InsuranceCompany.Enum.SINOSAFE_205000
                || insuranceCompany == InsuranceCompany.Enum.ZHONGAN_50000
                || insuranceCompany == InsuranceCompany.Enum.ANSWERN_65000) {
            return false
        }
        //只有报价方式是(自有)和(参考)的才支持手工报价,修改订单状态
        QuoteFlowConfig quoteFlowConfig = this.findQuoteFlowConfig(channel, area, insuranceCompany)
        if (quoteFlowConfig) {
            return [WEB_PARSER.index.toLong(), REFERENCE.index.toLong()].contains(quoteFlowConfig.configValue)
        }
        return false
    }

    Boolean isBaoXian(Channel channel, Area area, InsuranceCompany insuranceCompany) {
        this.quoteSourceEquals(channel, area, insuranceCompany, PLANTFORM_BX_6)
    }

    Boolean isBotpy(QuoteRecord qr) {
        this.quoteSourceEquals(qr.channel, qr.area, qr.insuranceCompany, PLATFORM_BOTPY_11)
    }

    Boolean isAgentParser(QuoteRecord qr) {
        this.quoteSourceEquals(qr.channel, qr.area, qr.insuranceCompany, AGENTPARSER_9)
    }

    Boolean quoteSourceEquals(Channel channel, Area area, InsuranceCompany insuranceCompany, QuoteSource quoteSource) {
        QuoteFlowConfig quoteFlowConfig = this.findQuoteFlowConfig(channel, area, insuranceCompany)
        if (!quoteFlowConfig) {
            return false
        }
        return quoteSource == findById(quoteFlowConfig.configValue)
    }

    Boolean isInnerPay(QuoteRecord quoteRecord, PurchaseOrder purchaseOrder) {
        (purchaseOrder.answernFinished(quoteRecord) && !isAnswernStop(purchaseOrder)) ||
            quoteRecord.insuranceCompany.useChecheCashier() || !THIRD_PAY_SOURCES.contains(quoteRecord.type)
    }

    private boolean isAnswernStop(PurchaseOrder order) {
        List status = new ArrayList()
        status.add(DailyInsuranceStatus.Enum.STOP_APPLY.getId())
        status.add(DailyInsuranceStatus.Enum.STOPPED.getId())
        List<DailyInsurance> records = dailyInsuranceRepository.findByPurchaseOrderAndStatusByIdDesc(order, status)
        return records != null && records.size() > 0 && records.get(0).getBankCard() != null
    }
}
