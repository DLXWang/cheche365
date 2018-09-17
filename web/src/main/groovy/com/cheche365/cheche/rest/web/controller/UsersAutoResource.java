package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.Address;
import com.cheche365.cheche.core.model.Auto;
import com.cheche365.cheche.core.model.Channel;
import com.cheche365.cheche.core.model.Gift;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.repository.PurchaseOrderRepository;
import com.cheche365.cheche.core.service.AddressService;
import com.cheche365.cheche.core.service.AutoService;
import com.cheche365.cheche.core.service.GiftService;
import com.cheche365.cheche.core.util.BeanUtil;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.util.ClientTypeUtil;
import com.cheche365.cheche.web.version.VersionedResource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by zhengwei on 12/10/15.
 * 与车车用户地址车辆相关的业务。API的URL和 {@link UsersResource}一致，只是为了方便管理代码。
 */

@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/users")
@VersionedResource(from = "1.0")
public class UsersAutoResource extends ContextResource {

    private Logger logger = LoggerFactory.getLogger(UsersResource.class);

    @Autowired
    private AutoService autoService;

    @Autowired
    public AddressService addressService;

    @Autowired
    public PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private GiftService giftService;


    @VersionedResource(from = "1.2")
    @RequestMapping(value = "/current/autos", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<Auto>> getAutoByCurrentUser1_2(@RequestParam(value = "licensePlateNo", required = true) String licensePlateNo,
                                                                          @RequestParam(value = "fields", required = false, defaultValue = "id,licensePlateNo,engineNo,vinNo,owner,enrollDate,identity") String fields,
                                                                          HttpServletRequest request) throws Exception {

        User currentUser = this.currentUser();
        logger.debug("begin to get the auto under current user, user id: " + currentUser.getId() + "  license: " + licensePlateNo);
        Auto auto = this.autoService.getAutoByUser(currentUser.getId(), licensePlateNo);
        logger.debug("finish get auto under current user, auto loaded {}.", auto != null);

        if (auto != null && !ClientTypeUtil.getChannel(request).isOrderCenterChannel()) {
            autoService.encryptAuto(session.getId(), auto);
        }

        RestResponseEnvelope envelope = new RestResponseEnvelope(auto);
        return new ResponseEntity<>(envelope, HttpStatus.OK);
    }

    @RequestMapping(value = "/current/gifts", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope<List<Auto>>> getGiftByCurrentUser(@RequestParam(value = "status", required = false, defaultValue = "") String status) throws Exception {

        User currentUser = this.currentUser();
        logger.debug("begin to get the gift under current user, user id: " + currentUser.getId() + "  status: " + status);
        List<Gift> gifts = this.giftService.getGiftByUser(currentUser, status);
        logger.debug("finish get gift under current user, there are " + gifts.size() + " gifts loaded.");
        RestResponseEnvelope envelope = new RestResponseEnvelope(gifts);
        return new ResponseEntity<>(envelope, HttpStatus.OK);
    }

    @RequestMapping(value = "/auto", method = RequestMethod.POST)
    public void addUserAndAuto(@RequestBody Auto auto) {
        autoService.saveAuto(auto);
    }

    @VersionedResource(from = "1.4")
    @RequestMapping(value = "/address/{id}", method = RequestMethod.DELETE)
    public HttpEntity<RestResponseEnvelope> addressDel1_4(@PathVariable("id") Long id) {//根据地址表ID进行删除
        Address address;
        address = addressService.getOne(id, this.currentUser());
        if (null == address)
            throw new BusinessException(BusinessException.Code.OBJECT_NOT_EXIST,"该地址不存在");
        address.setDisable(true);
        address = addressService.save(address, this.currentUser());
        return getResponseEntity(address);
    }

    @VersionedResource(from = "1.4")
    @RequestMapping(value = "/address", method = RequestMethod.PUT)
    public HttpEntity<RestResponseEnvelope> addressAddOrUpdate_14(@RequestBody Address address) {
        if (address.getId() == null || address.getId().intValue() <= 0) {
            return this.add(address);
        } else {
            return this.update(address);
        }
    }

    @VersionedResource(from = "1.2")
    @RequestMapping(value = "/address", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> addressListFor12(@RequestParam(value = "page", required = false) Integer page,
                                                             @RequestParam(value = "size", required = false) Integer size,
                                                             @RequestParam(value = "areaId", required = false) String areaId) {

        Pageable pageable = new PageRequest(toPageStart(page), toPageSize(size));
        Page<Address> pageAddress;
        if (StringUtils.isNotEmpty(areaId)){
            pageAddress = addressService.getAddressList(this.currentUser(), areaId.substring(0, 2) + "%", pageable);
        } else {
            pageAddress = addressService.getAddressList(this.currentUser(), pageable);
        }
        return getResponseEntity(pageAddress);
    }


    @VersionedResource(from = "1.1")
    @RequestMapping(value = "/auto", method = RequestMethod.GET)
    public HttpEntity<RestResponseEnvelope> currentUserAutoList(@RequestParam(value = "page", required = false) Integer page,
                                                                @RequestParam(value = "size", required = false) Integer size) {
        Pageable pageable = new PageRequest(toPageStart(page), toPageSize(size));
        Page autoPage = autoService.listByUserPage(this.currentUser(), pageable);
        return getResponseEntity(autoPage);
    }


    private HttpEntity<RestResponseEnvelope> add(Address address) {
        if (address.getId() != null) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "新增对象的主键不能有值");
        }
        address.setApplicant(this.currentUser());
        addressService.correctBadAddress(address);
        address = addressService.save(address, this.currentUser());
        return getResponseEntity(address);
    }


    private HttpEntity<RestResponseEnvelope> update(Address address) {

        User currentUser = this.currentUser();
        addressService.correctBadAddress(address);
        Address addressFromDB = addressService.getOne(address.getId());
        List purchaseOrderList = purchaseOrderRepository.findByDeliveryAddress(address);
        if (purchaseOrderList.size() == 0) {
            String[] edit_contain = new String[]{"id", "area", "street", "district", "city", "province", "name", "telephone", "mobile", "postalcode", "defaultAddress"};
            BeanUtil.copyPropertiesContain(address, addressFromDB, edit_contain);
            address = addressService.save(addressFromDB, currentUser);
        } else {
            //存在报价信息，则新增，并修改原纪录的是否可管理的状态
            address.setId(0L);
            address.setApplicant(this.currentUser());
            address = addressService.save(address, currentUser);
            //获取老的车辆信息，并修改状态
            addressFromDB.setDisable(true);//不可用
            addressService.save(addressFromDB);
        }
        return getResponseEntity(address);
    }

}
