package com.cheche365.cheche.core.service

import com.cheche365.cheche.common.util.DateUtils
import com.cheche365.cheche.core.exception.BusinessException
import com.cheche365.cheche.core.model.Area
import com.cheche365.cheche.core.model.CompulsoryInsurance
import com.cheche365.cheche.core.model.InsuranceCompany
import com.cheche365.cheche.core.model.OrderStatus
import com.cheche365.cheche.core.model.PurchaseOrder
import com.cheche365.cheche.core.model.PurchaseOrderImage
import com.cheche365.cheche.core.model.PurchaseOrderImageScene
import com.cheche365.cheche.core.model.PurchaseOrderImageSceneType
import com.cheche365.cheche.core.model.PurchaseOrderImageStatus
import com.cheche365.cheche.core.model.PurchaseOrderImageType
import com.cheche365.cheche.core.model.QuoteRecord
import com.cheche365.cheche.core.model.QuoteSupplementInfo
import com.cheche365.cheche.core.repository.CompulsoryInsuranceRepository
import com.cheche365.cheche.core.repository.InsuranceRepository
import com.cheche365.cheche.core.repository.PurchaseOrderImageRepository
import com.cheche365.cheche.core.repository.PurchaseOrderImageSceneTypeRepository
import com.cheche365.cheche.core.repository.PurchaseOrderImageStatusRepository
import com.cheche365.cheche.core.repository.PurchaseOrderImageTypeRepository
import com.cheche365.cheche.core.repository.PurchaseOrderRepository
import com.cheche365.cheche.core.repository.QuoteRecordRepository
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct

import static com.cheche365.cheche.core.model.PurchaseOrderImageScene.Enum.ORDER_FINISHED_12
import static com.cheche365.cheche.core.model.PurchaseOrderImageScene.Enum.SINOSAFE_INSURE_7
import static com.cheche365.cheche.core.model.PurchaseOrderImageScene.Enum.THIRD_PARTY_IMAGE_SCENES
import static com.cheche365.cheche.core.model.PurchaseOrderImageStatus.STATUS.FAIL
import static com.cheche365.cheche.core.model.PurchaseOrderImageStatus.STATUS.SUCCESS
import static com.cheche365.cheche.core.model.PurchaseOrderImageStatus.STATUS.WAITING_AUDIT
import static com.cheche365.cheche.core.model.PurchaseOrderImageStatus.STATUS.WAITING_UPLOAD
import static com.cheche365.cheche.core.model.QuoteSource.Enum.PLATFORM_SOURCES

/**
 * Created by shanxf on 2016/12/6.
 */

@Service
class OrderImageService {

    @Autowired
    private InsuranceRepository insuranceRepository;

    @Autowired
    private CompulsoryInsuranceRepository compulsoryInsuranceRepository;

    @Autowired
    private PurchaseOrderImageStatusRepository purchaseOrderImageStatusRepository;

    @Autowired
    private PurchaseOrderImageTypeRepository purchaseOrderImageTypeRepository;

    @Autowired
    private PurchaseOrderImageSceneTypeRepository purchaseOrderImageSceneTypeRepository;

    @Autowired
    private QuoteSupplementInfoService quoteSupplementInfoService

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private IResourceService resourceService;

    @Autowired
    PurchaseOrderImageService purchaseOrderImageService

    private static String ROOT_PATH;

    @PostConstruct
    init() {
        ROOT_PATH = resourceService.getProperties().getOrderImagePath()
    }


    enum ImagePathPolicy {
        Auto, Identity
    }

    @Autowired
    private PurchaseOrderImageRepository purchaseOrderImageRepository;

