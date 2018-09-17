package com.cheche365.cheche.ordercenter.web.controller.order;

import com.cheche365.cheche.core.model.InternalUser;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.model.PurchaseOrderImage;
import com.cheche365.cheche.core.model.ResultModel;
import com.cheche365.cheche.core.service.PurchaseOrderService;
import com.cheche365.cheche.core.service.ResourceService;
import com.cheche365.cheche.ordercenter.service.order.OrderCenterPurchaseOrderImageService;
import com.cheche365.cheche.ordercenter.service.order.PurchaseOrderImageTypeService;
import com.cheche365.cheche.ordercenter.service.user.OrderCenterInternalUserManageService;
import com.cheche365.cheche.ordercenter.web.model.order.PurchaseOrderImageTypeViewModel;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xu.yelong on 2016/3/8.
 */
@RestController
@RequestMapping("/orderCenter/order/image")
public class PurchaseOrderImageController {
    private Logger logger = LoggerFactory.getLogger(PurchaseOrderImageController.class);

    @Autowired
    private OrderCenterPurchaseOrderImageService orderCenterPurchaseOrderImageService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private PurchaseOrderImageTypeService purchaseOrderImageTypeService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private OrderCenterInternalUserManageService orderCenterInternalUserManageService;

    private final static Integer BUFFER_SIZE = 1024;

    @RequestMapping(value = "/imageType", method = RequestMethod.POST)
    public ResultModel addCustomImageType(@RequestParam(value = "purchaseOrderId", required = false) Long purchaseOrderId,
                                          @RequestParam(value = "customTypes", required = false) String[] customTypes,
                                          @RequestParam(value = "imageTypeIds", required = false) String[] imageTypeIds) {
        if (logger.isDebugEnabled()) {
            logger.debug("add customImageType for purchaseOrder[ purchaseOrderId : {}]", purchaseOrderId);
        }
        InternalUser currentUser = orderCenterInternalUserManageService.getCurrentInternalUser();
        purchaseOrderImageTypeService.save(purchaseOrderId, customTypes, imageTypeIds, currentUser);
        return new ResultModel(true, "保存成功");
    }

    @RequestMapping(value = "/list/{orderId}", method = RequestMethod.GET)
    public List<PurchaseOrderImageTypeViewModel> listOrderImages(@PathVariable Long orderId) {

        //组装照片类型
        List<PurchaseOrderImageTypeViewModel> imageTypeList = purchaseOrderImageTypeService.getOrderImageTypes(orderId);

        return imageTypeList;
    }

    @RequestMapping(value = "/{orderId}", method = RequestMethod.GET)
    public Map<String, Object> listAllOrderImages(@PathVariable Long orderId) {
        PurchaseOrder purchaseOrder = purchaseOrderService.findById(orderId);
        //组装照片类型
        Map<String, Object> resultMap = purchaseOrderImageTypeService.getAllOrderImageTypes(purchaseOrder);

        return resultMap;
    }


