package com.cheche365.cheche.core.service.image

import com.cheche365.cheche.core.model.DailyInsurance
import com.cheche365.cheche.core.model.DailyInsuranceStatus
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.PurchaseOrderImageScene
import com.cheche365.cheche.core.repository.DailyInsuranceRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.service.DailyInsuranceService
import com.cheche365.cheche.core.service.OrderImageService
import com.cheche365.cheche.core.service.PurchaseOrderImageService
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

import static com.cheche365.cheche.core.model.InsuranceCompany.Enum.ANSWERN_65000
import static com.cheche365.cheche.core.model.PurchaseOrderImageScene.Enum.DAILY_INSURE_5
import static com.cheche365.cheche.core.model.PurchaseOrderImageScene.Enum.DAILY_RESTART_6


/**
 * Created by zhengwei on 4/6/17.
 * 安心图片服务
 */
@Service
@Order(2)
class AnswernUploadService extends PurchaseOrderUploadService {

    private DailyInsuranceRepository dailyRepo
    private DailyInsuranceService dailyInsuranceService

    AnswernUploadService(PurchaseOrderImageService poiService,
                         PurchaseOrderRepository poRepo,
                         OrderImageService oiService,
                         DailyInsuranceRepository dailyRepo,
                         DailyInsuranceService dailyInsuranceService) {
        super(poiService, poRepo, oiService)
        this.dailyRepo = dailyRepo
        this.dailyInsuranceService = dailyInsuranceService
    }

    void perCheck(Map initParams){
        super.perCheck(initParams)

        if(initParams.restart){
            initParams << [objId: initParams.restart.id]
        }
    }

    @Override
    Map toUpload(Map initParams) {
        DailyInsurance dailyInsurance = dailyRepo.findAllByPurchaseOrderOrderByIdDesc(initParams.order)[0]
        if (DailyInsuranceStatus.Enum.ALLOW_RESTART.contains(dailyInsurance?.status)) {
            Boolean restartInPaymentProcess = dailyInsuranceService.checkOrderBinding(initParams.order)
            if (restartInPaymentProcess) {
                return null
            }
            if (poiService.findByLastDailyInsurance(dailyInsurance).empty) {
                return poiService.toUploadImage(PurchaseOrderImageScene.Enum.DAILY_RESTART_6)
            }
        } else if (OrderStatus.Enum.INSURE_FAILURE_7 == initParams.order.status) {
            return poiService.toUploadImage(PurchaseOrderImageScene.Enum.DAILY_INSURE_5)
        }
        null
    }

    @Override
    boolean support(Map initParams) {
        return ANSWERN_65000 == initParams.quoteRecord?.insuranceCompany
    }

    @Override
    PurchaseOrderImageScene getImageScene(Map initParams) {
        Integer businessType = initParams.businessType as Integer
        if (ImgUploadService.BUSINESS_TYPE_DAILY_INSURE == businessType) {
            return DAILY_INSURE_5;
        }
        if (ImgUploadService.BUSINESS_TYPE_DAILY_RESTART == businessType) {
            return DAILY_RESTART_6;
        }
        return super.getImageScene(initParams)
    }

    @Override
    Object responseBody(List<UnifyImageFile> files, Map initParams) {
        files.collect { toAbsoluteUrl(it) }
    }
}
