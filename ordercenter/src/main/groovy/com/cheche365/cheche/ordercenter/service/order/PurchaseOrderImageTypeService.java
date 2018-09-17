package com.cheche365.cheche.ordercenter.service.order;

import com.cheche365.cheche.common.util.StringUtil;
import com.cheche365.cheche.core.model.*;
import com.cheche365.cheche.core.repository.*;
import com.cheche365.cheche.core.service.OrderImageService;
import com.cheche365.cheche.core.service.ResourceService;
import com.cheche365.cheche.web.service.system.SystemUrlGenerator;
import com.cheche365.cheche.manage.common.service.sms.SMSHelper;
import com.cheche365.cheche.ordercenter.web.model.order.PurchaseOrderImageSubType;
import com.cheche365.cheche.ordercenter.web.model.order.PurchaseOrderImageTypeViewModel;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.util.UriEncoder;

import java.math.BigInteger;
import java.util.*;

/**
 * Created by xu.yelong on 2016/3/16.
 */
@Service
public class PurchaseOrderImageTypeService {

    @Autowired
    private PurchaseOrderImageRepository purchaseOrderImageRepository;

    @Autowired
    private PurchaseOrderImageTypeRepository purchaseOrderImageTypeRepository;

    @Autowired
    private PurchaseOrderImageStatusRepository purchaseOrderImageStatusRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private OrderImageService orderImageService;

    @Autowired
    PurchaseOrderImageSceneTypeRepository purchaseOrderImageSceneTypeRepository;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private QuoteRecordRepository quoteRecordRepository;

    @Autowired
    private SMSHelper smsHelper;

    @Autowired
    private SystemUrlGenerator systemUrlGenerator;


    public PurchaseOrderImageType findById(Long id) {
        return purchaseOrderImageTypeRepository.findOne(id);
    }

    public PurchaseOrderImageType save(PurchaseOrderImageType purchaseOrderImageType) {
        return purchaseOrderImageTypeRepository.save(purchaseOrderImageType);
    }

    public void save(List<PurchaseOrderImageType> purchaseOrderImageTypes) {
        this.purchaseOrderImageTypeRepository.save(purchaseOrderImageTypes);
    }

    @Transactional
    public void save(Long purchaseOrderId, String[] customTypes, String[] imageTypeIds, InternalUser currentUser) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(purchaseOrderId);
        List<PurchaseOrderImageType> imageTypeList = new ArrayList<>();
        List<PurchaseOrderImage> imageList = new ArrayList<>();
        PurchaseOrderImageScene purchaseOrderImageScene = orderImageService.getImageScene(purchaseOrder);

        PurchaseOrderImageType purchaseOrderImageType;
        PurchaseOrderImage purchaseOrderImage;
        Date now = new Date();

        //保存自定义类型
        for (String customType : customTypes) {
            purchaseOrderImageType = new PurchaseOrderImageType();
            purchaseOrderImageType.setName(customType);
            purchaseOrderImageType.setParentId(7L);
            purchaseOrderImageType.setOperator(currentUser);
            purchaseOrderImageType.setCreateTime(now);
            purchaseOrderImageType.setUpdateTime(now);
            imageTypeList.add(purchaseOrderImageType);
        }
        if (imageTypeList.size() > 0) {
            purchaseOrderImageTypeRepository.save(imageTypeList);
        }


        //保存自定义类型的图片,图片默认为空
        for (PurchaseOrderImageType orderImageType : imageTypeList) {
            purchaseOrderImage = new PurchaseOrderImage();
            purchaseOrderImage.setImageType(orderImageType);
            purchaseOrderImage.setOperator(currentUser);
            purchaseOrderImage.setCreateTime(now);
            purchaseOrderImage.setUpdateTime(now);
            purchaseOrderImage.setObjId(purchaseOrderId);
            purchaseOrderImage.setImageScene(purchaseOrderImageScene);
            purchaseOrderImage.setSource(PurchaseOrderImage.SOURCE.ORDER_CENTER);
            purchaseOrderImage.setStatus(PurchaseOrderImage.STATUS.UPLOAD);
            purchaseOrderImage.setUrl("");
            imageList.add(purchaseOrderImage);
        }
        if (imageList.size() > 0) {
            purchaseOrderImageRepository.save(imageList);
        }

