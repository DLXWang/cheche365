package com.cheche365.cheche.core.service.image

import com.cheche365.cheche.core.model.PurchaseOrderImageScene
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.OrderImageService
import com.cheche365.cheche.core.service.PurchaseOrderImageService
import com.cheche365.cheche.core.service.QuoteRecordCacheService
import groovy.util.logging.Slf4j
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.SINOSAFE_205000
import static com.cheche365.cheche.core.model.PurchaseOrderImageScene.Enum.SINOSAFE_INSURE_7
import static com.cheche365.cheche.core.service.QuoteRecordCacheService.persistQRParamHashKey

@Service
@Order(6)
@Slf4j
class SinosafeUploadService extends PurchaseOrderUploadService {

    private QuoteRecordCacheService cacheService
    SinosafeUploadService(PurchaseOrderImageService poiService,
                          PurchaseOrderRepository poRepo,
                          OrderImageService oiService,
                          QuoteRecordCacheService cacheService) {
        super(poiService, poRepo, oiService)
        this.cacheService = cacheService
    }

    @Override
    void perCheck(Map initParams) {

        super.perCheck(initParams)

        initParams << [
            thirdPartyServiceUpload: Boolean.TRUE,
            additionalParameters   : [
                CAL_APP_NO: cacheService.getPersistentState(persistQRParamHashKey(initParams.quoteRecord.id))?.persistentState?.CAL_APP_NO,
                conditions: [
                    insuranceCompany: initParams.quoteRecord.insuranceCompany,
                    quoteSource     : initParams.quoteRecord.type
                ]
            ]
        ]

    }

    @Override
    boolean support(Map initParams) {
        return SINOSAFE_205000 == initParams.quoteRecord?.insuranceCompany
    }

    @Override
    PurchaseOrderImageScene getImageScene(Map initParams) {
        return SINOSAFE_INSURE_7
    }
}