    public List<String> getUploadImgUrls(Long objId, PurchaseOrderImageScene imageScene) {
        List<String> imgUrls = new ArrayList<>();
        List<PurchaseOrderImage> images = purchaseOrderImageRepository.findAllByObjIdAndImageScene(objId, imageScene);
        for (PurchaseOrderImage image : images) {
            imgUrls.add(resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(ROOT_PATH), image.getUrl()))
        }
        return imgUrls;
    }

    Map getUploadImgGroupByOrderNo(String orderNo, PurchaseOrderImageScene imageScene) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findFirstByOrderNo(orderNo);
        QuoteRecord quoteRecord = this.quoteRecordRepository.findOne(purchaseOrder.getObjId());
        PurchaseOrderImageStatus imageStatus = purchaseOrderImageStatusRepository.findFirstByPurchaseOrder(purchaseOrder);
        imageStatus = validateOrder(purchaseOrder, quoteRecord, imageStatus, imageScene)
        List groupImages = uploadImgGroups(purchaseOrder.getId(), imageScene, imageStatus.getStatus(), quoteRecord);
        Map result = new HashMap<>();
        def auditFailReasons = loadImageHints(groupImages)
        auditFailReasons ? result.put("auditFailReasons", auditFailReasons) : null;
        result.put("status", imageStatus.getStatus());
        result.put("groupImages", groupImages);
        return result;
    }

    def loadImageHints(List groupImages){
        groupImages
            .images
            .flatten()
            .findAll{ PurchaseOrderImage.STATUS.NOT_PASS == it.status }
            .collect {
                [
                    name : it.name,
                    hint : it.hint
                ]
            }
    }

    List uploadImgGroups(Long objId, PurchaseOrderImageScene imageScene, Integer imageStatus, QuoteRecord quoteRecord) {
        List groupImages;
        Integer uploadedImageSize = purchaseOrderImageRepository.findByObjIdAndImageScene(objId, imageScene);
        if (imageStatus == WAITING_UPLOAD && uploadedImageSize == 0) {
            Map<Long, String> reusableImage = (quoteRecord == null) ? new HashMap<>() : findReusableImage(quoteRecord);
            groupImages = uploadFirstImage(objId, imageScene, reusableImage);
        } else {
            groupImages = uploadImages(objId, imageScene);
        }
        return groupImages;
    }

    PurchaseOrderImageStatus validateOrder(PurchaseOrder purchaseOrder, QuoteRecord quoteRecord, PurchaseOrderImageStatus imageStatus, PurchaseOrderImageScene imageScene) {
        if (imageStatus) {
            return imageStatus
        }

        if (purchaseOrder.orderStatusAllowedTab(quoteRecord)) {
            imageStatus = new PurchaseOrderImageStatus()
            imageStatus.setPurchaseOrder(purchaseOrder);
            imageStatus.setStatus(PurchaseOrderImageStatus.STATUS.WAITING_UPLOAD)
            if (!imageScene.csIgnore) {
                purchaseOrderImageStatusRepository.save(imageStatus)
            }
            imageStatus
        } else {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "订单状态不合法");
        }
    }

    List uploadFirstImage(Long objId, PurchaseOrderImageScene imageScene, Map<Long, String> reusableImages) {
        List groupImages = new ArrayList<>();
        List<PurchaseOrderImageSceneType> purchaseOrderImageSceneTypes = purchaseOrderImageSceneTypeRepository.findByImageSceneOrderByImageTypeAsc(imageScene);
        for (PurchaseOrderImageSceneType p : purchaseOrderImageSceneTypes) {
            Map<String, String> status = PurchaseOrderImageType.Enum.getUploadDesc(p.getImageType())
            List images = new ArrayList<>();
            Map data = new HashMap<>();
            data.put("title", status.get("title"));
            data.put("desc", status.get("desc"));
            data.put("status", WAITING_UPLOAD)
            List<PurchaseOrderImageType> imageTypes = purchaseOrderImageTypeRepository.findByParentId(p.getImageType().getId());
            for (PurchaseOrderImageType imageType : imageTypes) {
                PurchaseOrderImage afterSave = createPurchaseOrderImage(objId, imageScene, imageType, reusableImages)
                images.add(generateImageData(imageType, afterSave));
            }
            data.put("images", images);
            groupImages.add(data);
        }
        List<PurchaseOrderImage> purchaseOrderImagesExist = purchaseOrderImageRepository.findExistImagesByObjIdAndImageScene(objId, imageScene);
        if (purchaseOrderImagesExist.size() == 0 && !THIRD_PARTY_IMAGE_SCENES.contains(imageScene)) {
            PurchaseOrderImageStatus imageStatus = purchaseOrderImageStatusRepository.findFirstByPurchaseOrderId(objId);
            imageStatus.setStatus(WAITING_AUDIT);
            purchaseOrderImageStatusRepository.save(imageStatus);
        }
        Map oneselfImg = findOneselfDefineImg(objId, imageScene);
        if (oneselfImg.size() > 0) {
            oneselfImg.put("title", THIRD_PARTY_IMAGE_SCENES.contains(imageScene) ? "上传车辆照片" : "新增照片");
            oneselfImg.put("desc", "");
            groupImages.add(oneselfImg);
        }

        return groupImages;
    }

    private Map generateImageData(PurchaseOrderImageType purchaseOrderImageType, PurchaseOrderImage afterSave) {
        def image = [:]
        image.put("name", purchaseOrderImageType.getName());
        image.put("id", purchaseOrderImageType.getId());
        image.put("url", afterSave.url ? resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(ROOT_PATH), afterSave.url) : null);
        image.put("sampleUrl", purchaseOrderImageType.getSampleUrl() ? resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(ROOT_PATH), purchaseOrderImageType.getSampleUrl()) : null);
        image.put("status", afterSave.status);
        return image
    }

    private PurchaseOrderImage createPurchaseOrderImage(Long objId, PurchaseOrderImageScene imageScene, PurchaseOrderImageType imageType, Map<Long, String> reusableImages) {
        PurchaseOrderImage poi = new PurchaseOrderImage();
        poi.setObjId(objId);
        poi.setImageScene(imageScene);
        poi.setUrl(reusableImages.containsKey(imageType.getId()) ? reusableImages.get(imageType.getId()) : "");
        poi.setCreateTime(new Date());
        poi.setImageType(imageType);
        poi.setSource(PurchaseOrderImage.SOURCE.WEB);
        poi.setStatus(reusableImages.containsKey(imageType.getId()) ? PurchaseOrderImage.STATUS.AUDIT : PurchaseOrderImage.STATUS.UPLOAD);
        purchaseOrderImageRepository.save(poi);
    }


    List uploadImages(Long objId, PurchaseOrderImageScene imageScene) {
        List groupImages = new ArrayList<>();
        List<Map<String, String>> status = PurchaseOrderImage.STATUS.STATUS_AUDIT_LIST;
        for (int i = 0; i < status.size(); i++) {
            List<PurchaseOrderImage> purchaseOrderImages;
            List images = new ArrayList<>();
            Map data = new HashMap<>();
            Map<String, String> map = status.get(i);
            data.put("title", (imageScene.csIgnore) ? "影像资料" : map.get("title"));
            data.put("desc", map.get("desc"));
            data.put("status", map.get("status"))
            Integer key = Integer.parseInt(map.get("status").trim());
            if (key != PurchaseOrderImage.STATUS.NOT_PASS) {
                purchaseOrderImages = purchaseOrderImageRepository.findByObjIdAndImageSceneAndStatusOrderByImageTypeAsc(objId, imageScene, key);
            } else {
                purchaseOrderImages = purchaseOrderImageRepository.findByByObjIdAndImageSceneAndStatusAndOther(objId, imageScene, key, PurchaseOrderImage.STATUS.UPLOAD);
                List<PurchaseOrderImage> purchaseOrderImagesNoPass;
                List<PurchaseOrderImage> purchaseOrderImagesUpload;
                purchaseOrderImagesNoPass = purchaseOrderImageRepository.findByObjIdAndImageSceneAndStatusOrderByImageTypeAsc(objId, imageScene, key);
                purchaseOrderImagesUpload = purchaseOrderImageRepository.findByObjIdAndImageSceneAndStatusOrderByImageTypeAsc(objId, imageScene, PurchaseOrderImage.STATUS.UPLOAD);
                if (purchaseOrderImagesNoPass.size() > 0 && purchaseOrderImagesUpload.size() == 0) {
                    data.put("title", "未通过审核");
                }
                if (purchaseOrderImagesNoPass.size() == 0 && purchaseOrderImagesUpload.size() > 0) {
                    data.put("title", "新增照片");
                }
            }
            if (purchaseOrderImages != null) {
                for (PurchaseOrderImage purchaseOrderImage : purchaseOrderImages) {
                    PurchaseOrderImageType purchaseOrderImageType = purchaseOrderImageTypeRepository.findOne(purchaseOrderImage.getImageType().getId());
                    Map image = new HashMap<>();
                    image.put("name", purchaseOrderImageType.getName());
                    image.put("hint", purchaseOrderImage.hint);
                        image.put("id", purchaseOrderImageType.getId());
                    image.put("url", StringUtils.isNotEmpty(purchaseOrderImage.getUrl()) ? resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(ROOT_PATH), purchaseOrderImage.getUrl()) : null);
                    image.put("sampleUrl", StringUtils.isNotEmpty(purchaseOrderImageType.getSampleUrl()) ? resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(ROOT_PATH), purchaseOrderImageType.getSampleUrl()+"?version=2") : null);
                    image.put("status", purchaseOrderImage.getStatus());
                    images.add(image);
                }
            }
            if (images.size() > 0) {
                data.put("images", images);
                groupImages.add(data);
            }
        }
        return groupImages;
    }

    Map findReusableImage(QuoteRecord quoteRecord) {

        String identityCard = StringUtils.EMPTY;
        Map<Long, String> reusableImage = new TreeMap<Long, String>(new Comparator<Long>() {
            int compare(Long obj1, Long obj2) {
                //降序排序
                return obj1 <=> obj2
            }
        })
        CompulsoryInsurance compulsoryInsurance = compulsoryInsuranceRepository.findByQuoteRecordId(quoteRecord.getId());
        if (compulsoryInsurance != null) {
            identityCard = compulsoryInsuranceRepository.findByQuoteRecordId(quoteRecord.getId()).getInsuredIdNo();
        }
        if (StringUtils.isEmpty(identityCard)) {
            identityCard = insuranceRepository.findByQuoteRecordId(quoteRecord.getId()).getInsuredIdNo();
        }

        PurchaseOrderImageType.Enum.IDENTITY_PATH_GROUP.each {
            String findUserUrl = generateImagePath(ImagePathPolicy.Identity, quoteRecord.applicant, identityCard, it.id)
            def url = purchaseOrderImageRepository.findSecondImgUseUrl(findUserUrl, PurchaseOrderImage.STATUS.PASS)
            if (url) {
                reusableImage.put(it.id, url);
            }

        }

        PurchaseOrderImageType.Enum.AUTO_PATH_GROUP.each {
            String findCarUrl = generateImagePath(ImagePathPolicy.Auto, quoteRecord.auto, null, it.id)
            def url
            if (it.parentId != 2L) {
                Date auditTime = DateUtils.getCustomDate(new Date(), -90, 0, 0, 0);
                url = purchaseOrderImageRepository.findSecondImgCarUrlAndAuditTime(findCarUrl, PurchaseOrderImage.STATUS.PASS, auditTime);
            } else {
                url = purchaseOrderImageRepository.findSecondImgCarUrl(findCarUrl, PurchaseOrderImage.STATUS.PASS);
            }
            if (url) {
                reusableImage.put(it.id, url);
            }

        }

        return reusableImage;
    }

    def generateImagePath(ImagePathPolicy policy, sourceObj, identityCard, typeId) {
        def imagePath
        if (ImagePathPolicy.Auto == policy) {
            imagePath = sourceObj.getLicensePlateNo() + File.separator + sourceObj.getEngineNo() + File.separator + sourceObj.getVinNo() + File.separator + typeId + File.separator
        } else if (ImagePathPolicy.Identity == policy) {
            imagePath = sourceObj.getId() + File.separator + identityCard + File.separator + typeId + File.separator
        } else {
            throw new BusinessException(BusinessException.Code.UNEXPECTED_PARAMETER, "非预期路径类型" + policy)
        }

        return imagePath.replace("*", "米").replace("#", "井").trim()

    }

    /**
     * 初次上传检查是否有自定义上传类型
     */
    public Map findOneselfDefineImg(Long objId, PurchaseOrderImageScene imageScene) {
        Map data = new HashMap<>();
        List images = new ArrayList<>();
        List<PurchaseOrderImage> purchaseOrderImages = purchaseOrderImageRepository.findOneselfDefineImgByObjIdAndImageScene(objId, imageScene, 7L);
        for (PurchaseOrderImage purchaseOrderImage : purchaseOrderImages) {
            PurchaseOrderImageType purchaseOrderImageType = purchaseOrderImageTypeRepository.findOne(purchaseOrderImage.getImageType().getId());
            Map image = new HashMap<>();
            image.put("name", purchaseOrderImageType.getName());
            image.put("id", purchaseOrderImageType.getId());
            image.put("url", purchaseOrderImage.getUrl() ? resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(ROOT_PATH), purchaseOrderImage.getUrl()) : null);
            image.put("sampleUrl", purchaseOrderImageType.getSampleUrl() != null ? resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(ROOT_PATH), purchaseOrderImageType.getSampleUrl()) : null);
            image.put("status", purchaseOrderImage.status ? purchaseOrderImage.status : 0);
            images.add(image);
        }
        if (images.size() > 0) {
            data.put("images", images);
        }
        return data;
    }

    PurchaseOrderImageScene getImageScene(PurchaseOrder purchaseOrder) {
        List<QuoteSupplementInfo> supplementInfoList = quoteSupplementInfoService.getSupplementInfosByPurchaseOrder(purchaseOrder)
        if(purchaseOrderImageService.isNeedUploadImagesForOrder(purchaseOrder)){
            return PurchaseOrderImageScene.Enum.ORDER_FINISHED_12
        }

        Boolean transferFlag = false;
        if (supplementInfoList != null && !supplementInfoList.isEmpty()) {
            for (QuoteSupplementInfo quoteSupplementInfo : supplementInfoList) {
                if (quoteSupplementInfo.getFieldPath().contains("transferDate")) {
                    transferFlag = true
                }
            }
        }

        if (transferFlag) {
            return PurchaseOrderImageScene.Enum.TRANSFER_OWNERSHIP_2
        } else {
            return Area.isBJArea(purchaseOrder.getArea()) ? PurchaseOrderImageScene.Enum.BEI_JING_4 : PurchaseOrderImageScene.Enum.OTHER_PROVINCE_1
        }
    }

    Boolean showImageTab(QuoteRecord quoteRecord, PurchaseOrder purchaseOrder) {
        Boolean orderStatusAllowedTab = purchaseOrder.orderStatusAllowedTab(quoteRecord)
        Boolean sinosafe = (InsuranceCompany.Enum.SINOSAFE_205000 == quoteRecord.insuranceCompany)

        return orderStatusAllowedTab && (isBotpyOrUkCase(quoteRecord, purchaseOrder)|| sinosafe || !quoteRecord.apiQuote())
    }

    boolean uploadStatusEnabled(QuoteRecord quoteRecord, PurchaseOrder purchaseOrder) {

        if(quoteRecord.type in PLATFORM_SOURCES){
            List<PurchaseOrderImage> poi =  purchaseOrderImageRepository.findAllByObjIdAndImageScene(purchaseOrder.id, ORDER_FINISHED_12)
            if(poi && !poi.every {SUCCESS == it.status}) {
                return purchaseOrder.status != OrderStatus.Enum.PENDING_PAYMENT_1
            }
        }

        PurchaseOrderImageStatus imageStatus = purchaseOrderImageStatusRepository.findFirstByPurchaseOrder(purchaseOrder)
        if (!imageStatus) {
            if ((InsuranceCompany.Enum.SINOSAFE_205000 == quoteRecord.insuranceCompany )) {
                def poi = purchaseOrderImageRepository.findFirstByObjIdAndImageSceneOrderByIdDesc(purchaseOrder.id, SINOSAFE_INSURE_7)
                return !(poi?.url)
            }
            return true
        }

        imageStatus &&  [WAITING_UPLOAD, FAIL, WAITING_AUDIT].contains(imageStatus.status)
    }

    private boolean isBotpyOrUkCase(QuoteRecord quoteRecord, PurchaseOrder purchaseOrder){
        if(quoteRecord.type in PLATFORM_SOURCES){
            List<PurchaseOrderImage> poi =  purchaseOrderImageRepository.findAllByObjIdAndImageScene(purchaseOrder.id, ORDER_FINISHED_12)
            return poi as boolean
        }
        return false
    }



}
