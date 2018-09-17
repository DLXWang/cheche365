package com.cheche365.cheche.ordercenter.service.order;

import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.core.service.OrderImageService;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.core.service.ResourceService;
import com.cheche365.cheche.ordercenter.service.user.OrderCenterInternalUserManageService;
import com.cheche365.cheche.manage.common.util.ImageUploadUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by xu.yelong on 2016/3/16.
 */
@Service
public class OrderCenterPurchaseOrderImageService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PurchaseOrderImageRepository purchaseOrderImageRepository;

    @Autowired
    private PurchaseOrderImageTypeRepository purchaseOrderImageTypeRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderImageStatusRepository purchaseOrderImageStatusRepository;

    @Autowired
    private CompulsoryInsuranceRepository compulsoryInsuranceRepository;

    @Autowired
    private InsuranceRepository insuranceRepository;

    @Autowired
    private OrderImageService orderImageService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private OrderCenterInternalUserManageService orderCenterInternalUserManageService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private PurchaseOrderImageTypeService purchaseOrderImageTypeService;

    public PurchaseOrderImage findById(Long id) {
        return purchaseOrderImageRepository.findOne(id);
    }

    public void save(PurchaseOrderImage purchaseOrderImage) {
        purchaseOrderImageRepository.save(purchaseOrderImage);
    }

    public void updateExpireDate(String imageId, String expireTime) throws ParseException {
        InternalUser internalUser = orderCenterInternalUserManageService.getCurrentInternalUser();
        Long imageIdLong = NumberUtils.toLong(imageId);
        Date expireDate = new SimpleDateFormat("yyyy-MM-dd").parse(expireTime);
        PurchaseOrderImage purchaseOrderImage = purchaseOrderImageRepository.findOne(imageIdLong);
        purchaseOrderImage.setExpireDate(expireDate);
        Date currentDate = new Date();
        purchaseOrderImage.setUpdateTime(currentDate);
        purchaseOrderImage.setOperator(internalUser);
        purchaseOrderImageRepository.save(purchaseOrderImage);

        //  将身份证正面/行驶证副本的过期时间赋值相同的值
        Long preTypeId = purchaseOrderImage.getImageType().getId() - 1L;
        PurchaseOrderImageType purchaseOrderImageType = purchaseOrderImageTypeRepository.findOne(preTypeId);
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(purchaseOrderImage.getObjId());
        PurchaseOrderImageScene purchaseOrderImageScene = orderImageService.getImageScene(purchaseOrder);
        PurchaseOrderImage preTypeImage = purchaseOrderImageRepository.findFirstByObjIdAndImageSceneAndImageType(purchaseOrderImage.getObjId(), purchaseOrderImageScene, purchaseOrderImageType);
        if (preTypeImage != null) {
            preTypeImage.setExpireDate(expireDate);
            preTypeImage.setUpdateTime(currentDate);
            preTypeImage.setOperator(internalUser);
            purchaseOrderImageRepository.save(preTypeImage);
        }

    }


    public void save(List<PurchaseOrderImage> purchaseOrderImages) {
        purchaseOrderImageRepository.save(purchaseOrderImages);
    }


    @Transactional
    public void saveUploadImage(Long imageTypeId, Long imageId, Long purchaseOrderId, MultipartFile imageFile) {
        PurchaseOrderImageType imageType = purchaseOrderImageTypeService.findById(imageTypeId);
        PurchaseOrder purchaseOrder = purchaseOrderService.findById(purchaseOrderId);

        String basePath = resourceService.getResourceAbsolutePath(resourceService.getProperties().getOrderImagePath());
        String cusPath = this.getCusPath(purchaseOrder, imageType);
        String descPath = basePath + cusPath;
        String commonFileName = null;
        try {
            commonFileName = ImageUploadUtil.upload(imageFile, descPath);

        } catch (IOException e) {
            logger.error("upload purchaseOrderImage error ,purchaseOrderId:-->{}", purchaseOrderId, e);
            return;
        }
        String wholePath = getImagePath(cusPath + commonFileName);
        PurchaseOrderImage purchaseOrderImage;
        Date currentData = new Date();
        if (!imageId.equals(0L)) {
            purchaseOrderImage = this.findById(imageId);
        } else {
            purchaseOrderImage = new PurchaseOrderImage();
            purchaseOrderImage.setCreateTime(currentData);
        }
        purchaseOrderImage.setUrl(cusPath + commonFileName);
        purchaseOrderImage.setStatus(PurchaseOrderImage.STATUS.AUDIT);
        purchaseOrderImage.setUpdateTime(currentData);
        purchaseOrderImage.setOperator(orderCenterInternalUserManageService.getCurrentInternalUser());
        purchaseOrderImage.setImageType(imageType);
        purchaseOrderImage.setSource(PurchaseOrderImage.SOURCE.ORDER_CENTER);
        purchaseOrderImage.setObjId(purchaseOrderId);
        purchaseOrderImage.setUploadTime(currentData);
        purchaseOrderImage.setChannel(Channel.Enum.ORDER_CENTER_11);

        PurchaseOrderImageScene purchaseOrderImageScene = orderImageService.getImageScene(purchaseOrder);
        purchaseOrderImage.setImageScene(purchaseOrderImageScene);

        purchaseOrderImageRepository.save(purchaseOrderImage);

        //更新订单照片状态
        this.checkImageStatus(purchaseOrderImage.getObjId());
    }

    private String getImagePath(String path) {
        String prefix = resourceService.getProperties().getOrderImagePath();
        return resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(prefix), path);
    }

    @Transactional
    public int auditImages(PurchaseOrderImage purchaseOrderImage) {
        purchaseOrderImageRepository.save(purchaseOrderImage);

        //更新总照片状态
        return this.checkImageStatus(purchaseOrderImage.getObjId());
    }

    @Transactional
    public void delImage(Long imageId, Long imageTypeId, PurchaseOrder purchaseOrder) {
        PurchaseOrderImageType purchaseOrderImageType = purchaseOrderImageTypeRepository.findOne(imageTypeId);
        PurchaseOrderImage purchaseOrderImage = purchaseOrderImageRepository.findOne(imageId);
        InternalUser internalUser = orderCenterInternalUserManageService.getCurrentInternalUser();
        if (purchaseOrderImageType.getParentId().equals(7L)) {
            //自定义类型,删除图片,删除类型,并检查订单的照片状态,且更新
            purchaseOrderImageRepository.delete(purchaseOrderImage);
            purchaseOrderImageTypeRepository.delete(purchaseOrderImageType);
            this.checkImageStatus(purchaseOrderImage.getObjId());
        } else {
            //其他类型,清空url,更改状态为待上传
            purchaseOrderImage.setUrl("");
            purchaseOrderImage.setUpdateTime(new Date());
            purchaseOrderImage.setStatus(PurchaseOrderImage.STATUS.UPLOAD);
            purchaseOrderImage.setOperator(internalUser);
            purchaseOrderImageRepository.save(purchaseOrderImage);

            //更新订单的照片状态
            PurchaseOrderImageStatus purchaseOrderImageStatus = purchaseOrderImageStatusRepository.findFirstByPurchaseOrder(purchaseOrder);
            purchaseOrderImageStatus.setStatus(PurchaseOrderImage.STATUS.UPLOAD);
            purchaseOrderImageStatusRepository.save(purchaseOrderImageStatus);
        }


    }

    /**
     * @param purchaseOrderId
     * @return
     */
    private int checkImageStatus(Long purchaseOrderId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(purchaseOrderId);
        int status = 0;
        PurchaseOrderImageScene purchaseOrderImageScene = orderImageService.getImageScene(purchaseOrder);
        Integer subTypeSize = purchaseOrderImageTypeRepository.findByParentIdsAndOrderId(purchaseOrderImageScene.getId(), purchaseOrderId);
        List<PurchaseOrderImage> imageList = purchaseOrderImageRepository.findByObjIdGroupByImageType(purchaseOrder.getId(), purchaseOrderImageScene);

        int pass = 0, noPass = 0, audit = 0, upload = 0;

        //如果照片数小于类型数,不执行循环,直接更新为待上传
        if (imageList.size() == subTypeSize) {
            for (PurchaseOrderImage purchaseOrderImage : imageList) {
                int currentStatus = purchaseOrderImage.getStatus();
                if (currentStatus == PurchaseOrderImage.STATUS.UPLOAD) {
                    upload = 1;
                    break;
                } else if (currentStatus == PurchaseOrderImage.STATUS.PASS) {
                    pass++;
                } else if (currentStatus == PurchaseOrderImage.STATUS.NOT_PASS) {
                    noPass++;
                } else if (currentStatus == PurchaseOrderImage.STATUS.AUDIT) {
                    audit++;
                }
            }
            if (upload > 0) {//优先判断
                status = PurchaseOrderImage.STATUS.UPLOAD;
            } else if (pass == subTypeSize) {
                status = PurchaseOrderImage.STATUS.PASS;
            } else if (noPass > 0) {
                status = PurchaseOrderImage.STATUS.NOT_PASS;
            } else {
                status = PurchaseOrderImage.STATUS.AUDIT;
            }
        }

        //更新订单的照片状态
        PurchaseOrderImageStatus purchaseOrderImageStatus = purchaseOrderImageStatusRepository.findFirstByPurchaseOrder(purchaseOrder);
        if (purchaseOrderImageStatus == null) {
            purchaseOrderImageStatus = new PurchaseOrderImageStatus();
        }
        purchaseOrderImageStatus.setPurchaseOrder(purchaseOrder);
        purchaseOrderImageStatus.setStatus(status);
        purchaseOrderImageStatusRepository.save(purchaseOrderImageStatus);

        return status;
    }


    public String getCusPath(PurchaseOrder purchaseOrder, PurchaseOrderImageType purchaseOrderImageType) {
        String customPath;
        String identityCard = StringUtils.EMPTY;
        User user = purchaseOrder.getApplicant();

        if (purchaseOrderImageType.getParentId() == 1L || purchaseOrderImageType.getParentId() == 6L || purchaseOrderImageType.getParentId() == 7L) {
            CompulsoryInsurance compulsoryInsurance = compulsoryInsuranceRepository.findByQuoteRecordId(purchaseOrder.getObjId());
            if (compulsoryInsurance != null) {
                identityCard = compulsoryInsuranceRepository.findByQuoteRecordId(purchaseOrder.getObjId()).getInsuredIdNo();
            }
            if (StringUtils.isEmpty(identityCard)) {
                identityCard = insuranceRepository.findByQuoteRecordId(purchaseOrder.getObjId()).getInsuredIdNo();
            }
            customPath = user.getId() + File.separator + identityCard + File.separator + purchaseOrderImageType.getId() + File.separator;

        } else {
            customPath = purchaseOrder.getAuto().getLicensePlateNo() + File.separator + purchaseOrder.getAuto().getEngineNo()
                + File.separator + purchaseOrder.getAuto().getVinNo() + File.separator + purchaseOrderImageType.getId() + File.separator;
        }

        return customPath.replace("*", "米").replace("#", "井");
    }


    public String getImageStatus(PurchaseOrder purchaseOrder) {
        PurchaseOrderImageStatus purchaseOrderImageStatus = purchaseOrderImageStatusRepository.findFirstByPurchaseOrder(purchaseOrder);
        if (purchaseOrderImageStatus == null) {
            return PurchaseOrderImage.STATUS.STATUS_MAP.get(PurchaseOrderImage.STATUS.UPLOAD);
        } else {
            return PurchaseOrderImage.STATUS.STATUS_MAP.get(purchaseOrderImageStatus.getStatus());
        }
    }
}
