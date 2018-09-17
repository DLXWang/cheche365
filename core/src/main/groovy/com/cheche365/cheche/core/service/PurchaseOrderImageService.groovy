package com.cheche365.cheche.core.service

import com.cheche365.cheche.core.model.DailyInsurance
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.PurchaseOrderImage
import com.cheche365.cheche.core.model.PurchaseOrderImageCustom
import com.cheche365.cheche.core.model.PurchaseOrderImageScene
import com.cheche365.cheche.core.model.PurchaseOrderImageStatus
import com.cheche365.cheche.core.model.PurchaseOrderImageType
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.repository.PurchaseOrderImageCustomRepository
import com.cheche365.cheche.core.repository.PurchaseOrderImageRepository
import com.cheche365.cheche.core.repository.PurchaseOrderImageSceneTypeRepository
import com.cheche365.cheche.core.repository.PurchaseOrderImageStatusRepository
import com.cheche365.cheche.core.repository.PurchaseOrderImageTypeRepository
import groovy.util.logging.Slf4j
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import static com.cheche365.cheche.core.constants.BaoXianConstant.CALL_BACK_STATE
import static com.cheche365.cheche.core.constants.BaoXianConstant.LACK_OF_IMAGE
import static com.cheche365.cheche.core.constants.CacheConstant.STRING_INSURED_SUCCESS_FLAG
import static com.cheche365.cheche.core.model.OrderStatus.Enum.FINISHED_5
import static com.cheche365.cheche.core.model.OrderStatus.Enum.PAID_3
import static com.cheche365.cheche.core.model.OrderStatus.Enum.PENDING_PAYMENT_1
import static com.cheche365.cheche.core.model.PurchaseOrderImage.SOURCE.WEB
import static com.cheche365.cheche.core.model.PurchaseOrderImage.STATUS.UPLOAD
import static com.cheche365.cheche.core.model.PurchaseOrderImageScene.Enum.API_CUSTOM_9
import static com.cheche365.cheche.core.model.PurchaseOrderImageScene.Enum.BAOXIAN_INSURE_8
import static com.cheche365.cheche.core.model.PurchaseOrderImageScene.Enum.ORDER_FINISHED_12
import static com.cheche365.cheche.core.model.PurchaseOrderImageScene.Enum.VEHICLE_EXAMINATIOS_11
import static com.cheche365.cheche.core.model.PurchaseOrderImageType.Enum.API_CUSTOM_IMAGE_TYPE
import static com.cheche365.cheche.core.model.PurchaseOrderImageType.Enum.BAOXIAN_IMAGE_TYPE
import static com.cheche365.cheche.core.model.PurchaseOrderImageType.Enum.VEHICLE_EXAMINATIOS_IMAGE_TYPE
import static com.cheche365.cheche.core.model.QuoteSource.Enum.PLATFORM_SOURCES
import static java.util.Calendar.SECOND

/**
 * Created by zhengwei on 4/4/17.
 */
@Slf4j
@Service
class PurchaseOrderImageService {

    @Autowired
    private QuoteSupplementInfoService quoteSupplementInfoService;
    @Autowired
    private PurchaseOrderImageRepository purchaseOrderImageRepository;
    @Autowired
    private PurchaseOrderImageTypeRepository purchaseOrderImageTypeRepository;
    @Autowired
    private PurchaseOrderImageStatusRepository purchaseOrderImageStatusRepository;
    @Autowired
    private PurchaseOrderImageSceneTypeRepository sceneTypeRepository
    @Autowired
    private PurchaseOrderService purchaseOrderService
    @Autowired
    private QuoteRecordService quoteRecordService
    @Autowired
    private PurchaseOrderImageCustomRepository poicRepository
    @Autowired
    StringRedisTemplate stringRedisTemplate
    @Autowired
    private IResourceService resourceService;
    List<PurchaseOrderImage> findByOrderAndParentImageType(PurchaseOrder order, PurchaseOrderImageType imageType) {
        purchaseOrderImageRepository.findAllByObjIdAndParentImageType(order.id, imageType)
    }