    @RequestMapping(value = "/{imageId}/{status}", method = RequestMethod.PUT)
    public Map<String, Object> audit(@PathVariable Long imageId, @PathVariable Integer status) {
        if (logger.isDebugEnabled()) {
            logger.debug("update purchaseOrderImage status[ imageId : {}, status : {} ]", imageId, status);
        }
        InternalUser internalUser = orderCenterInternalUserManageService.getCurrentInternalUser();
        PurchaseOrderImage purchaseOrderImage = orderCenterPurchaseOrderImageService.findById(imageId);
        purchaseOrderImage.setStatus(status);
        purchaseOrderImage.setOperator(internalUser);
        Date currentDate = new Date();
        purchaseOrderImage.setUpdateTime(currentDate);
        purchaseOrderImage.setAuditTime(currentDate);

        //保存 并且个更新订单照片总状态
        int auditStatus = orderCenterPurchaseOrderImageService.auditImages(purchaseOrderImage);

        Map<String, Object> resultMap = new HashMap();
        resultMap.put("flag", true);
        resultMap.put("status", auditStatus);
        resultMap.put("statusMessage", PurchaseOrderImage.STATUS.STATUS_MAP.get(auditStatus));
        return resultMap;
    }


    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public void upload(@RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                       @RequestParam(value = "imgId", required = false) String imgId,
                       @RequestParam(value = "imgTypeId", required = false) String imgTypeId,
                       @RequestParam(value = "orderId", required = false) String orderId,
                       HttpServletResponse response) {

        if (logger.isDebugEnabled()) {
            logger.debug("upload purchaseOrderImage[ imageId : {}, imageTypeId : {} , purchaseOrderId : {} ]", imgId, imgTypeId, orderId);
        }
        if (!imageFile.isEmpty()) {

            Long imageId = NumberUtils.toLong(imgId);
            Long imageTypeId = NumberUtils.toLong(imgTypeId);
            Long purchaseOrderId = NumberUtils.toLong(orderId);

            //保存照片并更新订单的照片状态
            orderCenterPurchaseOrderImageService.saveUploadImage(imageTypeId, imageId, purchaseOrderId, imageFile);

            try {
                this.outPrint(response, "success");
            } catch (IOException e) {
                logger.error("upload purchaseOrderImage error ,purchaseOrderId:-->{}", orderId, e);
            }

        }

    }


    @RequestMapping(value = "/{imageId}", method = RequestMethod.POST)
    public ResultModel delImage(@PathVariable Long imageId,
                                @RequestParam(value = "imageTypeId", required = false) String imageTypeId,
                                @RequestParam(value = "orderId", required = false) String orderId) {
        if (logger.isDebugEnabled()) {
            logger.debug("delete orderImage by id -->[ id:{} ]", imageId);
        }
        Long imageTypeIdLong = NumberUtils.toLong(imageTypeId);
        Long orderIdLong = NumberUtils.toLong(orderId);
        PurchaseOrder purchaseOrder = purchaseOrderService.findById(orderIdLong);
        orderCenterPurchaseOrderImageService.delImage(imageId, imageTypeIdLong, purchaseOrder);

        return new ResultModel(true, "删除成功");
    }


    @RequestMapping(value = "/expireTime", method = RequestMethod.POST)
    public ResultModel updateExpireDate(@RequestParam(value = "imageId", required = false) String imageId,
                                        @RequestParam(value = "expireTime", required = false) String expireTime) throws ParseException {
        if (logger.isDebugEnabled()) {
            logger.debug("update orderImage expireTime by id -->[ id:{} ]", imageId);
        }
        orderCenterPurchaseOrderImageService.updateExpireDate(imageId, expireTime);
        return new ResultModel(true, "保存成功");
    }

    @RequestMapping(value = "/getMessage", method = RequestMethod.GET)
    public Map<String, Object> getMessage(@RequestParam(value = "purchaseOrderId", required = false) String purchaseOrderId) {
        return purchaseOrderImageTypeService.getMessage(purchaseOrderId);
    }

    @RequestMapping(value = "/sendMessage", method = RequestMethod.POST)
    public ResultModel sendMessage(@RequestParam(value = "purchaseOrderId", required = false) Long purchaseOrderId) {
        try {
            purchaseOrderImageTypeService.sendMessage(purchaseOrderId);
            return new ResultModel(true, "发送成功");
        } catch (Exception e) {
            logger.error("send message failed , purchaseOrderId: -->{}", purchaseOrderId, e);
            return new ResultModel(false, "发送失败");
        }
    }


    private void outPrint(HttpServletResponse response, String ajaxString) throws IOException {
        PrintWriter out = null;
        try {
            response.setContentType("text/html; charset=utf-8");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("X-Frame-Options", "SAMEORIGIN");
            out = response.getWriter();
            out.write(ajaxString);
            out.flush();
        } catch (Exception ex) {
            throw new RuntimeException("write return string has error", ex);
        } finally {
            if (out != null)
                out.close();
        }
    }

}
