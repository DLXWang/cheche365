package com.cheche365.cheche.rest.processor.order.step

import com.cheche365.cheche.core.constants.InsureFieldConstants
import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.exception.handler.LackOfSupplementInfoHandler
import com.cheche365.cheche.core.model.Insurance

import static com.cheche365.cheche.common.util.AreaUtils.getProvinceCode
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.HN_150000
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.QuoteSupplementInfo
import com.cheche365.cheche.core.repository.QuoteSupplementInfoRepository
import com.cheche365.cheche.core.service.IConfigService
import com.cheche365.cheche.core.service.QuoteRecordCacheService
import com.cheche365.cheche.web.service.PaymentCallbackURLHandler
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.common.util.FlowUtils.getEnvPropertyNew
import static com.cheche365.cheche.core.exception.Constants.getCAPTCHA_IMAGE_FIELD
import static com.cheche365.cheche.core.model.QuoteSource.Enum.AGENTPARSER_9
import static com.cheche365.cheche.core.model.QuoteSource.Enum.PLATFORM_BOTPY_11
import static com.cheche365.cheche.core.service.QuoteRecordCacheService.persistQRParamHashKey
import static com.cheche365.cheche.externalpayment.model.BotpyCallBackBody.IMAGES_PROPOSAL_STATUS_REDIS_KEY

/**
 * Created by zhengwei on 12/21/16.
 */

@Component
@Slf4j
class RestoreCachedStates implements TPlaceOrderStep {

    @Autowired
    private Environment env

    @Autowired
    private IConfigService configService

    @Override
    def run(Object context) {

        QuoteRecordCacheService cacheService = context.cacheService
        QuoteRecord quoteRecord = context.quoteRecord
        Insurance insurance = context.insurance
        Map additionalParameters = context.additionalParameters
        QuoteSupplementInfoRepository quoteSupplementInfoRepository =context.quoteSupplementInfoRepository
        PaymentCallbackURLHandler urlHandler = context.urlHandler

        List<QuoteSupplementInfo> quoteSupplementInfoList =quoteSupplementInfoRepository.findByQuoteRecord(quoteRecord)
        LackOfSupplementInfoHandler.getByQuoteSupplementInfo(quoteSupplementInfoList,additionalParameters)
        Map additionalQRMap = cacheService.getPersistentState(persistQRParamHashKey(quoteRecord.getId()))
        additionalParameters.persistentState =  additionalQRMap?.persistentState
        additionalParameters.persistentState?.proposal_id = context.order.orderSourceId
        if(PLATFORM_BOTPY_11 == quoteRecord.type){
            additionalParameters.persistentState?.proposal_status = context.stringRedisTemplate.opsForHash().get(IMAGES_PROPOSAL_STATUS_REDIS_KEY, context.order.orderNo)
        }
        additionalParameters.frontCallBackUrl = urlHandler.toServerCallbackUrl context.order, context.request
        additionalParameters.bgRetUrl = "https://" + WebConstants.getDomain()+getEnvPropertyNew([env: env, configService: configService, namespace: 'answern'],'bg_ret_url',null,[quoteRecord.area.id,getProvinceCode(quoteRecord.area.id)].toArray())

        if(HN_150000 == quoteRecord.insuranceCompany){
            additionalParameters.callbackUrl = "https://" + WebConstants.getDomain()+'/api/callback/huanong'
        }

        log.info(" persistentState缓存恢复：${additionalParameters?.persistentState}")

        Map cachedInfo = additionalQRMap?.supplementInfo
        if(cachedInfo){
            if(additionalParameters.supplementInfo) {
                log.debug("合并核保补充信息， 用户输入数据 {}， 缓存数据 {}", additionalParameters.supplementInfo, cachedInfo)
                cachedInfo.putAll(additionalParameters.supplementInfo)
            }

            if (AGENTPARSER_9 == quoteRecord.type) {//小鳄鱼图形验证码不进行合并
                additionalParameters.supplementInfo = cachedInfo.findAll {
                    CAPTCHA_IMAGE_FIELD.contains(it.key) && additionalParameters.supplementInfo?.get(it.key) ||
                        !CAPTCHA_IMAGE_FIELD.contains(it.key)
                }
            } else {
                additionalParameters.supplementInfo = cachedInfo
            }

        }

        handleSpecialAgreement(insurance,additionalParameters)

        clearCachedQR(context, additionalQRMap?.quoteRecordHashKey)

        cacheService.cachePersistentState(persistQRParamHashKey(quoteRecord.getId()), additionalQRMap)
        getContinueFSRV true
    }

    void handleSpecialAgreement(Insurance insurance, Map additionalParameters) {
        if(additionalParameters?.supplementInfo?.specialAgreement){
            log.debug("特约信息 : ${additionalParameters.supplementInfo.specialAgreement}")
            insurance.specialAgreement = additionalParameters.supplementInfo.specialAgreement
            additionalParameters.supplementInfo.specialAgreements = [
                InsureFieldConstants.CUSTOM_SPECIAL_AGREEMENT.clone() << [content : additionalParameters.supplementInfo.specialAgreement]
            ]
        }
    }

    def clearCachedQR(context, quoteRecordHashKey) {
        QuoteRecordCacheService cacheService = context.quoteRecordCacheService
        Boolean reInsure = ("reInsure" == context.orderFromWeb.getFlow())
        if (!reInsure && quoteRecordHashKey && context.quoteRecord.apiQuote()) {
            log.debug("quoteRecord.id : {} 已下单，orderNo : {}，清除报价缓存", context.quoteRecord.id, context.order.orderNo)
            cacheService.clearCachedQR(quoteRecordHashKey)
        }
    }

}

