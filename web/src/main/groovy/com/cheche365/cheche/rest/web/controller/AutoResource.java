package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.Auto;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.model.UserAuto;
import com.cheche365.cheche.core.model.VehicleLicense;
import com.cheche365.cheche.core.mongodb.repository.MoApplicationLogRepository;
import com.cheche365.cheche.core.service.AutoService;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.rest.service.auto.AutoModelService;
import com.cheche365.cheche.rest.service.vl.VehicleLicenseService;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.cheche365.cheche.web.version.VersionedResource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by zhengwei on 3/24/15.
 */
@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/autos")
@VersionedResource(from = "1.0")
public class AutoResource extends ContextResource {

    @Autowired
    private AutoService autoService;

    @Autowired
    private VehicleLicenseService vlService;


    @Autowired
    private AutoModelService autoModelService;


    @VersionedResource(from = "1.2")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope> insertAuto1_2(@RequestBody Auto auto, HttpServletRequest request) {
        User user = this.currentUser();
        if (auto.getId() != null) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "新增车辆的主键不能有值");
        }

        String clientIdentifier = request.getSession().getId();
        autoService.decryptAuto(auto, this.safeGetCurrentUser(), clientIdentifier);

        Auto autoFromDB = autoService.findAuto(auto, user);
        if (autoFromDB != null) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "新增车辆已存在");
        }
        auto = autoService.insertAuto(user, auto);
        return getResponseEntity(auto);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    public HttpEntity<RestResponseEnvelope> updateAuto(@RequestBody Auto auto, HttpServletRequest request) {

        if (null == auto.getId()) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "车辆ID为空");
        }

        String clientIdentifier = request.getSession().getId();
        autoService.decryptAuto(auto, this.safeGetCurrentUser(), clientIdentifier);

        UserAuto userAuto = this.autoService.searchUserAuto(this.currentUser().getId(), auto.getId());
        if (null == userAuto) {
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST, "车辆不存在");
        }

        Auto autoAfterSave = this.autoService.update(userAuto, auto);
        RestResponseEnvelope envelope = new RestResponseEnvelope(autoAfterSave);

        return new ResponseEntity(envelope, HttpStatus.OK);

    }

    @VersionedResource(from = "1.1")
    @RequestMapping(value = "/{autoId}", method = RequestMethod.DELETE)
    public HttpEntity<RestResponseEnvelope> deleteAuto1_1(@PathVariable Long autoId) {
        if (autoId == null) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "车辆ID不能为空");
        }
        return getResponseEntity(autoService.delAuto(autoId, this.currentUser()));
    }

    @RequestMapping(value = "/autoAddOrUpdates", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope> autoAddOrUpdates(@RequestBody Auto auto, HttpServletRequest request) {
        if (auto.getId() == null) {
            return this.insertAuto1_2(auto, request);
        } else {
            return this.updateAuto(auto, request);
        }
    }

    @VersionedResource(from = "1.2")
    @RequestMapping(value = "/license", method = RequestMethod.GET)
    public HttpEntity getVehicleLicense(
            @RequestParam(value = "owner", required = false) String owner,
            @RequestParam(value = "licensePlateNo", required = false) String licensePlateNo,
            @RequestParam(value = "newCarFlag", required = false) Boolean newCarFlag,
            @RequestParam(value = "extraFields", required = false) String extraFields) {

        if (Boolean.TRUE.equals(newCarFlag) || Auto.NEW_CAR_PLATE_NO.equals(licensePlateNo)) {
            return getResponseEntity(this.vlService.formatNullInsuranceInfo(owner, extraFields));
        }

        return this.vlService.find(licensePlateNo, owner, extraFields);
    }

    @VersionedResource(from = "1.7")
    @RequestMapping(value = "/license/internal", method = RequestMethod.GET)
    public HttpEntity getVehicleLicenseInternal(@RequestParam String licensePlateNo) {
        return getResponseEntity(this.vlService.findVLInternal(licensePlateNo));
    }

    @VersionedResource(from = "1.2", to = "1.5")
    @RequestMapping(value = "/supplementInfo", method = RequestMethod.GET)
    public List getSupplementInfo(@RequestParam(value = "cityCode") Long cityCode,
                                  @RequestParam(value = "isTransfer", required = false, defaultValue = "0") Boolean isTransfer) {
        return autoService.getSupplementInfo(cityCode, isTransfer, ClientTypeUtil.getChannel(request));
    }

    @VersionedResource(from = "1.2", to = "1.5")
    @RequestMapping(value = "/carModelSupplementInfo", method = RequestMethod.GET)
    public List getCarModelSupplementInfo(@RequestParam(value = "cityCode") Long cityCode, HttpServletRequest request) {
        return autoService.getCarModelSupplementInfo(cityCode, ClientTypeUtil.getChannel(request));
    }

    @VersionedResource(from = "1.6")
    @RequestMapping(value = "/autoTypes", method = RequestMethod.GET)
    public HttpEntity getAutoTypes(@RequestParam String vehicleLicense, @RequestParam(required = false) Long insuranceAreaId) {

        Auto auto = CacheUtil.doJacksonDeserialize(vehicleLicense, Auto.class);

        VehicleLicense vl = VehicleLicense.createVLByAuto(auto);

        if (StringUtils.isEmpty(vl.getLicensePlateNo()) && insuranceAreaId == null) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "新车查询输入参数缺少行驶地区");
        }

        List autoModels = autoModelService.getAutoModels(vl, insuranceAreaId);

        if (CollectionUtils.isNotEmpty(autoModels)) {
            return getResponseEntity(autoModels);
        }else{
            RestResponseEnvelope envelope = new RestResponseEnvelope(null);
            return new ResponseEntity(envelope, HttpStatus.OK);
        }
    }

    @VersionedResource(from = "1.7")
    @RequestMapping(value = "/dictionaries", method = RequestMethod.GET)
    public HttpEntity getDictionaries() {
        return getResponseEntity(AutoService.getDictionaries());
    }

}
