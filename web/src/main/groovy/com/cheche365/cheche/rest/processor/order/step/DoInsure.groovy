package com.cheche365.cheche.rest.processor.order.step

import com.cheche365.cheche.core.constants.WebConstants
import com.cheche365.cheche.core.exception.BadQuoteParameterException
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.exception.LackOfSupplementInfoException
import com.cheche365.cheche.core.exception.handler.LackOfSupplementInfoHandler
import com.cheche365.cheche.core.message.RedisPublisher
import com.cheche365.cheche.core.model.*
import com.cheche365.cheche.core.service.IThirdPartyHandlerService
import com.cheche365.cheche.core.service.PurchaseOrderImageService
import com.cheche365.cheche.core.service.QuoteConfigService
import com.cheche365.cheche.core.service.QuoteRecordCacheService
import com.cheche365.cheche.core.util.NotifyMessageUtils
import com.cheche365.cheche.rest.InsuranceServiceFinder
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

import java.util.concurrent.TimeUnit

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.core.constants.CacheConstant.STRING_INSURED_SUCCESS_FLAG
import static com.cheche365.cheche.core.exception.BusinessException.Code.INTERNAL_SERVICE_ERROR
import static com.cheche365.cheche.core.model.IdentityType.Enum.IDENTITYCARD
import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.*
import static com.cheche365.cheche.core.model.PurchaseOrderImageType.Enum.BAOXIAN_IMAGE_TYPE
import static com.cheche365.cheche.core.service.QuoteRecordCacheService.persistQRParamHashKey
import static com.cheche365.cheche.parser.Constants.get_SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING
import static com.cheche365.cheche.parser.Constants.get_VALUABLE_HINT_VERIFICATION_MOBILE_TEMPLATE_INSURING
import static com.cheche365.cheche.rest.util.WebFlowUtil.getBusinessErrorFSRV

/**
 * Created by zhengwei on 12/21/16.
 */
@Component
@Slf4j
class DoInsure implements TPlaceOrderStep {
    @Autowired
    private RedisPublisher redisPublisher
    @Override
    def run(Object context) {

        PurchaseOrder order = context.order
        QuoteRecord quoteRecord = context.quoteRecord
        Map persistentState = context.additionalParameters?.persistentState
        QuoteConfigService quoteConfigService = context.quoteConfigService


        try {
            doInsure(context)
            order.status = OrderStatus.Enum.PENDING_PAYMENT_1
            order.statusDisplay = null
            if(quoteConfigService.isBotpy(quoteRecord)){
                order.orderSourceId = persistentState?.proposal_id
                order.setOrderSourceType(OrderSourceType.Enum.PLANTFORM_BOTPY_8)
            }

            if(context.quoteRecord.type == QuoteSource.Enum.AGENTPARSER_9 ){
                log.info("小鳄鱼测试>>>太平洋>>>商业险投保单号${context.insurance?.proposalNo},交强险投保单号${context.compulsoryInsurance?.proposalNo}")
            }

            log.debug("华农debug，订单号${order.orderNo},保险公司:${quoteRecord.insuranceCompany},persistentState:${persistentState}")

            if(TK_80000 == quoteRecord.insuranceCompany || HN_150000 == quoteRecord.insuranceCompany){
                order.orderSourceId = persistentState?.formId
                log.info("订单号 ${order.orderNo} 投保单号 ${persistentState?.formId}")
            }

            context.toBePersistObjects.with {
                it << quoteRecord
                it << order
                it << context.insurance
                it << context.compulsoryInsurance
            }
            getContinueFSRV true
        } catch (Exception e) {

            context.insureFailException = e
            getContinueFSRV false
        }
    }


