package com.cheche365.cheche.rest.web.controller;

import com.cheche365.cheche.core.constants.WebConstants;
import com.cheche365.cheche.core.exception.BusinessException;
import com.cheche365.cheche.core.model.Device;
import com.cheche365.cheche.core.repository.DeviceRepository;
import com.cheche365.cheche.core.util.CacheUtil;
import com.cheche365.cheche.web.ContextResource;
import com.cheche365.cheche.web.response.RestResponseEnvelope;
import com.cheche365.cheche.web.version.VersionedResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * Created by mahong on 2015/7/20.
 */
@RestController
@RequestMapping("/" + ContextResource.VERSION_NO + "/devices")
@VersionedResource(from = "1.1")
public class DeviceResource extends ContextResource {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    private DeviceRepository deviceRepository;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public HttpEntity<RestResponseEnvelope<Device>> insertDevice(@RequestBody Device device) {

        if (device == null || device.getDeviceUniqueId() == null) {
            throw new BusinessException(BusinessException.Code.INPUT_FIELD_NOT_VALID, "设备唯一标识不能为空");
        }

        Device deviceDB = deviceRepository.findFirstByDeviceUniqueId(device.getDeviceUniqueId());
        Device deviceSaved;
        if (deviceDB == null) {
            device.setCreateTime(new Date());
            deviceSaved = deviceRepository.save(device);
        } else {
            device.setId(deviceDB.getId());
            device.setCreateTime(deviceDB.getCreateTime());
            device.setUpdateTime(new Date());
            deviceSaved = deviceRepository.save(device);
        }

        RestResponseEnvelope<Device> envelope = new RestResponseEnvelope<>(deviceSaved);
        return new ResponseEntity(envelope, HttpStatus.OK);

    }

}