        //增加用户勾选的别的子类型对应的照片
        List<PurchaseOrderImage> newSubImageList = new ArrayList<>();
        PurchaseOrderImage forSubTypeImage;
        for (String imageTypeIdString : imageTypeIds) {
            Long imageTypeId = NumberUtils.toLong(imageTypeIdString);
            PurchaseOrderImageType subImageType = purchaseOrderImageTypeRepository.findOne(imageTypeId);
            forSubTypeImage = new PurchaseOrderImage();
            forSubTypeImage.setOperator(currentUser);
            forSubTypeImage.setUpdateTime(now);
            forSubTypeImage.setExpireDate(null);
            forSubTypeImage.setStatus(PurchaseOrderImage.STATUS.UPLOAD);
            forSubTypeImage.setSource(PurchaseOrderImage.SOURCE.ORDER_CENTER);
            forSubTypeImage.setCreateTime(now);
            forSubTypeImage.setImageType(subImageType);
            forSubTypeImage.setObjId(purchaseOrderId);
            forSubTypeImage.setImageScene(purchaseOrderImageScene);
            forSubTypeImage.setUrl("");

            newSubImageList.add(forSubTypeImage);
        }
        if (newSubImageList.size() > 0) {
            purchaseOrderImageRepository.save(newSubImageList);
        }