    def doInsure(context) {

        PurchaseOrder order = context.order
        QuoteRecord quoteRecord = context.quoteRecord
        QuoteRecordCacheService quoteRecordCacheService = context.quoteRecordCacheService
        Insurance insurance = context.insurance
        CompulsoryInsurance compulsoryInsurance = context.compulsoryInsurance
        InsuranceServiceFinder serviceFinder = context.serviceFinder

        IThirdPartyHandlerService service = serviceFinder.find(quoteRecord.getInsuranceCompany(), quoteRecord.getArea(), quoteRecord.type)
        if (!service) {
            return getBusinessErrorFSRV(INTERNAL_SERVICE_ERROR.codeValue, "fail to load the third party service by insurance company id= $quoteRecord.insuranceCompany.code area= $quoteRecord.area.name")
        }

        Map additionalParameters = context.additionalParameters
        additionalParameters.put(WebConstants.QUOTE_FLOW_TYPE, quoteRecord.getQuoteFlowType().getId().toString());

        doSupplementInfo(context) //下单流程核保前推送补充信息

        LackOfSupplementInfoHandler.readRequest([auto: order.auto, supplementInfo: additionalParameters.supplementInfo])

        log.debug("call company insure service, orderNo : {}, additionalParameters:{}", order.orderNo, additionalParameters)
        try {
            service.insure(order, insurance, compulsoryInsurance, additionalParameters)
        } catch (ex) {
            if(ex instanceof BusinessException && BusinessException.Code.NOTIFICATION == ex.code){
                redisPublisher.publish(NotifyMessageUtils.getNotifyMessage(ex.message,String.valueOf(ex.errorObject)))
            }
            throw ex
        }  finally {
            StringRedisTemplate stringRedisTemplate = context.stringRedisTemplate
            stringRedisTemplate.opsForValue().set(STRING_INSURED_SUCCESS_FLAG + order.orderNo,'insureSuccess',24, TimeUnit.HOURS)
            processPersistentState(order.objId, additionalParameters, quoteRecordCacheService)
        }

    }

    static void doSupplementInfo(context) {
        PurchaseOrder order = context.order
        QuoteRecord quoteRecord = context.quoteRecord
        PurchaseOrderImageService poiService = context.poiService
        Map additionalParameters = context.additionalParameters
        QuoteConfigService quoteConfigService = context.quoteConfigService

        if (ANSWERN_65000 == quoteRecord.insuranceCompany && Area.isBJArea(quoteRecord.area)
            && !(additionalParameters?.supplementInfo?.verificationMobile)
            && (IDENTITYCARD == order.applicantIdentityType || IDENTITYCARD == order.insuredIdentityType)) {
            throw new BadQuoteParameterException(
                '提示信息',
                [
                    _VALUABLE_HINT_VERIFICATION_MOBILE_TEMPLATE_INSURING + [meta: [orderNo: context.order.orderNo], originalValue: context.order.applicant.mobile, fieldLabel: '请输入手机号', hints: ['用于接收保险行业协会验证码']]])
        }

        if (quoteConfigService.isBaoXian(quoteRecord.channel, quoteRecord.area, quoteRecord.insuranceCompany)) {
            poiService.findByOrderAndParentImageType(order, BAOXIAN_IMAGE_TYPE).findAll { !it.url }.with {
                if (!it.empty) { // 泛华补充图片
                    throw new LackOfSupplementInfoException('需要补充信息', [_SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING + [meta: [orderNo: order.orderNo]]])
                }
            }
        }

        if (poiService.needCustomUpload(order)) {
            throw new LackOfSupplementInfoException('需要补充信息', [_SUPPLEMENT_INFO_IMAGE_UPLOAD_TEMPLATE_INSURING + [meta: [orderNo: order.orderNo]]])
        }

    }

    private void processPersistentState(Long qrid, Map additionalParameters, QuoteRecordCacheService quoteRecordCacheService) {
        String hashKey = persistQRParamHashKey(qrid)
        Map additionalQRMap = quoteRecordCacheService.getPersistentState(hashKey);
        if (additionalQRMap?.persistentState && additionalParameters?.persistentState) {
            additionalQRMap.persistentState.putAll((Map) additionalParameters.get(WebConstants.PERSISTENT_STATE));
            quoteRecordCacheService.cachePersistentState(hashKey, additionalQRMap);
        } else {
            quoteRecordCacheService.cachePersistentState(hashKey, (Map) additionalParameters.get(WebConstants.PERSISTENT_STATE));
        }

    }
}