    List<PurchaseOrderImage> findByLastDailyInsurance(DailyInsurance dailyInsurance) {
        purchaseOrderImageRepository.findByLastDailyInsurance(dailyInsurance)
    }

    PurchaseOrderImage findOrCreatePOI(Long objId, PurchaseOrderImageScene imageScene, PurchaseOrderImageType imageType) {
        def existedPOI = purchaseOrderImageRepository.findFirstByObjIdAndImageSceneAndImageType(objId, imageScene, imageType)
        if (!existedPOI || (API_CUSTOM_IMAGE_TYPE.id == imageType.parentId)) {
            log.debug('无已存在的POI对象，新建对象 objId {}, imageType {}', objId, imageType?.name)
            return new PurchaseOrderImage(imageScene: imageScene, imageType: imageType, createTime: new Date(), objId: objId)
        } else {
            log.debug('待上传图片POI纪录存在，重用现有纪录 objId {}, imageType {}', objId, imageType?.name)
            existedPOI.updateTime = new Date()
            return existedPOI
        }
    }

    public save(List<PurchaseOrderImage> poi) {
        purchaseOrderImageRepository.save(poi)
    }

    public getImageType(Long id) {
        purchaseOrderImageTypeRepository.findOne(id);
    }

    public getImageTypeByExternalCode(Integer externalCode, Long parentId){
        purchaseOrderImageTypeRepository.findByExternalTypeAndParentId(externalCode, parentId)
    }

    public updatePOIStatus(Long orderId){
        PurchaseOrderImageStatus imageStatus = purchaseOrderImageStatusRepository.findFirstByPurchaseOrderId(orderId);
        if(imageStatus){
            imageStatus.setStatus(PurchaseOrderImageStatus.STATUS.WAITING_AUDIT);
            purchaseOrderImageStatusRepository.save(imageStatus);
        } else {
            log.warn('上传图片修改purchase order image status为空，忽略更新状态 order id: {}', orderId)
        }

    }

    Map toUploadImage(PurchaseOrderImageScene imageScene) {
        List groupImages = []
        def parentImageTypes = sceneTypeRepository.findByImageSceneOrderByImageTypeAsc(imageScene).imageType
        parentImageTypes.each { parentImageType ->
            def imageTypes = purchaseOrderImageTypeRepository.findByParentId(parentImageType.id)
            def groupImage = getGroupImage(imageTypes, parentImageType)
            groupImages.add(groupImage)
        }
        return [status: PurchaseOrderImageStatus.STATUS.WAITING_UPLOAD, groupImages: groupImages]
    }

    Map findToUploadByOrderAndImageType(PurchaseOrder order, PurchaseOrderImageType parentImageType) {
        def imageTypes = this.findByOrderAndParentImageType(order, parentImageType).findAll { !it.url }.imageType
        if (!imageTypes) {
            return null
        }
        def groupImage = getGroupImage(imageTypes, parentImageType)
        return [status: PurchaseOrderImageStatus.STATUS.WAITING_UPLOAD, groupImages: [groupImage]]
    }

    private LinkedHashMap<String, Object> getGroupImage(List<PurchaseOrderImageType> imageTypes, PurchaseOrderImageType parentImageType) {
        def images = imageTypes.collect {
            [
                name  : it.name,
                id    : it.id,
                status: PurchaseOrderImage.STATUS.UPLOAD,
                sampleUrl : StringUtils.isNotEmpty(it.getSampleUrl()) ? resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(resourceService.getProperties().getOrderImagePath()), it.getSampleUrl()+"?version=2") : null
            ]
        }
        Map<String, String> status = PurchaseOrderImageType.Enum.getUploadDesc(parentImageType)

