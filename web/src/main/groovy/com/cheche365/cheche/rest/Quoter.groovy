package com.cheche365.cheche.rest

import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.message.RedisPublisher
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.QuoteFlowConfig
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.QuoteSource
import com.cheche365.cheche.core.repository.QuoteFlowConfigRepository
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.core.service.QuoteRecordCacheService
import com.cheche365.cheche.rest.processor.quote.QuoteExceptionHandler
import com.cheche365.cheche.rest.processor.quote.QuoteProcessor
import com.cheche365.cheche.rest.util.CheckUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext

import static com.cheche365.cheche.core.exception.BusinessException.Code.INTERNAL_SERVICE_ERROR
import static com.cheche365.cheche.rest.processor.quote.QuoteExceptionHandler.checkKnownReasonError
import static com.cheche365.cheche.rest.processor.quote.QuoteExceptionHandler.knownReasonError



/**
 * Created by zhengwei on 4/29/15.
 */
class Quoter {

    private static Logger logger = LoggerFactory.getLogger(Quoter.class)

    QuoteRecord quoteRecord
    Map<String, Object> additionalParameters
    Map<String, Object> quoteContext
    IThirdPartyHandlerService quoteService
    QuoteRecordCacheService quoteRecordCacheService

    RedisPublisher redisPublisher

    QuoteRecord doQuote() {

        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext()
        QuoteProcessor quoteProcessor = applicationContext.getBean(QuoteProcessor.class)

        quoteProcessor.preCheck(quoteContext, this.quoteRecord)

        QuoteRecord cachedQuoteRecord = this.quoteRecordCacheService.getQuoteRecordFromCache(this.quoteRecord, additionalParameters)
        QuoteRecord resultQuoteRecord = null


        try {
            if (null == cachedQuoteRecord) {
                resultQuoteRecord = this.quoteRecord.clone()
                if (!this.quoteService) {
                    QuoteFlowConfigRepository quoteFlowConfigRepository = applicationContext.getBean(QuoteFlowConfigRepository.class)
                    QuoteFlowConfig quoteFlowConfig = quoteFlowConfigRepository.findByAreaAndInsuranceCompanyAndChannel(quoteRecord.area, quoteRecord.insuranceCompany, quoteRecord.channel.parent)
                    String message = "未匹配到报价服务，报价信息：channel: ${quoteRecord.channel?.parent?.id}, insuranceCompany:${quoteRecord.insuranceCompany?.id}, area: ${quoteRecord.area?.id}, config_value: ${quoteFlowConfig ? quoteFlowConfig.configValue : '未查到对应的QuoteFlowConfig'}"
                    logger.info(message)
                    throw new BusinessException(INTERNAL_SERVICE_ERROR, message)
                }
                this.quoteService.quote(resultQuoteRecord, this.additionalParameters)
                if (InsuranceCompany.Enum.PICC_10000 == quoteRecord.insuranceCompany) {//人保ip被禁问题查找日志，后期可删除
                    logger.info("web log, picc quote request, channel:{}, user id:{}, user mobile:{}, licensePlateNo:{}, insuranceCompany:{}, quoteService:{}", quoteRecord.channel?.name, quoteRecord.applicant?.id, quoteRecord.applicant?.mobile, quoteRecord.auto.licensePlateNo, quoteRecord.insuranceCompany.name, quoteService.class.simpleName)
                }

            } else {
                resultQuoteRecord = cachedQuoteRecord
            }
        } catch (Exception e) {
            QuoteExceptionHandler.handleQuoteException(this, this.additionalParameters, e)
            if (quoteRecord.channel.isOrderCenterChannel() && checkKnownReasonError(e)) {
                def (_0, _1, status, message) = knownReasonError(null, e)
                e = new BusinessException(INTERNAL_SERVICE_ERROR, status, message, null)
            }
            throw e
        } finally {
            if (!cachedQuoteRecord && (InsuranceCompany.Enum.PICC_10000 == quoteRecord.insuranceCompany || QuoteSource.Enum.REFERENCED_7 == quoteRecord.type)) {//人保ip被禁问题查找日志，后期可删除
                logger.info("web log, picc quote {}, channel:{}, user id:{}, user mobile:{}, licensePlateNo:{}, insuranceCompany:{}, quoteService:{}", (resultQuoteRecord.totalPremium > 0) ? 'success' : 'fail', quoteRecord.channel?.name, quoteRecord.applicant?.id, quoteRecord.applicant?.mobile, quoteRecord.auto.licensePlateNo, quoteRecord.insuranceCompany.name, quoteService.class.simpleName)
            }

            quoteRecordCacheService.saveQuoteRecordLog(resultQuoteRecord ? resultQuoteRecord : quoteRecord)
        }

        quoteProcessor.fillQuoteRecord(quoteRecord, resultQuoteRecord, additionalParameters)

        Map param = cachedQuoteRecord ? quoteRecordCacheService.getQuoteRecordParamByHashKey(cachedQuoteRecord.quoteRecordKey) : additionalParameters
        logger.info("报价additionalParameters为:{}", param)
        this.quoteRecordCacheService.cacheQuoteRecord(this.quoteRecord, resultQuoteRecord, this.additionalParameters, param)

        CheckUtil.checkQuoteable(this.quoteRecord.channel, quoteContext.sessionAttrs)

        return resultQuoteRecord
    }

}
