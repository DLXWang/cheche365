package com.cheche365.cheche.core.service.image

import com.cheche365.cheche.core.constants.BaoXianConstant
import com.cheche365.cheche.core.model.OrderSourceType
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.PurchaseOrderImageScene
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.OrderImageService
import com.cheche365.cheche.core.service.PurchaseOrderImageService
import groovy.util.logging.Slf4j
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.PurchaseOrderImageScene.Enum.BAOXIAN_INSURE_8
import static com.cheche365.cheche.core.model.PurchaseOrderImageType.Enum.BAOXIAN_IMAGE_TYPE

/**
 * Created by mahong on 15/04/2017.
 * 泛华图片服务
 */
@Service
@Order(4)
@Slf4j
class BaoXianUploadService extends PurchaseOrderUploadService {

    BaoXianUploadService(PurchaseOrderImageService poiService,
                         PurchaseOrderRepository poRepo,
                         OrderImageService oiService) {
        super(poiService, poRepo, oiService)
    }

    @Override
    Map toUpload(Map initParams) {
        return poiService.findToUploadByOrderAndImageType(initParams.order, BAOXIAN_IMAGE_TYPE)
    }

    @Override
    void persist(List<UnifyImageFile> files, Map initParams) {
        super.persist(files, initParams)
        initParams.order.statusDisplay = BaoXianConstant.INSURE_FAILED
        poRepo.save(initParams.order)
    }

    @Override
    boolean support(Map initParams) {
        return OrderSourceType.Enum.PLANTFORM_BX_5 == initParams.order?.orderSourceType
    }

    @Override
    PurchaseOrderImageScene getImageScene(Map initParams) {
        return BAOXIAN_INSURE_8
    }
}