        [
            title : status.get("title"),
            desc  : status.get("desc"),
            images: images
        ]
    }

    private vehicleExaminatiosOnImage(PurchaseOrder order){
        List<PurchaseOrderImageType> orderImageTypeList = purchaseOrderImageTypeRepository.findByParentIds([BigInteger.valueOf(VEHICLE_EXAMINATIOS_IMAGE_TYPE.id )])
        orderImageTypeList?.each { imageType->
            loadOrCreateOrderImages(order,VEHICLE_EXAMINATIOS_11,imageType)
        }
    }

    private loadOrCreateOrderImages(PurchaseOrder order,PurchaseOrderImageScene imageScene,PurchaseOrderImageType imageType){
        findOrCreatePOI(order.id as Long, imageScene, imageType).with {
            updateTime = new Date()
            channel = order.sourceChannel
            source = WEB
            status = UPLOAD
            url = ""
            it
        }
    }

    @Transactional
    onImage(PurchaseOrder order, List images,Map state,def imageState) {
        images.collect { bxImage ->
            PurchaseOrderImageType imageType = getImageTypeByExternalCode(bxImage.imageType as Integer, BAOXIAN_IMAGE_TYPE.id)
            loadOrCreateOrderImages(order,BAOXIAN_INSURE_8,imageType)
        }.with {
            order.statusDisplay = state.get(imageState).statusDisplay
            purchaseOrderService.saveOrder(order)
            save(it)
        }
    }

    Boolean needCustomUpload(PurchaseOrder order) {

        PurchaseOrderImageCustom poic = poicRepository.findFirstByPurchaseOrderOrderByIdDesc(order)
        if (!poic || !poic.needUpload) {
            return false
        }
        if(isNeedUploadImagesForOrder(order)){
            return false
        }
        PurchaseOrderImage poi = purchaseOrderImageRepository.findFirstByObjIdAndImageSceneOrderByIdDesc(order.id, API_CUSTOM_9)

        return (!poi || DateUtils.truncatedCompareTo(poic.createTime, poi.createTime, SECOND) > 0)
    }

    def persistCustomImage(PurchaseOrder order, String description) {
        PurchaseOrderImageCustom poic = poicRepository.findFirstByPurchaseOrderOrderByIdDesc(order)
        if (poic?.needUpload) {
            poic.description = description
            poic.updateTime = new Date()
        } else {
            poic = new PurchaseOrderImageCustom(
                purchaseOrder: order,
                description: description,
                needUpload: Boolean.TRUE,
                createTime: new Date(),
                updateTime: new Date()
            )
        }
        order.statusDisplay = CALL_BACK_STATE.get(LACK_OF_IMAGE).statusDisplay
        purchaseOrderService.saveOrder(order)
        poicRepository.save(poic)
    }

    def updateCustomImageStatus(PurchaseOrder order) {
        PurchaseOrderImageCustom poic = poicRepository.findFirstByPurchaseOrderOrderByIdDesc(order)
        if (poic?.needUpload) {
            poic.needUpload = Boolean.FALSE
            poic.updateTime = new Date()
            poicRepository.save(poic)
        }
    }

    def findLastOrderImageCustom(PurchaseOrder order) {
        poicRepository.findFirstByPurchaseOrderOrderByIdDesc(order)
    }

    def persistOrderFinishedImages(PurchaseOrder purchaseOrder){
        if(isNeedUploadImagesForOrder(purchaseOrder)){
            log.info '出单补充影像,orderNo {}', purchaseOrder.orderNo
            def parentImageTypes = sceneTypeRepository.findByImageSceneOrderByImageTypeAsc(ORDER_FINISHED_12).imageType
            def images = []
            parentImageTypes.each { parentImageType ->
                def imageTypes = purchaseOrderImageTypeRepository.findByParentId(parentImageType.id)
                imageTypes.each{imageType->
                    images << loadOrCreateOrderImages(purchaseOrder,ORDER_FINISHED_12,imageType)
                }
            }
            save(images)
        }
    }

    boolean isNeedUploadImagesForOrder(PurchaseOrder purchaseOrder){
        QuoteRecord quoteRecord = quoteRecordService.getByPurchaseOrderByObjId(purchaseOrder.objId)
        if(!quoteRecord){
            return false
        }

        quoteRecord.type in PLATFORM_SOURCES &&
            ((PENDING_PAYMENT_1 == purchaseOrder.status && stringRedisTemplate.hasKey(STRING_INSURED_SUCCESS_FLAG + purchaseOrder.orderNo)) ||
                purchaseOrder.status in [FINISHED_5,PAID_3])
    }

}