        //更新该订单的照片状态,为 未上传
        this.updateImageStatus(purchaseOrder);

    }

    /**
     * 更新该订单的照片状态,为 未上传
     *
     * @param purchaseOrder
     */
    public void updateImageStatus(PurchaseOrder purchaseOrder) {
        //更新该订单的照片状态,为 未上传
        PurchaseOrderImageStatus purchaseOrderImageStatus = purchaseOrderImageStatusRepository.findFirstByPurchaseOrder(purchaseOrder);
        if (purchaseOrderImageStatus == null) {
            purchaseOrderImageStatus = new PurchaseOrderImageStatus();
            purchaseOrderImageStatus.setPurchaseOrder(purchaseOrder);
        }
        purchaseOrderImageStatus.setStatus(PurchaseOrderImageStatus.STATUS.WAITING_UPLOAD);
        purchaseOrderImageStatusRepository.save(purchaseOrderImageStatus);
    }


    /**
     * 获取订单所有所需照片模型
     *
     * @param orderId
     * @return
     */
    public List<PurchaseOrderImageTypeViewModel> getOrderImageTypes(Long orderId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(orderId);
        List<PurchaseOrderImageTypeViewModel> imageTypeViewModels = new ArrayList<>();

        PurchaseOrderImageScene purchaseOrderImageScene = orderImageService.getImageScene(purchaseOrder);
        //必须要有的id集合
        List<BigInteger> parentMustTypeIds = purchaseOrderImageSceneTypeRepository.findImageTypeByImageScene(purchaseOrderImageScene);

        List<BigInteger> parentTypeIds = purchaseOrderImageSceneTypeRepository.findBySceneAndPurchaseOrder(purchaseOrderImageScene, orderId);
        List<PurchaseOrderImageType> parentTypes = purchaseOrderImageTypeRepository.findByIds(parentTypeIds);
        for (PurchaseOrderImageType parentType : parentTypes) {
            PurchaseOrderImageTypeViewModel viewModel = new PurchaseOrderImageTypeViewModel();
            List<PurchaseOrderImageSubType> subTypeViews = new ArrayList<>();

            List<PurchaseOrderImageType> loopList;
            if (parentMustTypeIds.contains(BigInteger.valueOf(parentType.getId()))) {
                loopList = purchaseOrderImageTypeRepository.findByParentId(parentType.getId());
            } else {
                loopList = purchaseOrderImageTypeRepository.findByParentIdAndObjId(parentType.getId(), purchaseOrder.getId(), purchaseOrderImageScene.getId());
            }
            for (PurchaseOrderImageType subType : loopList) {
                PurchaseOrderImageSubType subTypeView = new PurchaseOrderImageSubType();

                List<PurchaseOrderImage> images = purchaseOrderImageRepository.findByObjIdAndImageTypeAndImageScene(orderId, subType, purchaseOrderImageScene);

                if (images != null && images.size() > 0) {
                    PurchaseOrderImage image = images.get(0);
                    if (!StringUtil.isNull(image.getUrl())) {
                        image.setUrl(getImagePath(image.getUrl()));
                    }
                    subTypeView.setPurchaseOrderImage(image);
                } else {
                    subTypeView.setPurchaseOrderImage(new PurchaseOrderImage());
                }
                subTypeView.setPurchaseOrderImageType(subType);

                subTypeViews.add(subTypeView);
            }

            viewModel.setPurchaseOrderImageType(parentType);
            viewModel.setSubTypeList(subTypeViews);
            viewModel.setPurchaseOrderId(orderId);
            imageTypeViewModels.add(viewModel);
        }

        return imageTypeViewModels;
    }


    /**
     * 获取订单所有所需照片模型,增加照片时候调用
     * 获取所有的父类型和对应的子类型,以及当前订单的所有自定义类型
     *
     * @param purchaseOrder
     * @return
     */
    public Map<String, Object> getAllOrderImageTypes(PurchaseOrder purchaseOrder) {
        Map<String, Object> resultMap = new HashMap<>();
        List<PurchaseOrderImageTypeViewModel> imageTypeViewModels = new ArrayList<>();

        PurchaseOrderImageScene purchaseOrderImageScene = orderImageService.getImageScene(purchaseOrder);
        //根据场景得到,订单必须要有的照片
        List<BigInteger> parentMustTypeIds = purchaseOrderImageSceneTypeRepository.findImageTypeByImageScene(purchaseOrderImageScene);
        parentMustTypeIds.add(BigInteger.valueOf(7L));

        List<BigInteger> parentTypeIds = new ArrayList<>();
        parentTypeIds.add(BigInteger.valueOf(0L));
        List<PurchaseOrderImageType> parentTypes = purchaseOrderImageTypeRepository.findByParentIds(parentTypeIds);//所有的父类型
        Boolean parentCheckedFlag;
        for (PurchaseOrderImageType parentType : parentTypes) {
            parentCheckedFlag = false;
            PurchaseOrderImageTypeViewModel viewModel = new PurchaseOrderImageTypeViewModel();
            List<PurchaseOrderImageSubType> subTypeViews = new ArrayList<>();
            List<PurchaseOrderImageType> loopList;
            if (parentType.getId() == 7L) {
                loopList = purchaseOrderImageTypeRepository.findByParentIdAndObjId(parentType.getId(), purchaseOrder.getId(), purchaseOrderImageScene.getId());
            } else {
                loopList = purchaseOrderImageTypeRepository.findByParentId(parentType.getId());
            }
            Boolean checkedFlag;
            for (PurchaseOrderImageType subType : loopList) {
                checkedFlag = false;
                PurchaseOrderImageSubType subTypeView = new PurchaseOrderImageSubType();

                List<PurchaseOrderImage> images = purchaseOrderImageRepository.findByObjIdAndImageTypeAndImageScene(purchaseOrder.getId(), subType, purchaseOrderImageScene);
                if (images != null && images.size() > 0) {
                    PurchaseOrderImage image = images.get(0);
                    if (!StringUtil.isNull(image.getUrl())) {
                        image.setUrl(getImagePath(image.getUrl()));
                    }
                    subTypeView.setPurchaseOrderImage(image);
                    checkedFlag = true;//只要能查到image,则类型打勾
                } else {
                    subTypeView.setPurchaseOrderImage(new PurchaseOrderImage());
                }
                subTypeView.setPurchaseOrderImageType(subType);
                subTypeView.setCheckedFlag(checkedFlag);

                if (checkedFlag) {//如果子类型有不为空的,则父类型被选中
                    parentCheckedFlag = true;
                }
                subTypeViews.add(subTypeView);
            }

            viewModel.setParentCheckedFlag(parentCheckedFlag);
            viewModel.setPurchaseOrderImageType(parentType);
            viewModel.setSubTypeList(subTypeViews);
            viewModel.setPurchaseOrderId(purchaseOrder.getId());
            imageTypeViewModels.add(viewModel);
        }

        resultMap.put("dataList", imageTypeViewModels);
        resultMap.put("mustTypeIds", parentMustTypeIds);

        return resultMap;
    }

    private String getImagePath(String path) {
        String prefix = resourceService.getProperties().getOrderImagePath();
        return resourceService.absoluteUrl(resourceService.getResourceAbsolutePath(prefix), path);
    }


    /**
     * 获取短信内容
     *
     * @param orderId
     * @return
     */
    public Map<String, Object> getMessage(String orderId) {
        Long purchaseOrderId = NumberUtils.toLong(orderId);
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(purchaseOrderId);
        String shortUrl = UriEncoder.decode(systemUrlGenerator.toImageUrl(purchaseOrder.getOrderNo()));
//        if (!RuntimeUtil.isProductionEnv()) {
//            shortUrl = UriEncoder.decode(shortUrl);
//        }
        QuoteRecord quoteRecord = quoteRecordRepository.findOne(purchaseOrder.getObjId());
        String companyName;
        if (quoteRecord != null) {
            companyName = quoteRecord.getInsuranceCompany().getName();
        } else {
            companyName = "无结果";
        }

        String mobile = purchaseOrder.getApplicant().getMobile();

        String message = "尊敬的用户您好，您投保的" + companyName + "公司车险，需要提供照片资料，请点击链接进行上传 " + shortUrl;
        Map<String, Object> resultMap = new HashedMap();
        resultMap.put("pass", true);
        resultMap.put("sendMessage", message);
        resultMap.put("mobile", mobile);

        return resultMap;
    }

    /**
     * 获取短信内容
     *
     * @param purchaseOrderId
     */
    public void sendMessage(Long purchaseOrderId) {
        Map<String, Object> resultMap = new HashedMap();
        PurchaseOrder purchaseOrder = null;
        if (purchaseOrderId != null) {
            purchaseOrder = purchaseOrderRepository.findOne(purchaseOrderId);
        }
        smsHelper.sendOrderImageUploadMsg(purchaseOrder);
    }


}
