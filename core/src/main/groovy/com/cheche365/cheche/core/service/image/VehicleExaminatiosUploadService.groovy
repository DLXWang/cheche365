package com.cheche365.cheche.core.service.image

import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.PurchaseOrderImageScene
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.OrderImageService
import com.cheche365.cheche.core.service.PurchaseOrderImageService
import groovy.util.logging.Slf4j
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service
import static com.cheche365.cheche.core.model.PurchaseOrderImageScene.Enum.VEHICLE_EXAMINATIOS_11
import static com.cheche365.cheche.core.model.PurchaseOrderImageType.Enum.VEHICLE_EXAMINATIOS_IMAGE_TYPE

/**
 * Created by wen on 2018/8/17.
 *  通用验车图片服务，目前仅华农在使用
 */
@Service
@Order(3)
@Slf4j
class VehicleExaminatiosUploadService extends PurchaseOrderUploadService {

    VehicleExaminatiosUploadService(PurchaseOrderImageService poiService,
                                    PurchaseOrderRepository poRepo,
                                    OrderImageService oiService) {
        super(poiService, poRepo, oiService)
    }

    @Override
    Map toUpload(Map initParams) {
        if (OrderStatus.Enum.INSURE_FAILURE_7 == initParams.order.status) {
            return poiService.findToUploadByOrderAndImageType(initParams.order as PurchaseOrder,VEHICLE_EXAMINATIOS_IMAGE_TYPE) ?: poiService.toUploadImage(VEHICLE_EXAMINATIOS_11)
        }
        null
    }


    @Override
    boolean support(Map initParams) {
        return InsuranceCompany.Enum.HN_150000 == initParams.quoteRecord?.insuranceCompany
    }

    @Override
    PurchaseOrderImageScene getImageScene(Map initParams) {
        return VEHICLE_EXAMINATIOS_11
    }
}
