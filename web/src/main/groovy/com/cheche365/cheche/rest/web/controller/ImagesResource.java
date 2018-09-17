package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.PurchaseOrder;
import com.cheche365.cheche.core.repository.DailyRestartInsuranceRepository;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.repository.QuoteRecordRepository;
import com.cheche365.cheche.core.service.image.ImgUploadService;
import com.cheche365.cheche.core.service.image.UnifyImageFile;
import com.cheche365.cheche.rest.util.ImageFileUtil;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.cheche365.cheche.web.version.VersionedResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Created by zhengwei on 4/4/17.
 */

@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/images")
@VersionedResource(from = "1.5")
public class ImagesResource extends ContextResource{

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final Integer BUSINESS_TYPE_QUOTE_PHONE = 1;

    @Autowired
    private PurchaseOrderRepository poRepo;
    @Autowired
    private DailyRestartInsuranceRepository restartRepo;
    @Autowired
    private QuoteRecordRepository qrRepo;
    @Autowired
    List<ImgUploadService> services;


    @RequestMapping(value = "", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> getImages(@RequestParam(value = "orderNo") String orderNo) {
        Map initParams = initOrder(orderNo);
        ImgUploadService service = findService(initParams);
        return getResponseEntity(service.toUpload(initParams));
    }

    @RequestMapping(value = "", consumes="application/json", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope> uploadImageInJSON(@RequestBody Map body) {
        Integer businessType = Integer.parseInt(String.valueOf(body.get("businessType")));
        Map initParams = initParams(businessType, body.get("orderNo"), body.get("restartInsuranceId"));
        initParams.put("additionalParameters", body.get("additionalParameters"));


        if (body.get("scan") != null && (Boolean)body.get("scan")){
            initParams.put("scan", true);
        }
        ImgUploadService service = findService(initParams);

        List<UnifyImageFile> files = ImageFileUtil.unifyFormat((List)body.get("files"),(Integer)body.get("imageType"));
        return getResponseEntity(service.doService(files, initParams));

    }


    @RequestMapping(value = "", consumes="multipart/form-data", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope> uploadImageInBinary(MultipartHttpServletRequest request) {
        Integer businessType = Integer.valueOf(request.getParameter("businessType"));
        Map initParams = initParams(businessType, request.getParameter("orderNo"), request.getParameter("restartInsuranceId"));
        ImgUploadService service = findService(initParams);

        List<UnifyImageFile> files = ImageFileUtil.unifyFormat(request.getFileMap().values());
        if (request.getParameter("scan") != null && Boolean.valueOf(request.getParameter("scan"))){
            initParams.put("scan", true);
        }
        return getResponseEntity(service.doService(files, initParams));
    }

    private ImgUploadService findService(Map initParams){

        Optional<ImgUploadService> serviceOpt = services.stream().filter(service ->
            service.support(initParams)
        ).findFirst();
        if(!serviceOpt.isPresent()){
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "图片上传服务不存在 ");
        }
        return serviceOpt.get();
    }

    private Map initParams(Integer businessType, Object orderNo, Object restartId){
        Map params = new HashMap(){{
            put("user", safeGetCurrentUserWithCallback());
            put("channel", ClientTypeUtil.getChannel(request));
            put("businessType", businessType);
            put("apiVersion",apiVersion());
        }};

        if(!BUSINESS_TYPE_QUOTE_PHONE.equals(businessType)){
            params.putAll(initOrder(orderNo));
            if(null != restartId){
                params.put("restart", restartRepo.findOne(Long.valueOf(restartId.toString())));
            }
        } else {
            logger.debug("行驶证／驾驶证图片上传，忽略订单相关参数提取");
        }

        return params;
    }

    private Map initOrder(Object orderNo){
        Map params = new HashMap();
        if(null != orderNo){
            PurchaseOrder order = poRepo.findFirstByOrderNo(orderNo.toString());
            params.put("order", order);
            if(null != order){
                params.put("quoteRecord", qrRepo.findOne(order.getObjId()));
            }
        }
        return params;
    }

}
