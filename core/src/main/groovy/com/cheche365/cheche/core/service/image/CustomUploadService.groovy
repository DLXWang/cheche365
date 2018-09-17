package com.cheche365.cheche.core.service.image

import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.PurchaseOrderImageCustom
import com.cheche365.cheche.core.model.PurchaseOrderImageScene
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.OrderImageService
import com.cheche365.cheche.core.service.PurchaseOrderImageService
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.PurchaseOrderImageScene.Enum.API_CUSTOM_9

@Service
@Order(1)
@Slf4j
class CustomUploadService extends PurchaseOrderUploadService {

    CustomUploadService(PurchaseOrderImageService poiService,
                        PurchaseOrderRepository poRepo,
                        OrderImageService oiService) {
        super(poiService, poRepo, oiService)
    }

    @Override
    Map toUpload(Map initParams) {
        PurchaseOrderImageCustom poic = poiService.findLastOrderImageCustom(initParams.order)
        Map uploadImages = poiService.toUploadImage(API_CUSTOM_9)
        uploadImages.groupImages.each {
            it.title = '补充影像'
            it.desc = poic.description
        }
        uploadImages << ['customUpload': true]
        uploadImages

    }

    @Override
    boolean support(Map initParams) {
        initParams.order && poiService.needCustomUpload(initParams.order)
    }

    @Override
    void persist(List<UnifyImageFile> files, Map initParams) {
        super.persist(files, initParams)
        poiService.updateCustomImageStatus(initParams.order)
        initParams.order.statusDisplay = '核保中'
        poRepo.save(initParams.order)
    }

    @Override
    PurchaseOrderImageScene getImageScene(Map initParams) {
        return API_CUSTOM_9
    }
}
