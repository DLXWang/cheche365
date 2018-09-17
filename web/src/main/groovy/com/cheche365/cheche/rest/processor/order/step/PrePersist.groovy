package com.cheche365.cheche.rest.processor.order.step

import com.cheche365.cheche.core.constants.BaoXianConstant
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.service.PurchaseOrderImageService
import com.cheche365.cheche.core.service.QuoteConfigService
import com.cheche365.cheche.core.service.QuoteRecordCacheService
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static com.cheche365.cheche.common.util.FlowUtils.getContinueFSRV
import static com.cheche365.cheche.core.service.QuoteRecordCacheService.persistQRParamHashKey

/**
 * Created by wenling on 2018/1/11.
 */
@Component
@Slf4j
class PrePersist implements TPlaceOrderStep {

   @Override
    def run(Object context) {
        PurchaseOrder order = context.order
        QuoteRecordCacheService cacheService = context.cacheService
        QuoteRecord quoteRecord = context.quoteRecord
        Map additionalParameters = context.additionalParameters
        QuoteConfigService quoteConfigService = context.quoteConfigService
        PurchaseOrderImageService poiService=context.poiService

        log.info("预保存，缓存数据:${additionalParameters}")
        if (quoteConfigService.isBaoXian(quoteRecord.channel, quoteRecord.area, quoteRecord.insuranceCompany) && additionalParameters?.persistentState?.imageInfos) {

            log.info("泛华先核保后支付流程->预保存，影像信息:${additionalParameters?.persistentState?.imageInfos}")
            def images = additionalParameters.persistentState.imageInfos
            if (images) {
                poiService.onImage(order, images, BaoXianConstant.CALL_BACK_STATE as Map, BaoXianConstant.LACK_OF_IMAGE)
                Map additionalQRMap = cacheService.getPersistentState(persistQRParamHashKey(quoteRecord.getId()))
                additionalQRMap?.persistentState?.remove('imageInfos')
                cacheService.cachePersistentState(persistQRParamHashKey(quoteRecord.getId()), additionalQRMap)
                log.info("泛华先核保后支付流程， ${quoteRecord.area.id} 地区需要补充影像信息 ${images},订单号 : ${order.orderNo},订单状态为核保失败，additionalQRMap.persistentState.imageInfos : ${additionalQRMap?.persistentState?.imageInfos}")
            }

        }

       getContinueFSRV true

    }
}
